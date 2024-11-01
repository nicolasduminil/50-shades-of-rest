package fr.simplex_software.fifty_shades_of_rest.base;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

public interface BaseMpClient
{
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtDefaultZone();
  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
}
