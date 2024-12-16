package fr.simplex_software.fifty_shades_of_rest.orders.provider.health;

import jakarta.enterprise.context.*;
import org.eclipse.microprofile.health.*;

@ApplicationScoped
@Liveness
public class ServiceHealthCheck implements HealthCheck
{
  @Override
  public HealthCheckResponse call()
  {
    return HealthCheckResponse
      .named("Liveness health check for the Orders Management service")
      .up()
      .withData("Type","Liveness")
      .withData("Status", "Services are up and running !")
      .build();
  }
}
