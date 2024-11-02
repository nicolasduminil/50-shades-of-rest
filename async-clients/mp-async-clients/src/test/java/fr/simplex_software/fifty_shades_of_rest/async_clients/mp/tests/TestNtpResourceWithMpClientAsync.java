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
public class TestNtpResourceWithMpClientAsync
{
  protected static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

  @Inject
  @RestClient
  NtpResourceMpClientAsync mpClientAsync;

  @Test
  public void testCurrentTime()
  {
    mpClientAsync.currentTime().thenAccept(t ->
      assertThat(LocalDateTime.parse(t, DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS)))
      .toCompletableFuture().join();
  }

  @Test
  public void testZoneTime()
  {
    mpClientAsync.zonedTime("Europe/Paris").thenAccept(t ->
      assertThat(LocalDateTime.parse(t, DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS)))
      .toCompletableFuture().join();
  }
}
