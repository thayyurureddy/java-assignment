package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class WarehouseResourceTest {

        @Test
        public void testWarehouseEndpoints() {
                String buCode = "W-" + System.currentTimeMillis();
                Warehouse warehouse = new Warehouse();
                warehouse.setBusinessUnitCode(buCode);
                warehouse.setLocation("AMSTERDAM-001");
                warehouse.setCapacity(10);
                warehouse.setStock(2);

                // 1. Create
                given()
                                .contentType(ContentType.JSON)
                                .body(warehouse)
                                .when().post("/warehouse")
                                .then()
                                .statusCode(200)
                                .body("businessUnitCode", is(buCode));

                // 2. Get All
                given()
                                .when().get("/warehouse")
                                .then()
                                .statusCode(200)
                                .body("size()", is(not(0)));

                // 3. Get Single
                given()
                                .when().get("/warehouse/" + buCode)
                                .then()
                                .statusCode(200)
                                .body("location", is("AMSTERDAM-001"));

                // 4. Replace
                Warehouse replacement = new Warehouse();
                replacement.setLocation("AMSTERDAM-001");
                replacement.setCapacity(20);
                replacement.setStock(2);

                given()
                                .contentType(ContentType.JSON)
                                .body(replacement)
                                .when().post("/warehouse/" + buCode + "/replacement")
                                .then()
                                .statusCode(200)
                                .body("location", is("AMSTERDAM-001"))
                                .body("capacity", is(20));

                // 5. Archive (Delete)
                given()
                                .when().delete("/warehouse/" + buCode)
                                .then()
                                .statusCode(204);

                // 6. Get archived
                given()
                                .when().get("/warehouse/" + buCode)
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testGetNotFound() {
                given()
                                .when().get("/warehouse/NON-EXISTENT")
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testArchiveNotFound() {
                given()
                                .when().delete("/warehouse/NON-EXISTENT")
                                .then()
                                .statusCode(404);
        }
}
