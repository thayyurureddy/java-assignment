package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreResourceTest {

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    private Long createStore(String name) {
        Store store = new Store(name);
        return given()
                .contentType("application/json")
                .body(store)
                .when().post("/store")
                .then()
                .statusCode(201)
                .extract().as(Store.class).id;
    }

    @Test
    public void testGetSingleStore() {
        Long id = createStore("Get Single Store");

        given()
                .when().get("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Get Single Store"));
    }

    @Test
    public void testGetSingleStoreNotFound() {
        given()
                .when().get("/store/9999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllStores() {
        createStore("All Stores Test");
        given()
                .when().get("/store")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateStore() {
        Store store = new Store();
        store.name = "Test Create Store";
        store.quantityProductsInStock = 10;

        given()
                .contentType("application/json")
                .body(store)
                .when().post("/store")
                .then()
                .statusCode(201)
                .body("name", is("Test Create Store"));
    }

    @Test
    public void testCreateStoreWithId() {
        Store store = new Store("Invalid Store");
        store.id = 123L;

        given()
                .contentType("application/json")
                .body(store)
                .when().post("/store")
                .then()
                .statusCode(422);
    }

    @Test
    public void testUpdateStore() {
        Long id = createStore("Update Store");

        Store updated = new Store("Updated Name");
        updated.quantityProductsInStock = 20;

        given()
                .contentType("application/json")
                .body(updated)
                .when().put("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Updated Name"));
    }

    @Test
    public void testUpdateStoreNoName() {
        Long id = createStore("Update No Name");

        Store updated = new Store();
        updated.quantityProductsInStock = 20;

        given()
                .contentType("application/json")
                .body(updated)
                .when().put("/store/" + id)
                .then()
                .statusCode(422);
    }

    @Test
    public void testPatchStore() {
        Long id = createStore("Patch Store");

        Store patched = new Store("Patched Name");

        given()
                .contentType("application/json")
                .body(patched)
                .when().patch("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Patched Name"));
    }

    @Test
    public void testDeleteStore() {
        Long id = createStore("Delete Store");

        given()
                .when().delete("/store/" + id)
                .then()
                .statusCode(204);
    }
}
