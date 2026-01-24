package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private static final Logger LOGGER = Logger.getLogger(ArchiveWarehouseUseCase.class);

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    LOGGER.infof("Archiving warehouse: %s", warehouse.businessUnitCode);
    // If it's already archived, nothing to do
    if (warehouse.archivedAt == null) {
      warehouse.archivedAt = java.time.LocalDateTime.now();
    }
    warehouseStore.update(warehouse);
  }
}
