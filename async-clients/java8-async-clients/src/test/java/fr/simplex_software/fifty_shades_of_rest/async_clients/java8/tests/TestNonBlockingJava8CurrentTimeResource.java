package fr.simplex_software.fifty_shades_of_rest.async_clients.java8.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.java8.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
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
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

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
        {
          client.target(timeSrvUri).request().async().get(callback);
          try
          {
            return callback.getTime();
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        })
        .thenAccept(t -> assertThat(parseTime(t))
          .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES)))
        .exceptionally(ex -> fail("""
            ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTime():""",
          ex.getMessage()));
    }
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      CompletableFuture.supplyAsync(() ->
        {
          client.target(timeSrvUri).path(ENCODED).request().async().get(callback);
          try
          {
            return callback.getTime();
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        })
        .thenAccept(t -> assertThat(parseTime(t))
          .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES)))
        .exceptionally(ex -> fail("""
            ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():""",
          ex.getMessage()));
    }
  }

  private LocalDateTime parseTime(String time)
  {
    return OffsetDateTime.parse(time, DateTimeFormatter.ofPattern(FMT))
      .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }
}
