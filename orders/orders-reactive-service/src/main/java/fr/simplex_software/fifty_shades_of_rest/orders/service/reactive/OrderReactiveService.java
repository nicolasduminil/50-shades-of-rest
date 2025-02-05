package fr.simplex_software.fifty_shades_of_rest.orders.service.reactive;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.smallrye.mutiny.*;

import java.util.*;

public interface OrderReactiveService
{
  Uni<OrderDTO> createOrder(OrderDTO orderDTO);
  Uni<List<OrderDTO>> getOrdersForCustomer(Long customerId);
  Uni<List<OrderDTO>> getAllOrders();
  Uni<Optional<OrderDTO>> getOrder(Long id);
  Uni<Void> deleteOrder(OrderDTO orderDTO);
  Uni<Void> deleteOrder(Long id);
  Uni<OrderDTO> updateOrder(OrderDTO orderDTO);
  Uni<Void> deleteAllOrdersForCustomer(Long customerId);
  Uni<Void> deleteAllOrders();
  Uni<Order> findOrderById(Long id);
}
