package fr.simplex_software.fifty_shades_ofrest.orders.domain.dto;

import java.math.*;

public record OrderDTO(Long id, String item, BigDecimal price, Long customerId)
{
  public OrderDTO(String item, BigDecimal price, Long customerId)
  {
    this(null, item, price, customerId);
  }
}
