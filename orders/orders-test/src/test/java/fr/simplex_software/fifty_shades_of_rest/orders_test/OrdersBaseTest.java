package fr.simplex_software.fifty_shades_of_rest.orders_test;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import io.restassured.http.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.net.*;
import java.nio.charset.*;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class OrdersBaseTest
{
  private static final String JOHN_EMAIL =
    URLEncoder.encode("john.doe@email.com", StandardCharsets.UTF_8);
  private static final String JANE_EMAIL =
    URLEncoder.encode("jane.doe@email.com", StandardCharsets.UTF_8);
  protected static String customersUrl;
  protected static String ordersUrl;

  @Test
  @Order(10)
  public void testCreateCustomer()
  {
    CustomerDTO customer = new CustomerDTO("John", "Doe",
      "john.doe@email.com", "1234567890");
    given().body(customer).contentType(ContentType.JSON)
      .when().log().all().post(customersUrl).then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_CREATED);
  }

  @Test
  @Order(20)
  public void testCreateOrder()
  {
    CustomerDTO customerDTO = given().basePath(customersUrl).when().log().all()
      .pathParam("email", JOHN_EMAIL)
      .get("/email/{email}").then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body().as(CustomerDTO.class);
    OrderDTO order = new OrderDTO("myItem01", new BigDecimal("100.25"), customerDTO.id());
    given().body(order).contentType(ContentType.JSON).when()
      .log().all()
      .post(ordersUrl).then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_CREATED);
  }

  /*@Test
  @Order(30)
  public void testGetOrders()
  {
    assertThat(given().when()
      .log().all().get(ordersUrl).then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body().as(OrderDTO[].class))
      .hasSize(1);
  }

  @Test
  @Order(40)
  public void testGetCustomers()
  {
    assertThat(given().when().log().all()
      .get(customersUrl).then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body()
      .as(CustomerDTO[].class)).hasSize(1);
  }

  @Test
  @Order(50)
  public void testUpdateCustomer()
  {
    CustomerDTO customerDTO = given().basePath(customersUrl).pathParam("email", JOHN_EMAIL)
      .when().log().all()
      .get("/email/{email}").then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK)
      .extract().body().as(CustomerDTO.class);
    assertThat(customerDTO).isNotNull();
    CustomerDTO updatedCustomer = new CustomerDTO(customerDTO.id(), "Jane", "Doe",
      "jane.doe@email.com", "0987654321");
    assertThat(given().body(updatedCustomer).contentType(ContentType.JSON).when()
      .log().all()
      .put(customersUrl).then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_ACCEPTED).extract().body()
      .as(CustomerDTO.class).firstName()).isEqualTo("Jane");
  }

  @Test
  @Order(60)
  public void testGetCustomer()
  {
    assertThat(given().basePath(customersUrl).pathParam("email", JANE_EMAIL).when()
      .log().all()
      .get("/email/{email}").then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body()
      .as(CustomerDTO.class).firstName()).isEqualTo("Jane");
  }

  @Test
  @Order(70)
  public void testGetOrderByCustomer()
  {
    CustomerDTO customerDTO = given().basePath(customersUrl).pathParam("email", JANE_EMAIL)
      .when().log().all().get("/email/{email}")
      .then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body().as(CustomerDTO.class);
    assertThat(customerDTO.firstName()).isEqualTo("Jane");
    OrderDTO orderDTO = given().basePath(ordersUrl).pathParam("id", customerDTO.id()).when()
      .log().all()
      .get("/customer/{id}")
      .then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body().as(OrderDTO[].class)[0];
    assertThat(orderDTO.item()).isEqualTo("myItem01");
  }

  @Test
  @Order(90)
  public void testDeleteCustomer()
  {
    CustomerDTO customerDTO = given().basePath(customersUrl).pathParam("email", JANE_EMAIL)
      .when().log().all().get("/email/{email}")
      .then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK).extract().body().as(CustomerDTO.class);
    given().body(customerDTO).contentType(ContentType.JSON).when()
      .log().all()
      .delete(customersUrl).then()
      .log().ifValidationFails()
      .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @Order(80)
  public void testDeleteOrder()
  {
    OrderDTO[] orders = given().when().log().all()
      .get(ordersUrl).then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_OK)
      .extract().body().as(OrderDTO[].class);
    assertThat(orders).hasSize(1);
    OrderDTO orderDTO = orders[0];
    given().body(orderDTO).when().log().all()
      .contentType(ContentType.JSON).delete(ordersUrl)
      .then().log().ifValidationFails()
      .statusCode(HttpStatus.SC_NO_CONTENT);
  }*/
}
