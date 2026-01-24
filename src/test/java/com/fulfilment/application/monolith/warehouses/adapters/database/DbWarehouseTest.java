package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DbWarehouseTest {

    @Test
    public void testToWarehouse() {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.businessUnitCode = "BU1";
        dbWarehouse.location = "Loc1";
        dbWarehouse.capacity = 10;
        dbWarehouse.stock = 5;
        dbWarehouse.createdAt = LocalDateTime.now();
        dbWarehouse.archivedAt = null;

        Warehouse warehouse = dbWarehouse.toWarehouse();

        Assertions.assertEquals(dbWarehouse.businessUnitCode, warehouse.businessUnitCode);
        Assertions.assertEquals(dbWarehouse.location, warehouse.location);
        Assertions.assertEquals(dbWarehouse.capacity, warehouse.capacity);
        Assertions.assertEquals(dbWarehouse.stock, warehouse.stock);
        Assertions.assertEquals(dbWarehouse.createdAt, warehouse.createdAt);
        Assertions.assertEquals(dbWarehouse.archivedAt, warehouse.archivedAt);
    }
}
