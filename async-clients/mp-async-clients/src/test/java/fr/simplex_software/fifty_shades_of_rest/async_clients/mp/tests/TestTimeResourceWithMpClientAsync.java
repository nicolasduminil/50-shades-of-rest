package fr.simplex_software.fifty_shades_of_rest.async_clients.mp.tests;

import fr.simplex_software.fifty_shades_of_rest.async_clients.mp.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestTimeResourceWithMpClientAsync
{
  protected static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

  @Inject
  @RestClient
  TimeResourceMpClientAsync mpClientAsync;

  @Test
  public void testCurrentTime()
  {
    mpClientAsync.getCurrentDateAndTimeAtDefaultZone().thenAccept(t ->
      assertThat(LocalDateTime.parse(t, DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS)))
      .toCompletableFuture().join();
  }

  @Test
  public void testZoneTime()
  {
    mpClientAsync.getCurrentDateAndTimeAtZone("Europe/Paris").thenAccept(t ->
      assertThat(LocalDateTime.parse(t, DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS)))
      .toCompletableFuture().join();
  }
}
