package fr.simplex_software.fifty_shades_of_rest.async_clients.java8.tests;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.assertj.core.api.Assertions.*;

public class Callback implements InvocationCallback<String>
{
  private final CountDownLatch latch;
  private final AtomicReference<String> time;

  public Callback()
  {
    this.latch = new CountDownLatch(1);
    this.time = new AtomicReference<>();
  }

  @Override
  public void completed(String strTime)
  {
    time.set(strTime);
    latch.countDown();
  }

  @Override
  public void failed(Throwable throwable)
  {
    latch.countDown();
    fail("### Callback.failed(): Unexpected exception", throwable);
  }

  public String getTime() throws InterruptedException
  {
    String strTime = null;
    if (latch.await(5, TimeUnit.SECONDS))
      strTime = time.get();
    return strTime;
  }
}
