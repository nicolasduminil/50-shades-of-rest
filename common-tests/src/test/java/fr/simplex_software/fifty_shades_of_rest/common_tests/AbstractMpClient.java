package fr.simplex_software.fifty_shades_of_rest.common_tests;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import static org.assertj.core.api.Assertions.*;

public abstract class AbstractMpClient
{
  protected static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

  @Test
  public void testCurrentTime()
  {
    assertThat(LocalDateTime.parse(getMpClient()
      .getCurrentDateAndTimeAtDefaultZone(), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    assertThat(LocalDateTime.parse(getMpClient()
      .getCurrentDateAndTimeAtZone("Europe/Paris"), DateTimeFormatter.ofPattern(FMT)))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.HOURS));
  }

  protected abstract BaseMpClient getMpClient();
}
