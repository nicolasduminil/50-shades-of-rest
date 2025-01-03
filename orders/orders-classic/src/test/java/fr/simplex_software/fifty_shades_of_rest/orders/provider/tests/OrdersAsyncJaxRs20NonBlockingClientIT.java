package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.net.*;
import java.nio.charset.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersAsyncJaxRs20NonBlockingClientIT
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  URI customerSrvUri;

  @TestHTTPEndpoint(OrderResource.class)
  @TestHTTPResource
  URI orderSrvUri;

  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  @Test
  @Order(5)
  public void testURIs()
  {
    assertThat(customerSrvUri).isNotNull();
    assertThat(orderSrvUri).isNotNull();
  }

  @Test
  @Order(10)
  public void testCreateCustomer() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customer = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      client.target(customerSrvUri).request().async()
        .post(Entity.entity(customer, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
      customer = response.readEntity(CustomerDTO.class);
      assertThat(customer).isNotNull();
      assertThat(customer.id()).isNotNull();
      response.close();
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri).request().async().get(callback);
      Response response = callback.getResponse();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
      CustomerDTO[] customers = response.readEntity(CustomerDTO[].class);
      assertThat(customers).isNotNull();
      assertThat(customers).hasSize(1);
      assertThat(customers[0].firstName()).isEqualTo("John");
      response.close();
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      assertThat(customerDTO.email()).isEqualTo("john.doe@email.com");
    }
  }

  @Test
  @Order(40)
  public void testUpdateCustomer() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      CustomerDTO updatedCustomer = new CustomerDTO(customerDTO.id(),
        "Jane", "Doe",
        "jane.doe@email.com", "0987654321");
      callback = new Callback();
      client.target(customerSrvUri).request()
        .async().put(Entity.entity(updatedCustomer, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
      CustomerDTO updatedCustomerDTO = response.readEntity(CustomerDTO.class);
      assertThat(updatedCustomerDTO).isNotNull();
      assertThat(updatedCustomerDTO.firstName()).isEqualTo("Jane");
      response.close();
    }
  }

  @Test
  @Order(50)
  public void testCreateOrder() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      OrderDTO order = new OrderDTO("myItem01",
        new BigDecimal("100.25"), customerDTO.id());
      callback = new Callback();
      client.target(orderSrvUri).request()
        .async().post(Entity.entity(order, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
      response.close();
    }
  }

  @Test
  @Order(60)
  public void testGetOrders() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(orderSrvUri).request().async().get(callback);
      OrderDTO[] orders = callback.getResponse().readEntity(OrderDTO[].class);
      assertThat(orders).isNotNull();
      assertThat(orders).hasSize(1);
    }
  }

  @Test
  @Order(70)
  public void testGetOrder() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      callback = new Callback();
      client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id()).request()
        .async().get(callback);
      OrderDTO order = callback.getResponse().readEntity(OrderDTO.class);
      assertThat(order).isNotNull();
      assertThat(order.customerId()).isEqualTo(customerDTO.id());
    }
  }

  @Test
  @Order(80)
  public void testUpdateOrder() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      callback = new Callback();
      client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id()).request()
        .async().get(callback);
      OrderDTO orderDTO = callback.getResponse().readEntity(OrderDTO.class);
      assertThat(orderDTO).isNotNull();
      OrderDTO updatedOrder = new OrderDTO(orderDTO.id(), "myItem02",
        new BigDecimal("200.50"), orderDTO.customerId());
      callback = new Callback();
      client.target(orderSrvUri)
        .request().async().put(Entity.entity(updatedOrder, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
      OrderDTO updatedOrderDTO = response.readEntity(OrderDTO.class);
      assertThat(updatedOrderDTO).isNotNull();
      assertThat(updatedOrderDTO.item()).isEqualTo("myItem02");
      response.close();
    }
  }

  @Test
  @Order(90)
  public void testDeleteOrder() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      callback = new Callback();
      client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id())
        .request().async().get(callback);
      OrderDTO orderDTO = callback.getResponse().readEntity(OrderDTO.class);
      assertThat(orderDTO).isNotNull();
      callback = new Callback();
      client.target(orderSrvUri).request().async()
        .method("DELETE", Entity.entity(orderDTO, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
      response.close();
    }
  }

  @Test
  @Order(100)
  public void testDeleteCustomer() throws Exception
  {
    Callback callback = new Callback();
    try (Client client = ClientBuilder.newClient())
    {
      client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().async().get(callback);
      CustomerDTO customerDTO = callback.getResponse().readEntity(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      callback = new Callback();
      client.target(customerSrvUri).request().async()
        .method("DELETE", Entity.entity(customerDTO, MediaType.APPLICATION_JSON), callback);
      Response response = callback.getResponse();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
      response.close();
    }
  }
}
