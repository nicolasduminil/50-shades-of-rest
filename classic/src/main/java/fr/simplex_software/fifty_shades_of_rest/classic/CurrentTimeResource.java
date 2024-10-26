package fr.simplex_software.fifty_shades_of_rest.classic;

import fr.simplex_software.fifty_shades_of_rest.base.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;

@ApplicationScoped
@Path("time")
public class CurrentTimeResource extends BaseTimeResource
{
}
