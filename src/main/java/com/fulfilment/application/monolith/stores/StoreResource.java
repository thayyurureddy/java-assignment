package com.fulfilment.application.monolith.stores;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.jboss.logging.Logger;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  @Inject
  jakarta.enterprise.event.Event<StoreEvent> storeEvent;

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class);

  @GET
  public List<Store> get() {
    LOGGER.info("Fetching all stores");
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Store getSingle(Long id) {
    LOGGER.infof("Fetching store by ID: %d", id);
    Store entity = Store.findById(id);
    if (entity == null) {
      LOGGER.warnf("Store not found: %d", id);
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(Store store) {
    LOGGER.infof("Creating a new store: %s", store.name);
    if (store.id != null) {
      LOGGER.warn("Attempted to create store with pre-set ID");
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }

    store.persist();
    LOGGER.infof("Store created with ID: %d", store.id);

    storeEvent.fire(new StoreEvent(store, StoreEvent.Operation.CREATE));

    return Response.ok(store).status(201).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Store update(Long id, Store updatedStore) {
    LOGGER.infof("Updating store ID: %d", id);
    if (updatedStore.name == null) {
      LOGGER.warn("Store update failed: Name is null");
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      LOGGER.warnf("Store not found for update: %d", id);
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

    LOGGER.infof("Store updated: %d", id);
    storeEvent.fire(new StoreEvent(entity, StoreEvent.Operation.UPDATE));

    return entity;
  }

  @PATCH
  @Path("{id}")
  @Transactional
  public Store patch(Long id, Store updatedStore) {
    LOGGER.infof("Patching store ID: %d", id);
    Store entity = Store.findById(id);

    if (entity == null) {
      LOGGER.warnf("Store not found for patch: %d", id);
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    if (updatedStore.name != null) {
      entity.name = updatedStore.name;
    }

    if (updatedStore.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    LOGGER.infof("Store patched: %d", id);
    storeEvent.fire(new StoreEvent(entity, StoreEvent.Operation.UPDATE));

    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(Long id) {
    LOGGER.infof("Deleting store ID: %d", id);
    Store entity = Store.findById(id);
    if (entity == null) {
      LOGGER.warnf("Store not found for deletion: %d", id);
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    // Fire event before deletion so we have the data,
    // but it will only be processed AFTER_SUCCESS of the transaction.
    storeEvent.fire(new StoreEvent(entity, StoreEvent.Operation.DELETE));

    entity.delete();
    LOGGER.infof("Store deleted: %d", id);
    return Response.status(204).build();
  }

}
