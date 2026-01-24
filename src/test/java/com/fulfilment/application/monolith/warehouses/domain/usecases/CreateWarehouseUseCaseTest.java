package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CreateWarehouseUseCaseTest {

    @Test
    public void testCreateSuccess() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "L1";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        Location location = new Location("L1", 5, 1000);

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(Collections.emptyList());

        useCase.create(warehouse);

        verify(store, times(1)).create(warehouse);
    }

    @Test
    public void testCreateDuplicateCode() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";

        when(store.findByBusinessUnitCode("W1")).thenReturn(new Warehouse());

        assertThrows(RuntimeException.class, () -> useCase.create(warehouse), "Business unit code already exists");
    }

    @Test
    public void testCreateInvalidLocation() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "INVALID";

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("INVALID")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> useCase.create(warehouse), "Invalid location");
    }

    @Test
    public void testCreateMaxWarehousesReached() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "L1";

        Location location = new Location("L1", 1, 1000);

        Warehouse existing = new Warehouse();
        existing.location = "L1";

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(List.of(existing));

        assertThrows(RuntimeException.class, () -> useCase.create(warehouse),
                "Maximum number of warehouses reached for this location");
    }

    @Test
    public void testCreateMaxCapacityExceeded() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "L1";
        warehouse.capacity = 600;

        Location location = new Location("L1", 5, 1000);

        Warehouse existing = new Warehouse();
        existing.location = "L1";
        existing.capacity = 500;

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(List.of(existing));

        assertThrows(RuntimeException.class, () -> useCase.create(warehouse),
                "Total capacity exceeds maximum capacity for this location");
    }

    @Test
    public void testCreateStockExceedsCapacity() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "L1";
        warehouse.capacity = 100;
        warehouse.stock = 110;

        Location location = new Location("L1", 5, 1000);

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> useCase.create(warehouse), "Stock exceeds warehouse capacity");
    }

    @Test
    public void testCreateWithArchivedWarehousesIgnored() {
        WarehouseStore store = mock(WarehouseStore.class);
        LocationResolver resolver = mock(LocationResolver.class);
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        warehouse.location = "L1";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        Location location = new Location("L1", 1, 1000);

        Warehouse archived = new Warehouse();
        archived.location = "L1";
        archived.archivedAt = java.time.LocalDateTime.now();

        when(store.findByBusinessUnitCode("W1")).thenReturn(null);
        when(resolver.resolveByIdentifier("L1")).thenReturn(location);
        when(store.getAll()).thenReturn(List.of(archived));

        useCase.create(warehouse);

        verify(store, times(1)).create(warehouse);
    }
}
