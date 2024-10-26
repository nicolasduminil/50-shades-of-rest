package fr.simplex_software.fifty_shades_of_rest.failures;

import io.quarkus.vertx.web.*;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.*;
import jakarta.enterprise.context.*;

import java.util.*;

@ApplicationScoped
public class FaultSimulator
{
  private enum Fault
  {
    NONE,
    INBOUND_REQUEST_LOSS,
    SERVICE_FAILURE,
    OUTBOUND_RESPONSE_LOSS
  }

  private Fault fault = Fault.NONE;
  private double faultRate = 0.5;
  private final Random rnd = new Random();

  @Route(path = "/fail")
  public void injectFault(RoutingContext routingContext)
  {
    String failure = routingContext.request().getParam("failure");
    String rate = routingContext.request().getParam("rate");
    if (failure == null && rate == null)
    {
      fault = Fault.NONE;
      faultRate = 0.5;
      routingContext.response().end("Faults are disabled");
      return;
    }
    if (failure != null)
      fault = Fault.valueOf(failure.toUpperCase());
    if (rate != null)
    {
      double d = Double.parseDouble(rate);
      if (d > 1)
      {
        routingContext.response()
          .setStatusCode(400)
          .end("Invalid failure rate, must be in [0, 1)");
        return;
      }
      faultRate = d;
    }
    routingContext.response().end("Faults are enabled: fault = " + fault.name() + ", failure rate = " + faultRate);
  }

  @RouteFilter
  public void routeFilter(RoutingContext routingContext)
  {
    if (fault == Fault.NONE || routingContext.request().path().equals("/fail"))
    {
      routingContext.next();
      return;
    }
    if (faultRate >= rnd.nextDouble())
      switch (fault)
      {
        case INBOUND_REQUEST_LOSS:
          break;
        case SERVICE_FAILURE:
          routingContext.next();
          routingContext.response().setStatusCode(500).end("FAULTY RESPONSE!");
          break;
        case OUTBOUND_RESPONSE_LOSS:
          routingContext.next();
          routingContext.response().close();
      }
    else
      routingContext.next();
  }
}
