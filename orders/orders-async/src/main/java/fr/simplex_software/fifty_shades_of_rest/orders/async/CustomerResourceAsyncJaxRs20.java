package fr.simplex_software.fifty_shades_of_rest.orders.async;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;

@ApplicationScoped
@Path("customers-async")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResourceAsyncJaxRs20 implements CustomerAsyncApi
{
  @Inject
  CustomerService customerService;

  @Override
  @GET
  public void getCustomers(@Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok().entity(customerService.getCustomers()).build());
  }

  @Override
  @GET
  @Path("/{id}")
  public void getCustomer(@PathParam("id") Long id, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok().entity(customerService.getCustomer(id)).build());
  }

  @Override
  @GET
  @Path("/email/{email}")
  public void getCustomerByEmail(@PathParam("email") String email, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.ok()
      .entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8)))
      .build());
  }

  @Override
  @POST
  public void createCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.created(URI.create("/customers/" + customerDTO.id()))
      .entity(customerService.createCustomer(customerDTO))
      .build());
  }

  @Override
  @PUT
  public void updateCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar)
  {
    ar.resume(Response.accepted().entity(customerService.updateCustomer(customerDTO)).build());
  }

  @Override
  @DELETE
  public void deleteCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar)
  {
    customerService.deleteCustomer(customerDTO.id());
    ar.resume(Response.noContent().build());
  }
}
