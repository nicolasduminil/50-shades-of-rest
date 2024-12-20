package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

public interface BaseCustomerApiClient extends CustomerApi
{
  @Override
  @GET
  Response getCustomers();

  @Override
  @GET
  @Path("/{id}")
  Response getCustomer(@PathParam("id") Long id);

  @Override
  @GET
  @Path("/email/{email}")
  Response getCustomerByEmail(@PathParam("email") String email);

  @Override
  @POST
  Response createCustomer(CustomerDTO customerDTO);

  @Override
  @PUT
  Response updateCustomer(CustomerDTO customerDTO);

  @Override
  @DELETE
  Response deleteCustomer(CustomerDTO customerDTO);
}
