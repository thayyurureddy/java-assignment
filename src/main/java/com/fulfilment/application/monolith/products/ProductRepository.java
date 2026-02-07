package com.fulfilment.application.monolith.products;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;



@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    private static final org.jboss.logging.Logger LOGGER = org.jboss.logging.Logger.getLogger(ProductRepository.class);

    @Override
    public void persist(Product product) {
        LOGGER.infof("Persisting product: %s", product.name);
        PanacheRepository.super.persist(product);
    }

    @Override
    public void delete(Product product) {
        LOGGER.infof("Deleting product: %s", product.name);
        PanacheRepository.super.delete(product);
    }
}
