package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.impl.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.quarkus.test.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;

import java.math.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TestOrderMapper
{
  @InjectMock
  CustomerServiceImpl customerService;

  @Test
  public void testFromEntity()
  {
    Order order = new Order("myItem1", new BigDecimal("1.11"));
    OrderDTO orderDTO = OrderMapper.INSTANCE.fromEntity(order);
    assertThat(orderDTO).isNotNull();
    assertThat(orderDTO.item()).isEqualTo(order.getItem());
  }

  @Test
  public void testToEntity()
  {
    when(customerService.getCustomer(1L))
      .thenReturn(new CustomerDTO("John", "Doe", "john.doe@email.com", "555-1234"));
    OrderDTO orderDTO = new OrderDTO("myItem2", new BigDecimal("2.22"));
    Order order = OrderMapper.INSTANCE.toEntity(orderDTO, customerService);
    assertThat(order).isNotNull();
    assertThat(order.getItem()).isEqualTo(orderDTO.item());
  }
}
