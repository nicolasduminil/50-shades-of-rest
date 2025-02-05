package fr.simplex_software.fifty_shades_of_rest.orders.react;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.*;
import io.quarkus.hibernate.reactive.panache.common.*;
import io.smallrye.mutiny.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;

@ApplicationScoped
@Path("orders-react")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResourceReact
{
  @Inject
  OrderReactiveService orderService;
  @Inject
  CustomerReactiveService customerService;


  @GET
  @WithSession
  public Uni<Response> getOrders()
  {
    return orderService.getAllOrders()
      .map(orders -> Response.ok(orders).build());
  }

  @GET
  @Path("/{id}")
  @WithSession
  public Uni<Response> getOrder(@PathParam("id") Long id)
  {
    return Uni.createFrom()
      .item(() -> id)
      .map(orderId -> Response.ok()
        .entity(orderService.getOrder(orderId))
        .build());
  }

  @GET
  @Path("/customer/{customerId}")
  @WithSession
  public Uni<Response> getOrdersByCustomer(@PathParam("customerId") Long customerId)
  {
    return orderService.getOrdersForCustomer(customerId)
      .map(orders -> Response.ok(orders).build());
  }


  @GET
  @Path("/email/{email}")
  @WithSession
  public Uni<Response> getOrderByEmail(@PathParam("email") String email)
  {
    return Uni.createFrom()
      .item(() -> URLDecoder.decode(email, StandardCharsets.UTF_8))
      .chain(decodedEmail -> customerService.getCustomerByEmail(decodedEmail))
      .chain(customerDTO -> orderService.getOrdersForCustomer(customerDTO.id()))
      .map(orders -> Response.ok(orders).build());
  }

  @POST
  @WithTransaction
  public Uni<Response> createOrder(OrderDTO orderDTO)
  {
    return orderService.createOrder(orderDTO)
      .map(createdOrderDTO -> Response
        .created(URI.create("/orders/" + createdOrderDTO.id()))
        .entity(createdOrderDTO)
        .build());
  }


  @PUT
  @WithTransaction
  public Uni<Response> updateOrder(OrderDTO orderDTO)
  {
    return Uni.createFrom()
      .item(() -> orderDTO)
      .map(dto -> Response.accepted()
        .entity(orderService.updateOrder(dto))
        .build());
  }


  @DELETE
  @WithTransaction
  public Uni<Response> deleteOrder(OrderDTO orderDTO)
  {
     return orderService.deleteOrder(orderDTO.id())
      .map(ignored -> Response.noContent().build());
  }
}
