# Asynchronous processing with REST services

There are two levels of asynchronous processing as far as REST services are concerned:

  - the asynchronous client processing: in this scenario the consumer invokes an endpoint which returns immediately. Depending on the type of asynchronous invocation the return might be of type `Future`, `CompletionStage`, etc. But the operation didn't finish yet at the moment when the consumer call returns. Perhaps it did even start yet. In order to get the result, the consumer has different options, for example to do polling or to use callbacks and continuations.
  - the asynchronous server processing: in this scenario, the producer itself processes the request asynchronously.

The two cases above may be combined such that to have asynchronous consumers 
with synchronous producers, asynchronous consumers with asynchronous producers 
and synchronous consumers with asynchronous producers. Let's examine them closer
with the help of some examples.

## Asynchronously invoking REST services

Asynchronous REST services consumers have been introduced with the 2.0 release
of the JAX-RS specifications, back in 2011. The idea is simple: once the consumer
has invoked a service endpoint, it doesn't wait for completion, instead it returns
control immediately. But now the question is: how does the consumer get the 
response to its request ? And here purists classify the asynchronous invocation
process in two categories:

  - blocking asynchronous invocations where the consumer needs to poll the task status in order to check for completion;
  - non-blocking asynchronous invocations where the consumer doesn't do any polling, as it is notified when the task is complete.

Later, in 2014, Java 8 has been released and, with it, a couple of new classes
have been added to the `java.util.concurrent` package. Some of them, like 
`CompletableFuture` and `CompletionStage` have greatly improved the way that the
asynchronous processing was implemented in REST services. We'll see how.

And in order to further complicate the whole context, in 2017, the 2.1 release 
of the JAX-RS specifications have brought some more improvements. This is the 
reason why, when it comes to asynchronous processing with REST services, we need
to address all these flavours: JAX-RS 2.0, Java 8 and JAX-RS 2.1.

That's what we'll be trying to do in the next paragraphs.

### JAX-RS 2.0: Asynchronously invoking REST services

Let's have a look at some examples of how this kind of asynchronous consumers, 
blocking and non-blocking, might be implemented based on the JAX-RS 2.0 
specifications.

#### Blocking asynchronous consumers

The listing below shows a Quarkus integration test that invokes in a blocking 
while asynchronous mode the endpoint `/time` of the `CurrentTimeResource` REST
service.

    @Test
    public void testCurrentTime()
    {
      try (Client client = ClientBuilder.newClient())
      {
        Future<String> timeFuture = client.target(TIME_SRV_URL).request().async().get(String.class);
        String time = timeFuture.get(5, TimeUnit.SECONDS);
        assertThat(parseTime(time)).isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
      }
      catch (Exception ex)
      {
        fail("### BlockingAsyncCurrentTimeResourceIT.testCurrentTime(): Unexpected exception %s", ex.getMessage());
      }
    }

This code may be found in the `/home/nicolas/50-shades-of-rest/async-clients
/blocking-async-clients/` directory of the GitHub repository. Please notice the 
`async()` verb in the request definition.

As you can see, this time the endpoint invocation doesn't return a `String`, 
but a `Future<String>`, i.e a kind of promise that, once the operation completed,
the result will be returned. The call to the service returns immediately, before 
it had a chance to complete. Then, the `get()` method will block the current
thread waiting for completion during 5 seconds. Hence, the blocking side of the 
call.

#### Non-blocking asynchronous consumers

Now let's look at a 2nd example implementing the same integration test but in a 
non-blocking way.

    @Test
    public void testCurrentTime()
    {
      try (Client client = ClientBuilder.newClient())
      {
        CountDownLatch latch = new CountDownLatch(1);
        client.target(TIME_SRV_URL).request().async().get(new InvocationCallback<String>()
        {
          @Override
          public void completed(String t)
          {
            ldt = parseTime(t);
            latch.countDown();
          }

          @Override
          public void failed(Throwable throwable)
          {
            fail(""" 
              ### NonBlockingAsyncCurrentTimeResourceIT.testCurrentTime(): 
              Unexpected exception %s""", throwable.getMessage());
            latch.countDown();
          }
        });
        if (latch.await(5, TimeUnit.SECONDS))
          assertThat(ldt)
            .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
      }
      catch (Exception ex)
      {
        fail("""
          ### NonBlockingAsyncCurrentTimeResourceIT.testCurrentTime()): 
          Unexpected exception %s""", ex.getMessage());
      }
    }

We're still using the `async()` method to invoke our endpoint but, this time,
the `get()` method won't take anymore the type of the expected result as its
input parameter, but an instance of the interface `jakarta.ws.rs.client.InvocationCallback`.
This interface has two methods:

    public void completed(T t);
    public void failed(Throwable throwable);

The first one notifies the task completion. It takes as its input parameter the 
operation result, i.e. the `String` containing the current date and time.
The 2nd one notifies the task failure and takes as its input parameter the current
exception which prevented it to complete.

Our callback is executed by a different thread than the one making the call. 
This thread, called *worker thread*, will be started automatically. In order
to synchronize execution, that is to say, to make sure that our main thread doesn't
exit before the worker one finishes the job, a count-down with the initial value 
of 1 is armed. It will be decremented by both `completed()` and `failed()` methods, 
such that, by using the `await()` function, we can wait the end of the worker 
thread, before exiting the main one. 

### Java 8: Asynchronously invoking REST services

Using the `java.util.concurrent.Future` class for asynchronously invoking REST
endpoints was already great progress compared to the initial 1.x release of the
JAX-RS specifications which only allowed synchronous calls. Introduced in Java 5,
this class represents,as we've seen, the result of an asynchronous processing.
It contains all the required methods to poll and wait for the result, however
it remains quite basic and limited. 

Several years later, Java 8 improves the asynchronous processing by introducing
the class `CompletableFuture` as an enhancement of `Future`. It not only represents
a future result but also provides a plethora of methods to compose, combine, 
execute asynchronous tasks, and handle their results without blocking.

So let's examine how to use these new classes in the Java 8 style.

#### Blocking asynchronous consumers

Have a look at the listing below, that you can found in the `/home/nicolas/
50-shades-of-rest/async-clients/java8-async-clients/` directory of the GitHub 
repository.

    @Test
    public void testCurrentTimeWithZoneId()
    {
      try (Client client = ClientBuilder.newClient())
      {
        CompletableFuture<String> timeFuture = CompletableFuture.supplyAsync(() ->
          client.target(TIME_SRV_URL).path(ENCODED).request().get(String.class))
          .exceptionally(ex -> fail("""
             ### BlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():\s"""
             + ex.getMessage()));
        assertThat(parseTime(timeFuture.join())).isCloseTo(LocalDateTime.now(),
          byLessThan(1, ChronoUnit.MINUTES));
      }
    }

What you see here is that the endpoint invocation is done now using 
`CompletableFuture.supplyAsync()` to which we provide the JAX-RS client request
as a supplier, implemented on the behalf of a Lambda function. The `supplyAsync()`
method executes the given task on the behalf of a new thread that it creates. We
have here the possibility to specify an `Executor` or, in the default case, it
will fork a new thread from the common thread pool shared across the application.

This method will return immediately, without waiting the task completion. The
supplied task (the lambda function) will run on this separate thread. Since this 
is a blocking asynchronous endpoint invocation, we use the `join()` method in 
order to wait for the task completion.

To resume, this example is very similar to the one in `/home/nicolas/
50-shades-of-rest/async-clients/jaxrs20-async-clients/`, with the only difference
that it returns a `CompletableFuture` instead of a `Future`. Also, the `async()`
method isn't anymore required when instantiating the JAX-RS client request.

#### Non-Blocking asynchronous consumers

The same similarities that we noticed above are also in effect as far as the 
non-blocking asynchronous invocation are concerned. Here is a code fragment:

    @Test
    public void testCurrentTimeWithZoneId()
    {
      try (Client client = ClientBuilder.newClient())
      {
        Callback callback = new Callback();
        CompletableFuture.supplyAsync(() ->
          client.target(TIME_SRV_URL).path(ENCODED).request().async().get(callback))
          .exceptionally(ex -> fail("""
            ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():
            \s""" + ex.getMessage()));
      }
    }

If we didn't need to use the `async()` method in the previous example, here we 
do as, otherwise, we cannot pass a `Callback` instance to our `get()` method.
For the rest, everything works nearly the same, differing slightly only that the
`CompletionFuture` class is now used instead of `Future`. Also, we separately 
defined the `Callback` class instead of providing it in-line as this facilitates
its reuse. Here's the listing:

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

Very few things have changed here, if any, compared to the preceding version.

### JAX-RS 2.1: Asynchronously invoking REST services

As we have seen, asynchronously invoking REST services in an either blocking or
non-blocking way, as defined by the JAX-RS 2.0 specifications, would either result
in polling the response, by calling `get()`, or registering a callback that would
be invoked when the HTTP response is available. Both of these alternatives are
interesting but things usually get complicated when you want to nest callbacks
or add conditional cases in the asynchronous execution flows.
JAX-RS 2.1 offers a new way to overcome these problems. It is called *Reactive
Client API* and it simply consists in invoking the `rx()` method, instead of 
`async()`, as it was the case with JAX-RS 2.0. 

#### Blocking asynchronous consumers

In the listing below, using `rx()` returns a response of type `CompletionStage`.
Then the method `thenAccept()` will execute the given action in a blocking mode,
on the same thread. 

    @Test
    public void testCurrentTime()
    {
      try (Client client = ClientBuilder.newClient())
      {
        client.target(TIME_SRV_URL).request().rx().get(String.class)
        .thenAccept(time -> {
          assertThat(parseTime(time)).isCloseTo(LocalDateTime.now(),
            byLessThan(1, ChronoUnit.MINUTES));
        })
        .exceptionally(ex -> {
          fail("""
            ### BlockingRxCurrentTimeResourceIT.testCurrentTime():
             Unexpected exception %s""", ex.getMessage());
          return null;
        })
        .toCompletableFuture().join();
      }
    }

Here, the call to `join()` will block the current thread until the operation 
completes.

#### Non-Blocking asynchronous consumers

Let's have a look now at the non-blocking asynchronous consumer:

    @Test
    public void testCurrentTime()
    {
      try (Client client = ClientBuilder.newClient())
      {
        client.target(TIME_SRV_URL).request().rx().get(String.class)
          .thenApply(time -> {
            assertThat(parseTime(time)).isCloseTo(LocalDateTime.now(),
              byLessThan(1, ChronoUnit.MINUTES));
             return time;
          })
          .exceptionally(ex -> {
            fail("""
              ### BlockingRxCurrentTimeResourceIT.testCurrentTime():
              Unexpected exception %s""", ex.getMessage());
            return null;
          });
      }
    }

We have replaced the call to `thenAccept(...)` by `thenApply(...)`. Doing that,
we don't wait anymore the task completion, as we did previously by calling 
`join()`. Instead, `thenApply()` returns a `CompletionStage` with the result of
the endpoint call which, in our case, is a `String`. So, as opposed to the 
approach using `thenAccept()` where `join()` blocks the current thread until 
the operation completes, the one using `thenApply()` remains non-blocking as it
returns a `CompletionStage` that will complete in the future. However, as opposed
to the first one which guarantees that the assertion has been evaluated before
processing, the last one doesn't.

This is furthermore what happens because, modifying the assertion  as follows:

    assertThat(LocalDateTime.of(2019, Month.MARCH, 28, 14, 33))
      .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));

the test will still pass, showing that the assertion isn't in fact evaluated, 
otherwise an `AssertionError` would have been raised. Accordingly, while non-blocking
calls are still possible with the JAX-RS 2.1 `rx()` flavour, they are more suitable
for being integrated in larger asynchronous flows than in tests, where a terminal
`join()` is at some point required.