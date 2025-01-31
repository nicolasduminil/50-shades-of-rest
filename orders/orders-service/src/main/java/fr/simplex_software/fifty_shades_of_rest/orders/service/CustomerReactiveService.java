package fr.simplex_software.fifty_shades_of_rest.orders.service;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.smallrye.mutiny.*;

import java.util.*;

public interface CustomerReactiveService
{
  Uni<List<CustomerDTO>> getCustomers();
  Uni<CustomerDTO> getCustomer(Long id);
  Uni<CustomerDTO> getCustomerByEmail(String email);
  Uni<CustomerDTO> createCustomer(CustomerDTO customerDTO);
  Uni<Integer> updateCustomer(Long id, CustomerDTO customerDTO);
  Uni<Boolean> deleteCustomer(Long id);
  Uni<Customer> findCustomerById(Long id);
}
