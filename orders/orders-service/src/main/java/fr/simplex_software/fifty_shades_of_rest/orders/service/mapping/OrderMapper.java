package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper
{
  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
  @Mapping(source = "customer.id", target = "customerId")
  OrderDTO fromEntity(Order order);
  @Mapping(source = "customerId", target = "customer", qualifiedByName = "findCustomerById")
  Order toEntity(OrderDTO dto, @Context CustomerService customerService);
  @Named("findCustomerById")
  default Customer findCustomerById(Long customerId, @Context CustomerService customerService)
  {
    return customerService.findCustomerById(customerId);
  }
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(OrderDTO customerDTO, @MappingTarget Order order);
}
