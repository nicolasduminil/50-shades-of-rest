package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests.unit;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import io.quarkus.hibernate.orm.panache.*;
import io.quarkus.panache.common.*;
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
  public void testCustomerRepositoryFindAll()
  {
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

  @Test
  public void testCustomerRepositoryFindById()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.findById(1L)).thenReturn(expectedCustomer);
    Customer actualCustomer = customerRepository.findById(1L);
    assertThat(actualCustomer).isNotNull();
    assertThat(actualCustomer).isEqualTo(expectedCustomer);
    Mockito.verify(customerRepository).findById(1L);
  }

  @Test
  public void testCustomerRepositorySave()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.save(expectedCustomer)).thenReturn(expectedCustomer);
    Customer actualCustomer = customerRepository.save(expectedCustomer);
    assertThat(actualCustomer).isNotNull();
    assertThat(actualCustomer).isEqualTo(expectedCustomer);
    Mockito.verify(customerRepository).save(expectedCustomer);
  }

  @Test
  public void testCustomerRepositoryDelete()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.findById(1L)).thenReturn(expectedCustomer);
    customerRepository.delete(expectedCustomer);
    Mockito.verify(customerRepository).delete(expectedCustomer);
  }

  @Test
  public void testCustomerRepositoryDeleteById()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.findById(1L)).thenReturn(expectedCustomer);
    customerRepository.deleteById(1L);
    Mockito.verify(customerRepository).deleteById(1L);
  }

  @Test
  public void testCustomerRepositoryUpdate()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.update("query", expectedCustomer)).thenReturn(1);
    assertThat(customerRepository.update("query", expectedCustomer)).isEqualTo(1);
    Mockito.verify(customerRepository).update("query", expectedCustomer);
  }

  @Test
  public void testCustomerRepositoryUpdateById()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.updateById(1L, expectedCustomer)).thenReturn(1);
    assertThat(customerRepository.updateById(1L, expectedCustomer)).isEqualTo(1);
    Mockito.verify(customerRepository).updateById(1L, expectedCustomer);
  }

  @Test
  public void testCustomerRepositoryListAll()
  {
    List<Customer> expectedCustomers = getCustomers();
    when(customerRepository.listAll()).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.listAll();
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).hasSize(expectedCustomers.size());
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).listAll();
  }

  @Test
  public void testCustomerRepositoryListAllWithOrder()
  {
    List<Customer> expectedCustomers = getCustomers();
    when(customerRepository.listAll(any(Sort.class))).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.listAll(Sort.by("id"));
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).hasSize(expectedCustomers.size());
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).listAll(any(Sort.class));
  }

  @Test
  public void testCustomerRepositoryFindAllWithOrderAndPage()
  {
    List<Customer> expectedCustomers = getCustomers();
    PanacheQuery<Customer> mockQuery = mock(PanacheQuery.class);
    when(customerRepository.findAll(any(Sort.class))).thenReturn(mockQuery);
    when(mockQuery.page(any(Page.class))).thenReturn(mockQuery);
    when(mockQuery.list()).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.findAll(Sort.by("id")).page(Page.of(0, 10)).list();
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).hasSize(expectedCustomers.size());
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    verify(customerRepository).findAll(any(Sort.class));
    verify(mockQuery).page(any(Page.class));
    verify(mockQuery).list();
  }

  @Test
  public void testCustomerRepositoryFindByLastName()
  {
    List<Customer> expectedCustomers = getCustomers();
    when(customerRepository.findByLastName("John")).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.findByLastName("John");
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).findByLastName("John");
  }

  @Test
  public void testFindByEmail()
  {
    Customer expectedCustomer = getCustomers().getFirst();
    when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.ofNullable(expectedCustomer));
    Optional<Customer> actualCustomer = customerRepository.findByEmail("john.doe@example.com");
    assertThat(actualCustomer).isNotNull();
    assertThat(actualCustomer.orElseThrow()).isEqualTo(expectedCustomer);
    Mockito.verify(customerRepository).findByEmail("john.doe@example.com");
  }

  @Test
  public void testFindByEmailNotFound()
  {
    when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
    Optional<Customer> actualCustomer = customerRepository.findByEmail("john.doe@example.com");
    assertThat(actualCustomer).isNotNull();
    assertThat(actualCustomer).isEmpty();
    Mockito.verify(customerRepository).findByEmail("john.doe@example.com");
  }

  @Test
  public void testListCustomersWithOrders()
  {
    List<Customer> expectedCustomers = getCustomers();
    when(customerRepository.listCustomersWithOrders()).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.listCustomersWithOrders();
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).listCustomersWithOrders();
  }

  @Test
  public void testListCustomersWithOrdersNotFound()
  {
    when(customerRepository.listCustomersWithOrders()).thenReturn(Collections.emptyList());
    List<Customer> actualCustomers = customerRepository.listCustomersWithOrders();
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).isEmpty();
    Mockito.verify(customerRepository).listCustomersWithOrders();
  }

  @Test
  public void testListCustomersByLastName()
  {
    List<Customer> expectedCustomers = getCustomers();
    when(customerRepository.listCustomersByLastName("John")).thenReturn(expectedCustomers);
    List<Customer> actualCustomers = customerRepository.listCustomersByLastName("John");
    assertThat(actualCustomers).isNotNull();
    assertThat(actualCustomers).isEqualTo(expectedCustomers);
    Mockito.verify(customerRepository).listCustomersByLastName("John");
  }

  private List<Customer> getCustomers()
  {
    Order order = new Order(1L, "myItem1", new BigDecimal("100.25"), null);
    Customer customer = new Customer(1L, "John", "Doe", "john.doe@email.com", "1234567890", new ArrayList<>());
    order.setCustomer(customer);
    customer.addOrder(order);
    return List.of(customer);
  }
}
