package fr.simplex_software.fifty_shades_of_rest.orders.api;

import jakarta.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

@Path("/api")
@RegisterRestClient(baseUri = "https://randomuser.me")
public interface RandomCustomerApiClient
{
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  JsonObject getRandomCustomer();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @QueryParam("results")
  JsonObject getRandomCustomers(@QueryParam("results") int count);
}
