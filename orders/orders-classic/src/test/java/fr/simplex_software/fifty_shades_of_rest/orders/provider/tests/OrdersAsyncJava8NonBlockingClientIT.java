package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;
import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdersAsyncJava8NonBlockingClientIT
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI customerSrvUri;

  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  @BeforeAll
  public void beforeAll()
  {
    withCustomerUri(customerSrvUri);
  }

  @Test
  @Order(10)
  @Timeout(5)
  public void testCreateCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      createCustomer(client, customerDTO)
        .thenAccept(response ->
          assertCustomer(response, HttpStatus.SC_CREATED, "John"))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(20)
  @Timeout(5)
  public void testGetCustomers() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomers(client).thenAccept(response ->
        {
          assertThat(response).isNotNull();
          assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
          CustomerDTO[] customerDTOS = response.readEntity(CustomerDTO[].class);
          assertThat(customerDTOS).isNotNull();
          assertThat(customerDTOS).hasAtLeastOneElementOfType(CustomerDTO.class);
        })
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(30)
  @Timeout(5)
  public void testGetCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomerByEmail(client, JOHN_EMAIL)
        .thenAccept(response ->
          assertCustomer(response, HttpStatus.SC_OK, "John"))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(40)
  @Timeout(5)
  public void testUpdateCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomerByEmail(client, JOHN_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          CustomerDTO updatedCustomerDTO = new CustomerDTO(customerDTO.id(),
            "Jane", "Doe",
            "jane.doe@email.com", "0987654321");
          return updateCustomer(client, updatedCustomerDTO);
        })
        .thenAccept(response -> assertCustomer(response, HttpStatus.SC_ACCEPTED, "Jane"))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(50)
  @Timeout(5)
  public void testDeleteCustomer() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomerByEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO -> deleteCustomer(client, customerDTO))
        .thenAccept(response ->
          assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT))
        .get(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @Order(60)
  @Timeout(5)
  public void testGetCustomersShouldFail() throws Exception
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomers(client).thenAccept(response ->
        {
          assertThat(response).isNotNull();
          assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
          CustomerDTO[] customerDTOS = response.readEntity(CustomerDTO[].class);
          assertThat(customerDTOS).isNotNull();
          assertThat(customerDTOS).isEmpty();
        })
        .get(5, TimeUnit.SECONDS);
    }
  }
}
