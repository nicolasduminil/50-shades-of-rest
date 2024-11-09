# Asynchronous processing with REST services

There are two levels of asynchronous processing as far as REST services are concerned:

  - the asynchronous client processing: in this scenario the consumer invokes an endpoint and the invocation returns immediately. The endpoint itself might still be synchronous. Depending on the type of asynchronous invocation the return might be of type `Future`, `CompletionStage`, `CompletableFuture`, etc. But the operation didn't finish yet at the moment when the consumer call returns. Perhaps it did even start yet. In order to get the result, the consumer has different options, for example to do polling or to use callbacks and continuations.
  - the asynchronous server processing: in this scenario, the producer itself processes the request asynchronously.

The two cases above may be combined such that to have asynchronous consumers 
with synchronous producers, asynchronous consumers with asynchronous producers 
and synchronous consumers with asynchronous producers. Let's examine them closer
with the help of some examples.

## Asynchronously invoking REST services

Asynchronous REST services consumers have been introduced with the 2.0 release
of the JAX-RS specifications, back in 2011. The idea is simple: the consumer
invokes a service endpoint using a call statement that doesn't wait for completion, 
instead it returns immediately. But now the question is: how does the consumer get the 
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
        fail("### TestBlockingAsyncCurrentTimeResource.testCurrentTime(): ...);
      }
    }
<p style="text-align: center;">Listing 3.1: Using the JAX-RS 2.0 blocking client to asynchronously invoke endpoints</p>

This code may be found in the `async-clients/jaxrs20-async-clients/` directory
of the GitHub repository. Please notice the `async()` verb in the request definition.

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
<p style="text-align: center;">Listing 3.2: Using the JAX-RS 2.0 non-blocking client to asynchronously invoke endpoints</p>

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

Have a look at the listing below, that you can found in the `async-clients/java8
-async-clients/` directory of the GitHub repository.

    @Test
    public void testCurrentTimeWithZoneId()
    {
      try (Client client = ClientBuilder.newClient())
      {
        CompletableFuture<String> timeFuture = CompletableFuture.supplyAsync(() ->
          client.target(timeSrvUrl).path(ENCODED).request().get(String.class))
          .exceptionally(ex -> fail("""
             ### TestBlockingJava8CurrentTimeResource.testCurrentTimeWithZoneId():""",
             ex.getMessage()));
        assertThat(parseTime(timeFuture.join())).isCloseTo(LocalDateTime.now(),
          byLessThan(1, ChronoUnit.MINUTES));
      }
    }
<p style="text-align: center;">Listing 3.3: Using the Java 8 blocking client to asynchronously invoke endpoints</p>

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

To resume, this example is very similar to the one in `async-clients/jaxrs20-
async-clients/`, with the only difference that it returns a `CompletableFuture`
instead of a `Future`. Also, the `async()` method isn't anymore required when 
instantiating the JAX-RS client request.

> ** _NOTE:_ In order to reuse the same unit test scenarii in different subprojects 
while avoiding the code duplication, a shared subproject named `common-tests` 
has been provided. The class `BaseBlockingJava8` captures the code shown in the 
Listing 3.3 above and becomes the base class of all the ones implementing the 
same test strategy.

#### Non-Blocking asynchronous consumers

The same similarities that we noticed above are also in effect as far as the 
non-blocking asynchronous invocation are concerned. Here is a code fragment from
the class `TestNonBlockingAsyncTimeResource` found in the same directory:

    @Test
    public void testCurrentTimeWithZoneId()
    {
      try (Client client = ClientBuilder.newClient())
      {
        Callback callback = new Callback();
        CompletableFuture.supplyAsync(() ->
        {
          client.target(timeSrvUri).path(ENCODED).request().async().get(callback);
          try
          {
            return callback.getTime();
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        })
        .thenAccept(t -> assertThat(parseTime(t))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES)))
        .exceptionally(ex -> fail("""
          ### NonBlockingJava8CurrentTimeResourceIT.testCurrentTimeWithZoneId():""",
           ex.getMessage()));
      }
    }
<p style="text-align: center;">Listing 3.4: Using the Java 8 non-blocking client to asynchronously invoke endpoints</p>

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
<p style="text-align: center;">Listing 3.5: The class Callback</p>

Very few things have changed here, if any, compared to the preceding version.

> ** _NOTE:_ In order to reuse the same unit test scenarii in different subprojects
while avoiding the code duplication, a shared subproject named `common-tests`
has been provided. The class `BaseNonBlockingJava8` captures the code shown in the
Listing 3.4 above and becomes the base class of all the ones implementing the
same test strategy.


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
<p style="text-align: center;">Listing 3.6 Using the JAX-RS 2.1 blocking client to asynchronously invoke endpoints</p>

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
<p style="text-align: center;">Listing 3.7 Using the JAX-RS 2.1 non-blocking client to asynchronously invoke endpoints</p>

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

### Eclipse MicroProfile REST Client asynchronous consumers

In the previous chapter we've already discussed the Eclipse MicroProfile specs 
and, especially, the REST Client ones which facilitates the communication between
REST producers and consumers, on HTTP. We've demonstrated how this communication
works when synchrounous producers and consumers are used. Let's look now at how
this same communication works with synchronous producers and asynchronous consumers.

As you probably remember from the `classic` project, our MP REST Client was the
interface `CurrentTimeResourceClient`, shown below:

    ...
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String getCurrentDateAndTimeAtDefaultZone();
    @GET
    @Path("{zoneId}")
    @Produces(MediaType.TEXT_PLAIN)
    String getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
    ...
<p style="text-align: center;">Listing 3.8 The synchronous MP REST Client interface</p>

Now we're demonstrating how to use the same producer as before, but with an async
consumer. Hence, our MP REST Client inteface becomes `TimeResourceMpClientAsync`
in the `async-clients/mp-async-clients` project of the GitHub repository.

    @RegisterRestClient(configKey = "base_uri")
    @Path("time2")
    public interface TimeResourceMpClientAsync
    {
      @GET
      @Produces(MediaType.TEXT_PLAIN)
      CompletionStage<String> getCurrentDateAndTimeAtDefaultZone();
      @GET
      @Path("{zoneId}")
      @Produces(MediaType.TEXT_PLAIN)
      CompletionStage<String> getCurrentDateAndTimeAtZone(@PathParam("zoneId") String zoneId);
    }
<p style="text-align: center;">Listing 3.9 The asynchronous MP REST Client interface</p>

As you can see, our new MP REST Client interface doesn't return anymore directly
the result, but a promise to this result as an instance of `CompletionStage<String>`.
This new interface will be used by the Quarkus augmentation process, as explained
previously, in order to generate a new asynchronous consumer for the same old 
synchronous producer (only the base URL has been changed, from `time` to `time2`).
And everything works like before, as you can notice by executing the command:

    $ mvn -pl async-clients/mp-async-clients/ test
<p style="text-align: center;">Listing 3.9 Running unit tests with Maven</p>

Here above the `-pl` Maven option will first move to the project `mp-async-clients`
project before execute the Maven test lifecycle. This is a convenient way to run
only partially unit tests, in separate subprojects, avoiding this way to run all
of them, which might be time-consuming.

## Asynchronous REST producers

In the preceding chapter we examined how to asynchronously invoke synchronous 
REST services and we presented several examples to illustrate this process. Let's 
now look at how these REST services could themselves asynchronously process the 
incoming requests and produce responses. 

Most of the requests processed by the most common REST services are short-lived 
and, hence, the synchronous processing mode is very convenient in this case, 
as a few hundreds users could call them, while getting relatively decent response
times. Each incomming request is processed by a dedicated thread, meaning that a 
few hundreds users will require a few hundreds threads. This model is known as 
"one thread per connection".

However, in more special cases, in FinTech applications, for example, 
where each consumer request might result in a long-running operation, the associated
threads and sockets would block indefinetly, doing nothing other than idling. 
Then, having a few hundreds threads which don't do anything else than idling 
means consuming a lots of the OS resources. This kind of application is very
hard to scale.

The "one thread by connection" processing model that we described here was imposed
by the Servlet API specs on which the REST ones, like JAX-RS and now Jakarta REST,
are based. But in 2009, the Servlet 3.0 specs introduced an asynchronous API that 
allow for suspending on the server side the current request and handling it by 
a separate thread, other than the calling one. For the long-running applications
described above, this meant that a small handfull of threads could manage to send 
responses back to consumers, who can poll for results or be waked up by callbacks,
avoiding this way all the overhead of the "one thread per connection" model.

JAS-RS 2.0, released in 2013, was the first release of the specs supporting server
side asynchronous processing.

### JAX-RS 2.0 asynchronous producers

To use REST JAX-RS 2.0 asynchronous producers requires to interact with the 
`AsynResponse` inteface introduced by the version of the specs.

    public interface AsyncResponse
    {
      boolean resume(Object response);
      boolean resume(Throwable response);
    }
<p style="text-align: center;">Listing 4.1 The interface AsyncResponse introduced by JAX-RS 2.0</p>

The subproject `async-jaxrs20` in the `async-services` folder of the GitHub repository
shows an example to illustrate the use of the `AsyncResponse` interface. Here is
an extract:

    ...
    private static final String TIME_SERVER = "time.google.com";
    private static final String FMT = "d MMM uuuu, HH:mm:ss XXX z";
    private final NTPUDPClient ntpClient = new NTPUDPClient();
    private static InetAddress inetAddress;
    private static final Logger LOG = LoggerFactory.getLogger(BaseNtpResourceAsync.class);

    @PostConstruct
    public void postConstruct() throws Exception
    {
      inetAddress = InetAddress.getByName(TIME_SERVER);
      ntpClient.setDefaultTimeout(5000);
      ntpClient.open();
      ...
    }

    @GET
    public void currentTime(@Suspended AsyncResponse ar) throws IOException
    {
      long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
      ar.resume(Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(FMT)));
    }

    @GET
    @Path("{zoneId}")
    public void zonedTime(@PathParam("zoneId") String zoneId, @Suspended AsyncResponse ar) throws IOException
    {
      long time = ntpClient.getTime(inetAddress).getMessage().getTransmitTimeStamp().getTime();
      ar.resume(Instant.ofEpochMilli(time).atZone(ZoneId.of(URLDecoder.decode(zoneId, StandardCharsets.UTF_8))).format(DateTimeFormatter.ofPattern(FMT)));
    }
    ...
<p style="text-align: center;">Listing 4.2 Using the interface AsyncResponse introduced by JAX-RS 2.0</p>

The first thing to notice is that our textbook REST service business case has 
changed. Since we implement now an asynchronous processing, we needed an endpoint 
which doesn't return instantly, as it was the case of the preceding one, that 
sent back the current date and time in different time zones. Now, we're invoking 
the Google's time-server, at `time.google.com`, to request date and time and this
operation, consisting in calling and external service, is supposed to take longer.

The other thing we need to notice is that, this time, our endpoints don't 
return anymore a `String` instance formatted as a date and time, but a `void`.
That's a surprise as one could legimately wonder what could be the point of a 
REST endpoint returning a `void` ?

And last but not least, the 3rd thing to notice is the `@Suspended` annotated 
instance of the `AsyncResponse` input parameter passed to each endpoint.

Here is what things work: injecting an instance of `AsyncResponse` as an input 
parameter of our endpoints, using the `@Suspended` annotation, has the effect of
suspending, from the current thread of execution, the HTTP request which will 
be handled by a new background thread spawned on this purpose. Once that this thread
did its work, in our case getting the date and time from the Google service, 
it sends a response back to the consumer by calling `AsyncResponse.resume()...`.
This means a successfull response and, hence, a status code 200 is sent back to 
the consumer. Also, the `resume()` method will automatically marshall the formatted
date and time into the HTTP request body.

We can use a variety of synchronous or asynchronous, blocking or non-blocking 
consumers with such a REST service. For example, the Quarkus test class 
`TestNtpResourceAsyncJaxrs20` demonstrates a RESTassured synchronous consumer, 
while `TestBlockingJava8NtpResourceJaxrs20` and `TestNonBlockingJava8NtpResourceJaxrs20`
show the same test in an asynchronous blocking and, respectivelly, unblocking way.
We have already discussed the blocking and unblocking asynchronous clients during
the preceding chapter.

## JAX-RS 2.1 asynchronous producers

