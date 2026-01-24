package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FulfillmentResourceTest {

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
}
