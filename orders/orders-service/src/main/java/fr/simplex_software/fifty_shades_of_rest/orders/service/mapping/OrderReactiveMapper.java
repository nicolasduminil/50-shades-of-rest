package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderReactiveMapper
{
  OrderReactiveMapper INSTANCE = Mappers.getMapper(OrderReactiveMapper.class);
  @Mapping(source = "customer.id", target = "customerId")
  @Mapping(target = "customer.orders", ignore = true)
  OrderDTO fromEntity(Order order);
  @Mapping(source = "orderDTO.id", target = "id")
  Order toEntity(OrderDTO orderDTO, Customer customer);
  void updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
