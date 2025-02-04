package fr.simplex_software.fifty_shades_of_rest.orders.react.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import io.restassured.*;
import io.restassured.http.*;
import io.restassured.parsing.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class OrdersReactRestAssuredClientIT extends OrdersBaseTest
{
  @BeforeAll
  public static void beforeAll()
  {
    customersUrl = "/customers-react";
    ordersUrl = "/orders-react";
  }

  @AfterAll
  public static void afterAll()
  {
    customersUrl = null;
    ordersUrl = null;
  }
  /*@Test
  public void testGetCustomers()
  {
    CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
      "john.doe@email.com", "921-018765");
    given()
      .when()
      .body(customerDTO)
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .post("/customers-react")
      .then()
      .log().ifValidationFails() // Log response if test fails
      .statusCode(201)
      .contentType(ContentType.JSON)
      .extract()
      .as(CustomerDTO.class);
  }*/
}
