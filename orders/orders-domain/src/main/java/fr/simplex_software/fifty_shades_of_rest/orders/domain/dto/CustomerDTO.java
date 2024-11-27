package fr.simplex_software.fifty_shades_ofrest.orders.domain.dto;

public record CustomerDTO(Long id, String firstName, String lastName, String email, String phone)
{
  public CustomerDTO(String firstName, String lastName, String email, String phone)
  {
    this(null, firstName, lastName, email, phone);
  }
}
