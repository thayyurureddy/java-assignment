package com.fulfilment.application.monolith.fulfillment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

    @POST
    @Transactional
    public Response createAssociation(Fulfillment fulfillment) {
        // 1. Each Product can be fulfilled by a maximum of 2 different Warehouses per
        // Store
        long warehousesPerProductStore = Fulfillment.count("productId = ?1 and storeId = ?2",
                fulfillment.productId, fulfillment.storeId);
        if (warehousesPerProductStore >= 2) {
            throw new WebApplicationException("A product can be fulfilled by a maximum of 2 warehouses per store", 422);
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

        if (!warehouseAlreadyInStore && uniqueWarehousesPerStore >= 3) {
            throw new WebApplicationException("A store can be fulfilled by a maximum of 3 different warehouses", 422);
        }

        // 3. Each Warehouse can store maximally 5 types of Products
        long uniqueProductsPerWarehouse = Fulfillment
                .find("warehouseBusinessUnitCode = ?1", fulfillment.warehouseBusinessUnitCode)
                .stream()
                .map(f -> ((Fulfillment) f).productId)
                .distinct()
                .count();

        // Check if the product being added is already in the warehouse
        boolean productAlreadyInWarehouse = Fulfillment.count("warehouseBusinessUnitCode = ?1 and productId = ?2",
                fulfillment.warehouseBusinessUnitCode, fulfillment.productId) > 0;

        if (!productAlreadyInWarehouse && uniqueProductsPerWarehouse >= 5) {
            throw new WebApplicationException("A warehouse can store maximally 5 types of products", 422);
        }

        // Save association
        fulfillment.persist();

        return Response.ok(fulfillment).status(201).build();
    }
}
