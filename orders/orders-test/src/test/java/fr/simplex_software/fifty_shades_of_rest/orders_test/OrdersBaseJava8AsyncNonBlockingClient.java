package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OrdersBaseJava8AsyncNonBlockingClient
{
  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  public abstract URI getCustomerSrvUri();
  public abstract URI getOrderSrvUri();

  @BeforeAll
  public void beforeAll()
  {
    withCustomerUri(getCustomerSrvUri());
    withOrderUri(getOrderSrvUri());
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
    }
  }

  /*@Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomers(client);
      futureResponse.thenAccept(OrdersJava8AsyncCommon::assertCustomers);
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
      assertCustomer(response, HttpStatus.SC_OK, "John204");
      //futureResponse.thenAccept(response -> assertCustomer(response, HttpStatus.SC_OK, "John204"));
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
  public void testCreateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          OrderDTO orderDTO = new OrderDTO("myItem01",
            new BigDecimal("100.25"), customerDTO.id());
          return createOrder(client, orderDTO);
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response -> assertOrder(response,
        HttpStatus.SC_CREATED, "myItem01"));
    }
  }

  @Test
  @Order(60)
  public void testGetOrders()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getOrders(client);
      futureResponse.thenAccept(OrdersJava8AsyncCommon::assertOrders);
    }
  }

  @Test
  @Order(70)
  public void testGetOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getOrderByCustomerEmail(client, JANE_EMAIL)
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response -> assertOrder(response,
        HttpStatus.SC_OK, "myItem01"));
    }
  }

  @Test
  @Order(80)
  public void testUpdateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getOrderByCustomerEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(OrderDTO.class))
        .thenCompose(orderDTO ->
        {
          assertThat(orderDTO).isNotNull();
          OrderDTO updatedOrder = new OrderDTO(orderDTO.id(), "myItem02",
            new BigDecimal("200.50"), orderDTO.customerId());
          return updateOrder(client, updatedOrder);
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response -> assertOrder(response, HttpStatus.SC_ACCEPTED, "myItem02"));
    }
  }

  @Test
  @Order(90)
  public void testDeleteOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getOrderByCustomerEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(OrderDTO.class))
        .thenCompose(orderDTO ->
        {
          assertThat(orderDTO).isNotNull();
          return deleteOrder(client, orderDTO);
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        response.close();
      });
    }
  }

  @Test
  @Order(100)
  public void testDeleteCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO -> deleteCustomer(client, customerDTO)
          .exceptionally(handleFailure()));
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        response.close();
      });
    }
  }*/
}
