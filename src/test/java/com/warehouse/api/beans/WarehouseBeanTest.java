package com.warehouse.api.beans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WarehouseBeanTest {

    @Test
    public void testWarehouseBean() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId("1");
        warehouse.setBusinessUnitCode("BU1");
        warehouse.setLocation("Loc1");
        warehouse.setCapacity(100);
        warehouse.setStock(50);

        Assertions.assertEquals("1", warehouse.getId());
        Assertions.assertEquals("BU1", warehouse.getBusinessUnitCode());
        Assertions.assertEquals("Loc1", warehouse.getLocation());
        Assertions.assertEquals(100, warehouse.getCapacity());
        Assertions.assertEquals(50, warehouse.getStock());
    }
}
