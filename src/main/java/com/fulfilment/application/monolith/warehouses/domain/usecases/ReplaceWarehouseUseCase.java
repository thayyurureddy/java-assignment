package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.common.exceptions.ConflictException;
import com.fulfilment.application.monolith.common.exceptions.ResourceNotFoundException;
import com.fulfilment.application.monolith.common.exceptions.ValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver locationResolver;
  private static final Logger LOGGER = Logger.getLogger(ReplaceWarehouseUseCase.class);

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore,
      com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    LOGGER.infof("Replacing warehouse: %s", newWarehouse.businessUnitCode);
    var oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (oldWarehouse == null) {
      throw new ResourceNotFoundException("Warehouse to replace not found");
    }

    // Capacity Accommodation
    if (newWarehouse.capacity < oldWarehouse.stock) {
      throw new ValidationException("New capacity cannot accommodate existing stock");
    }

    // Stock Matching
    if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
      throw new ValidationException("Stock must match the previous warehouse stock");
    }

    // Location Validation
    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new ResourceNotFoundException("Invalid location");
    }

    // Warehouse Creation Feasibility
    var existingWarehouses = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(newWarehouse.location) && w.archivedAt == null)
        .filter(w -> !w.businessUnitCode.equals(oldWarehouse.businessUnitCode))
        .toList();
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      throw new ConflictException("Maximum number of warehouses reached for this location");
    }

    // Capacity and Stock Validation
    int currentTotalCapacity = existingWarehouses.stream().mapToInt(w -> w.capacity).sum();
    if (currentTotalCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new ValidationException("Total capacity exceeds maximum capacity for this location");
    }

    // Archive old one (logical delete)
    oldWarehouse.archivedAt = java.time.LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    newWarehouse.createdAt = java.time.LocalDateTime.now();

    // Create new one
    warehouseStore.create(newWarehouse);
  }
}
