package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.core.*;

public interface CustomerApi
{
  Response getCustomers();
  Response getCustomer(Long id);
  Response getCustomerByEmail(String email);
  Response createCustomer(CustomerDTO customerDTO);
  Response updateCustomer(CustomerDTO customerDTO);
  Response deleteCustomer(CustomerDTO customerDTO);
}
