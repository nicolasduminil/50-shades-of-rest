package fr.simplex_software.fifty_shades_of_rest.orders.provider;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.json.*;
import org.eclipse.microprofile.rest.client.inject.*;

import java.util.*;
import java.util.stream.*;

@ApplicationScoped
public class RandomCustomerService
{
  @Inject
  @RestClient
  RandomCustomerApiClient randomCustomerApiClient;

  public CustomerDTO getRandomCustomer()
  {
    try
    {
      JsonObject response = randomCustomerApiClient.getRandomCustomer();
      JsonArray results = response.getJsonArray("results");
      return convertToCustomerDTO(results.getJsonObject(0));
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to fetch random user", e);
    }
  }

  public List<CustomerDTO> getRandomCustomers(int count)
  {
    try
    {
      JsonObject response = randomCustomerApiClient.getRandomCustomers(count);
      JsonArray results = response.getJsonArray("results");
      return results.stream()
        .map(user -> convertToCustomerDTO((JsonObject) user))
        .collect(Collectors.toList());
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to fetch random users", e);
    }
  }

  private CustomerDTO convertToCustomerDTO(JsonObject customer)
  {
    JsonObject name = customer.getJsonObject("name");
    return new CustomerDTO(
      name.getString("first"),
      name.getString("last"),
      customer.getString("email"),
      customer.getString("phone")
    );
  }
}
