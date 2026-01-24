package com.fulfilment.application.monolith.fulfillment;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fulfillment")
public class Fulfillment extends PanacheEntity {

    public Long productId;

    public Long storeId;

    public String warehouseBusinessUnitCode;

    public Fulfillment() {
    }

    public Fulfillment(Long productId, Long storeId, String warehouseBusinessUnitCode) {
        this.productId = productId;
        this.storeId = storeId;
        this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    }
}
