package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

@QuarkusTest
public class OrdersRestAssuredClientIT extends OrdersBaseTest
{
  @BeforeAll
  public static void beforeAll()
  {
    customersUrl = "/customers";
    ordersUrl = "/orders";
  }

  @AfterAll
  public static void afterAll()
  {
    customersUrl = null;
    ordersUrl = null;
  }
}
