package fr.simplex_software.fifty_shades_of_rest.orders.service;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;

import java.util.*;

public interface OrderService
{
  OrderDTO createOrder(OrderDTO orderDTO);
  List<OrderDTO> getOrdersForCustomer(Long customerId);
  List<OrderDTO> getAllOrders();
  Optional<OrderDTO> getOrder(Long id);
  void deleteOrder(OrderDTO orderDTO);
  void deleteOrder(Long id);
  OrderDTO updateOrder(OrderDTO orderDTO);
  void deleteAllOrdersForCustomer(Long customerId);
  void deleteAllOrders();
}
