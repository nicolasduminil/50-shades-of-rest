package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;


@QuarkusTest
public class TestCustomerMapper
{
  @Test
  public void testFromEntity()
  {
    Customer customer = new Customer("John", "Doe", "john.doe@email.com", "555-1234");
    CustomerDTO customerDTO = CustomerMapper.INSTANCE.fromEntity(customer);
    assertThat(customerDTO).isNotNull();
    assertThat(customerDTO.firstName()).isEqualTo(customer.getFirstName());
  }

  @Test
  public void testToEntity()
  {
    CustomerDTO customerDTO = new CustomerDTO("John", "Doe", "john.doe@email.com", "555-1234");
    Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
    assertThat(customer).isNotNull();
    assertThat(customer.getFirstName()).isEqualTo(customerDTO.firstName());
  }
}
