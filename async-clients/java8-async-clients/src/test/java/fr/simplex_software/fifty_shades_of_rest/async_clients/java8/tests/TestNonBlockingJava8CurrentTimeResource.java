package fr.simplex_software.fifty_shades_of_rest.async_clients.java8.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.java8.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestNonBlockingJava8CurrentTimeResource extends BaseRestAssured
{
  @TestHTTPEndpoint(CurrentTimeResource.class)
  @TestHTTPResource
  private URL timeSrvUrl;

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
  @Override
  public void testCurrentTime()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      assertThat(parseTime(CompletableFuture.supplyAsync(() ->
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
        }).join())).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  @Test
  @Override
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      assertThat(parseTime(CompletableFuture.supplyAsync(() ->
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
      }).join())).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  private LocalDateTime parseTime(String time)
  {
    return OffsetDateTime.parse(time, DateTimeFormatter.ofPattern(FMT))
      .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }
}
