package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
@DataSet(value = "orders.yml", cleanAfter = true)
public class OrderServiceIT
{
  @Inject
  OrderService orderService;

  @Test
  public void testGetOrders()
  {
    List<OrderDTO> orderDTOs = orderService.getAllOrders();
    assertThat(orderDTOs).isNotEmpty();
    assertThat(orderDTOs).hasSize(4);
    assertThat(orderDTOs.getFirst().item()).isEqualTo("myItem1");
  }

  @Test
  public void testGetOrder()
  {
    OrderDTO orderDTO = orderService.getOrder(1L).orElseThrow();
    assertThat(orderDTO).isNotNull();
    assertThat(orderDTO.item()).isEqualTo("myItem1");
  }

  @Test
  public void testGetOrderNotFound()
  {
    assertThatThrownBy(() -> orderService.getOrder(10L).orElseThrow())
      .isInstanceOf(Exception.class);
  }

  @Test
  @DataSet(value = "orders2.yml", cleanAfter = true)
  public void testCreateOrder()
  {
    OrderDTO orderDTO = new OrderDTO(10L, "myItem10", new BigDecimal("25.43"), 2L);
    OrderDTO createdOrderDTO = orderService.createOrder(orderDTO);
    assertThat(createdOrderDTO).isNotNull();
    assertThat(createdOrderDTO.id()).isNotNull();
    assertThat(createdOrderDTO.item()).isEqualTo("myItem10");
  }

  @Test
  public void testUpdateOrder()
  {
    OrderDTO orderDTO = orderService.getOrder(1L).orElseThrow();
    orderDTO = new OrderDTO(1L, "myItem10", new BigDecimal("87.90"));
    OrderDTO updatedOrderDTO = orderService.updateOrder(orderDTO);
    assertThat(updatedOrderDTO).isNotNull();
    assertThat(updatedOrderDTO.id()).isNotNull();
    assertThat(updatedOrderDTO.item()).isEqualTo("myItem10");
  }

  @Test
  public void testUpdateOrderNotFound()
  {
    OrderDTO orderDTO = new OrderDTO(10L, "myItem10", new BigDecimal("87.90"), 1L);
    assertThatThrownBy(() -> orderService.updateOrder(orderDTO))
      .isInstanceOf(OrderNotFoundException.class);
  }

  @Test
  public void testDeleteOrder()
  {
    orderService.deleteOrder(1L);
    assertThatThrownBy(() -> orderService.getOrder(1L).orElseThrow())
      .isInstanceOf(Exception.class);
  }

  @Test
  public void testFindOrderById()
  {
    Order order = orderService.findOrderById(1L);
    assertThat(order).isNotNull();
    assertThat(order.getItem()).isEqualTo("myItem1");
  }
}
