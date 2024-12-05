package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.impl.*;
import jakarta.inject.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper
{
  @Inject
  OrderService orderService;
  @Inject
  CustomerService customerService;

  @Mapping(source = "customer.id", target = "customerId")
  public abstract OrderDTO fromEntity(Order order);
  /*@Mapping(source = "customerId", target = "customer", qualifiedByName = "findCustomerById")
  Order toEntity(OrderDTO dto, @Context CustomerService customerService);
  @Named("findCustomerById")
  default Customer findCustomerById(Long customerId, @Context CustomerService customerService)
  {
    return customerService.findCustomerById(customerId);
  }*/

  public Order toEntity(OrderDTO orderDTO) {
    if (orderDTO.id() != null) {
      // If the order exists, load it first
      Order existingOrder = orderService.findOrderById(orderDTO.id());
      if (existingOrder != null) {
        updateEntityFromDTO(orderDTO, existingOrder);
        return existingOrder;
      }
    }
    // Create new order if it doesn't exist
    Customer customer = customerService.findCustomerById(orderDTO.customerId());
    Order order = new Order(orderDTO.item(), orderDTO.price(), customer);
    return order;
  }

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateEntityFromDTO(OrderDTO customerDTO, @MappingTarget Order order);
}
