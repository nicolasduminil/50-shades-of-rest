package fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerReactiveMapper
{
  CustomerReactiveMapper INSTANCE = Mappers.getMapper(CustomerReactiveMapper.class);
  Customer toEntity(CustomerDTO dto);
  CustomerDTO fromEntity(Customer entity);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(CustomerDTO customerDTO, @MappingTarget Customer customer);
}
