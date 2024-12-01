package fr.simplex_software.fifty_shades_of_rest.orders.test_resources;

import io.quarkus.test.common.*;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.*;

import java.util.*;

public class DbTestResource implements QuarkusTestResourceLifecycleManager
{
  private static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:12-alpine")
    .withDatabaseName("orders-test")
    .withUsername("postgres")
    .withPassword("postgres")
    .waitingFor(
      Wait.forLogMessage(".*server started.*", 1));

    /*.withClasspathResourceMapping("init.sql",
      "/docker-entrypoint-initdb.d/init.sql",
      BindMode.READ_ONLY);*/

  @Override
  public Map<String, String> start()
  {
    DATABASE.start();
    return Collections.singletonMap("quarkus.datasource.jdbc.url", DATABASE.getJdbcUrl());
  }

  @Override
  public void stop()
  {
    DATABASE.stop();
  }
}
