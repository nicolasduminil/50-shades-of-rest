package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

public interface BaseOrderApiClient extends OrderApi
{
  @GET
  Response getOrders();

  @Override
  @GET
  @Path("/{id}")
  Response getOrder(@PathParam("id") Long id);

  @Override
  @GET
  @Path("/customer/{customerId}")
  Response getOrdersByCustomer(@PathParam("customerId") Long customerId);

  @Override
  @POST
  Response createOrder(OrderDTO orderDTO);

  @Override
  @PUT
  Response updateOrder(OrderDTO orderDTO);

  @Override
  @DELETE
  Response deleteOrder(OrderDTO orderDTO);
}
