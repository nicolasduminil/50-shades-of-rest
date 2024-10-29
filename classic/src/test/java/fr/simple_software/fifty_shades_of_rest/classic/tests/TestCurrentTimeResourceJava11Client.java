package fr.simple_software.fifty_shades_of_rest.classic.tests;

import fr.simplex_software.fifty_shades_of_rest.classic.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCurrentTimeResourceJava11Client
{
  private static URI timeSrvUri;
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

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
  public void testTimeZoneResource() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      HttpRequest request = HttpRequest.newBuilder().uri(timeSrvUri).GET().build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response).isNotNull();
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
      assertThat(LocalDateTime.parse(response.body(), DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
    }
  }

  @Test
  public void testTimeZoneResourceWithTimeZone() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(timeSrvUri + "/" + URLEncoder.encode("Europe/Paris"))).GET().build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response).isNotNull();
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
      assertThat(LocalDateTime.parse(response.body(), DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(ZoneId.of("Europe/Paris")), byLessThan(1, ChronoUnit.HOURS));
    }
  }
}
