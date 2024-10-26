package fr.simple_software.fifty_shades_of_rest.classic.tests;

import fr.simplex_software.fifty_shades_of_rest.classic.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.time.*;
import java.time.ZoneId;
import java.time.format.*;
import java.time.temporal.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCurrentTimeResourceJakartaClient
{
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
  public void testTimeZoneResource()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Response response = client.target(timeSrvUri).request().get();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
      assertThat(LocalDateTime.parse(response.readEntity(String.class), DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
    }
  }

  @Test
  public void testTimeZoneResourceWithTimeZone()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Response response = client.target(timeSrvUri).path(URLEncoder.encode("Europe/Paris")).request().get();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
      assertThat(LocalDateTime.parse(response.readEntity(String.class), DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(ZoneId.of("Europe/Paris")), byLessThan(1, ChronoUnit.HOURS));
    }
  }
}
