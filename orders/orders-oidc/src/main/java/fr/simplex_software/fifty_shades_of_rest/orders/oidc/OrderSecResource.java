package fr.simplex_software.fifty_shades_of_rest.orders.oidc;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import jakarta.annotation.security.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;

@ApplicationScoped
@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderSecResource implements OrderApi
{
  @Inject
  OrderService orderService;

  @GET
  @RolesAllowed("user")
  public Response getOrders()
  {
    return Response.ok().entity(orderService.getAllOrders()).build();
  }

  @Override
  @GET
  @Path("/{id}")
  @RolesAllowed("user")
  public Response getOrder(@PathParam("id") Long id)
  {
    return Response.ok().entity(orderService.getOrder(id).orElseThrow(() -> new OrderNotFoundException("### OrderServiceImpl.getOrder(): Order not found for id: " + id))).build();
  }

  @Override
  @GET
  @Path("/customer/{customerId}")
  @RolesAllowed("user")
  public Response getOrdersByCustomer(@PathParam("customerId") Long customerId)
  {
    return Response.ok().entity(orderService.getOrdersForCustomer(customerId)).build();
  }

  @Override
  @POST
  @RolesAllowed("admin")
  public Response createOrder(OrderDTO orderDTO)
  {
    return Response.created(URI.create("/orders/" + orderDTO.id())).entity(orderService.createOrder(orderDTO)).build();
  }

  @Override
  @PUT
  @RolesAllowed("admin")
  public Response updateOrder(OrderDTO orderDTO)
  {
    return Response.accepted().entity(orderService.updateOrder(orderDTO)).build();
  }

  @Override
  @DELETE
  @RolesAllowed("admin")
  public Response deleteOrder(OrderDTO orderDTO)
  {
    orderService.deleteOrder(orderDTO);
    return Response.noContent().build();
  }
}
