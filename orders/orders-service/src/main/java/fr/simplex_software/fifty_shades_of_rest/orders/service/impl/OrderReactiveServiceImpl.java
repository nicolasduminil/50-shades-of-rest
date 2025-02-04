package fr.simplex_software.fifty_shades_of_rest.orders.service.impl;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
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
  OrderReactiveMapper orderMapper;
  @Inject
  CustomerReactiveService customerService;

  @Override
  public Uni<OrderDTO> createOrder(OrderDTO orderDTO)
  {
    return customerService.findCustomerById(orderDTO.customerId())
      .map(customer -> orderMapper.toEntity(orderDTO, customer))
      .chain(order -> orderRepository.persist(order))
      .map(order -> orderMapper.fromEntity(order));
  }

  @Override
  public Uni<List<OrderDTO>> getOrdersForCustomer(Long customerId)
  {
    System.out.println("OrderReactiveServiceImpl.getOrdersForCustomer() - customerId: " + customerId);
    return orderRepository.findByCustomerId(customerId)
      .map(orders -> orders.stream()
        .map(orderMapper::fromEntity)
        .toList());
  }

  @Override
  public Uni<List<OrderDTO>> getAllOrders()
  {
    return orderRepository.listAll()
      .map(orders -> orders.stream()
        .map(orderMapper::fromEntity)
        .toList());
  }

  @Override
  public Uni<Optional<OrderDTO>> getOrder(Long id)
  {
    return orderRepository.findByIdOptional(id)
      .map(optionalOrder -> optionalOrder
        .map(orderMapper::fromEntity));
  }

  @Override
  public Uni<Void> deleteOrder(OrderDTO orderDTO)
  {
    return deleteOrder(orderDTO.id());
  }

  @Override
  public Uni<Void> deleteOrder(Long id)
  {
    return orderRepository.deleteById(id)
      .replaceWithVoid();
  }

  @Override
  public Uni<OrderDTO> updateOrder(OrderDTO orderDTO)
  {
    return orderRepository.findById(orderDTO.id())
      .onItem().ifNull().failWith(() ->
        new OrderNotFoundException("Order not found for id: " + orderDTO.id()))
      .chain(existingOrder -> customerService.findCustomerById(orderDTO.customerId())
        .map(customer -> {
          orderMapper.updateEntityFromDTO(orderDTO, existingOrder);
          existingOrder.setCustomer(customer);
          return existingOrder;
        }))
      .chain(orderRepository::persist)
      .map(orderMapper::fromEntity);
  }

  @Override
  public Uni<Void> deleteAllOrdersForCustomer(Long customerId)
  {
    return orderRepository.delete("customerId", customerId)
      .replaceWithVoid();
  }

  @Override
  public Uni<Void> deleteAllOrders()
  {
    return orderRepository.deleteAll()
      .replaceWithVoid();
  }

  @Override
  public Uni<Order> findOrderById(Long id)
  {
    return orderRepository.findByIdOptional(id)
      .map(optionalOrder -> optionalOrder
        .orElseThrow(() ->
          new OrderNotFoundException("Order not found for id: " + id)));
  }
}
