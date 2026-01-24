package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LegacyStoreManagerGatewayTest {

    @Inject
    LegacyStoreManagerGateway gateway;

    @Test
    public void testCreateStoreOnLegacySystem() {
        Store store = new Store();
        store.name = "TestStore";
        store.quantityProductsInStock = 10;
        gateway.createStoreOnLegacySystem(store);
    }

    @Test
    public void testUpdateStoreOnLegacySystem() {
        Store store = new Store();
        store.name = "UpdateStore";
        store.quantityProductsInStock = 20;
        gateway.updateStoreOnLegacySystem(store);
    }
}
