package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests.unit;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestOrderMapper
{
  @Inject
  CustomerService customerService;
  @Inject
  OrderMapper orderMapper;

  @Test
  public void testFromEntity()
  {
    Order order = new Order("myItem1", new BigDecimal("1.11"));
    OrderDTO orderDTO = orderMapper.fromEntity(order);
    assertThat(orderDTO).isNotNull();
    assertThat(orderDTO.item()).isEqualTo(order.getItem());
  }
}
