package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LocationGatewayTest {

  @Inject
  LocationGateway locationGateway;

  @Test
  public void testResolveExisting() {
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");
    Assertions.assertNotNull(location);
    Assertions.assertEquals("ZWOLLE-001", location.identification);
  }

  @Test
  public void testResolveNonExistent() {
    Location location = locationGateway.resolveByIdentifier("NON-EXISTENT");
    Assertions.assertNull(location);
  }
}
