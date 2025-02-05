package fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.reactive.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.exceptions.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.mapping.*;
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
        .map(CustomerReactiveMapper.INSTANCE::fromEntity)
        .toList());
  }

  @Override
  public Uni<CustomerDTO> getCustomer(Long id)
  {
    return customerRepository.findByIdOptional(id)
      .map(optionalCustomer -> optionalCustomer
        .map(CustomerReactiveMapper.INSTANCE::fromEntity)
        .orElseThrow(() -> new CustomerNotFoundException("""
          ### CustomerServiceImpl.getCustomer():
          Customer not found for id: %s""".formatted(id))));
  }

  @Override
  public Uni<CustomerDTO> getCustomerByEmail(String email)
  {
    return customerRepository.findByEmail(email)
      .map(optionalCustomer -> optionalCustomer
        .map(CustomerReactiveMapper.INSTANCE::fromEntity)
        .orElseThrow(() -> new CustomerNotFoundException("""
          ### CustomerServiceImpl.getCustomerByEmail():
          Customer not found for email: %s""".formatted(email))));
  }

  @Override
  public Uni<CustomerDTO> createCustomer(CustomerDTO customerDTO)
  {
    return customerRepository
      .createCustomer(CustomerReactiveMapper.INSTANCE.toEntity(customerDTO))
      .map(CustomerReactiveMapper.INSTANCE::fromEntity);
  }

  @Override
  public Uni<CustomerDTO> updateCustomer(CustomerDTO customerDTO)
  {
    return customerRepository.findById(customerDTO.id())
      .onItem().ifNull()
      .failWith(() -> new CustomerNotFoundException("""
            ### CustomerServiceImpl.updateCustomer():
            Customer not found for id:""" + customerDTO.id()))
      .onItem().transform(existingCustomer -> {
        CustomerReactiveMapper.INSTANCE.updateEntityFromDTO(customerDTO, existingCustomer);
        return existingCustomer;
      })
      .chain(customer -> customerRepository.createCustomer(customer))
      .map(customer -> CustomerReactiveMapper.INSTANCE.fromEntity(customer));
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
