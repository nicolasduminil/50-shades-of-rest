package fr.simplex_software.fifty_shades_of_rest.async_services.react.tests;

import fr.simplex_software.fifty_shades_of_rest.async_services.react.*;
import fr.simplex_software.fifty_shades_of_rest.base.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class TestNtpResourceAsyncReactWithMpClient extends AbstractMpClient
{
  @Inject
  @RestClient
  NtpResourceClientReact ntpResourceClient;

  @Override
  protected BaseMpClient getMpClient()
  {
    return ntpResourceClient;
  }
}
