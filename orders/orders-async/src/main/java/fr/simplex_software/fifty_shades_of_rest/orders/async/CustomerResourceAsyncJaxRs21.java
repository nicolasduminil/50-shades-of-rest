package fr.simplex_software.fifty_shades_of_rest.orders.async;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.context.*;

import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@Path("customers-async21")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResourceAsyncJaxRs21
{
  @Inject
  private CustomerService customerService;
  @Inject
  private ManagedExecutor managedExecutor;

  @GET
  public CompletionStage<Response> getCustomers()
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok().entity(customerService.getCustomers()).build());
  }

  @GET
  @Path("/{id}")
  public CompletionStage<Response> getCustomer(@PathParam("id") Long id)
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok().entity(customerService.getCustomer(id)).build());
  }

  @GET
  @Path("/email/{email}")
  public CompletionStage<Response> getCustomerByEmail(@PathParam("email") String email)
  {
    return managedExecutor.supplyAsync(() ->
      Response.ok()
        .entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8)))
        .build());
  }


  @POST
  public CompletionStage<Response> createCustomer(CustomerDTO customerDTO)
  {
    return managedExecutor.supplyAsync(() ->
      Response.created(URI.create("/customers/" + customerDTO.id()))
        .entity(customerService.createCustomer(customerDTO))
        .build());
  }


  @PUT
  public CompletionStage<Response> updateCustomer(CustomerDTO customerDTO)
  {
    return managedExecutor.supplyAsync(() ->
      Response.accepted().entity(customerService.updateCustomer(customerDTO)).build());
  }


  @DELETE
  public CompletionStage<Response> deleteCustomer(CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
    {
      customerService.deleteCustomer(customerDTO.id());
      return Response.noContent().build();
    });
  }
}
