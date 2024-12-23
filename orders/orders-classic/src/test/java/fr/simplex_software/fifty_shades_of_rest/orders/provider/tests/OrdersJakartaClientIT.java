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
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersJakartaClientIT
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
  public void testCreateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customer = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      Response response = client.target(customerSrvUri).request()
        .post(Entity.entity(customer, MediaType.APPLICATION_JSON));
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
      customer = response.readEntity(CustomerDTO.class);
      assertThat(customer).isNotNull();
      assertThat(customer.id()).isNotNull();
      response.close();
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Response response = client.target(customerSrvUri).request().get();
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
  public void testGetCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      assertThat(customerDTO.email()).isEqualTo("john.doe@email.com");
    }
  }

  @Test
  @Order(40)
  public void testUpdateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JOHN_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      CustomerDTO updatedCustomer = new CustomerDTO(customerDTO.id(),
        "Jane", "Doe",
        "jane.doe@email.com", "0987654321");
      Response response = client.target(customerSrvUri).request()
        .put(Entity.entity(updatedCustomer, MediaType.APPLICATION_JSON));
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
  public void testCreateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      OrderDTO order = new OrderDTO("myItem01",
        new BigDecimal("100.25"), customerDTO.id());
      Response response = client.target(orderSrvUri).request()
        .post(Entity.entity(order, MediaType.APPLICATION_JSON));
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
      response.close();
    }
  }

  @Test
  @Order(60)
  public void testGetOrders()
  {
    try (Client client = ClientBuilder.newClient())
    {
      OrderDTO[] orders = client.target(orderSrvUri).request().get(OrderDTO[].class);
      assertThat(orders).isNotNull();
      assertThat(orders).hasSize(1);
    }
  }

  @Test
  @Order(70)
  public void testGetOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      System.out.println("### customerId " + customerDTO.id());
      OrderDTO order = client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id()).request().get(OrderDTO.class);
      assertThat(order).isNotNull();
      assertThat(order.customerId()).isEqualTo(customerDTO.id());
    }
  }

  @Test
  @Order(80)
  public void testUpdateOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      OrderDTO orderDTO = client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id()).request().get(OrderDTO.class);
      assertThat(orderDTO).isNotNull();
      OrderDTO updatedOrder = new OrderDTO(orderDTO.id(), "myItem02",
        new BigDecimal("200.50"), orderDTO.customerId());
      Response response = client.target(orderSrvUri)
        .request()
        .put(Entity.json(updatedOrder));
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
  public void testDeleteOrder()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      OrderDTO orderDTO = client.target(orderSrvUri).path("{id}")
        .resolveTemplate("id", customerDTO.id()).request().get(OrderDTO.class);
      assertThat(orderDTO).isNotNull();
      Response response = client.target(orderSrvUri).request()
        .build("DELETE", Entity.entity(orderDTO, MediaType.APPLICATION_JSON)).invoke();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
      response.close();
    }
  }

  @Test
  @Order(100)
  public void testDeleteCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = client.target(customerSrvUri)
        .path("email/{email}").resolveTemplate("email", JANE_EMAIL)
        .request().get(CustomerDTO.class);
      assertThat(customerDTO).isNotNull();
      Response response = client.target(customerSrvUri).request()
        .build("DELETE", Entity.entity(customerDTO, MediaType.APPLICATION_JSON)).invoke();
      assertThat(response).isNotNull();
      assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
      response.close();
    }
  }
}
