package fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.*;
import io.smallrye.mutiny.*;
import jakarta.inject.*;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderReactiveMapper
{
  @Inject
  OrderReactiveService orderService;
  @Inject
  CustomerReactiveService customerService;

  @Mapping(source = "customer.id", target = "customerId")
  public abstract OrderDTO fromEntity(Order order);

  public Uni<Order> toEntity(OrderDTO orderDTO)
  {
    if (orderDTO.id() != null)
    {
      return orderService.findOrderById(orderDTO.id())
        .onItem().ifNotNull()
        .transform(existingOrder ->
        {
          updateEntityFromDTO(orderDTO, existingOrder);
          return existingOrder;
        })
        .onItem().ifNull()
        .switchTo(() -> createNewOrder(orderDTO));
    }
    return createNewOrder(orderDTO);
  }

  private Uni<Order> createNewOrder(OrderDTO orderDTO)
  {
    return customerService.findCustomerById(orderDTO.customerId())
      .map(customer -> new Order(orderDTO.item(), orderDTO.price(), customer));
  }

  public abstract void updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
