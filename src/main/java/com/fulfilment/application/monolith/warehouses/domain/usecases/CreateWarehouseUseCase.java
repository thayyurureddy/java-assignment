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
      throw new ConflictException("Business unit code already exists");
    }

    // Location Validation
    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new ResourceNotFoundException("Invalid location");
    }

    // Warehouse Creation Feasibility
    var existingWarehouses = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(warehouse.location) && w.archivedAt == null)
        .toList();
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      throw new ConflictException("Maximum number of warehouses reached for this location");
    }

    // Capacity and Stock Validation
    int currentTotalCapacity = existingWarehouses.stream().mapToInt(w -> w.capacity).sum();
    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
      throw new ValidationException("Total capacity exceeds maximum capacity for this location");
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new ValidationException("Stock exceeds warehouse capacity");
    }

    warehouse.createdAt = java.time.LocalDateTime.now();

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
