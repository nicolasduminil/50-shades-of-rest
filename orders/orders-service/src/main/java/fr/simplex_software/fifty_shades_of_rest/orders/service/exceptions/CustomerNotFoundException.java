package fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions;

public class CustomerNotFoundException extends RuntimeException
{
  public CustomerNotFoundException(String message)
  {
    super(message);
  }

  public CustomerNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
