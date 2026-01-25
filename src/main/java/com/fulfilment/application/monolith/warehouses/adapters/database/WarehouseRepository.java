package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {
  private static final Logger LOGGER = Logger.getLogger(WarehouseRepository.class);

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    LOGGER.infof("Persisting warehouse to DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = fromWarehouse(warehouse);
    this.persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    LOGGER.infof("Updating warehouse in DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = findActiveByBuCode(warehouse.businessUnitCode);
    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt;
      this.persist(dbWarehouse);
    } else {
      LOGGER.warnf("Warehouse not found for update in DB: %s", warehouse.businessUnitCode);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    LOGGER.infof("Removing warehouse from DB: %s", warehouse.businessUnitCode);
    DbWarehouse dbWarehouse = findActiveByBuCode(warehouse.businessUnitCode);
    if (dbWarehouse != null) {
      this.delete(dbWarehouse);
    } else {
      LOGGER.warnf("Warehouse not found for removal in DB: %s", warehouse.businessUnitCode);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = findActiveByBuCode(buCode);
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  private DbWarehouse findActiveByBuCode(String buCode) {
    return this.find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
  }

  private DbWarehouse fromWarehouse(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    return dbWarehouse;
  }
}
