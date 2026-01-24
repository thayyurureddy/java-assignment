package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    var oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (oldWarehouse == null) {
      throw new RuntimeException("Warehouse to replace not found");
    }

    // Capacity Accommodation
    if (newWarehouse.capacity < oldWarehouse.stock) {
      throw new RuntimeException("New capacity cannot accommodate existing stock");
    }

    // Stock Matching
    if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
      throw new RuntimeException("Stock must match the previous warehouse stock");
    }

    // Archive old one (logical delete)
    oldWarehouse.archivedAt = java.time.LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    // Create new one (since BuCode is same, and we find by BuCode and archivedAt is
    // null, we need to handle this)
    // Actually, the requirement says "Replacing a Warehouse".
    // Usually this means a new BU code or same BU code but different unit?
    // "Ensure the new warehouse's capacity can accommodate the stock from the
    // warehouse being replaced."
    // This implies we are replacing one WITH ANOTHER.

    // For this assignment, I'll assume replace updates the existing active one OR
    // creates a new record if BU code is same.
    // Given the repository implementation findActiveByBuCode, we should probably
    // just update it if we want to "replace" it in place,
    // OR create a new one with a different BU code if that's what's intended.
    // But "replaceTheCurrentActiveWarehouse(businessUnitCode, data)" suggests we
    // are replacing the one with THAT BU code.

    // I'll implement it as updating the existing one with new data, but applying
    // the constraints.
    warehouseStore.update(newWarehouse);
  }
}
