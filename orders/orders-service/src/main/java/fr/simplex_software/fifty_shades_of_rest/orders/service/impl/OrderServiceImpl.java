package fr.simplex_software.fifty_shades_of_rest.orders.service.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.transaction.*;

import java.util.*;
import java.util.stream.*;

@ApplicationScoped
public class OrderServiceImpl implements OrderService
{
  @Inject
  OrderRepository orderRepository;
  @Inject
  OrderMapper orderMapper;

  @Override
  @Transactional
  public OrderDTO createOrder(OrderDTO orderDTO)
  {
    Order order = orderMapper.toEntity(orderDTO);
    orderRepository.persistAndFlush(order);
    return orderMapper.fromEntity(order);
  }

  @Override
  public List<OrderDTO> getOrdersForCustomer(Long customerId)
  {
    List<Order> orders = orderRepository.find("customer.id", customerId).list();
    return orders.stream().map(orderMapper::fromEntity).collect(Collectors.toList());
  }

  @Override
  public List<OrderDTO> getAllOrders()
  {
    return orderRepository.streamAll().map(orderMapper::fromEntity).toList();
  }

  @Override
  public Optional<OrderDTO> getOrder(Long id)
  {
    return orderRepository.findByIdOptional(id).map(orderMapper::fromEntity);
  }

  @Override
  @Transactional
  public void deleteOrder(OrderDTO orderDTO)
  {
    orderRepository.delete(orderMapper.toEntity(orderDTO));
  }

  @Transactional
  @Override
  public void deleteOrder(Long id)
  {
    orderRepository.deleteById(id);
  }

  @Override
  @Transactional
  public OrderDTO updateOrder(OrderDTO orderDTO)
  {
    Optional<Order> optionalOrder = orderRepository.findByIdOptional(orderDTO.id());
    return optionalOrder.map(existingOrder ->
    {
      orderMapper.updateEntityFromDTO(orderDTO, existingOrder);
      orderRepository.persist(existingOrder);
      return orderMapper.fromEntity(existingOrder);
    }).orElseThrow(() -> new OrderNotFoundException("### OrderServiceImpl.updateOrder(): Order not found for id: " + orderDTO.id()));
  }

  @Override
  @Transactional
  public void deleteAllOrdersForCustomer(Long customerId)
  {
    orderRepository.find("customer.id", customerId).list().forEach(order -> orderRepository.delete(order));
  }

  @Override
  @Transactional
  public void deleteAllOrders()
  {
    orderRepository.deleteAll();
  }

  @Override
  public Order findOrderById(Long id)
  {
    return orderRepository.findById(id);
  }
}
