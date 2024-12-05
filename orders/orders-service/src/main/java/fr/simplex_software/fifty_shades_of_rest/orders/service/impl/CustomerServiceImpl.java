package fr.simplex_software.fifty_shades_of_rest.orders.service.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.transaction.*;

import java.util.*;

@ApplicationScoped
@Transactional
public class CustomerServiceImpl implements CustomerService
{
  @Inject
  CustomerRepository customerRepository;

  @Override
  public List<CustomerDTO> getCustomers()
  {
    return customerRepository.findAll().stream().map(CustomerMapper.INSTANCE::fromEntity).toList();
  }

  @Override
  public CustomerDTO getCustomer(Long id)
  {
    return CustomerMapper.INSTANCE.fromEntity(customerRepository.findByIdOptional(id)
      .orElseThrow(() -> new CustomerNotFoundException("### CustomerServiceImpl.getCustomer(): Customer not found for ID: " + id)));
  }

  @Override
  public CustomerDTO getCustomerByEmail(String email)
  {
    return CustomerMapper.INSTANCE.fromEntity(customerRepository.find("email", email).singleResultOptional()
      .orElseThrow(() -> new CustomerNotFoundException("### CustomerServiceImpl.getCustomerByEmail(): Customer not found for email: " + email)));
  }

  @Override
  @Transactional
  public CustomerDTO createCustomer(CustomerDTO customerDTO)
  {
    Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
    customer.setId(null);
    customerRepository.persistAndFlush(customer);
    return CustomerMapper.INSTANCE.fromEntity(customer);
  }

  @Override
  @Transactional
  public CustomerDTO updateCustomer(CustomerDTO customerDTO)
  {
    Optional<Customer> optionalCustomer = customerRepository.findByIdOptional(customerDTO.id());
    return optionalCustomer.map(existingCustomer ->
    {
      CustomerMapper.INSTANCE.updateEntityFromDTO(customerDTO, existingCustomer);
      customerRepository.persist(existingCustomer);
      return CustomerMapper.INSTANCE.fromEntity(existingCustomer);
    }).orElseThrow(() -> new CustomerNotFoundException("### CustomerServiceImpl.updateCustomer(): Customer not found for id: " + customerDTO.id()));

  }

  @Override
  @Transactional
  public void deleteCustomer(Long id)
  {
    customerRepository.deleteById(id);
  }

  @Override
  @Transactional
  public Customer findCustomerById(Long id)
  {
    return customerRepository.findById(id);
  }
}
