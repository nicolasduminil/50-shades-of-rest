package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
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
public class OrderRepositoryIT
{
  @Inject
  OrderRepository orderRepository;
  @Inject
  CustomerRepository customerRepository;

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindAll()
  {
    List<Order> orders = orderRepository.findAll().stream().toList();
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(4);
  }

  @Test
  @Transactional
  @DataSet(cleanAfter = true)
  @ExpectedDataSet(value = "expected-orders.yml")
  public void testPersist()
  {
    customerRepository.persist(getCustomer());
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindById()
  {
    Order order = orderRepository.findById(1L);
    assertThat(order).isNotNull();
    assertThat(order.getItem()).isEqualTo("myItem1");
    assertThat(order.getPrice()).isEqualTo(new BigDecimal("100.00"));
    assertThat(order.getCustomer()).isNotNull();
    assertThat(order.getCustomer().getLastName()).isEqualTo("Doe");
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindByItem()
  {
    List<Order> orders = orderRepository.findByItem("myItem1");
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(1);
    Order order = orders.getFirst();
    assertThat(order.getItem()).isEqualTo("myItem1");
    assertThat(order.getPrice()).isEqualTo(new BigDecimal("100.00"));
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testFindByCustomer()
  {
    Customer customer = customerRepository.findById(1L);
    assertThat(customer).isNotNull();
    List<Order> orders = orderRepository.findByCustomer(customer);
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(2);
    assertThat(orders.getFirst().getItem()).isEqualTo("myItem1");
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testDeleteById()
  {
    orderRepository.deleteById(1L);
    List<Order> orders = orderRepository.findAll().stream().toList();
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(3);
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testDeleteByCustomer()
  {
    Customer customer = customerRepository.findById(1L);
    assertThat(customer).isNotNull();
    orderRepository.deleteByCustomer(customer);
    List<Order> orders = orderRepository.findAll().stream().toList();
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(2);
  }

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testUpdateOrder()
  {
    Order order = orderRepository.findById(1L);
    order.setItem("myUpdatedItem");
    order.setPrice(new BigDecimal("200.00"));
    orderRepository.updateById(1L, order);
    Order updatedOrder = orderRepository.findById(1L);
    assertThat(updatedOrder).isNotNull();
    assertThat(updatedOrder.getItem()).isEqualTo("myUpdatedItem");
    assertThat(updatedOrder.getPrice()).isEqualTo(new BigDecimal("200.00"));
  }

  @Test
  @DataSet(value = "orders.yml", cleanAfter = true)
  public void testListOrdersByItem()
  {
    List<Order> orders = orderRepository.listOrdersByItem("myItem1");
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(1);
  }

  private Customer getCustomer()
  {
    Customer customer = new Customer("John", "Doe",
      "john.doe@email.com", "555-1234");
    customer.addOrder(new Order("myItem1", new BigDecimal("100.25"), customer));
    customer.addOrder(new Order("myItem2", new BigDecimal("200.25"), customer));
    return customer;
  }
}
