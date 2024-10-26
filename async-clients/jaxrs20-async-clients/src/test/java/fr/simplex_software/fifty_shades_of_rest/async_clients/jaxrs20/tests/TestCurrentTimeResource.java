package fr.simplex_software.fifty_shades_of_rest.async_clients.jaxrs20.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.jaxrs20.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

import java.net.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestCurrentTimeResource extends BaseRestAssured
{
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
}
