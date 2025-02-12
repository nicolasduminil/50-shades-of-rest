package fr.simplex_software.fifty_shades_of_rest.orders.jwt;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.annotation.security.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.jwt.*;

import java.net.*;
import java.nio.charset.*;
import java.util.*;

@RequestScoped
@Path("customers-jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerJwtResource implements CustomerApi
{
  @Inject
  JsonWebToken jwt;
  @Inject
  @Claim(standard = Claims.birthdate)
  String birthdate;

  @Inject
  CustomerService customerService;

  @Override
  @GET
  @RolesAllowed({"User", "Admin"})
  public Response getCustomers()
  {
    return Response.ok().entity(new GenericEntity<List<CustomerDTO>>(customerService.getCustomers()) {}).build();
  }

  @Override
  @GET
  @Path("/{id}")
  @RolesAllowed({"User", "Admin"})
  public Response getCustomer(@PathParam("id") Long id)
  {
    return Response.ok().entity(customerService.getCustomer(id)).build();
  }

  @Override
  @GET
  @Path("/email/{email}")
  @RolesAllowed({"User", "Admin"})
  public Response getCustomerByEmail(@PathParam("email") String email)
  {
    return Response.ok().entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8))).build();
  }

  @Override
  @POST
  @RolesAllowed("Admin")
  public Response createCustomer(CustomerDTO customerDTO)
  {
    return Response.created(URI.create("/customers/" + customerDTO.id())).entity(customerService.createCustomer(customerDTO)).build();
  }

  @Override
  @PUT
  @RolesAllowed("Admin")
  public Response updateCustomer(CustomerDTO customerDTO)
  {
    return Response.accepted().entity(customerService.updateCustomer(customerDTO)).build();
  }

  @Override
  @DELETE
  @RolesAllowed("Admin")
  public Response deleteCustomer(CustomerDTO customerDTO)
  {
    customerService.deleteCustomer(customerDTO.id());
    return Response.noContent().build();
  }
}
