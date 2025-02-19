package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.slf4j.*;

import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public final class OrdersJava8AsyncCommon
{
  private static URI customerSrvUri;
  private static URI orderSrvUri;
  public static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  public static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);
  private static final Logger LOGGER = LoggerFactory.getLogger(OrdersJava8AsyncCommon.class.getName());

  private OrdersJava8AsyncCommon()
  {
    throw new AssertionError("Utility class - should not be instantiated");
  }

  public static void withCustomerUri(URI uri)
  {
    if (uri == null)
      throw new IllegalArgumentException("Customer URI cannot be null");
    customerSrvUri = uri;
  }

  public static void withOrderUri(URI uri)
  {
    if (uri == null)
      throw new IllegalArgumentException("Order URI cannot be null");
    orderSrvUri = uri;
  }

  public static CompletableFuture<Response> createCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request()
          .post(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> createCustomerRx(Client client, CustomerDTO customerDTO)
  {
    return client.target(customerSrvUri)
      .request()
      .rx()
      .post(Entity.entity(customerDTO, MediaType.APPLICATION_JSON))
      .toCompletableFuture()
      .exceptionally(throwable -> handleFailure().apply(throwable));
  }

  public static CompletableFuture<Response> getCustomers(Client client)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request().get())
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> getCustomersRx(Client client)
  {
    return client.target(customerSrvUri)
      .request()
      .rx()
      .get()
      .toCompletableFuture()
      .exceptionally(throwable -> handleFailure().apply(throwable));
  }


  public static void assertCustomers(Response response)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO[] customers = response.readEntity(CustomerDTO[].class);
    assertThat(customers).isNotNull();
    assertThat(customers).hasAtLeastOneElementOfType(CustomerDTO.class);
  }

  public static CompletableFuture<Response> updateCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request()
          .put(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> updateCustomerRx(Client client, CustomerDTO customerDTO)
  {
    return client.target(customerSrvUri).request().rx()
      .put(Entity.entity(customerDTO, MediaType.APPLICATION_JSON))
      .toCompletableFuture()
      .exceptionally(throwable -> handleFailure().apply(throwable));
  }


  public static CompletableFuture<Response> getCustomerByEmail(Client client, String email)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri)
          .path("email/{email}").resolveTemplate("email", email)
          .request().get())
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> getCustomerByEmailRx(Client client, String email)
  {
    return client.target(customerSrvUri)
      .path("email/{email}").resolveTemplate("email", email)
      .request()
      .rx()
      .get()
      .toCompletableFuture()
      .exceptionally(throwable -> handleFailure().apply(throwable));
   }


  public static void assertCustomer(Response response, int status, String firstName)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(status);
    CustomerDTO customer = response.readEntity(CustomerDTO.class);
    assertThat(customer).isNotNull();
    assertThat(customer.firstName()).isEqualTo(firstName);
  }

  public static CompletableFuture<Response> createOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(orderSrvUri).request()
          .post(Entity.entity(orderDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  public static void assertOrder(Response response, int status, String item)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(status);
    OrderDTO order = response.readEntity(OrderDTO.class);
    assertThat(order).isNotNull();
    assertThat(order.item()).isEqualTo(item);
  }

  public static CompletableFuture<Response> getOrders(Client client)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(orderSrvUri).request().get())
      .exceptionally(handleFailure());
  }

  public static void assertOrders(Response response)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    OrderDTO[] orders = response.readEntity(OrderDTO[].class);
    assertThat(orders).isNotNull();
    assertThat(orders).hasAtLeastOneElementOfType(OrderDTO.class);
  }

  public static CompletableFuture<Response> getOrderByCustomerEmail(Client client, String email)
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

  public static CompletableFuture<Response> updateOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(orderSrvUri).request()
          .put(Entity.entity(orderDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> deleteOrder(Client client, OrderDTO orderDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(orderSrvUri).request()
          .build("DELETE", Entity.entity(orderDTO, MediaType.APPLICATION_JSON))
          .invoke())
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> deleteCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request()
          .build("DELETE", Entity.entity(customerDTO, MediaType.APPLICATION_JSON))
          .invoke())
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> deleteCustomerRx(Client client, CustomerDTO customerDTO)
  {
    return client.target(customerSrvUri).request().rx().delete()
      .toCompletableFuture()
      .exceptionally(throwable -> handleFailure().apply(throwable));
  }

  public static CompletableFuture<Response> getOrdersByCustomerId(Client client, Long id)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(orderSrvUri).path("{id}")
          .resolveTemplate("id", id).request().get())
      .exceptionally(handleFailure());
  }

  public static Function<Throwable, Response> handleFailure()
  {
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    return ex ->
    {
      String msg = "### OrdersBaseJava8AsyncBlockingClient.%s(): %s".formatted(methodName, ex);
      LOGGER.error(msg);
      return CompletableFuture.<Response>failedFuture(new Throwable(msg)).join();
    };
  }
}
