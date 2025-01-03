package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersSyncJava11ClientIT
{
  private final ObjectMapper objectMapper = new ObjectMapper();
  private HttpRequest.Builder builder =
    HttpRequest.newBuilder()
      .header("Content-Type", "application/json");
  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);

  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  URI customersUri;
  @TestHTTPEndpoint(OrderResource.class)
  @TestHTTPResource
  URI ordersUri;

  @Test
  @Order(10)
  public void testCreatCustomer() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customer = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      HttpResponse<String> response = httpClient.
        send(builder.uri(customersUri)
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(customer)))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
      customer = objectMapper.readValue(response.body(), CustomerDTO.class);
      assertThat(customer.id()).isNotNull();
      assertThat(customer.firstName()).isEqualTo("John");
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      HttpResponse<String> response = httpClient.send(builder.uri(customersUri)
        .GET().build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
      CustomerDTO[] customers =
        objectMapper.readValue(response.body(), CustomerDTO[].class);
      assertThat(customers.length).isEqualTo(1);
      assertThat(customers[0].firstName()).isEqualTo("John");
    }
  }

  @Test
  @Order(30)
  public void testGetCustomer() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      assertThat(getCustomerDtoByEmail(httpClient, JOHN_EMAIL)
        .firstName()).isEqualTo("John");
    }
  }

  @Test
  @Order(35)
  public void testUpdateCustomer() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JOHN_EMAIL);
      assertThat(customerDTO).isNotNull();
      customerDTO = new CustomerDTO(customerDTO.id(),
        "Jane", "Doe",
        "jane.doe@email.com", "0987654321");
      HttpResponse<String> response = httpClient.send(builder.uri(customersUri)
        .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(customerDTO)))
        .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
      customerDTO = objectMapper.readValue(response.body(), CustomerDTO.class);
      assertThat(customerDTO.firstName()).isEqualTo("Jane");
    }
  }

  @Test
  @Order(80)
  public void testDeleteCustomer() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JANE_EMAIL);
      assertThat(customerDTO).isNotNull();
      HttpResponse response = httpClient.send(builder.uri(customersUri)
        .method("DELETE",
          HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(customerDTO)))
        .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }
  }

  @Test
  @Order(40)
  public void testCreateOrder() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JANE_EMAIL);
      assertThat(customerDTO).isNotNull();
      OrderDTO order = new OrderDTO("myItem01",
        new BigDecimal("100.25"), customerDTO.id());
      HttpResponse<String> response = httpClient.
        send(builder.uri(ordersUri)
          .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(order)))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
      OrderDTO orderDTO = objectMapper.readValue(response.body(), OrderDTO.class);
      assertThat(orderDTO.id()).isNotNull();
      assertThat(orderDTO.item()).isEqualTo("myItem01");
    }
  }

  @Test
  @Order(50)
  public void testGetOrders() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      HttpResponse<String> response = httpClient.send(builder.uri(ordersUri)
        .GET().build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
      OrderDTO[] orders =
        objectMapper.readValue(response.body(), OrderDTO[].class);
      assertThat(orders.length).isEqualTo(1);
      assertThat(orders[0].item()).isEqualTo("myItem01");
    }
  }

  @Test
  @Order(60)
  public void testGetOrder() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JANE_EMAIL);
      List<OrderDTO> orderDTOs = getOrdersDtoByCustomerId(httpClient, customerDTO.id());
      assertThat(orderDTOs).isNotNull();
      assertThat(orderDTOs).hasSize(1);
      assertThat(orderDTOs.getFirst().customerId()).isEqualTo(customerDTO.id());
    }
  }

  @Test
  @Order(70)
  public void testUpdateOrder() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JANE_EMAIL);
      assertThat(customerDTO).isNotNull();
      List<OrderDTO> orderDTOs = getOrdersDtoByCustomerId(httpClient, customerDTO.id());
      assertThat(orderDTOs).isNotNull();
      assertThat(orderDTOs).hasSize(1);
      OrderDTO orderDTO = new OrderDTO(orderDTOs.getFirst().id(), "myItem02",
        new BigDecimal("298.65"), customerDTO.id());
      HttpResponse<String> response =
        httpClient.send(builder.uri(ordersUri)
          .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(orderDTO)))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
      orderDTO = objectMapper.readValue(response.body(), OrderDTO.class);
      assertThat(orderDTO.item()).isEqualTo("myItem02");
    }
  }

  @Test
  @Order(75)
  public void testDeleteOrder() throws Exception
  {
    try (HttpClient httpClient = HttpClient.newHttpClient())
    {
      CustomerDTO customerDTO = getCustomerDtoByEmail(httpClient, JANE_EMAIL);
      assertThat(customerDTO).isNotNull();
      List<OrderDTO> orderDTOs = getOrdersDtoByCustomerId(httpClient, customerDTO.id());
      assertThat(orderDTOs).isNotNull();
      assertThat(orderDTOs).hasSize(1);
      HttpResponse response =
        httpClient.send(builder.uri(ordersUri)
          .method("DELETE", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(orderDTOs.getFirst())))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }
  }

  private URI getEmailUri(String email) throws Exception
  {
    return UriBuilder.fromPath(customersUri.toString())
      .path("email")
      .path("{email}")
      .build(email);
  }

  private CustomerDTO getCustomerDtoByEmail(HttpClient client, String email) throws Exception
  {
    HttpResponse<String> response = client
      .send(builder.uri(getEmailUri(email))
        .GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    return
      objectMapper.readValue(response.body(), CustomerDTO.class);
  }

  private List<OrderDTO> getOrdersDtoByCustomerId(HttpClient client, Long customerId) throws Exception
  {
    URI uri = UriBuilder.fromPath(ordersUri.toString())
      .path("customer")
      .path("{customerId}")
      .build(customerId);
    HttpResponse<String> response = client.send(builder.uri(uri)
      .GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    return objectMapper.readValue(response.body(), new TypeReference<List<OrderDTO>>() {});
  }
}