package fr.simplex_software.fifty_shades_of_rest.orders.service;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;

import java.util.*;

public interface CustomerService
{
  List<CustomerDTO> getCustomers();
  CustomerDTO getCustomer(Long id);
  CustomerDTO getCustomerByEmail(String email);
  CustomerDTO createCustomer(CustomerDTO customerDTO);
  CustomerDTO updateCustomer(CustomerDTO customerDTO);
  void deleteCustomer(Long id);
  Customer findCustomerById(Long id);
}
