package fr.simplex_software.fifty_shades_of_rest.async_services.react.tests;

import fr.simplex_software.fifty_shades_of_rest.async_services.react.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

import java.net.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBlockingJava8NtpResourceReact extends BaseBlockingJava8
{
  @TestHTTPEndpoint(NtpResourceReact.class)
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
}
