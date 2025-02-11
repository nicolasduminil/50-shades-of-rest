package fr.simplex_software.fifty_shades_of_rest.orders.oidc.tests;

import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import io.restassured.http.*;
import io.restassured.specification.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class OrdersSecRestAssuredClientIT extends OrdersBaseTest
{
  @ConfigProperty(name = "quarkus.oidc.client-id")
  String clientId;
  @ConfigProperty(name = "quarkus.oidc.credentials.secret")
  String secret;
  @ConfigProperty(name = "quarkus.keycloak.devservices.users.john")
  String userName;
  @ConfigProperty(name = "quarkus.keycloak.devservices.users.john")
  String johnPwd;

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
    return given()
      .auth()
      .preemptive()
      .basic(clientId, secret)
      .param("client_id", clientId)
      .param("username", "john")
      .param("password", johnPwd)
      .param("grant_type", "password")
      .header("Accept", ContentType.JSON.getAcceptHeader())
      .contentType(ContentType.JSON);
  }
}
