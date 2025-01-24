package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

import java.util.concurrent.*;

@RegisterRestClient(configKey = "base_uri")
@Path("customers")
public interface CustomerAsyncApiClient
{
  @GET
  CompletionStage<Response> getCustomers();

  @GET
  @Path("/{id}")
  CompletionStage<Response> getCustomer(@PathParam("id") Long id);

  @GET
  @Path("/email/{email}")
  CompletionStage<Response> getCustomerByEmail(@PathParam("email") String email);

  @POST
  Response createCustomer(CustomerDTO customerDTO);

  @PUT
  CompletionStage<Response> updateCustomer(CustomerDTO customerDTO);

  @DELETE
  CompletionStage<Response> deleteCustomer(CustomerDTO customerDTO);
}
