package fr.simplex_software.fifty_shades_of_rest.orders.provider.health;

import jakarta.enterprise.context.*;
import org.eclipse.microprofile.config.inject.*;
import org.eclipse.microprofile.health.*;

import java.io.*;
import java.net.*;

@ApplicationScoped
@Readiness
public class DbHealthCheck implements HealthCheck
{
  @ConfigProperty(name = "POSTGRES_HOST", defaultValue = "localhost")
  String host;
  @ConfigProperty(name = "POSTGRES_PORT", defaultValue = "5432")
  Integer port;

  @Override
  public HealthCheckResponse call()
  {
    HealthCheckResponseBuilder responseBuilder =
      HealthCheckResponse.named(String.format("Database connection health check on %s %d", host, port));
    try
    {
      serverListening(host, port);
      responseBuilder.up();
    }
    catch (Exception ex)
    {
      responseBuilder.down()
        .withData("reason", ex.getMessage());
    }
    return responseBuilder.build();
  }

  private void serverListening (String host, Integer port) throws IOException
  {
    new Socket(host, port).close();
  }
}
