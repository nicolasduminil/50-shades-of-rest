package fr.simplex_software.fifty_shades_of_rest.orders.async.tests;

import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

@QuarkusTest
public class OrdersAsyncJaxRs20RestAssuredClientIT extends OrdersBaseTest
{
  @BeforeAll
  public static void beforeAll()
  {
    customersUrl = "/customers-async";
    ordersUrl = "/orders-async";
  }

  @AfterAll
  public static void afterAll()
  {
    customersUrl = null;
    ordersUrl = null;
  }
}
