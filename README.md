# Quarkus: 50 Shades of RESTful Services

REST is now 25 years old. The birth certificate of this almost impossible to 
remember acronym (*REpresentational State Transfer*) is considered to be the Y2K
doctoral dissertation of Roy Fielding, which aimed at creating a *standard* software
architecture, making easier the communication between systems using HTTP (*HyperText
Transfer Protocol*).

25 years is a long time and, at the IT scale, it's even much longer. We could 
think that, after a so long period of practising and testing, this paradigm has 
yielded up all its secrets. But no, screams often our daily activity, constraining
us to observe exactly the opposite.

Hence, the idea of this booklet which tries to address the essential aspects 
of this old, yet unknown, web technology, from its most basic features, like verbs
and resource naming conventions, to the most advanced ones, like non-blocking, 
asynchronous or reactive processing, together with the whole diversity of the REST
clients, blocking or non-blocking, synchronous or asynchronous, reactive or classic.

And since in order to illustrate the discourse we need code examples, I chose to
write them in Java, with its [supersonic subatomic dedicated stack](https://quarkus.io/), 
that doesn't need any longer presentation.

## A bit of history

In the begging there was nothing. Before the 70s, there weren't networks at all.
Computers were standalone boxes, like the one in the figure below, which didn't communicate 
each other. No files transfer, no remote access, no email, no internet, nothing.

![](history.png)

### Arpanet, IBM SNA, DECnet and friends

This started to change in the beginning '70s with the birth of the ARPANET. 
But it wasn't until the end of the '70s that ARPANET became a backbone with 
several hundreds of nodes, using as processors minicomputers that later 
became routers. ARPANET was a network of networks, an inter-network, hence
the Internet that it became later.

ARPANET was based on TCP/IP (*Transmission Control Protocol/Internet Protocol*),
the first network protocol which continues to be the *lingua franca* of our 
nowadays internet. However, starting with the beginning of the '80s, other network
protocols have raised as well. Among the most famous were IBM SNA (*System Network
Architecture* ) and DECnet (*Digital Equipment Coorportaion net*). They were both
proprietary, yet popular network architectures connecting mainframes, minicomputers
peripheral devices like teleprinters and displays, etc.

IBM SNA and DECnet have competed until early '80s when another protocol, OSI (*Open 
System Interconnect*), backed by European telephone monopolies and most governments,
was favored. Well-defined and supported by many state organizations, OSI became
quickly an ISO (*International Standardization Organization*) standard and was 
in the process of imposing itself on the market,
but it suffered from too much complexity and, finally, it gave way to TCP/IP which
was already a *de-facto* standard. Accordingly, at the end of the '80s, the network 
protocols war was finished and TCP/IP was the winner.

### RPC

But the network protocols wasn't the only war that took place in that period. 
Once that the computer networks became democratic and affordable, new distributed 
software applications started to emerge. These applications weren't designed 
anymore to run in a single isolated box but as standalone components, on different
nodes of the network. And in order to communicate, in order that local components
be able to call remote ones, software communication protocols were required.

The first major software communication protocol was RPC (*Remote Procedure Call*),
developed by SUN Microsystems in the 80s. One of the first problems that the 
distributed computing had to solve was the fact that, in order to perform a remote
call, the caller needs to capture the essence of the callee. A call to another
component, be it local or remote, needs to be compiled and, in order to be compiled, 
the callee procedure needs to be known by the compiler, such that it can translate
its name to a memory address. But when the callee is located on a different node
than the caller, the compiler cannot know the callee procedure since it is remote.
Hence, the notion of *stub*, i.e. a local proxy of the remote procedure that, when
called, transforms the local call into remote one. 

Which means that, in order to call a remote procedure, in addition of the two 
components, the caller and the callee, a caller stub able to transform the local
call into a remote one, as well as a callee stub, able to transform the local return 
into a remote one, are required. These stubs are very complex artifacts and coding
them manually would have been a nightmare for the poor programmer. One of the 
greatest merits of RPC was to recognize the difficulty of such an undertaking and
to provide `genrpc`, a dedicated tool to generate the stubs using a standard 
format, named XDR (*eXternal Data Representation*), in a way the grandfather of
the nowadays' XML and JSON.

RPC was a big success as it proposed a rather straightforward model for distributed
applications development in C, on Unix operating system. But like any success, it 
has been forgotten as soon as other new and more interesting paradigms have 
emerged.

### DCOM

Nevertheless, the success of RPC has encouraged other software editors to take
a real interest in this new paradigm called henceforth *middleware* and which
allowed programs on different machines to talk each other. Microsoft, for example,
adopted RPC but, living up to its reputation, modified it and, in the early '90s,
released a specific Windows NT version of it, known as MSRPC. Several years later,
in September 1996, Microsoft launched DCOM (*Distributed Component Object Model*).

Based on MSRPC and on RPC, which underlying mechanism it was using, DCOM imposed
itself as a new middleware construct supporting OOP (*Object Oriented Programming*).
The OOP support provided by DCOM was great progress compared with the RPC layer
as it allowed a higher abstraction level and to manipulate complex types instead 
of the XDR basic ones.

Unlike RPC and MSRPC accessible only in C, DCOM supported MS Visual C/C++ and 
Visual Basic. However, like all the Microsoft products, DCOM was tied to Windows 
and, hence, unable to represent a reliable middleware, especially in heterogeneous
environments involving different hardware, operating systems and programming languages.

### CORBA

The *Common Object Request Broker Architecture* is an OMG (*Object Management 
Group*) standard that emerged in 1991 and which aimed at bringing solutions to
the DCOM's most essential concerns, especially its associated vendor lock-in 
pattern that made its customers dependent on the Windows operating system.

As a multi-language, multi-os and multi-constructor platform, Corba was the first
true distributed object-oriented middleware. It replaced the `rpcgen` utility, inherited 
from RPC and MSRPC, by IDL (*Interface Definition Language*), a plain text notation.
And instead of the old XDR, the IDL compiler generated C++ or Java code directly.

Corba has definitely been a major player in the middleware history thanks to its
innovative architecture based on components like POA (*Portable Object Adapter*),
PI (*Portable Interceptors*), INS (*Interoperable Naming Service*) and many others.

But Corba was complex and required a quite steep learning curve. Its approach was
powerful but using it carelessly could have led to terrible applications, impacting
dramatically the infrastructure performances. Moreover, it was based on IIOP 
(*Internet Inter ORB Protocol*), an unfriendly firewall communication protocol
that used raw TCP/IP connections to transmit data.

All these aspects made feel like, despite Corba's great qualities, the community 
wasn't yet ready to adopt the first distributed object-oriented middleware.

### RMI

Positioned initially as the natural outgrowth of Corba, RMI (*Remote Method 
Invocation*) has been introduced with JDK (*Java Development Kit*) 1.1, in 1997.
One year later, JDK 1.2 introduced Java IDL and `idl2java`, the Java counterpart 
of Corba's IDL, supporting IIOP. 

In 1999, the RMI/IIOP extension to the JDK 1.2 enabled the remote access of any
Java distributed objects from any IIOP supported language. This was a major 
evolution as it delivered Corba distributed capabilities to the Java platform.

Two years later, in 2001, the JDK 1.4 introduced support for POA, PI and 
INS, signing this way the Corba's death sentence. A couple of the most widespread
implementations, like Borland's VisiBroker or Iona's Orbix, have still subsisted
until 2003, when they got lost into oblivion.

From now on, Java RMI became the universal distributed object-oriented object model.

### Jakarta Enterprise Beans (EJB)

In 1999, SUN Microsystems has released the first version of what they're calling
the Java Enterprise platform, named a bit confusing J2EE (*Java 2 Enterprise 
Edition*). This new Java based framework was composed of 4 specifications: JDBC
(*Java Data Base Connection*), EJB (*Enterprise Java Beans*), Servlet and JSP 
(*Java Server Pages*). In 2006 J2EE became Java EE and, 11 years later, in 2017,
it changed again its name to become Jakarta EE.

Between 1999 and today, the Jakarta EE specifications have evolved dramatically.
Started with the previous mentioned 4 subprojects, they represent today more 
than 30. But the EJB specifications, currently named Jakarta Enterprise Beans,  
remain among the most the innovative Java APIs, the legitimate heir of Java RMI.

Enhanced under the JCP (*Java Community Process*) as JSR (*Java Specification 
Request*) 19 (EJB 2.0), JSR 153 (EJB 2.1), JSR 220 (EJB 3.0), JSR 318 (EJB 3.1)
and JSR 345 (EJB 3.2), these specifications provide even today the standard way
to implement the server-side components, often called the backend. They handle 
common concerns in enterprise grade applications, like security, persistence, 
transactional integrity, concurrency, remote access, race conditions management,
and others.

### Jakarta XML Web Services (JAX-WS)

While Jakarta Enterprise Beans compliant components were the standard solution 
to implement and encapsulate business logic, a new markup notation for storing, 
transmitting and reconstructing arbitrary data, has emerged. This notation, named 
XML (*eXtended Markup Language*), finished by being adopted as a standard by WWW
(*World Wide Web*) consortium, in 1999. And as that's often the case in the IT 
history, barely adopted, it immediately became so essential, so much so that it 
was quickly considered that any XML application was mandatory great. 

Consequently, it didn't need much to architectures boards to consider that 
exchanging XML documents, instead of RMI/IIOP Jakarta Enterprise Beans payloads,
would be easier and more proficient. It was also considered that Jakarta 
Enterprise Beans was heavy because it required stubs to be automatically downloaded
from servers to clients and, once downloaded, these stubs acted like client-side
objects, making remote calls. This required that the byte-code for the various
programmer-defined Java classes be available on the client machine and, this setup
was considered a significant challenge.

The alternative was the so-called *web services*, a newly coined concept supposed 
to simplify the distributed processing. According to this new paradigm, clients
and servers would exchange XML documents, i.e. text data. This documents grammar
is described by a new notation, called XSD (*XML Schema Defintion*), having the 
same capabilities as an object-oriented programming language, supporting inheritance,
polymorphism, etc. This XSD notation was to the web services what XDR was to RPC.

As for the interface contracts between clients and servers, another new XML based 
notation, called WSDL (*Web Service Definition Language*), was required. Last but 
not least, the payload exchanged between clients and servers was expressed using a 
yet another new XML based notation, called SOAP (*Simple Object Access Protocol*)
which, despite its name, was anything but simple. The funy thing is that all this
huge labyrinth was considered simpler that the old good Jakarta Enterprise Beans
components.

Nevertheless, all this madness became standard in 2003, as JSR 101, known also
under the name of JAX-RPC (*Java API for XML-Based RPC*) and later, in 2017 as 
JSR 224, named JAX-WS (*Java API for XML-Based Services*). These specifications 
gave rise to a lot other, including but not limited to WS-I Basic Profile, WS-I
Attachments, WS-Addressing, SAAJ, etc.

### Jakarta RESTful Web Services (JAX-RS)

After this so convoluted piece of history, we come finally at the end of our 
journey, in 2009, when the specifications JAX-RS became a part of Java EE 6. Today,
in 2024, they are named Jakarta RESTful Web Services and are a part of Jakarta EE 11.
Since 15 years they represent the main substratum making service and microservices 
to communicate each-other, as well as with the external world.

In this post series we'll examine all their 50 shades :-).

# 101 RESTful services

As we've seen, RESTful services are services that follow the *Representation State 
Transfer* principles. They are based on manipulating resources addressable via 
their URLs (*Unified Resource Locator*), which can contain static or dynamic data.

RESTful resources have their own URLs and are handled through HTTP methods (`GET`, `POST`, 
`PUT`, `DELETE`, etc.) to perform different operations like, for example, CRUD-ing
or using them wherever efficient communication over the web is crucial.

RESTful APIs represent the foundation of modern web development, allowing for seamless
integration and interaction of various web services. Their adaptability and 
efficiency make them ideal for various applications, including cloud services, 
mobile apps, and IoT devices. By adhering to REST principles and best practices,
RESTful APIs allow developers to build robust, scalable, and secure web services that
meet the users and enterprises requirements.

## 101 Java RESTful Web Services 

RESTful services can be implemented in any programming language. In this booklet we're 
using Java and, accordingly, we'll focus on Java RESTful APIs.

In Java, RESTful services can be implemented using several APIs: 

  - [Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/). This is the specification provided by Jakarta EE for development and building REST services. As such, this specification is key to the development of microservices and cloud based applications, and it is part of the Jakarta EE Web Profile as well as the full platform. The most popular implementations of this specification are [Jersey](https://eclipse-ee4j.github.io/jersey/) by Oracle, [RESTeasy](https://resteasy.dev/) by Red Hat and [CXF](https://cxf.apache.org/) by Apache; 
  - [Quarkus](https://quarkus.io). This is the supersonic, subatomic Java stack which supports Jakarta RESTful Web Services specifications via its RESTeasy implementation.
  - [Micronaut](https://micronaut.io/). This is a modern, open source, JVM-based, full-stack framework for building microservice and serverless applications.
  - [Spring](https://spring.io/). This is an open source software development framework that provides support for building Java applications, including REST services.
  - [Spark](https://spark.apache.org/). This is a unified analytics engine for large-scale data processing providing, among others, a rapid development web framework.
  - etc.

In this booklet we're using Quarkus to build Jakarta REST compliant REST services. 
In addition to these specificatione and their implementation by RESTeasy, Quarkus
also relies on [Eclipse MicroProfile](https://microprofile.io/) specifications and, more specifically, on 
[Eclipse MicroProfile REST Client API](https://download.eclipse.org/microprofile/microprofile-rest-client-2.0/microprofile-rest-client-spec-2.0.html) as well as [Eclipse MicroProfile OpenAPI](https://download.eclipse.org/microprofile/microprofile-open-api-1.1.2/microprofile-openapi-spec.html).

The Jakarta RESTful Web Services specifications define a set of annotations, classes
and interfaces that facilitates the development and deployment of REST endpoints
producers and consumers. They are, of course, implemented by RESTeasy and supported
by Quarkus. The package `jakarta.ws.rs` contains all these annotations, classes
and interfaces, as shown by the table below:

| **Subpackage** | **Description** |
|:---------------|:----------------|
| root           | The API root package |
| `client`       | Classes and interfaces in the client API |
| `container`    | Container specific API |
| `core`         | Low-level interfaces and annotations |
| `ext`          | Extensions API |

The annotations defined by the Jakarta REST Web Services and supported by RESTeasy
and Quarkus are listed below:

| **Annotation**                     | **Description**                                                    |
:-----------------------------------|:-------------------------------------------------------------------|
| `@GET`, `@Post`, `@PUT`, `@DELETE` | Indicates that the annotated methods serve the given HTTP requests |
| `@Path`                            | The URI path of the resource                                       |
| `@PathParam`                       | A resource path parameter                                          |
| `@QueryParam` | A resource query parameter |
| `@Produces`, `@Consumes` | Defines the produced or consumed media types |
| `@Context` | Inject the context information |

In order to use RESTeasy in your Quarkus application you need to include the 
`quarkus-resteasy` extension in your Maven dependencies, as follows:

    ...
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy</artifactId>
    </dependency>
    ...

# Your first Quarkus RESTful service

The RESTeasy documentation, as well as the Jakarta RESTful Web Services specifications,
are at your disposal for more details about the use of the classes, interfaces 
and annotations summarized in the tables above. However, a more empiric approach,
for unatients, is to look at an example.

Throughout this booklet, in order to illustrate the presented material, we'll be
using a real world use case consisting in a simplified order management system.
This use case is implemented as a Maven multi-module project and the code source
can be found at https://github.com/nicolasduminil/50-shades-of-rest.git.

In order to get the code source, to build and test it, proceed as follows:

    $ git clone https://github.com/nicolasduminil/50-shades-of-rest.git
    $ cd 50-shades-of-rest
    $ mvn install

The last command will compile the source code, package it in a Quarkus *fast JAR*,
execute the unit and integration tests and deploy the Maven artifacts in the 
local repository. Please notice that, as a multi-module project, the `install` 
command is mandatory. If you only run `package` or `test`, you might experience
exceptions due to the fact that common shared artrifacts aren't deployed in the
local Maven repository.

Once that you did a first Maven build and successfuly executed the tests, you 
can look at each Maven submodule. They are presented below.

## The domain module

The Maven subproject `orders-domain` in our GitHub repository shows the classes
which are parts of the orders management business model. They are divided in 
two packages: 

  - the package `dto` containing the *data transfer object* classes `CustomerDto` and `OrderDto`;
  - the package `jpa` containing the JPA (*Java Persistence API*) classes `Customer` and `Order`;

The figure below shows the class diagram of the domain layer.

<img src="orders.png" width="300" height="300" />

As you can see, our simplified model consists in only two entities: order and 
customer, each one being represented by a DTO and a JPA class. The DTO class is
purely functional, defining the properties and methods required from a strict 
business point of view, while the JPA class includes, in addition, persistence 
logic. 

Looking at the DTO classes, you'll see that they are, in fact, records. And you'll
notice also that the `OrderDTO` is in an association relationship with `CustomerDTO` 
as it contains the `customerId` as one of its properties.

The JPA `Customer` and `Order` classes are in a relationship of one-to-many 
bidirectional shown below:

    @Entity
    @Table(name = "CUSTOMERS")
    @Cacheable
    public class Customer
    {
      @Id
      @GeneratedValue
      private Long id;
      @Column(name = "FIRST_NAME", nullable = false, length = 40)
      private String firstName;
      @Column(name ="LAST_NAME", nullable = false, length = 40)
      private String lastName;
      @Column(name = "EMAIL", nullable = false, length = 40)
      private String email;
      @Column(name = "PHONE", nullable = false, length = 40)
      private String phone;
      @OneToMany(mappedBy = "customer")
      private List<Order> orders;
      ...
    }

Here a `Customer` maintains the list of its associated `Order` instances, i.e. 
the orders passed by the given customer.

    @Entity
    @Table(name = "ORDERS")
    public class Order
    {
      @Id
      @GeneratedValue
      private Long id;
      @Column(name = "ITEM", nullable = false, length = 40)
      private String item;
      @Column(name = "PRICE", nullable = false)
      private BigDecimal price;
      @ManyToOne
      @JoinColumn(name = "CUSTOMER_ID", nullable = false)
      @MapsId
      private Customer customer;
      ...
    }

As you can see, an `Order`is in a relationship of *many-to-one* with its associated
`Customer`, i.e. the customer having passed the given order. While an order is 
associated to one and only one customer, a customer is associated to one or more
orders. Please notice also the use of the `@MapsId` annotation which allows the 
two JPA entities, `Customer` and `Order`, to share the same database primary key.

The idea is that a *one-to-many* or a *many-to-one* relationship is implemented by 
using a parent-child hierarchy at the database level. In this case the table 
associated to the `Customer` entity would be the parent, while the one associated
to the `Order` entity would be the child. This parent-child relationship is defined
via a foreign key on the child side, referring to the parent. Hence, the child 
database table will have a primary key, to uniquely identify an order, and a 
foreign key, to identify the customer to which it belongs. By sharing the primary
key between these two database table, no foreign key is required and, consequently,
our data model is simpler.

## The repository module

Once we defined our business case domain model, we need to implement the persistence
logic responsible for mapping JPA entities to database tables and conversely. 
Quarkus provides support for JPA through its [Hibernate](https://hibernate.org/) ORM (*Object Relational
Mapping*) implementation, which makes possible complex mappings and queries. But
in order to facilitate even more these operations, Quarkus provides [Panache](https://quarkus.io/guides/hibernate-orm-panache).
To use it, the Quarkus extension `quarkus-hibernate-orm-panache` is required, as shown bellow:

    ...
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-orm-panache</artifactId>
    </dependency>
    ...

Panache provides the following two persistance design patterns:

  - *Active Record*. In this case the entity has to extend the `PanacheEntity` class and, this way, it encapsulates both database access and domain logic.
  - *Data Access Object Repository*. In this case the entities are purely POJOs (*Plain Old Java Objetct*) and, as such, don't need  to extend any class. They handle only the domain logic while the database access is handled by the repository implementation.

In our example we're using the 2nd approach, the Repository pattern, as it seems
more suitable, thanks to the concern separation that it provides.

The repository module can be found in the `orders-repository` subproject on GitHub.
Let's look quickly at the class `CustomerRespository`:

    @ApplicationScoped
    public class CustomerRepository implements PanacheRepository<Customer> {}

That's all. The UML diagram below shows the class hierarchy of our `CustomerRepository`.

<img src="CustomerRepository.png" width="300" height="300" />

As you can see, the `CustomerRepository` class implements `PanacheRepository` interface
which, in turn, extends the `PanacheRepositoryBase` one. This way our class 
benefits already of the most common CRUD operations, like `persist(...)`, `flush()`,
`find(...)`, `list(...)` or `stream(...)`, as well as a few queries.

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

