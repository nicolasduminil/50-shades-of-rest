package fr.simplex_software.fifty_shades_of_rest.async_clients.mp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

import java.util.concurrent.*;

@RegisterRestClient(configKey = "base_uri")
@Path("time2")
public interface TimeResourceMpClientAsync
{
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  CompletionStage<String> getCurrentDateAndTimeAtDefaultZone();
  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  CompletionStage<String> getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
}
