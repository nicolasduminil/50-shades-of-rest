package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

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

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;
import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersAsyncMpClientIT
{
  @Inject
  @RestClient
  OrderAsyncApiClient orderAsyncApiClient;
  @Inject
  @RestClient
  CustomerAsyncApiClient customerAsyncApiClient;

  /*@Test
  @Order(10)
  public void testCreateCustomer()
  {
    CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
      "john.doe@email.com", "1234567890");
    assertCustomer(customerAsyncApiClient.createCustomer(customerDTO).toCompletableFuture().join(),
      HttpStatus.SC_CREATED, "John");
  }

  @Test
  @Order(20)
  public void testCreateOrder()
  {
    Long customerDTOID = customerAsyncApiClient.getCustomerByEmail("john.doe@email.com")
      .toCompletableFuture().join().readEntity(CustomerDTO.class).id();
    assertThat(customerDTOID).isNotNull();
    OrderDTO orderDTO = new OrderDTO("myItem01", new BigDecimal("100.25"), customerDTOID);
    assertOrder(orderAsyncApiClient.createOrder(orderDTO).toCompletableFuture().join(),
      HttpStatus.SC_CREATED, "myItem01");
  }

  @Test
  @Order(30)
  public void testGetOrders()
  {
    assertOrders(orderAsyncApiClient.getOrders().toCompletableFuture().join());
  }

  @Test
  @Order(40)
  public void testGetCustomers()
  {
    assertCustomers(customerAsyncApiClient.getCustomers().toCompletableFuture().join());
  }

  @Test
  @Order(50)
  public void testUpdateCustomer()
  {
    Long customerDTOID = customerAsyncApiClient.getCustomerByEmail("john.doe@email.com")
      .toCompletableFuture().join().readEntity(CustomerDTO.class).id();
    assertThat(customerDTOID).isNotNull();
    CustomerDTO updatedCustomer =
      new CustomerDTO(customerDTOID, "Jane", "Doe",
        "jane.doe@email.com", "0987654321");
    assertCustomer(customerAsyncApiClient.updateCustomer(updatedCustomer).toCompletableFuture().join(),
      HttpStatus.SC_ACCEPTED, "Jane");
  }

  @Test
  @Order(60)
  public void testGetCustomer()
  {
    assertCustomer(customerAsyncApiClient.getCustomerByEmail("jane.doe@email.com")
      .toCompletableFuture().join(), HttpStatus.SC_OK, "Jane");
  }

  @Test
  @Order(70)
  public void testGetOrderByCustomer()
  {
    Long customerDTOID = customerAsyncApiClient.getCustomerByEmail("jane.doe@email.com")
      .toCompletableFuture().join().readEntity(CustomerDTO.class).id();
    assertThat(customerDTOID).isNotNull();
    assertOrders(orderAsyncApiClient.getOrdersByCustomer(customerDTOID).toCompletableFuture().join());
  }

  @Test
  @Order(90)
  public void testDeleteCustomer()
  {
    CustomerDTO customerDTO = customerAsyncApiClient.getCustomerByEmail("jane.doe@email.com")
      .toCompletableFuture().join().readEntity(CustomerDTO.class);
    customerAsyncApiClient.deleteCustomer(customerDTO).toCompletableFuture()
      .thenAccept(response -> assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT))
      .join();
  }

  @Test
  @Order(80)
  public void testDeleteOrder()
  {
    orderAsyncApiClient.getOrders()
      .thenAccept(response ->
      {
        assertOrders(response);
        List<OrderDTO> orderDTOs = response.readEntity(new GenericType<>()
        {
        });
        orderAsyncApiClient.deleteOrder(orderDTOs.getFirst())
          .toCompletableFuture()
          .thenAccept(response1 -> assertThat(response1.getStatus())
            .isEqualTo(HttpStatus.SC_NO_CONTENT))
          .join();
      });
  }*/
}
