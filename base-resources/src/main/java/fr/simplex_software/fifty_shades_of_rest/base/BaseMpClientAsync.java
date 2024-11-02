package fr.simplex_software.fifty_shades_of_rest.base;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.concurrent.*;

public interface BaseMpClientAsync
{
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  CompletionStage<String> currentTime();
  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  CompletionStage<String> zonedTime(@PathParam("zoneId") String zoneId);
}
