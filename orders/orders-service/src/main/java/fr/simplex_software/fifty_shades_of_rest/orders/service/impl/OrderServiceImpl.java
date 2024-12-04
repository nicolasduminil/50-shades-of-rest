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
@Transactional
public class OrderServiceImpl implements OrderService
{
  @Inject
  OrderRepository orderRepository;
  @Inject
  CustomerService customerService;

  @Override
  @Transactional
  public OrderDTO createOrder(OrderDTO orderDTO)
  {
    Order order = OrderMapper.INSTANCE.toEntity(orderDTO, customerService);
    orderRepository.persistAndFlush(order);
    return OrderMapper.INSTANCE.fromEntity(order);
  }

  @Override
  public List<OrderDTO> getOrdersForCustomer(Long customerId)
  {
    List<Order> orders = orderRepository.find("customer.id", customerId).list();
    return orders.stream().map(OrderMapper.INSTANCE::fromEntity).collect(Collectors.toList());
  }

  @Override
  public List<OrderDTO> getAllOrders()
  {
    return orderRepository.streamAll().map(OrderMapper.INSTANCE::fromEntity).toList();
  }

  @Override
  public Optional<OrderDTO> getOrder(Long id)
  {
    return orderRepository.findByIdOptional(id).map(OrderMapper.INSTANCE::fromEntity);
  }

  @Override
  @Transactional
  public void deleteOrder(OrderDTO orderDTO)
  {
    orderRepository.delete(OrderMapper.INSTANCE.toEntity(orderDTO, customerService));
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
      OrderMapper.INSTANCE.updateEntityFromDTO(orderDTO, existingOrder);
      orderRepository.persist(existingOrder);
      return OrderMapper.INSTANCE.fromEntity(existingOrder);
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
}
