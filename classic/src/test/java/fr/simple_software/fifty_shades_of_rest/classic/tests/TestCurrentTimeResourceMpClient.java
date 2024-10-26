package fr.simple_software.fifty_shades_of_rest.classic.tests;

import fr.simplex_software.fifty_shades_of_rest.classic.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestCurrentTimeResourceMpClient
{
  @Inject
  @RestClient
  CurrentTimeResourceClient currentTimeResourceClient;
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

  @Test
  public void testCurrentTime()
  {
    assertThat(LocalDateTime.parse(currentTimeResourceClient
      .getCurrentDateAndTimeAtDefaultZone(), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    assertThat(LocalDateTime.parse(currentTimeResourceClient
      .getCurrentDateAndTimeAtDefaultZone(), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }
}
