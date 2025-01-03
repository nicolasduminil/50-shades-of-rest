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
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OrdersBaseJava8AsyncBlockingClient
{
  protected URI customerSrvUri;
  protected URI orderSrvUri;
  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  public abstract URI getCustomerSrvUri();
  public abstract URI getOrderSrvUri();

  @BeforeAll
  public void beforeAll()
  {
    customerSrvUri = getCustomerSrvUri();
    orderSrvUri = getOrderSrvUri();
  }

  @AfterAll
  public void afterAll()
  {
    customerSrvUri = null;
    orderSrvUri = null;
  }

  @Test
  @Order(5)
  public void testURIs()
  {
    assertThat(customerSrvUri).isNotNull();
    assertThat(orderSrvUri).isNotNull();
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
      futureResponse.thenAccept(response -> assertCustomer(response, HttpStatus.SC_CREATED, "John"));
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomers(client);
      futureResponse.thenAccept(this::assertCustomers);
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JOHN_EMAIL);
      futureResponse.thenAccept(response -> assertCustomer(response, HttpStatus.SC_OK, "John"));
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
          return updateCustomer (client, updatedCustomerDTO);
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response -> assertCustomer(response,
        HttpStatus.SC_ACCEPTED, "Jane"));
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
      futureResponse.thenAccept(this::assertOrders);
    }
  }

  @Test
  @Order(70)
  public void testGetOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getCustomerByEmail(client, JANE_EMAIL)
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
          CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri).path("{id}")
              .resolveTemplate("id", customerDTO.id()).request()
              .get()))
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response -> assertOrder(response, HttpStatus.SC_OK, "myItem01"));
    }
  }

  @Test
  @Order(80)
  public void testUpdateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = getOrderByCustomerEmail(client, JANE_EMAIL)        .thenApply(response -> response.readEntity(OrderDTO.class))
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
      futureResponse.thenAccept(response -> {
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
  }

  private CompletableFuture<Response> createCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(customerSrvUri).request()
        .post(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  private CompletableFuture<Response> getCustomers(Client client)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(customerSrvUri).request().get())
      .exceptionally(handleFailure());
  }

  private void assertCustomers(Response response)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO[] customers = response.readEntity(CustomerDTO[].class);
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(1);
    response.close();
  }

  private CompletableFuture<Response> updateCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(customerSrvUri).request()
        .put(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  private CompletableFuture<Response> getCustomerByEmail (Client client, String email)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", email)
        .request().get())
      .exceptionally(handleFailure());
  }

  private void assertCustomer(Response response, int status, String firstName)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(status);
    CustomerDTO customer = response.readEntity(CustomerDTO.class);
    assertThat(customer).isNotNull();
    assertThat(customer.firstName()).isEqualTo(firstName);
    response.close();
  }

  private CompletableFuture<Response> createOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(orderSrvUri).request()
        .post(Entity.entity(orderDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  private void assertOrder(Response response, int status, String item)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(status);
    OrderDTO order = response.readEntity(OrderDTO.class);
    assertThat(order).isNotNull();
    assertThat(order.item()).isEqualTo(item);
    response.close();
  }

  private CompletableFuture<Response> getOrders(Client client)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(orderSrvUri).request().get())
      .exceptionally(handleFailure());
  }

  private void assertOrders(Response response)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    OrderDTO[] orders = response.readEntity(OrderDTO[].class);
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(1);
    response.close();
  }

  private CompletableFuture<Response> getOrderByCustomerEmail(Client client, String email)
  {
    return getCustomerByEmail(client, email)
      .thenApply(response -> response.readEntity(CustomerDTO.class))
      .thenCompose(customerDTO ->
        CompletableFuture.supplyAsync(() ->
          client.target(orderSrvUri).path("{id}")
            .resolveTemplate("id", customerDTO.id()).request()
            .get()))
      .exceptionally(handleFailure());
  }

  private CompletableFuture<Response> updateOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(orderSrvUri).request()
        .put(Entity.entity(orderDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  private CompletableFuture<Response> deleteOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(orderSrvUri).request()
        .build("DELETE", Entity.entity(orderDTO, MediaType.APPLICATION_JSON))
        .invoke())
      .exceptionally(handleFailure());
  }

  private CompletableFuture<Response> deleteCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
      client.target(customerSrvUri).request()
        .build("DELETE", Entity.entity(customerDTO, MediaType.APPLICATION_JSON))
        .invoke())
      .exceptionally(handleFailure());
  }

  private Function<Throwable, Response> handleFailure()
  {
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    return ex -> CompletableFuture.<Response>failedFuture(
      new CompletionException(String.format("""
        ### OrdersBaseJava8AsyncBlockingClient.%s():
        %s""", methodName, ex.getMessage()), ex)
    ).join();
  }
}
