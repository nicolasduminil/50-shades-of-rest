package fr.simplex_software.fifty_shades_of_rest.classic;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

@RegisterRestClient(configKey = "base_uri")
@Path("time")
public interface CurrentTimeResourceClient
{
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtDefaultZone();
  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
}
