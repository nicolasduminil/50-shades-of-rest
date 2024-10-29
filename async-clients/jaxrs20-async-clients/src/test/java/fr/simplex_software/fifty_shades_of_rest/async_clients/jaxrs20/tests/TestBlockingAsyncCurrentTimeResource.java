package fr.simplex_software.fifty_shades_of_rest.async_clients.jaxrs20.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.jaxrs20.*;
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
public class TestBlockingAsyncCurrentTimeResource
{
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
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
      Future<String> timeFuture = client.target(timeSrvUri).request().async().get(String.class);
      String time = timeFuture.get(5, TimeUnit.SECONDS);
      assertThat(parseTime(time)).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
    catch (Exception ex)
    {
      fail("### TestBlockingAsyncCurrentTimeResource.testCurrentTime(): Unexpected exception %s", ex.getMessage());
    }
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Future<String> timeFuture = client.target(timeSrvUri).path(ENCODED).request().async().get(String.class);
      String time = timeFuture.get(5, TimeUnit.SECONDS);
      assertThat(parseTime(time))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
    catch (Exception ex)
    {
      fail("### TestBlockingAsyncCurrentTimeResource.testCurrentTimeWithZoneId(): Unexpected exception %s", ex.getMessage());
    }
  }

  private LocalDateTime parseTime(String time)
  {
    return OffsetDateTime.parse(time, DateTimeFormatter.ofPattern(FMT))
      .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }
}
