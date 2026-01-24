package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductResourceTest {

    private Long createProduct(String name) {
        Product p = new Product();
        p.name = name;
        return given()
                .contentType("application/json")
                .body(p)
                .when().post("/product")
                .then()
                .statusCode(201)
                .extract().as(Product.class).id;
    }

    @Test
    public void testGetProducts() {
        createProduct("Get Products Test");
        given()
                .when().get("/product")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetSingleProduct_Found() {
        Long id = createProduct("Test Product");

        given()
                .when().get("/product/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Test Product"));
    }

    @Test
    public void testGetSingleProduct_NotFound() {
        given()
                .when().get("/product/9999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateProduct() {
        Product p = new Product();
        p.name = "New Product";

        given()
                .contentType("application/json")
                .body(p)
                .when().post("/product")
                .then()
                .statusCode(201)
                .body("name", is("New Product"));
    }

    @Test
    public void testCreateProductWithId() {
        Product p = new Product();
        p.id = 123L;
        p.name = "Invalid Product";

        given()
                .contentType("application/json")
                .body(p)
                .when().post("/product")
                .then()
                .statusCode(422);
    }

    @Test
    public void testUpdateProduct() {
        Long id = createProduct("Old Name");

        Product updated = new Product();
        updated.name = "New Name";

        given()
                .contentType("application/json")
                .body(updated)
                .when().put("/product/" + id)
                .then()
                .statusCode(200)
                .body("name", is("New Name"));
    }

    @Test
    public void testUpdateProductNoName() {
        Long id = createProduct("No Name Test");
        Product p = new Product();

        given()
                .contentType("application/json")
                .body(p)
                .when().put("/product/" + id)
                .then()
                .statusCode(422);
    }

    @Test
    public void testDeleteProduct() {
        Long id = createProduct("Delete Product");

        given()
                .when().delete("/product/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteProductNotFound() {
        given()
                .when().delete("/product/9999")
                .then()
                .statusCode(404);
    }
}
