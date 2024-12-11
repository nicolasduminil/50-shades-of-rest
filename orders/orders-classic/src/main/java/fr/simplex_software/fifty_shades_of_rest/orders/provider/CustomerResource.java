package fr.simplex_software.fifty_shades_of_rest.orders.provider;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;

@ApplicationScoped
@Path("customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource implements CustomerApi
{
  @Inject
  CustomerService customerService;

  @Override
  @GET
  public Response getCustomers()
  {
    return Response.ok().entity(customerService.getCustomers()).build();
  }

  @Override
  @GET
  @Path("/{id}")
  public Response getCustomer(@PathParam("id") Long id)
  {
    return Response.ok().entity(customerService.getCustomer(id)).build();
  }

  @Override
  @GET
  @Path("/email/{email}")
  public Response getCustomerByEmail(@PathParam("email") String email)
  {
    return Response.ok().entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8))).build();
  }

  @Override
  @POST
  public Response createCustomer(CustomerDTO customerDTO)
  {
    return Response.created(URI.create("/customers/" + customerDTO.id())).entity(customerService.createCustomer(customerDTO)).build();
  }

  @Override
  @PUT
  public Response updateCustomer(CustomerDTO customerDTO)
  {
    return Response.accepted().entity(customerService.updateCustomer(customerDTO)).build();
  }

  @Override
  @DELETE
  public Response deleteCustomer(CustomerDTO customerDTO)
  {
    customerService.deleteCustomer(customerDTO.id());
    return Response.noContent().build();
  }
}
