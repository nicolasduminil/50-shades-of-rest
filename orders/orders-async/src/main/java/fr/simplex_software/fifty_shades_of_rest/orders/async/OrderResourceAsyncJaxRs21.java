package fr.simplex_software.fifty_shades_of_rest.orders.async;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.context.*;

import java.net.*;
import java.util.concurrent.*;

@ApplicationScoped
@Path("orders-async21")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResourceAsyncJaxRs21
{
  @Inject
  private OrderService orderService;
  @Inject
  private ManagedExecutor managedExecutor;

  @GET
  public CompletionStage<Response> getOrders()
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok().entity(orderService.getAllOrders()).build());
  }

  
  @GET
  @Path("/{id}")
  public CompletionStage<Response> getOrder(@PathParam("id") Long id)
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok().entity(orderService.getOrder(id).orElseThrow(() ->
        new OrderNotFoundException("### OrderServiceImpl.getOrder(): Order not found for id: " + id))).build());
  }

  @GET
  @Path("/customer/{customerId}")
  public CompletionStage getOrdersByCustomer(@PathParam("customerId") Long customerId)
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok().entity(orderService.getOrdersForCustomer(customerId)).build());
  }

  
  @POST
  public CompletionStage<Response> createOrder(OrderDTO orderDTO)
  {
    return managedExecutor.supplyAsync(() ->
      Response.created(URI.create("/orders/" + orderDTO.id())).entity(orderService.createOrder(orderDTO)).build());
  }

  
  @PUT
  public CompletionStage<Response> updateOrder(OrderDTO orderDTO)
  {
    return managedExecutor.supplyAsync(() ->
      Response.accepted().entity(orderService.updateOrder(orderDTO)).build());
  }

  
  @DELETE
  public CompletionStage<Response> deleteOrder(OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() -> {
      orderService.deleteOrder(orderDTO);
      return Response.noContent().build();
    });
  }
}
