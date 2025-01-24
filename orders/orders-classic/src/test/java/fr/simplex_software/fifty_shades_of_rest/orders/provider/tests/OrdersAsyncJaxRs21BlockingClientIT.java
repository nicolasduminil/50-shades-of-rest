package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.concurrent.*;

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;
import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdersAsyncJaxRs21BlockingClientIT
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI customerSrvUri;

  @BeforeAll
  public void beforeAll()
  {
    withCustomerUri(customerSrvUri);
  }

  @Test
  @Order(10)
  public void testCreateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      Response response = createCustomerRx(client, customerDTO).join();
      assertCustomer(response, HttpStatus.SC_CREATED, "John");
      CustomerDTO createdCustomer = response.readEntity(CustomerDTO.class);
      assertThat(createdCustomer).isNotNull();
      assertThat(createdCustomer.id()).isNotNull();
      assertThat(createdCustomer.firstName()).isEqualTo("John");
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      assertCustomers(getCustomersRx(client).join());
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      assertCustomer(getCustomerByEmailRx(client, JOHN_EMAIL).join(), HttpStatus.SC_OK, "John");
    }
  }

  @Test
  @Order(40)
  public void testUpdateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmailRx(client, JOHN_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          CustomerDTO updatedCustomerDTO = new CustomerDTO(customerDTO.id(),
            "Jane", "Doe",
            "jane.doe@email.com", "0987654321");
          return updateCustomerRx(client, updatedCustomerDTO);
        })
        .exceptionally(handleFailure());
      assertCustomer(futureResponse.join(), HttpStatus.SC_ACCEPTED, "Jane");
    }
  }

  @Test
  @Order(50)
  public void testDeleteCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Response response = getCustomerByEmailRx(client, JANE_EMAIL)
        .thenApply(resp -> resp.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO -> deleteCustomer(client, customerDTO))
        .exceptionally(handleFailure()).join();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }
  }
}
