package fr.simplex_software.fifty_shades_of_rest.orders.async.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class OrdersAsyncJaxRs20MpClientIT extends AbstractOrdersApiClient
{
  @Inject
  @RestClient
  CustomerAsyncApiClient customerApiClient;
  @Inject
  @RestClient
  OrderAsyncApiClient orderApiClient;

  @Override
  protected BaseCustomerApiClient getCustomerApiClient()
  {
    return customerApiClient;
  }

  @Override
  protected BaseOrderApiClient getOrderApiClient()
  {
    return orderApiClient;
  }
}
