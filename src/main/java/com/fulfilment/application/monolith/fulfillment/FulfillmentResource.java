package com.fulfilment.application.monolith.fulfillment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

        private static final Logger LOGGER = Logger.getLogger(FulfillmentResource.class);
        private static final int MAX_WAREHOUSES_PER_PRODUCT_STORE = 2;
        private static final int MAX_WAREHOUSES_PER_STORE = 3;
        private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

        @POST
        @Transactional
        public Response createAssociation(Fulfillment fulfillment) {
                LOGGER.infof("Creating association: Product=%d, Store=%d, Warehouse=%s",
                                fulfillment.productId, fulfillment.storeId, fulfillment.warehouseBusinessUnitCode);

                // 1. Each Product can be fulfilled by a maximum of 2 different Warehouses per
                // Store
                long warehousesPerProductStore = Fulfillment.count("productId = ?1 and storeId = ?2",
                                fulfillment.productId, fulfillment.storeId);
                if (warehousesPerProductStore >= MAX_WAREHOUSES_PER_PRODUCT_STORE) {
                        LOGGER.warnf("Validation failed: Product %d already has %d warehouses in store %d",
                                        fulfillment.productId, MAX_WAREHOUSES_PER_PRODUCT_STORE, fulfillment.storeId);
                        throw new com.fulfilment.application.monolith.common.exceptions.ValidationException(
                                        "A product can be fulfilled by a maximum of " + MAX_WAREHOUSES_PER_PRODUCT_STORE
                                                        + " warehouses per store");
                }

                // 2. Each Store can be fulfilled by a maximum of 3 different Warehouses
                long uniqueWarehousesPerStore = Fulfillment.find("storeId = ?1", fulfillment.storeId)
                                .stream()
                                .map(f -> ((Fulfillment) f).warehouseBusinessUnitCode)
                                .distinct()
                                .count();

                // Check if the warehouse being added is already one of the 3
                boolean warehouseAlreadyInStore = Fulfillment.count("storeId = ?1 and warehouseBusinessUnitCode = ?2",
                                fulfillment.storeId, fulfillment.warehouseBusinessUnitCode) > 0;

                if (!warehouseAlreadyInStore && uniqueWarehousesPerStore >= MAX_WAREHOUSES_PER_STORE) {
                        LOGGER.warnf("Validation failed: Store %d already has %d unique warehouses",
                                        fulfillment.storeId, MAX_WAREHOUSES_PER_STORE);
                        throw new com.fulfilment.application.monolith.common.exceptions.ValidationException(
                                        "A store can be fulfilled by a maximum of " + MAX_WAREHOUSES_PER_STORE
                                                        + " different warehouses");
                }

                // 3. Each Warehouse can store maximally 5 types of Products
                long uniqueProductsPerWarehouse = Fulfillment
                                .find("warehouseBusinessUnitCode = ?1", fulfillment.warehouseBusinessUnitCode)
                                .stream()
                                .map(f -> ((Fulfillment) f).productId)
                                .distinct()
                                .count();

                // Check if the product being added is already in the warehouse
                boolean productAlreadyInWarehouse = Fulfillment.count(
                                "warehouseBusinessUnitCode = ?1 and productId = ?2",
                                fulfillment.warehouseBusinessUnitCode, fulfillment.productId) > 0;

                if (!productAlreadyInWarehouse && uniqueProductsPerWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
                        LOGGER.warnf("Validation failed: Warehouse %s already has %d product types",
                                        fulfillment.warehouseBusinessUnitCode, MAX_PRODUCTS_PER_WAREHOUSE);
                        throw new com.fulfilment.application.monolith.common.exceptions.ValidationException(
                                        "A warehouse can store maximally 5 types of products");
                }

                // Save association
                fulfillment.persist();
                LOGGER.info("Association created successfully");

                return Response.ok(fulfillment).status(201).build();
        }
}
