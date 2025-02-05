package fr.simplex_software.fifty_shades_of_rest.orders.service.reactive.exceptions;

public class OrderNotFoundException extends RuntimeException
{
  public OrderNotFoundException(String message)
  {
    super(message);
  }

  public OrderNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
