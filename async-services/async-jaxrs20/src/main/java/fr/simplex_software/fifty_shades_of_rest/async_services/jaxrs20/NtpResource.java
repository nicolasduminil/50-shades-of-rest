package fr.simplex_software.fifty_shades_of_rest.async_services.jaxrs20;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@ApplicationScoped
@Path("ntp")
@Produces(MediaType.TEXT_PLAIN)
public class NtpResource extends BaseNtpResource
{
}
