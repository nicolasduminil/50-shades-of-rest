package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.provider.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;

import java.net.*;

@QuarkusTest
public class OrdersAsyncJava8ClientIT extends OrdersBaseJava8AsyncClient
{
  @TestHTTPEndpoint(CustomerResource.class)
  @TestHTTPResource
  private URI customerSrvUri;


  public URI getCustomerSrvUri()
  {
    return customerSrvUri;
  }
}
