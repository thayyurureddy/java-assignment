package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocationTest {

    @Test
    public void testLocationModel() {
        Location location = new Location("ID-1", 5, 100);
        Assertions.assertEquals("ID-1", location.identification);
        Assertions.assertEquals(5, location.maxNumberOfWarehouses);
        Assertions.assertEquals(100, location.maxCapacity);

        location.identification = "ID-2";
        location.maxNumberOfWarehouses = 10;
        location.maxCapacity = 200;

        Assertions.assertEquals("ID-2", location.identification);
        Assertions.assertEquals(10, location.maxNumberOfWarehouses);
        Assertions.assertEquals(200, location.maxCapacity);
    }
}
