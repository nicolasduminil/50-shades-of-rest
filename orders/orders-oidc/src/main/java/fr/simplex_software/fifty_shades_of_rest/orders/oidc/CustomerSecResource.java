package fr.simplex_software.fifty_shades_of_rest.orders.oidc;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.annotation.security.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.*;
import java.nio.charset.*;
import java.util.*;

@ApplicationScoped
@Path("customers-sec")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerSecResource implements CustomerApi
{
  @Inject
  CustomerService customerService;

  @Override
  @GET
  @RolesAllowed("user")
  public Response getCustomers()
  {
    return Response.ok().entity(new GenericEntity<List<CustomerDTO>>(customerService.getCustomers()) {}).build();
  }

  @Override
  @GET
  @Path("/{id}")
  @RolesAllowed("user")
  public Response getCustomer(@PathParam("id") Long id)
  {
    return Response.ok().entity(customerService.getCustomer(id)).build();
  }

  @Override
  @GET
  @Path("/email/{email}")
  @RolesAllowed("user")
  public Response getCustomerByEmail(@PathParam("email") String email)
  {
    return Response.ok().entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8))).build();
  }

  @Override
  @POST
  @RolesAllowed("admin")
  public Response createCustomer(CustomerDTO customerDTO)
  {
    return Response.created(URI.create("/customers/" + customerDTO.id())).entity(customerService.createCustomer(customerDTO)).build();
  }

  @Override
  @PUT
  @RolesAllowed("admin")
  public Response updateCustomer(CustomerDTO customerDTO)
  {
    return Response.accepted().entity(customerService.updateCustomer(customerDTO)).build();
  }

  @Override
  @DELETE
  @RolesAllowed("admin")
  public Response deleteCustomer(CustomerDTO customerDTO)
  {
    customerService.deleteCustomer(customerDTO.id());
    return Response.noContent().build();
  }
}
