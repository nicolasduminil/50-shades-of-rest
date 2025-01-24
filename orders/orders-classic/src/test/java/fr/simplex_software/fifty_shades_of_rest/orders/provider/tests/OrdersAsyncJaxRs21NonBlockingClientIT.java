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
import java.util.concurrent.TimeUnit;

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;
import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdersAsyncJaxRs21NonBlockingClientIT
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
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testCreateCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      Response resp = createCustomerRx(client, customerDTO)
        .thenApply(response ->
        {
          assertCustomer(response, HttpStatus.SC_CREATED, "John");
          return response;
        })
        .exceptionally(throwable -> handleFailure().apply(throwable))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(20)
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testGetCustomers() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomersRx(client)
        .thenApply(response ->
        {
          assertCustomers(response);
          return response;
        })
        .exceptionally(throwable -> handleFailure().apply(throwable))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(30)
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testGetCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomerByEmailRx(client, JOHN_EMAIL)
        .thenApply(response ->
        {
          assertCustomer(response, HttpStatus.SC_OK, "John");
          return response;
        })
        .exceptionally(throwable -> handleFailure().apply(throwable))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(40)
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testUpdateCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      Response response = getCustomerByEmailRx(client, JOHN_EMAIL)
        .thenApply(resp -> resp.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          CustomerDTO updatedCustomerDTO = new CustomerDTO(customerDTO.id(),
            "Jane", "Doe",
            "jane.doe@email.com", "0987654321");
          return updateCustomerRx(client, updatedCustomerDTO);
        })
        .thenApply(resp ->
        {
          assertCustomer(resp, HttpStatus.SC_ACCEPTED, "Jane");
          return resp;
        })
        .exceptionally(handleFailure())
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(50)
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  public void testDeleteCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomerByEmailRx(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO -> deleteCustomer(client, customerDTO))
        .thenApply(response ->
        {
          assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
          return response;
        })
        .exceptionally(handleFailure())
        .get(5, TimeUnit.SECONDS);
    }
  }
}
