package fr.simplex_software.fifty_shades_of_rest.async_services.jaxrs21.tests;

import fr.simplex_software.fifty_shades_of_rest.async_services.jaxrs21.*;
import fr.simplex_software.fifty_shades_of_rest.base.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class TestNtpResourceAsyncJaxrs21WithMpClient extends AbstractMpClient
{
  @Inject
  @RestClient
  NtpResourceClientJaxrs21 ntpResourceClient;

  @Override
  protected BaseMpClient getMpClient()
  {
    return ntpResourceClient;
  }
}
