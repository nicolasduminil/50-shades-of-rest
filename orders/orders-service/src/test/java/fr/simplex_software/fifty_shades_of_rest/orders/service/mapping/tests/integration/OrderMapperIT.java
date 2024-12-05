package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
@DataSet(value = "orders.yml", cleanAfter = true)
public class OrderMapperIT
{
  @Inject
  CustomerService customerService;
  @Inject
  OrderMapper orderMapper;
  @Inject
  OrderService orderService;

  @Test
  public void testToEntity()
  {
    OrderDTO orderDTO = orderService.getOrder(1L).orElseThrow();
    assertThat(orderDTO).isNotNull();
    assertThat(orderDTO.customerId()).isEqualTo(1L);
    Customer customer = customerService.findCustomerById(1l);
    assertThat(customer).isNotNull();
    assertThat(customer.getId()).isEqualTo(1L);
    Order order = orderMapper.toEntity(orderDTO);
    assertThat(order).isNotNull();
    assertThat(order.getItem()).isEqualTo(orderDTO.item());
  }
}
