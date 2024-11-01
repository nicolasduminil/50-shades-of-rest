package fr.simplex_software.fifty_shades_of_rest.classic;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

@RegisterRestClient(configKey = "base_uri")
@Path("time")
public interface CurrentTimeResourceClient extends BaseMpClient
{
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtDefaultZone();
  @GET
  @Path("{zoneId}")
  @Produces(MediaType.TEXT_PLAIN)
  String getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
}
