package fr.simplex_software.fifty_shades_of_rest.orders.jwt.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import io.restassured.http.*;
import io.restassured.specification.*;
import io.smallrye.jwt.build.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class OrdersJwtRestAssuredClientIT extends OrdersBaseTest
{
  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;
  @ConfigProperty(name = "mp.jwt.verify.upn")
  String upn;

  @BeforeAll
  public static void beforeAll()
  {
    customersUrl = "/customers-jwt";
    ordersUrl = "/orders-jwt";
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
      .oauth2(getAccessToken("Admin"));
  }

  @Test
  public void testCreateCustomerFails()
  {
    CustomerDTO customer = new CustomerDTO("Nick", "Doe",
      "nick.doe@email.com", "1234567899");
    given().contentType(ContentType.JSON)
      .auth()
      .oauth2(getAccessToken("User"))
      .body(customer)
      .when().log().all().post(customersUrl).then().log().ifValidationFails()
      .statusCode(403);
  }

  private String getAccessToken(String role)
  {
    return Jwt.upn(upn)
      .issuer(issuer)
      .groups(role)
      .sign();
  }
}
