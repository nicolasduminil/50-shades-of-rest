package fr.simplex_software.fifty_shades_of_rest.async_clients.java8.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.java8.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
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
public class TestBlockingJava8CurrentTimeResource extends BaseRestAssured
{
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
  private static final String ENCODED = URLEncoder.encode("Europe/Kaliningrad", StandardCharsets.UTF_8);
  @TestHTTPEndpoint(CurrentTimeResource.class)
  @TestHTTPResource
  URL timeSrvUrl;

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
      CompletableFuture<String> timeFuture = CompletableFuture.supplyAsync(() ->
        client.target(timeSrvUri).request().get(String.class))
        .exceptionally(ex -> fail("""
           ### BlockingJava8CurrentTimeResourceIT.testCurrentTime():\s"""
          + ex.getMessage()));
      assertThat(parseTime(timeFuture.join())).isCloseTo(LocalDateTime.now(),
        byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<String> timeFuture = CompletableFuture.supplyAsync(() ->
        client.target(timeSrvUri).path(ENCODED).request().get(String.class))
        .exceptionally(ex -> fail("""
           ### BlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():\s"""
          + ex.getMessage()));
      assertThat(parseTime(timeFuture.join())).isCloseTo(LocalDateTime.now(),
        byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  private LocalDateTime parseTime(String time)
  {
    return OffsetDateTime.parse(time, DateTimeFormatter.ofPattern(FMT))
      .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }
}
