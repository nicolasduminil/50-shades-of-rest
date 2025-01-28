package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.container.*;

public interface CustomerAsyncApi
{
  void getCustomers(@Suspended AsyncResponse ar);
  void getCustomer(Long id, @Suspended AsyncResponse ar);
  void getCustomerByEmail(String email, @Suspended AsyncResponse ar);
  void createCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar);
  void updateCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar);
  void deleteCustomer(CustomerDTO customerDTO, @Suspended AsyncResponse ar);
}
