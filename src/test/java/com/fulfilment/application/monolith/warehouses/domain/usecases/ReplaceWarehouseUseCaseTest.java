package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseTest {

    @Test
    public void testReplaceSuccess() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.location = "L1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.location = "L1";
        newW.capacity = 100;
        newW.stock = 50;

        Location location = new Location("L1", 5, 1000);

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(List.of(oldW));

        useCase.replace(newW);

        assertNotNull(oldW.archivedAt);
        verify(store, times(1)).update(oldW);
        verify(store, times(1)).create(newW);
    }

    @Test
    public void testReplaceNotFound() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "NON-EXISTENT";

        when(store.findByBusinessUnitCode("NON-EXISTENT")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> useCase.replace(newW), "Warehouse to replace not found");
    }

    @Test
    public void testReplaceCapacityTooSmall() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

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
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

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

    @Test
    public void testReplaceInvalidLocation() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.location = "L1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.location = "INVALID";
        newW.capacity = 100;
        newW.stock = 50;

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);
        when(resolver.resolveByIdentifier("INVALID")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> useCase.replace(newW), "Invalid location");
    }

    @Test
    public void testReplaceMaxWarehousesReached() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "W1";
        oldW.location = "L1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "W1";
        newW.location = "L2"; // Moving to L2
        newW.capacity = 100;
        newW.stock = 50;

        Location locationL2 = new Location("L2", 1, 1000);
        Warehouse existingAtL2 = new Warehouse();
        existingAtL2.businessUnitCode = "W2";
        existingAtL2.location = "L2";

        when(store.findByBusinessUnitCode("W1")).thenReturn(oldW);
        when(resolver.resolveByIdentifier("L2")).thenReturn(locationL2);
        when(store.getAll()).thenReturn(List.of(oldW, existingAtL2));

        assertThrows(RuntimeException.class, () -> useCase.replace(newW),
                "Maximum number of warehouses reached for this location");
    }
}
