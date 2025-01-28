package fr.simplex_software.fifty_shades_of_rest.orders.api;

import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.*;

@RegisterRestClient(configKey = "base_uri")
@Path("customers-async21")
public interface CustomerApiAsyncClient extends BaseCustomerApiClient
{
}
