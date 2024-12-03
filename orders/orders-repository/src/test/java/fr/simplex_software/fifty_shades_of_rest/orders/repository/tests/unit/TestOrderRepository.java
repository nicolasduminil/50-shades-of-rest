package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests.unit;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.tests.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TestOrderRepository
{
  @InjectMock
  OrderRepository orderRepository;

  @Test
  public void testOrderRepository()
  {
    assertThat(orderRepository).isNotNull();
    when(orderRepository.count()).thenReturn(23L);
    assertThat(orderRepository.count()).isEqualTo(23L);
  }

  @Test
  void testOrderRepositoryFindAll() {
    List<Order> expectedOrders = getOrders();
    PanacheQuery<Order> mockQuery = mock(PanacheQuery.class);
    when(orderRepository.findAll()).thenReturn(mockQuery);
    when(mockQuery.stream()).thenReturn(expectedOrders.stream());
    List<Order> actualOrders = orderRepository.findAll().stream().collect(Collectors.toList());
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).hasSize(expectedOrders.size());
    assertThat(actualOrders).isEqualTo(expectedOrders);
    Mockito.verify(orderRepository).findAll();
  }

  @Test
  void testOrderRepositoryFindById() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.findById(1L)).thenReturn(expectedOrder);
    Order actualOrder = orderRepository.findById(1L);
    assertThat(actualOrder).isNotNull();
    assertThat(actualOrder).isEqualTo(expectedOrder);
    Mockito.verify(orderRepository).findById(1L);
  }

  @Test
  void testOrderRepositorySave() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.save(expectedOrder)).thenReturn(expectedOrder);
    Order actualOrder = orderRepository.save(expectedOrder);
    assertThat(actualOrder).isNotNull();
    assertThat(actualOrder).isEqualTo(expectedOrder);
    Mockito.verify(orderRepository).save(expectedOrder);
  }

  @Test
  void testOrderRepositoryDelete() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.findById(1L)).thenReturn(expectedOrder);
    orderRepository.delete(expectedOrder);
    Mockito.verify(orderRepository).delete(expectedOrder);
  }

  @Test
  void testOrderRepositoryDeleteById() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.findById(1L)).thenReturn(expectedOrder);
    orderRepository.deleteById(1L);
    Mockito.verify(orderRepository).deleteById(1L);
  }

  @Test
  void testOrderRepositoryUpdate() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.update("query", expectedOrder)).thenReturn(1);
    assertThat(orderRepository.update("query", expectedOrder)).isEqualTo(1);
    Mockito.verify(orderRepository).update("query", expectedOrder);
  }

  @Test
  void testOrderRepositoryUpdateById() {
    Order expectedOrder = getOrders().getFirst();
    when(orderRepository.updateById(1L, expectedOrder)).thenReturn(1);
    assertThat(orderRepository.updateById(1L, expectedOrder)).isEqualTo(1);
    Mockito.verify(orderRepository).updateById(1L, expectedOrder);
  }

  @Test
  void testOrderRepositoryListAll() {
    List<Order> expectedOrders = getOrders();
    when(orderRepository.listAll()).thenReturn(expectedOrders);
    List<Order> actualOrders = orderRepository.listAll();
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).hasSize(expectedOrders.size());
    assertThat(actualOrders).isEqualTo(expectedOrders);
    Mockito.verify(orderRepository).listAll();
  }

  @Test
  void testOrderRepositoryListAllWithOrder() {
    List<Order> expectedOrders = getOrders();
    when(orderRepository.listAll(any(Sort.class))).thenReturn(expectedOrders);
    List<Order> actualOrders = orderRepository.listAll(Sort.by("id"));
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).hasSize(expectedOrders.size());
    assertThat(actualOrders).isEqualTo(expectedOrders);
    Mockito.verify(orderRepository).listAll(any(Sort.class));
  }

  @Test
  void testOrderRepositoryFindAllWithOrderAndPage() {
    List<Order> expectedOrders = getOrders();
    PanacheQuery<Order> mockQuery = mock(PanacheQuery.class);
    when(orderRepository.findAll(any(Sort.class))).thenReturn(mockQuery);
    when(mockQuery.page(any(Page.class))).thenReturn(mockQuery);
    when(mockQuery.list()).thenReturn(expectedOrders);
    List<Order> actualOrders = orderRepository.findAll(Sort.by("id")).page(Page.of(0, 10)).list();
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).hasSize(expectedOrders.size());
    assertThat(actualOrders).isEqualTo(expectedOrders);
    verify(orderRepository).findAll(any(Sort.class));
    verify(mockQuery).page(any(Page.class));
    verify(mockQuery).list();
  }

  @Test
  public void testOrderRepositoryFindByLastName() {
    List<Order> expectedOrders = getOrders();
    when(orderRepository.findByItem("myItem1")).thenReturn(expectedOrders);
    List<Order> actualOrders = orderRepository.findByItem("myItem1");
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).isEqualTo(expectedOrders);
    Mockito.verify(orderRepository).findByItem("myItem1");
  }

  @Test
  public void testListOrdersByLastName() {
    List<Order> expectedOrders = getOrders();
    when(orderRepository.listOrdersByItem("myItem1")).thenReturn(expectedOrders);
    List<Order> actualOrders = orderRepository.listOrdersByItem("myItem1");
    assertThat(actualOrders).isNotNull();
    assertThat(actualOrders).isEqualTo(expectedOrders);
    Mockito.verify(orderRepository).listOrdersByItem("myItem1");
  }

  private List<Order> getOrders()
  {
    Order order = new Order(1L, "myItem1", new BigDecimal("100.25"), null);
    Customer customer = new Customer(1L, "John", "Doe", "john.doe@email.com", "1234567890", new ArrayList<>());
    order.setCustomer(customer);
    customer.addOrder(order);
    return List.of(order);
  }
}
