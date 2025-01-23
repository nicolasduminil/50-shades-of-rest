package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;

import java.net.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public final class OrdersJava8AsyncCommon
{
  private static URI customerSrvUri;
  private static URI orderSrvUri;

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

  public static CompletableFuture<Response> customerCreate(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request()
          .post(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }


  public static CompletableFuture<Response> getCustomers(Client client)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request().get())
      .exceptionally(handleFailure());
  }

  public static void assertCustomers(Response response)
  {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO[] customers = response.readEntity(CustomerDTO[].class);
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(1);
  }

  public static CompletableFuture<Response> updateCustomer(Client client, CustomerDTO customerDTO)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri).request()
          .put(Entity.entity(customerDTO, MediaType.APPLICATION_JSON)))
      .exceptionally(handleFailure());
  }

  public static CompletableFuture<Response> getCustomerByEmail(Client client, String email)
  {
    return CompletableFuture.supplyAsync(() ->
        client.target(customerSrvUri)
          .path("email/{email}").resolveTemplate("email", email)
          .request().get())
      .exceptionally(handleFailure());
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
    assertThat(orders).hasSize(1);
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
    return ex -> CompletableFuture.<Response>failedFuture(
      new CompletionException(String.format("""
        ### OrdersBaseJava8AsyncBlockingClient.%s():
        %s""", methodName, ex.getMessage()), ex)
    ).join();
  }
}
