package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.jboss.logging.Logger;

@jakarta.ws.rs.Path("/warehouse")
@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation createWarehouseUseCase;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation replaceWarehouseUseCase;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation archiveWarehouseUseCase;

  private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class);

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    LOGGER.info("Listing all warehouse units");
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOGGER.infof("Creating a new warehouse unit: %s", data.getBusinessUnitCode());
    var domainWarehouse = toDomain(data);
    createWarehouseUseCase.create(domainWarehouse);
    LOGGER.infof("Warehouse unit created: %s", domainWarehouse.businessUnitCode);
    return toWarehouseResponse(domainWarehouse);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    LOGGER.infof("Fetching warehouse unit by ID: %s", id);
    var domainWarehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      LOGGER.warnf("Warehouse not found: %s", id);
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    return toWarehouseResponse(domainWarehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    LOGGER.infof("Archiving warehouse unit by ID: %s", id);
    var domainWarehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      LOGGER.warnf("Warehouse not found for archiving: %s", id);
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    archiveWarehouseUseCase.archive(domainWarehouse);
    LOGGER.infof("Warehouse unit archived: %s", id);
  }

  @Override
  @Transactional
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    LOGGER.infof("Replacing warehouse unit: %s", businessUnitCode);
    var newWarehouse = toDomain(data);
    newWarehouse.businessUnitCode = businessUnitCode;
    replaceWarehouseUseCase.replace(newWarehouse);
    LOGGER.infof("Warehouse unit replaced: %s", businessUnitCode);
    return toWarehouseResponse(newWarehouse);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(Warehouse warehouse) {
    var domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.businessUnitCode = warehouse.getBusinessUnitCode();
    domain.location = warehouse.getLocation();
    domain.capacity = warehouse.getCapacity();
    domain.stock = warehouse.getStock();
    return domain;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
