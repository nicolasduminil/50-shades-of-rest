package fr.simple_software.fifty_shades_of_rest.classic.tests;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import fr.simplex_software.fifty_shades_of_rest.classic.*;
import fr.simplex_software.fifty_shades_of_rest.common_tests.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

@QuarkusTest
public class TestCurrentTimeResourceMpClient extends AbstractMpClient
{
  @Inject
  @RestClient
  CurrentTimeResourceClient currentTimeResourceClient;

  @Override
  protected BaseMpClient getMpClient()
  {
    return currentTimeResourceClient;
  }
}
