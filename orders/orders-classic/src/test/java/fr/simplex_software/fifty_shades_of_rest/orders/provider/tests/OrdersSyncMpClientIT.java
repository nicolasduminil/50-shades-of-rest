package fr.simplex_software.fifty_shades_of_rest.orders.provider.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders_test.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.junit.jupiter.api.*;

@QuarkusTest
public class OrdersSyncMpClientIT extends AbstractOrdersApiClient
{
  @Inject
  @RestClient
  CustomerApiClient customerApiClient;
  @Inject
  @RestClient
  OrderApiClient orderApiClient;

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
