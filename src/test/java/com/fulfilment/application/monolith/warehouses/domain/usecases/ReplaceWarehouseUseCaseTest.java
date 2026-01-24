package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseTest {

    @Test
    public void testReplaceSuccess() {
        WarehouseStore store = mock(WarehouseStore.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.capacity = 100;
        newW.stock = 50;

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);

        useCase.replace(newW);

        assertNotNull(oldW.archivedAt);
        verify(store, times(1)).update(oldW);
        verify(store, times(1)).create(newW);
    }

    @Test
    public void testReplaceNotFound() {
        WarehouseStore store = mock(WarehouseStore.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store);

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "NON-EXISTENT";

        when(store.findByBusinessUnitCode("NON-EXISTENT")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> useCase.replace(newW), "Warehouse to replace not found");
    }

    @Test
    public void testReplaceCapacityTooSmall() {
        WarehouseStore store = mock(WarehouseStore.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.capacity = 40; // Too small
        newW.stock = 50;

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);

        assertThrows(RuntimeException.class, () -> useCase.replace(newW),
                "New capacity cannot accommodate existing stock");
    }

    @Test
    public void testReplaceStockMismatch() {
        WarehouseStore store = mock(WarehouseStore.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.capacity = 100;
        newW.stock = 60; // Mismatch

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);

        assertThrows(RuntimeException.class, () -> useCase.replace(newW),
                "Stock must match the previous warehouse stock");
    }
}
