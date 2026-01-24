package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FulfillmentResourceTest {

    private void createFulfillment(Fulfillment f) {
        given()
                .contentType("application/json")
                .body(f)
                .when().post("/fulfillment")
                .then()
                .statusCode(201);
    }

    private Long createProduct(String name) {
        java.util.Map<String, Object> product = new java.util.HashMap<>();
        product.put("name", name);
        product.put("stock", 100);

        return given()
                .contentType("application/json")
                .body(product)
                .when().post("/product")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private void createWarehouse(String code, String location) {
        java.util.Map<String, Object> warehouse = new java.util.HashMap<>();
        warehouse.put("businessUnitCode", code);
        warehouse.put("location", location);
        warehouse.put("capacity", 100);
        warehouse.put("stock", 0);

        given()
                .contentType("application/json")
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreateAssociation_Success() {
        // Success case using pre-seeded data from import.sql
        // import.sql uses IDs 1, 2, 3 for both products and stores.
        Fulfillment f = new Fulfillment(1L, 1L, "MWH.001");

        given()
                .contentType("application/json")
                .body(f)
                .when().post("/fulfillment")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreateAssociation_Max2PerProductStore() {
        Long pId = createProduct("P-Max2");
        Long sId = 1L; // Using existing store ID 1

        createFulfillment(new Fulfillment(pId, sId, "MWH.001"));
        createFulfillment(new Fulfillment(pId, sId, "MWH.012"));

        // 3rd attempt for same Product + Store should fail
        Fulfillment f3 = new Fulfillment(pId, sId, "MWH.023");

        given()
                .contentType("application/json")
                .body(f3)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max3PerStore() {
        Long sId = 3L; // Using an existing store ID 3

        // We need 3 unique warehouses for this store.
        Long pId1 = createProduct("P-Max3-1");
        Long pId2 = createProduct("P-Max3-2");
        Long pId3 = createProduct("P-Max3-3");

        createWarehouse("W-M3-1", "L1");
        createWarehouse("W-M3-2", "L2");
        createWarehouse("W-M3-3", "L3");
        createWarehouse("W-M3-4", "L4");

        createFulfillment(new Fulfillment(pId1, sId, "W-M3-1"));
        createFulfillment(new Fulfillment(pId2, sId, "W-M3-2"));
        createFulfillment(new Fulfillment(pId3, sId, "W-M3-3"));

        // 4th unique warehouse for same store should fail
        Fulfillment f4 = new Fulfillment(pId1, sId, "W-M3-4");

        given()
                .contentType("application/json")
                .body(f4)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max5PerWarehouse() {
        String warehouseCode = "W-Max5";
        createWarehouse(warehouseCode, "L5");

        // Add 5 unique products to the same warehouse
        for (int i = 1; i <= 5; i++) {
            Long pId = createProduct("P-M5-" + i);
            createFulfillment(new Fulfillment(pId, 1L, warehouseCode));
        }

        // Add 6th unique product to the same warehouse should fail
        Long pId6 = createProduct("P-M5-6");
        Fulfillment f6 = new Fulfillment(pId6, 1L, warehouseCode);

        given()
                .contentType("application/json")
                .body(f6)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }
}
