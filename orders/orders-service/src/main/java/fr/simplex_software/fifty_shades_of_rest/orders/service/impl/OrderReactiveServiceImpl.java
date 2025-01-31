package fr.simplex_software.fifty_shades_of_rest.orders.service.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.smallrye.mutiny.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;

import java.util.*;

@ApplicationScoped
public class OrderReactiveServiceImpl implements OrderReactiveService
{
  @Inject
  OrderReactiveRepository orderRepository;
  @Inject
  OrderMapper orderMapper;

  @Override
  public Uni<OrderDTO> createOrder(OrderDTO orderDTO)
  {
    return orderMapper.fromEntity(orderRepository.createOrder(orderMapper.toEntity(orderDTO)));
  }

  @Override
  public Uni<List<OrderDTO>> getOrdersForCustomer(Long customerId)
  {
    return null;
  }

  @Override
  public Uni<List<OrderDTO>> getAllOrders()
  {
    return null;
  }

  @Override
  public Uni<Optional<OrderDTO>> getOrder(Long id)
  {
    return null;
  }

  @Override
  public Uni<Void> deleteOrder(OrderDTO orderDTO)
  {
    return null;
  }

  @Override
  public Uni<Void> deleteOrder(Long id)
  {
    return null;
  }

  @Override
  public Uni<OrderDTO> updateOrder(OrderDTO orderDTO)
  {
    return null;
  }

  @Override
  public Uni<Void> deleteAllOrdersForCustomer(Long customerId)
  {
    return null;
  }

  @Override
  public Uni<Void> deleteAllOrders()
  {
    return null;
  }

  @Override
  public Uni<Order> findOrderById(Long id)
  {
    return null;
  }
}
