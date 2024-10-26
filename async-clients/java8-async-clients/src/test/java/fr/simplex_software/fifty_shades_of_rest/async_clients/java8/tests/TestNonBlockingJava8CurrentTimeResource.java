package fr.simplex_software.fifty_shades_of_rest.async_clients.java8.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.java8.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestNonBlockingJava8CurrentTimeResource
{
  private static final String ENCODED = URLEncoder.encode("Europe/Kaliningrad", StandardCharsets.UTF_8);
  @TestHTTPEndpoint(CurrentTimeResource.class)
  @TestHTTPResource
  URL timeSrvUrl;
  private URI timeSrvUri;

  @BeforeAll
  public void beforeAll() throws URISyntaxException
  {
    timeSrvUri = timeSrvUrl.toURI();
    assertThat(timeSrvUri).isNotNull();
  }

  @AfterAll
  public void afterAll()
  {
    timeSrvUri = null;
  }

  @Test
  public void testCurrentTime()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      CompletableFuture.supplyAsync(() ->
          client.target(timeSrvUri).request().async().get(callback))
        .exceptionally(ex -> fail("""
          ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTime():\s"""
          + ex.getMessage()));
    }
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      CompletableFuture.supplyAsync(() ->
          client.target(timeSrvUri).path(ENCODED).request().async().get(callback))
        .exceptionally(ex -> fail("""
          ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():\s"""
          + ex.getMessage()));
    }
  }
}
