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

    @Test
    public void testCreateAssociation_Success() {
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
        createFulfillment(new Fulfillment(1L, 1L, "MWH.001"));
        createFulfillment(new Fulfillment(1L, 1L, "MWH.012"));

        Fulfillment f3 = new Fulfillment(1L, 1L, "MWH.023");

        given()
                .contentType("application/json")
                .body(f3)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max3PerStore() {
        createFulfillment(new Fulfillment(2L, 2L, "MWH.001"));
        createFulfillment(new Fulfillment(3L, 2L, "MWH.001"));
        createFulfillment(new Fulfillment(1L, 2L, "MWH.012"));

        Fulfillment f4 = new Fulfillment(1L, 2L, "MWH.023");

        given()
                .contentType("application/json")
                .body(f4)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max5PerWarehouse() {
        createFulfillment(new Fulfillment(1L, 3L, "MWH.001"));
        createFulfillment(new Fulfillment(2L, 3L, "MWH.001"));
        createFulfillment(new Fulfillment(3L, 3L, "MWH.001"));
        // Reuse product IDs since we only have 3 products, but different pairs or logic
        // might be needed.
        // The rule is "Max 5 per Warehouse". It doesn't say unique products.
        // Assuming (Product, Store) pairs.
        createFulfillment(new Fulfillment(1L, 1L, "MWH.001"));
        createFulfillment(new Fulfillment(2L, 1L, "MWH.001"));

        Fulfillment f6 = new Fulfillment(3L, 1L, "MWH.001");

        given()
                .contentType("application/json")
                .body(f6)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }
}
