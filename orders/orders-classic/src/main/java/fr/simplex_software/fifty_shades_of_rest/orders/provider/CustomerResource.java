package fr.simplex_software.fifty_shades_of_rest.orders.provider;

import fr.simplex_software.fifty_shades_of_rest.orders.api.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.*;

import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.atomic.*;

@ApplicationScoped
@Path("customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource implements CustomerApi
{
  @Inject
  CustomerService customerService;
  @Inject
  RandomCustomerService randomCustomerService;
  private final AtomicLong createdCustomers = new AtomicLong(0);


  @Override
  @GET
  public Response getCustomers()
  {
    return Response.ok().entity(new GenericEntity<List<CustomerDTO>>(customerService.getCustomers()) {}).build();
  }

  @Override
  @GET
  @Path("/{id}")
  public Response getCustomer(@PathParam("id") Long id)
  {
    return Response.ok().entity(customerService.getCustomer(id)).build();
  }

  @Override
  @GET
  @Path("/email/{email}")
  public Response getCustomerByEmail(@PathParam("email") String email)
  {
    return Response.ok().entity(customerService.getCustomerByEmail(URLDecoder.decode(email, StandardCharsets.UTF_8))).build();
  }

  @Override
  @POST
  public Response createCustomer(CustomerDTO customerDTO)
  {
    createdCustomers.incrementAndGet();
    return Response.created(URI.create("/customers/" + customerDTO.id())).entity(customerService.createCustomer(customerDTO)).build();
  }

  @Override
  @PUT
  public Response updateCustomer(CustomerDTO customerDTO)
  {
    return Response.accepted().entity(customerService.updateCustomer(customerDTO)).build();
  }

  @Override
  @DELETE
  public Response deleteCustomer(CustomerDTO customerDTO)
  {
    customerService.deleteCustomer(customerDTO.id());
    return Response.noContent().build();
  }

  @GET
  @Path("/random")
  @Counted(name = "randomCustomerCounter",
    description = "Counts how many times the getRandomCustomer() method has been called")
  @Timed(name = "randomCustomerTimer",
    description = "Times the getRandomCustomer() method",
    unit = MetricUnits.MILLISECONDS)
  public Response getRandomCustomer()
  {
    CustomerDTO randomCustomer = randomCustomerService.getRandomCustomer();
    return Response.ok().entity(randomCustomer).build();
  }

  @GET
  @Path("/random/{count}")
  @Metered(name = "randomCustomerMeter",
    description = "Measures the getRandomCustomers() method",
    unit = MetricUnits.NONE)
  @CircuitBreaker(requestVolumeThreshold = 4,
   failureRatio = 0.5, delay = 2000, successThreshold = 3)
  @Fallback(fallbackMethod = "getFallbackRandomCustomers")
  @Timeout(250)
  public Response getRandomCustomers(@PathParam("count") int count)
  {
    List<CustomerDTO> randomCustomers = randomCustomerService.getRandomCustomers(count);
    return Response.ok().entity(randomCustomers).build();
  }

  @Gauge(name = "randomCustomerGauge",
    description = "The number of newly created customers",
    unit = MetricUnits.NONE)
  public long getCreatedCustomerCount()
  {
    return createdCustomers.get();
  }

  private Response getFallbackRandomCustomers(int count)
  {
    CustomerDTO customerDTO = new CustomerDTO("john", "doe",
      "john.doe@email.com", "222-123456");
    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
      .entity(customerDTO).build();
  }
}
