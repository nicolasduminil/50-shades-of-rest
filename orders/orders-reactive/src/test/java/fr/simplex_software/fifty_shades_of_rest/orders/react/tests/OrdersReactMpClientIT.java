package fr.simplex_software.fifty_shades_of_rest.orders.react.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class OrdersReactMpClientIT extends AbstractOrdersApiClient
{
  @Inject
  @RestClient
  CustomerReactApiClient customerApiClient;
  @Inject
  @RestClient
  OrderReactApiClient orderApiClient;

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
