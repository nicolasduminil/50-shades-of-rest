package fr.simplex_software.fifty_shades_of_rest.orders.async;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import java.net.*;

@ApplicationScoped
@Path("orders-async")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResourceAsyncJaxRs20 implements OrderAsyncApi
{
  @Inject
  OrderService orderService;

  @Override
  @GET
  public void getOrders(@Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok().entity(orderService.getAllOrders()).build());
  }

  @Override
  @GET
  @Path("/{id}")
  public void getOrder(@PathParam("id") Long id, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok().entity(orderService.getOrder(id).orElseThrow(() ->
      new OrderNotFoundException("### OrderServiceImpl.getOrder(): Order not found for id: " + id))).build());
  }

  @Override
  @GET
  @Path("/customer/{customerId}")
  public void getOrdersByCustomer(@PathParam("customerId") Long customerId, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok().entity(orderService.getOrdersForCustomer(customerId)).build());
  }

  @Override
  @POST
  public void createOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.created(URI.create("/orders/" + orderDTO.id())).entity(orderService.createOrder(orderDTO)).build());
  }

  @Override
  @PUT
  public void updateOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.accepted().entity(orderService.updateOrder(orderDTO)).build());
  }

  @Override
  @DELETE
  public void deleteOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar)
  {
    orderService.deleteOrder(orderDTO);
    ar.resume(Response.noContent().build());
  }
}
