package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.common.exceptions.ConflictException;
import com.fulfilment.application.monolith.common.exceptions.ResourceNotFoundException;
import com.fulfilment.application.monolith.common.exceptions.ValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver locationResolver;
  private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class);

  public CreateWarehouseUseCase(WarehouseStore warehouseStore,
      com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    LOGGER.infof("Creating warehouse: %s", warehouse.businessUnitCode);
    // Business Unit Code Verification
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      LOGGER.warnf("Validation failed: Business unit code already exists: %s", warehouse.businessUnitCode);
      throw new ConflictException("Business unit code already exists");
    }

    // Location Validation
    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      LOGGER.warnf("Validation failed: Invalid location identifier: %s", warehouse.location);
      throw new ResourceNotFoundException("Invalid location");
    }

    // Warehouse Creation Feasibility
    var existingWarehouses = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(warehouse.location) && w.archivedAt == null)
        .toList();
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      LOGGER.warnf("Validation failed: Maximum number of warehouses (%d) reached for location: %s",
          location.maxNumberOfWarehouses, warehouse.location);
      throw new ConflictException("Maximum number of warehouses reached for this location");
    }

    // Capacity and Stock Validation
    int currentTotalCapacity = existingWarehouses.stream().mapToInt(w -> w.capacity).sum();
    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
      LOGGER.warnf("Validation failed: Total capacity (%d) exceeds max capacity (%d) for location: %s",
          currentTotalCapacity + warehouse.capacity, location.maxCapacity, warehouse.location);
      throw new ValidationException("Total capacity exceeds maximum capacity for this location");
    }

    if (warehouse.stock > warehouse.capacity) {
      LOGGER.warnf("Validation failed: Stock (%d) exceeds capacity (%d) for warehouse: %s",
          warehouse.stock, warehouse.capacity, warehouse.businessUnitCode);
      throw new ValidationException("Stock exceeds warehouse capacity");
    }

    warehouse.createdAt = java.time.LocalDateTime.now();

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
    LOGGER.infof("Warehouse created successfully in store: %s", warehouse.businessUnitCode);
  }
}
