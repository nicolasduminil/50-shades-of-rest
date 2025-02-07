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
@Path("customers-react")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResourceReact
{
  @Inject
  CustomerReactiveService customerService;

  @GET
  @WithSession
  public Uni<Response> getCustomers()
  {
    return customerService.getCustomers()
      .map(customers -> Response.ok(customers).build());
  }

  @GET
  @Path("/{id}")
  @WithSession
  public Uni<Response> getCustomer(@PathParam("id") Long id)
  {
    return Uni.createFrom()
      .item(() -> id)
      .map(customerId -> Response.ok()
        .entity(customerService.getCustomer(customerId))
        .build());
  }

  @GET
  @Path("/email/{email}")
  @WithSession
  public Uni<Response> getCustomerByEmail(@PathParam("email") String email)
  {
    return Uni.createFrom()
      .item(() -> URLDecoder.decode(email, StandardCharsets.UTF_8))
      .chain(decodedEmail -> customerService.getCustomerByEmail(decodedEmail))
      .map(orders -> Response.ok(orders).build());
  }


  @POST
  @WithTransaction
  public Uni<Response> createCustomer(CustomerDTO customerDTO)
  {
    return Uni.createFrom()
      .item(customerDTO)
      .flatMap(dto -> customerService.createCustomer(dto))
      .map(customer -> Response.created(URI.create("/customers/" + customer.id()))
        .entity(customer)
        .build());
  }


  @PUT
  @WithTransaction
  public Uni<Response> updateCustomer(CustomerDTO customerDTO)
  {
    return Uni.createFrom()
      .item(customerDTO)
      .flatMap(dto -> customerService.updateCustomer(dto)
      .map (updated -> Response.accepted()
        .entity(updated)
        .build()));
  }

  @DELETE
  @WithTransaction
  public Uni<Response> deleteCustomer(CustomerDTO customerDTO)
  {
    return customerService.deleteCustomer(customerDTO.id())
      .map(ignored -> Response.noContent().build());  }
}
