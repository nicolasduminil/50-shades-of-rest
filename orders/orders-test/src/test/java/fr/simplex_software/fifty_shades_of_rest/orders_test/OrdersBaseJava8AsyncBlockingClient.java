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
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri).request()
            .post(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
        CustomerDTO newCustomerDTO = response.readEntity(CustomerDTO.class);
        assertThat(newCustomerDTO).isNotNull();
        assertThat(newCustomerDTO.id()).isNotNull();
        response.close();
      });
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri).request().get())
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        CustomerDTO[] customers = response.readEntity(CustomerDTO[].class);
        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(1);
        assertThat(customers[0].firstName()).isEqualTo("John");
        response.close();
      });
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
            .request().get())
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        CustomerDTO customer = response.readEntity(CustomerDTO.class);
        assertThat(customer).isNotNull();
        assertThat(customer.firstName()).isEqualTo("John");
        response.close();
      });
    }
  }

  @Test
  @Order(40)
  public void testUpdateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
            .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          CustomerDTO updatedCustomerDTO = new CustomerDTO(customerDTO.id(),
            "Jane", "Doe",
            "jane.doe@email.com", "0987654321");
          return CompletableFuture.supplyAsync(() ->
            client.target(customerSrvUri).request()
              .put(Entity.entity(updatedCustomerDTO, MediaType.APPLICATION_JSON)));
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
        CustomerDTO updatedCustomerDTO = response.readEntity(CustomerDTO.class);
        assertThat(updatedCustomerDTO).isNotNull();
        assertThat(updatedCustomerDTO.firstName()).isEqualTo("Jane");
        response.close();
      });
    }
  }

  @Test
  @Order(50)
  public void testCreateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture
        .supplyAsync(() -> client.target(customerSrvUri)
          .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
          .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          OrderDTO order = new OrderDTO("myItem01",
            new BigDecimal("100.25"), customerDTO.id());
          return CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri).request()
              .post(Entity.entity(order, MediaType.APPLICATION_JSON)));
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        int status = response.getStatus();
        assertThat(status).isEqualTo(HttpStatus.SC_CREATED);
      });
    }
  }

  @Test
  @Order(60)
  public void testGetOrders()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(orderSrvUri).request().get())
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        OrderDTO[] orders = response.readEntity(OrderDTO[].class);
        assertThat(orders).isNotNull();
        assertThat(orders).hasSize(1);
      });
    }
  }

  @Test
  @Order(70)
  public void testGetOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
            .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
          CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri).path("{id}")
              .resolveTemplate("id", customerDTO.id()).request()
              .get()))
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        OrderDTO order = response.readEntity(OrderDTO.class);
        assertThat(order).isNotNull();
        assertThat(order.item()).isEqualTo("myItem01");
      });
    }
  }

  @Test
  @Order(80)
  public void testUpdateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
            .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          assertThat(customerDTO).isNotNull();
          return CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri).path("{id}")
              .resolveTemplate("id", customerDTO.id()).request()
              .get());
        })
        .thenApply(response -> response.readEntity(OrderDTO.class))
        .thenCompose(orderDTO ->
        {
          assertThat(orderDTO).isNotNull();
          OrderDTO updatedOrder = new OrderDTO(orderDTO.id(), "myItem02",
            new BigDecimal("200.50"), orderDTO.customerId());
          return CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri)
              .request().put(Entity.entity(updatedOrder, MediaType.APPLICATION_JSON)));
        })
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        OrderDTO updatedOrderDTO = response.readEntity(OrderDTO.class);
        assertThat(updatedOrderDTO).isNotNull();
        assertThat(updatedOrderDTO.item()).isEqualTo("myItem02");
        response.close();
      });
    }
  }

  @Test
  @Order(90)
  public void testDeleteOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
            .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
        {
          assertThat(customerDTO).isNotNull();
          return CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri).path("{id}")
              .resolveTemplate("id", customerDTO.id()).request()
              .get());
        })
        .thenCompose(orderDTO ->
        {
          assertThat(orderDTO).isNotNull();
          return CompletableFuture.supplyAsync(() ->
            client.target(orderSrvUri)
              .request()
              .build("DELETE", Entity.entity(orderDTO, MediaType.APPLICATION_JSON))
              .invoke());
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
      CompletableFuture<Response> futureResponse = CompletableFuture.supplyAsync(() ->
          client.target(customerSrvUri)
            .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
            .request().get())
        .thenApply(response -> response.readEntity(CustomerDTO.class))
        .thenCompose(customerDTO ->
          CompletableFuture.supplyAsync(() ->
            client.target(customerSrvUri).request()
              .build("DELETE", Entity.entity(customerDTO, MediaType.APPLICATION_JSON))
              .invoke()))
        .exceptionally(handleFailure());
      futureResponse.thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        response.close();
      });
    }
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
