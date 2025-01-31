package fr.simplex_software.fifty_shades_of_rest.orders.react;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import io.smallrye.mutiny.*;
import io.smallrye.mutiny.infrastructure.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;
import java.util.*;

@ApplicationScoped
@Path("orders-react")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResourceReact
{
  @Inject
  OrderService orderService;
  @Inject
  CustomerService customerService;


  @GET
  public Uni<Response> getOrders()
  {
    return Uni.createFrom()
      .<Response>emitter(em -> {
        List<OrderDTO> orders = orderService.getAllOrders();
        em.complete(Response.ok().entity(orders).build());
      })
      .emitOn(Infrastructure.getDefaultWorkerPool());
  }

  @GET
  @Path("/{id}")
  public Uni<Response> getOrder(@PathParam("id") Long id)
  {
    return Uni.createFrom()
      .item(() -> id)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(orderId -> Response.ok()
        .entity(orderService.getOrder(orderId))
        .build());
  }

  @GET
  @Path("/email/{email}")
  public Uni<Response> getOrderByEmail(@PathParam("email") String email)
  {
    return Uni.createFrom()
      .item(() -> URLDecoder.decode(email, StandardCharsets.UTF_8))
      .chain(decodedEmail -> Uni.createFrom().item(() ->
        customerService.getCustomerByEmail(decodedEmail)))
      .chain(customer -> Uni.createFrom().item(() ->
        orderService.getOrdersForCustomer(customer.id())))
      .map(orders -> Response.ok(orders).build());
  }

  @POST
  public Uni<Response> createOrder(OrderDTO orderDTO)
  {
    return Uni.createFrom()
      .item(() -> orderDTO)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(dto -> Response.created(URI.create("/orders/" + dto.id()))
        .entity(orderService.createOrder(dto))
        .build());
  }


  @PUT
  public Uni<Response> updateOrder(OrderDTO orderDTO)
  {
    return Uni.createFrom()
      .item(() -> orderDTO)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(dto -> Response.accepted()
        .entity(orderService.updateOrder(dto))
        .build());
  }


  @DELETE
  public Uni<Response> deleteOrder(OrderDTO orderDTO)
  {
    return Uni.createFrom()
      .<Response>emitter(em ->
      {
        orderService.deleteOrder(orderDTO.id());
        em.complete(Response.noContent().build());
      })
      .emitOn(Infrastructure.getDefaultWorkerPool());
  }
}
