package fr.simplex_software.fifty_shades_of_rest.orders.service.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.smallrye.mutiny.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;

import java.util.*;

@ApplicationScoped
public class CustomerReactiveServiceImpl implements CustomerReactiveService
{
  @Inject
  CustomerReactiveRepository customerRepository;

  @Override
  public Uni<List<CustomerDTO>> getCustomers()
  {
    return customerRepository.listAll()
      .map(customers -> customers.stream()
        .map(CustomerMapper.INSTANCE::fromEntity)
        .toList());
  }

  @Override
  public Uni<CustomerDTO> getCustomer(Long id)
  {
    return customerRepository.findByIdOptional(id)
      .map(optionalCustomer -> optionalCustomer
        .map(CustomerMapper.INSTANCE::fromEntity)
        .orElseThrow(() -> new CustomerNotFoundException("""
          ### CustomerServiceImpl.getCustomer():
          Customer not found for id: %s""".formatted(id))));
  }

  @Override
  public Uni<CustomerDTO> getCustomerByEmail(String email)
  {
    System.out.println("CustomerReactiveServiceImpl.getCustomerByEmail() - email: " + email);
    Uni<Optional<Customer>> customerRepositoryByEmail =
      customerRepository.findByEmail(email);
    System.out.println("CustomerReactiveServiceImpl.getCustomerByEmail() - customerRepositoryByEmail: " + customerRepositoryByEmail.toString());
    customerRepositoryByEmail.onItem().ifNull().failWith(new CustomerNotFoundException("""
      ### CustomerServiceImpl.getCustomerByEmail():
      Customer not found for email: %s""".formatted(email)));
    Uni<CustomerDTO> customerDTOUni = customerRepositoryByEmail.onItem().ifNotNull()
      .transformToUni(customer ->
      Uni.createFrom().item(CustomerMapper.INSTANCE.fromEntity(customer.get())));
    System.out.println("CustomerReactiveServiceImpl.getCustomerByEmail() - customerDTOUni: " + customerDTOUni.toString());
    return customerDTOUni;
    /*return customerRepository.findByEmail(email)
      .map(optionalCustomer -> optionalCustomer
        .map(CustomerMapper.INSTANCE::fromEntity)
        .orElseThrow(() -> new CustomerNotFoundException("""
          ### CustomerServiceImpl.getCustomerByEmail():
          Customer not found for email: %s""".formatted(email))));*/
  }

  @Override
  public Uni<CustomerDTO> createCustomer(CustomerDTO customerDTO)
  {
    return customerRepository
      .createCustomer(CustomerMapper.INSTANCE.toEntity(customerDTO))
      .map(CustomerMapper.INSTANCE::fromEntity);
  }

  @Override
  public Uni<Integer> updateCustomer(Long id, CustomerDTO customerDTO)
  {
    return customerRepository.updateById(id, CustomerMapper.INSTANCE.toEntity(customerDTO));
  }

  @Override
  public Uni<Boolean> deleteCustomer(Long id)
  {
    return customerRepository.deleteById(id);
  }

  @Override
  public Uni<Customer> findCustomerById(Long id)
  {
    return customerRepository.findById(id);
  }
}
