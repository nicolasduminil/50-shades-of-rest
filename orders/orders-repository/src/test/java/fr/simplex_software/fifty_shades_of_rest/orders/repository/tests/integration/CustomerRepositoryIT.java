package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import jakarta.transaction.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
public class CustomerRepositoryIT
{
  @Inject
  CustomerRepository customerRepository;

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindAll()
  {
    List<Customer> customers = customerRepository.findAll().stream().toList();
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(2);
    assertThat(customers.getFirst().getOrders()).hasSize(2);
  }

  @Test
  @Transactional
  @DataSet(cleanAfter = true)
  @ExpectedDataSet(value = "expected-orders.yml")
  public void testPersist()
  {
    Customer customer = new Customer("John", "Doe",
      "john.doe@email.com", "555-1234");
    customer.addOrder(new Order("myItem1", new BigDecimal("100.25"), customer));
    customer.addOrder(new Order("myItem2", new BigDecimal("200.25"), customer));
    customerRepository.persist(customer);
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindById()
  {
    Customer customer = customerRepository.findById(1L);
    assertThat(customer).isNotNull();
    assertThat(customer.getFirstName()).isEqualTo("John");
    assertThat(customer.getLastName()).isEqualTo("Doe");
    assertThat(customer.getEmail()).isEqualTo("john.doe@email.com");
    assertThat(customer.getPhone()).isEqualTo("555-1234");
    assertThat(customer.getOrders()).hasSize(2);
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindByEmail()
  {
    Customer customer = customerRepository.findByEmail("john.doe@email.com").orElseThrow();
    assertThat(customer).isNotNull();
    assertThat(customer.getFirstName()).isEqualTo("John");
    assertThat(customer.getLastName()).isEqualTo("Doe");
    assertThat(customer.getEmail()).isEqualTo("john.doe@email.com");
    assertThat(customer.getPhone()).isEqualTo("555-1234");
    assertThat(customer.getOrders()).hasSize(2);
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindByLastName()
  {
    List<Customer> customers = customerRepository.findByLastName("Doe");
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(2);
    assertThat(customers.getFirst().getOrders()).hasSize(2);
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testDeleteById()
  {
    customerRepository.deleteById(1L);
    List<Customer> customers = customerRepository.findAll().stream().toList();
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(1);
    assertThat(customers.getFirst().getOrders()).hasSize(2);
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testDeleteByLastName()
  {
    customerRepository.deleteByLastName("Doe");
    List<Customer> customers = customerRepository.findAll().stream().toList();
    assertThat(customers).isNotNull();
    assertThat(customers).isEmpty();
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testUpdateCustomer()
  {
    Customer customer = customerRepository.findById(1L);
    customer.setFirstName("Mike");
    customer.setEmail("mike.doe@email.com");
    customer.setPhone("777-1220");
    customerRepository.updateById(1L, customer);
    Customer updatedCustomer = customerRepository.findById(1L);
    assertThat(updatedCustomer).isNotNull();
    assertThat(updatedCustomer.getFirstName()).isEqualTo("Mike");
    assertThat(updatedCustomer.getLastName()).isEqualTo("Doe");
    assertThat(updatedCustomer.getEmail()).isEqualTo("mike.doe@email.com");
    assertThat(updatedCustomer.getPhone()).isEqualTo("777-1220");
    assertThat(updatedCustomer.getOrders()).hasSize(2);
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testListCustomersByLastName()
  {
    List<Customer> customers = customerRepository.listCustomersByLastName("Doe");
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(2);
    assertThat(customers.getFirst().getOrders()).hasSize(2);
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testListCustomersWithOrders()
  {
    List<Customer> customers = customerRepository.listCustomersWithOrders();
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(2);
    assertThat(customers.getFirst().getOrders()).hasSize(2);
  }
}
