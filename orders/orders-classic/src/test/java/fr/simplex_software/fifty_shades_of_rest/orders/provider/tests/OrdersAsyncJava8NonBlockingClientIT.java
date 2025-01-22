package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.apache.http.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.concurrent.*;

import static fr.simplex_software.fifty_shades_of_rest.orders_test.OrdersJava8AsyncCommon.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersAsyncJava8NonBlockingClientIT
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI customerSrvUri;

  /*@Test
  @Order(10)
  @Timeout(5)
  public void testCreateCustomer()
  {
    try (Client client = ClientBuilder.newClient())
    {
      CustomerDTO customerDTO = new CustomerDTO("John", "Doe",
        "john.doe@email.com", "1234567890");
      createCustomer(client, customerDTO)
        .thenAccept(response ->
          assertCustomer(response, HttpStatus.SC_CREATED, "John"))
        .get(5, TimeUnit.SECONDS);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(20)
  public void testGetCustomers()
  {
    try (Client client = ClientBuilder.newClient())
    {
      getCustomers(client).thenAccept(response ->
      assertCustomer(response, HttpStatus.SC_CREATED, "John"))
        .get(5, TimeUnit.SECONDS);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }*/
}
