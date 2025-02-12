package fr.simplex_software.fifty_shades_of_rest.orders.oidc.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import io.quarkus.test.keycloak.client.*;
import io.restassured.http.*;
import io.restassured.specification.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class OrdersSecRestAssuredClientIT extends OrdersBaseTest
{
  private static KeycloakTestClient keycloakClient;

  @BeforeAll
  public static void beforeAll()
  {
    customersUrl = "/customers-sec";
    ordersUrl = "/orders-sec";
  }

  @AfterAll
  public static void afterAll()
  {
    customersUrl = null;
    ordersUrl = null;
  }

  @Override
  protected RequestSpecification getRequestSpec() {
    return given().contentType(ContentType.JSON)
      .auth()
      .oauth2(getAccessToken("alice"));
  }

  @Test
  public void testCreateCustomerFails()
  {
    CustomerDTO customer = new CustomerDTO("Nick", "Doe",
      "nick.doe@email.com", "1234567899");
    given().contentType(ContentType.JSON)
      .auth()
      .oauth2(getAccessToken("bob"))
      .body(customer)
      .when().log().all().post(customersUrl).then().log().ifValidationFails()
      .statusCode(403);
  }

  private String getAccessToken(String userName)
  {
    if (keycloakClient == null)
      keycloakClient = new KeycloakTestClient();
    return keycloakClient.getAccessToken(userName);
  }
}
