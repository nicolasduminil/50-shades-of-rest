package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import io.quarkus.hibernate.orm.panache.*;
import io.quarkus.test.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TestCustomerRepository
{
  @InjectMock
  CustomerRepository customerRepository;

  @Test
  public void testCustomerRepository()
  {
    assertThat(customerRepository).isNotNull();
    when(customerRepository.count()).thenReturn(23L);
    assertThat(customerRepository.count()).isEqualTo(23L);
  }

  @Test
  void testCustomerRepositoryFindAll() {
    List<Customer> expectedCustomers = getCustomers();
    PanacheQuery<Customer> mockQuery = mock(PanacheQuery.class);
    when(customerRepository.findAll()).thenReturn(mockQuery);
    when(mockQuery.stream()).thenReturn(expectedCustomers.stream());
    List<Customer> actualCustomers = customerRepository.findAll().stream().collect(Collectors.toList());
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).hasSize(expectedCustomers.size());
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).findAll();
  }

  private List<Customer> getCustomers()
  {
    Order order = new Order(1L, "myItem1", new BigDecimal("100.25"), null);
    Customer customer = new Customer(1L, "John", "Doe", "john.doe@email.com", "1234567890", null);
    order.setCustomer(customer);
    customer.setOrders(List.of(order));
    return List.of(customer);
  }
}
