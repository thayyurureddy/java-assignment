package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    @TestTransaction
    public void testCreateAndGetWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W001";
        warehouse.location = "Location 1";
        warehouse.capacity = 100;
        warehouse.stock = 50;

        warehouseRepository.create(warehouse);

        Warehouse found = warehouseRepository.findByBusinessUnitCode("W001");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("Location 1", found.location);
    }

    @Test
    @TestTransaction
    public void testUpdateWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W002";
        warehouse.location = "Old Location";
        warehouseRepository.create(warehouse);

        warehouse.location = "New Location";
        warehouseRepository.update(warehouse);

        Warehouse updated = warehouseRepository.findByBusinessUnitCode("W002");
        Assertions.assertEquals("New Location", updated.location);
    }

    @Test
    @TestTransaction
    public void testRemoveWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W003";
        warehouseRepository.create(warehouse);

        warehouseRepository.remove(warehouse);

        Warehouse deleted = warehouseRepository.findByBusinessUnitCode("W003");
        Assertions.assertNull(deleted);
    }

    @Test
    @TestTransaction
    public void testGetAll() {
        Warehouse w1 = new Warehouse();
        w1.businessUnitCode = "W004";
        warehouseRepository.create(w1);

        Warehouse w2 = new Warehouse();
        w2.businessUnitCode = "W005";
        warehouseRepository.create(w2);

        List<Warehouse> all = warehouseRepository.getAll();
        Assertions.assertTrue(all.size() >= 2);
    }
}
