package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.inject.*;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper
{
  @Inject
  OrderService orderService;
  @Inject
  CustomerService customerService;

  @Mapping(source = "customer.id", target = "customerId")
  public abstract OrderDTO fromEntity(Order order);

  public Order toEntity(OrderDTO orderDTO) {
    if (orderDTO.id() != null) {
      Order existingOrder = orderService.findOrderById(orderDTO.id());
      if (existingOrder != null) {
        updateEntityFromDTO(orderDTO, existingOrder);
        return existingOrder;
      }
    }
    Customer customer = customerService.findCustomerById(orderDTO.customerId());
    Order order = new Order(orderDTO.item(), orderDTO.price(), customer);
    return order;
  }

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateEntityFromDTO(OrderDTO customerDTO, @MappingTarget Order order);
}
