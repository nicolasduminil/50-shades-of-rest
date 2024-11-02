package fr.simplex_software.fifty_shades_of_rest.async_clients.mp;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.*;

@RegisterRestClient(configKey = "base_uri")
@Path("ntp-async")
public interface NtpResourceMpClientAsync extends BaseMpClientAsync
{
}
