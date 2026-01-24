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
        Fulfillment f = new Fulfillment(1L, 1L, "BU001");

        given()
                .contentType("application/json")
                .body(f)
                .when().post("/fulfillment")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreateAssociation_Max2PerProductStore() {
        createFulfillment(new Fulfillment(101L, 101L, "BU101"));
        createFulfillment(new Fulfillment(101L, 101L, "BU102"));

        Fulfillment f3 = new Fulfillment(101L, 101L, "BU103");

        given()
                .contentType("application/json")
                .body(f3)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max3PerStore() {
        createFulfillment(new Fulfillment(201L, 201L, "BU201"));
        createFulfillment(new Fulfillment(202L, 201L, "BU202"));
        createFulfillment(new Fulfillment(203L, 201L, "BU203"));

        Fulfillment f4 = new Fulfillment(204L, 201L, "BU204");

        given()
                .contentType("application/json")
                .body(f4)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }

    @Test
    public void testCreateAssociation_Max5PerWarehouse() {
        createFulfillment(new Fulfillment(301L, 301L, "BU301"));
        createFulfillment(new Fulfillment(302L, 301L, "BU301"));
        createFulfillment(new Fulfillment(303L, 301L, "BU301"));
        createFulfillment(new Fulfillment(304L, 301L, "BU301"));
        createFulfillment(new Fulfillment(305L, 301L, "BU301"));

        Fulfillment f6 = new Fulfillment(306L, 301L, "BU301");

        given()
                .contentType("application/json")
                .body(f6)
                .when().post("/fulfillment")
                .then()
                .statusCode(422);
    }
}
