package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import jakarta.ws.rs.core.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractOrdersApiClient
{
  protected abstract BaseCustomerApiClient getCustomerApiClient();
  protected abstract BaseOrderApiClient getOrderApiClient();

  @Test
  @Order(10)
  public void testCreateCustomer()
  {
    CustomerDTO customer = new CustomerDTO("John", "Doe",
      "john.doe@email.com", "1234567890");
    assertThat(getCustomerApiClient().createCustomer(customer).getStatus())
      .isEqualTo(HttpStatus.SC_CREATED);
  }

  @Test
  @Order(20)
  public void testCreateOrder()
  {
    Response response = getCustomerApiClient()
      .getCustomerByEmail("john.doe@email.com");
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO customerDTO = response.readEntity(CustomerDTO.class);
    assertThat(customerDTO).isNotNull();
    OrderDTO order = new OrderDTO("myItem01",
      new BigDecimal("100.25"), customerDTO.id());
    assertThat(getOrderApiClient().createOrder(order).getStatus())
      .isEqualTo(HttpStatus.SC_CREATED);
  }

  @Test
  @Order(30)
  public void testGetOrders()
  {
    Response response = getOrderApiClient().getOrders();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    OrderDTO[] orders = getOrderApiClient().getOrders().readEntity(OrderDTO[].class);
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(1);
  }

  @Test
  @Order(40)
  public void testGetCustomers()
  {
    Response response = getCustomerApiClient().getCustomers();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO[] customers = getCustomerApiClient().getCustomers()
      .readEntity(CustomerDTO[].class);
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(1);
  }

  @Test
  @Order(50)
  public void testUpdateCustomer()
  {
    Response response = getCustomerApiClient()
      .getCustomerByEmail("john.doe@email.com");
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO customerDTO = response.readEntity(CustomerDTO.class);
    assertThat(customerDTO).isNotNull();
    CustomerDTO updatedCustomer =
      new CustomerDTO(customerDTO.id(), "Jane", "Doe",
      "jane.doe@email.com", "0987654321");
    response = getCustomerApiClient().updateCustomer(updatedCustomer);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_ACCEPTED);
    CustomerDTO updatedCustomerDTO = response.readEntity(CustomerDTO.class);
    assertThat(updatedCustomerDTO).isNotNull();
    assertThat(updatedCustomerDTO.firstName()).isEqualTo("Jane");
  }

  @Test
  @Order(60)
  public void testGetCustomer()
  {
    Response response = getCustomerApiClient()
      .getCustomerByEmail("jane.doe@email.com");
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO customerDTO = response.readEntity(CustomerDTO.class);
    assertThat(customerDTO).isNotNull();
    assertThat(customerDTO.firstName()).isEqualTo("Jane");
  }

  @Test
  @Order(70)
  public void testGetOrderByCustomer()
  {
    Response response = getCustomerApiClient()
      .getCustomerByEmail("jane.doe@email.com");
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO customerDTO = response.readEntity(CustomerDTO.class);
    assertThat(customerDTO.firstName()).isEqualTo("Jane");
    response = getOrderApiClient().getOrdersByCustomer(customerDTO.id());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    OrderDTO[] orders = response.readEntity(OrderDTO[].class);
    assertThat(orders).isNotNull();
    assertThat(orders).hasSize(1);
    assertThat(orders[0].item()).isEqualTo("myItem01");
  }

  @Test
  @Order(90)
  public void testDeleteCustomer()
  {
    Response response = getCustomerApiClient()
      .getCustomerByEmail("jane.doe@email.com");
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    CustomerDTO customerDTO = response.readEntity(CustomerDTO.class);
    assertThat(customerDTO).isNotNull();
    response = getCustomerApiClient().deleteCustomer(customerDTO);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @Order(80)
  public void testDeleteOrder()
  {
    Response response = getOrderApiClient().getOrders();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
    OrderDTO[] orders = response.readEntity(OrderDTO[].class);
    assertThat(orders).hasSize(1);
    OrderDTO orderDTO = orders[0];
    response = getOrderApiClient().deleteOrder(orderDTO);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_NO_CONTENT);
  }
}
