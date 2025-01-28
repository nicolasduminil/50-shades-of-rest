package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.container.*;

public interface OrderAsyncApi
{
  void getOrders(@Suspended AsyncResponse ar);
  void getOrder(Long id, @Suspended AsyncResponse ar);
  void getOrdersByCustomer(Long customerId, @Suspended AsyncResponse ar);
  void createOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar);
  void updateOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar);
  void deleteOrder(OrderDTO orderDTO, @Suspended AsyncResponse ar);
}
