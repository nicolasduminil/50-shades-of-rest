package fr.simplex_software.fifty_shades_of_rest.orders.async.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersAsyncJaxRs20MpClientIT
{
  private static Long customerId;
  private static Long orderId;

  @Inject
  @RestClient
  CustomerAsyncApiClient customerResourceClient;
  @Inject
  @RestClient
  OrderAsyncApiClient orderResourceClient;

  @Test
  @Order(10)
  public void testCreateCustomer()
  {
    CustomerDTO customer = new CustomerDTO("John", "Doe","john.doe@email.com", "1234567890");
    customerResourceClient.createCustomer(customer)
      .thenAccept(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
          CustomerDTO createdCustomer = r.readEntity(CustomerDTO.class);
          assertThat(createdCustomer).isNotNull();
          assertThat(createdCustomer.id()).isNotNull();
          customerId = createdCustomer.id();
        }
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    customerResourceClient.getCustomers()
      .thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        List<CustomerDTO> customers = response.readEntity(new GenericType<>()
        {
        });
        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(1);
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(30)
  public void testGetCustomer()
  {
    customerResourceClient.getCustomer(customerId)
      .thenAccept(response ->
      {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        CustomerDTO customer = response.readEntity(CustomerDTO.class);
        assertThat(customer).isNotNull();
        assertThat(customer.id()).isEqualTo(customerId);
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(40)
  public void testUpdateCustomer()
  {
    customerResourceClient.getCustomer(customerId)
      .thenCompose(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
          CustomerDTO customer = r.readEntity(CustomerDTO.class);
          CustomerDTO updatedCustomer = new CustomerDTO(customer.id(), "Jane", "Doe", "jane.doe@email.com", "0987654321");
          return customerResourceClient.updateCustomer(updatedCustomer);
        }
      })
      .thenAccept(updateResponse -> {
        try (Response r = updateResponse) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
          CustomerDTO updatedCustomer = r.readEntity(CustomerDTO.class);
          assertThat(updatedCustomer).isNotNull();
          assertThat(updatedCustomer.firstName()).isEqualTo("Jane");
        }
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(60)
  public void testCreateOrder()
  {
    OrderDTO order = new OrderDTO("1234567890", new BigDecimal("100.00"), customerId);
    orderResourceClient.createOrder(order)
      .thenAccept(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
          OrderDTO createdOrder = r.readEntity(OrderDTO.class);
          assertThat(createdOrder).isNotNull();
          assertThat(createdOrder.id()).isNotNull();
          orderId = createdOrder.id();
        }
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(70)
  public void testGetOrders()
  {
    orderResourceClient.getOrders()
      .thenAccept(r ->
      {
        assertThat(r).isNotNull();
        assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
        List<OrderDTO> orders = r.readEntity(new GenericType<>()
        {
        });
        assertThat(orders).isNotNull();
        assertThat(orders).hasSize(1);
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(80)
  public void testGetOrder()
  {
    orderResourceClient.getOrder(orderId)
      .thenAccept(r ->
      {
        assertThat(r).isNotNull();
        assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
        OrderDTO order = r.readEntity(OrderDTO.class);
        assertThat(order).isNotNull();
        assertThat(order.id()).isEqualTo(orderId);
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(90)
  public void testUpdateOrder()
  {
    orderResourceClient.getOrder(orderId)
      .thenCompose(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
          OrderDTO order = r.readEntity(OrderDTO.class);
          OrderDTO updatedOrder = new OrderDTO(order.id(), "myItem204", BigDecimal.valueOf(305.18), customerId);
          return orderResourceClient.updateOrder(updatedOrder);
        }
      })
      .thenAccept(updateResponse -> {
        try (Response r = updateResponse) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
          OrderDTO updatedOrder = r.readEntity(OrderDTO.class);
          assertThat(updatedOrder).isNotNull();
          assertThat(updatedOrder.item()).isEqualTo("myItem204");
        }
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(110)
  public void testGetOrdersByCustomer()
  {
    orderResourceClient.getOrdersByCustomer(customerId)
      .thenAccept(r ->
      {
        assertThat(r).isNotNull();
        assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
        List<OrderDTO> orders = r.readEntity(new GenericType<>()
        {
        });
        assertThat(orders).isNotNull();
        assertThat(orders).hasSize(1);
      })
      .exceptionally(ex -> {
        System.out.println("### Unexpected exception " + ex.getMessage());
        return null;
      })
      .toCompletableFuture().join();
  }

  @Test
  @Order(120)
  public void testDeleteOrder()
  {
    orderResourceClient.getOrder(orderId)
      .thenCompose(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
          OrderDTO order = r.readEntity(OrderDTO.class);
          return orderResourceClient.deleteOrder(order);
        }
      })
      .thenAccept(r ->
        assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT));
  }

  @Test
  @Order(130)
  public void testDeleteCustomer()
  {
    customerResourceClient.getCustomer(customerId)
      .thenCompose(response -> {
        try (Response r = response) {
          assertThat(r).isNotNull();
          assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_OK);
          CustomerDTO customer = r.readEntity(CustomerDTO.class);
          return customerResourceClient.deleteCustomer(customer);
        }
      })
      .thenAccept(r ->
        assertThat(r.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT));
  }
}
