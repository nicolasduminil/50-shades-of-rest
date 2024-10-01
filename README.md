# 50 Shades of REST

REST is now 25 years old. The birth certificate of this almost impossible to 
remember acronym (*REpresentational State Transfer*) is considered to be the Y2K
Roy Fielding's doctoral dissertation, which aimed at creating a *standard* software
architecture, making easier the communication between systems using HTTP (*HyperText
Transfer Protocol*).

25 years is a long time and, at the IT scale, it's even much longer. We could 
think that, after a so long period of practising and testing, this paradigm has 
yielded up all its secrets. But no, screams often our daily activity, constraining
us to observe exactly the opposite.

Hence, the idea of this posts series which tries to address the essential aspects 
of this old, yet unknown, web technology, from its most basic features, like verbs
and resource naming conventions, to the most advanced ones, like non-blocking, 
asynchronous or reactive processing, together with the whole diversity of the REST
clients, blocking or non-blocking, synchronous or asynchronous, reactive or classic.

And since in order to illustrate my discourse I need code examples, I chose to
write them in Java, with its [supersonic subatomic dedicated stack](https://quarkus.io/), that needs no
longer presentation.

## A bit of history