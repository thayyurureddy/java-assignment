package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreSyncObserver {

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    public void onStoreChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {
        switch (event.operation) {
            case CREATE:
                legacyStoreManagerGateway.createStoreOnLegacySystem(event.store);
                break;
            case UPDATE:
                legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store);
                break;
            case DELETE:
                legacyStoreManagerGateway.deleteStoreOnLegacySystem(event.store);
                break;
        }
    }
}
