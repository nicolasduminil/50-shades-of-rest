package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

import java.util.concurrent.*;

@RegisterRestClient(configKey = "base_uri")
@Path("orders")
public interface OrderAsyncApiClient
{
  @GET
  CompletionStage<Response> getOrders();

  @GET
  @Path("/{id}")
  CompletionStage<Response> getOrder(@PathParam("id") Long id);

  @GET
  @Path("/customer/{customerId}")
  CompletionStage<Response> getOrdersByCustomer(@PathParam("customerId") Long customerId);

  @POST
  CompletionStage<Response> createOrder(OrderDTO orderDTO);

  @PUT
  CompletionStage<Response> updateOrder(OrderDTO orderDTO);

  @DELETE
  CompletionStage<Response> deleteOrder(OrderDTO orderDTO);
}
