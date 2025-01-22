package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OrdersBaseJava8AsyncClient
{
  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  public abstract URI getCustomerSrvUri();

  @BeforeAll
  public void beforeAll()
  {
    withCustomerUri(getCustomerSrvUri());
  }

  @Test
  @Order(10)
  public void testCreateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      CompletableFuture<Response> futureResponse = createCustomer(client, customerDTO);
      Response response = futureResponse.join();
      assertCustomer(response, HttpStatus.SC_CREATED, "John");
      response.close();
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomers(client);
      Response response = futureResponse.join();
      assertCustomers(response);
      response.close();
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JOHN_EMAIL);
      Response response = futureResponse.join();
      assertCustomer(response, HttpStatus.SC_OK, "John");
      response.close();
    }
  }

  @Test
  @Order(40)
  public void testUpdateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JOHN_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          CustomerDTO updatedCustomerDTO = new CustomerDTO(customerDTO.id(),
            "Jane", "Doe",
            "jane.doe@email.com", "0987654321");
          return updateCustomer(client, updatedCustomerDTO);
        })
        .exceptionally(handleFailure());
      Response response = futureResponse.join();
      assertCustomer(response, HttpStatus.SC_ACCEPTED, "Jane");
      response.close();
    }
  }

  @Test
  @Order(50)
  public void testDeleteCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO -> deleteCustomer(client, customerDTO)
          .exceptionally(handleFailure()));
      Response response = futureResponse.join();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
      response.close();
    }
  }
}
