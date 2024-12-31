package fr.simplex_software.fifty_shades_of_rest.orders.provider;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

public class Callback implements InvocationCallback<Response>
{
  private final CountDownLatch latch;
  private final AtomicReference<Response> ar;

  public Callback()
  {
    this.latch = new CountDownLatch(1);
    this.ar = new AtomicReference<>();
  }

  @Override
  public void completed(Response response)
  {
    ar.set(response);
    latch.countDown();
  }

  @Override
  public void failed(Throwable throwable)
  {
    latch.countDown();
    fail("### Callback.failed(): Unexpected exception", throwable);
  }

  public Response getResponse() throws InterruptedException
  {
    Response resp = null;
    if (latch.await(5, TimeUnit.SECONDS))
      resp = ar.get();
    return resp;
  }
}
