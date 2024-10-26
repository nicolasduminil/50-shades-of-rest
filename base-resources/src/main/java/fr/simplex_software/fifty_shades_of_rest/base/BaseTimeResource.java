package fr.simplex_software.fifty_shades_of_rest.base;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;

public class BaseTimeResource
{
  private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getCurrentDateAndTimeAtDefaultZone()
  {
    return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(FMT)
      .withZone(ZoneId.systemDefault()));
  }

  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId)
  {
    String decodedZoneId = URLDecoder.decode(zoneId, StandardCharsets.UTF_8);
    return ZonedDateTime.now(ZoneId.of(decodedZoneId)).format(DateTimeFormatter
      .ofPattern(FMT).withZone(ZoneId.of(decodedZoneId)));
  }
}
