package fr.simplex_software.fifty_shades_of_rest.orders.react.tests;

import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

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
}
