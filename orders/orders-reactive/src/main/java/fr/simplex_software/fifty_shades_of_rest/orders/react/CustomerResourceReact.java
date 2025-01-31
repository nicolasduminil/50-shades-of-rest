package fr.simplex_software.fifty_shades_of_rest.orders.react;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import io.quarkus.hibernate.reactive.panache.common.*;
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
@Path("customers-react")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResourceReact
{
  @Inject
  CustomerService customerService;

  @GET
  public Uni<Response> getCustomers()
  {
    return Uni.createFrom()
      .<Response>emitter(em -> {
        List<CustomerDTO> customers = customerService.getCustomers();
        em.complete(Response.ok().entity(customers).build());
      })
      .emitOn(Infrastructure.getDefaultWorkerPool());
  }

  @GET
  @Path("/{id}")
  public Uni<Response> getCustomer(@PathParam("id") Long id)
  {
    return Uni.createFrom()
      .item(() -> id)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(customerId -> Response.ok()
        .entity(customerService.getCustomer(customerId))
        .build());
  }

  @GET
  @Path("/email/{email}")
  public Uni<Response> getCustomerByEmail(@PathParam("email") String email)
  {
    return Uni.createFrom()
      .item(() -> email)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(customerDto -> Response.ok()
        .entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8)))
        .build());
  }


  @POST
  public Uni<Response> createCustomer(CustomerDTO customerDTO)
  {
    return Uni.createFrom().emitter(em -> {
      Uni.createFrom()
        .item(customerDTO)
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .flatMap(dto ->
          Uni.createFrom().item(() -> {
            // This will run on a worker thread
            return customerService.createCustomer(dto);
          })
        )
        .map(customer -> Response.created(URI.create("/customers/" + customer.id()))
          .entity(customer)
          .build())
        .subscribe().with(
          em::complete,
          em::fail
        );
    });
  }


  @PUT
  @WithTransaction
  public Uni<Response> updateCustomer(CustomerDTO customerDTO)
  {
    return Uni.createFrom()
      .item(() -> customerDTO)
      .emitOn(Infrastructure.getDefaultWorkerPool())
      .map(dto -> Response.accepted()
        .entity(customerService.updateCustomer(dto))
        .build());
  }

  @DELETE
  @WithTransaction
  public Uni<Response> deleteCustomer(CustomerDTO customerDTO)
  {
    return Uni.createFrom()
      .<Response>emitter(em -> {
        customerService.deleteCustomer(customerDTO.id());
        em.complete(Response.noContent().build());
      })
      .emitOn(Infrastructure.getDefaultWorkerPool());
  }
}
