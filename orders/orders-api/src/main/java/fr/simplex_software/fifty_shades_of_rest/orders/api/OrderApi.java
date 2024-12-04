package fr.simplex_software.fifty_shades_of_rest.orders.api;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.core.*;

public interface OrderApi
{
  Response getOrder(Long id);
  Response getOrdersByCustomer(Long customerId);
  Response createOrder(OrderDTO orderDTO);
  Response updateOrder(OrderDTO orderDTO);
  Response deleteOrder(OrderDTO orderDTO);
}
