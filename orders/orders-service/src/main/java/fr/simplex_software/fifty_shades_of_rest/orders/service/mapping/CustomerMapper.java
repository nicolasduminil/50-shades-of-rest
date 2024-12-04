package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper
{
  CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
  Customer toEntity(CustomerDTO dto);
  CustomerDTO fromEntity(Customer entity);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(CustomerDTO customerDTO, @MappingTarget Customer customer);
}
