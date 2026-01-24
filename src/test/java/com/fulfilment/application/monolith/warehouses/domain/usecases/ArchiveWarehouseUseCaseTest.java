package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ArchiveWarehouseUseCaseTest {

    @Test
    public void testArchive() {
        WarehouseStore store = mock(WarehouseStore.class);
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(store);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
        verify(store, times(1)).update(warehouse);
    }

    @Test
    public void testArchiveAlreadyArchived() {
        WarehouseStore store = mock(WarehouseStore.class);
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(store);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "W1";
        java.time.LocalDateTime archivedAt = java.time.LocalDateTime.now().minusDays(1);
        warehouse.archivedAt = archivedAt;

        useCase.archive(warehouse);

        org.junit.jupiter.api.Assertions.assertEquals(archivedAt, warehouse.archivedAt);
        verify(store, times(1)).update(warehouse);
    }
}
