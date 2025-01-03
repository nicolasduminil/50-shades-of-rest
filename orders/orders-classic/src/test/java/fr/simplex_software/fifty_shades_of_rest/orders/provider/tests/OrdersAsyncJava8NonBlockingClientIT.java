package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;

import java.net.*;

@QuarkusTest
public class OrdersAsyncJava8BlockingClientIT extends OrdersBaseJava8AsyncBlockingClient
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI customerSrvUri;
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI orderSrvUri;

  public URI getCustomerSrvUri()
  {
    return customerSrvUri;
  }

  public URI getOrderSrvUri()
  {
    return orderSrvUri;
  }
}
