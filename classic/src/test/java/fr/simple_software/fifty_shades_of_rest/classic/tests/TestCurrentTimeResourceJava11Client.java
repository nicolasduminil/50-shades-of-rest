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
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCurrentTimeResourceJava11Client
{
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private HttpRequest.Builder httpRequestBuilder;
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
    httpRequestBuilder = HttpRequest.newBuilder().header("Content-Type", "application/json");
  }

  @AfterAll
  public void afterAll()
  {
    executorService.shutdown();
    try
    {
      if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS))
        executorService.shutdownNow();
    } catch (InterruptedException e)
    {
      executorService.shutdownNow();
    }
    timeSrvUri = null;
    httpRequestBuilder = null;
  }

  @Test
  public void testTimeZoneResource() throws Exception
  {
    HttpResponse<String> response = httpClient.send(httpRequestBuilder.uri(timeSrvUri)
      .GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    assertThat(LocalDateTime.parse(response.body(), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }

  @Test
  public void testTimeZoneResourceWithTimeZone() throws Exception
  {
    URI uri = URI.create(timeSrvUri + "/" + URLEncoder.encode("Europe/Paris", StandardCharsets.UTF_8));
    HttpResponse<String> response = httpClient.send(httpRequestBuilder.uri(uri).GET()
      .build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    assertThat(LocalDateTime.parse(response.body(), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }
}
