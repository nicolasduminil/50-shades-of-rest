package fr.simplex_software.fifty_shades_of_rest.common_tests;

import jakarta.ws.rs.client.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

public class BaseNonBlockingJava8
{
  protected static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
  protected static final String ENCODED = URLEncoder.encode("Europe/Kaliningrad", StandardCharsets.UTF_8);
  protected URI timeSrvUri;

  @Test
  public void testCurrentTime()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      assertThat(parseTime(CompletableFuture.supplyAsync(() ->
      {
        client.target(timeSrvUri).request().async().get(callback);
        try
        {
          return callback.getTime();
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }).join())).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  @Test
  public void testCurrentTimeWithZoneId()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Callback callback = new Callback();
      assertThat(parseTime(CompletableFuture.supplyAsync(() ->
      {
        client.target(timeSrvUri).path(ENCODED).request().async().get(callback);
        try
        {
          return callback.getTime();
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }).join())).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
  }

  private LocalDateTime parseTime(String time)
  {
    return OffsetDateTime.parse(time, DateTimeFormatter.ofPattern(FMT))
      .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

}
