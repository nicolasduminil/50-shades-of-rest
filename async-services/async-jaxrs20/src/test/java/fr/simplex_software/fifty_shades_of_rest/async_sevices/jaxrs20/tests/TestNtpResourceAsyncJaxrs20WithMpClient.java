package fr.simplex_software.fifty_shades_of_rest.async_sevices.jaxrs20.tests;

import fr.simplex_software.fifty_shades_of_rest.async_services.jaxrs20.*;
import fr.simplex_software.fifty_shades_of_rest.base.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class TestNtpResourceAsyncJaxrs20WithMpClient extends AbstractMpClient
{
  @Inject
  @RestClient
  NtpResourceClient ntpResourceClient;

  @Override
  protected BaseMpClient getMpClient()
  {
    return ntpResourceClient;
  }
}
