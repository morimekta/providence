---
layout: page
toc_title: "Core : Server"
title: "Providence Core : Server"
category: core
date: 2018-02-03 12:00:00
order: 3
---

Code for handling the server side of client-server communication. It contains
the actual server implementations, and helpers to make working with the
providence services more powerful. Since some of this requires a fair bit of
dependencies it is kept separate from the rest of providence.

The servlets are designed to be easy to use with any javax servlet system (j2ee,
jetty etc), example setup of a providence servlet (where `MyServlet` is the
thrift defined service, and `MyServletImpl` is the implementation of the
interface):

```java
/**
 * Simple jetty server with a providence / thrift HTTP servlet.
 */
class MyServer {
    public void start() {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(
                                   new ProvidenceServlet(
                                           new MyServlet.Processor(new MyServletImpl()),
                                           new DefaultSerializerProvider())),
                           "/thrift");
        server.setHandler(handler);
        servet.start();
    }
}
```

### Request Aware Servlets

The class to note here is the `ProvidenceServlet`. The processor servlet is
designed to handle two different cases, the "standard" thrift way where the
internal implementation of the service is insulated from the transport layer,
and a "web servlet" variant, where each HTTP request generates a new processor
instance using the `ProcessorProvider` interface. This way the providence
servlet can react to out-of-bound context information, e.g. results from
Geo IP filtering, HTTP authorization, etc. E.g.:

```java
/**
 * The server can be set up like this. Note the function reference instead of
 * the processor instance, and the filter. The BarFilter in this instance will
 * set the 'bar' attribute on the request object in the same way many
 * information annotating filters operate.
 */
public class MyServer {
    public void start() {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addFilter(new FilterHolder(new BarFilter()),
                          "/thrift",
                          EnumSet.of(DispatcherType.REQUEST));
        handler.addServlet(new ServletHolder(
                                   new ProvidenceServlet(
                                           MyServletImpl::makeProcessor,
                                           new DefaultSerializerProvider())),
                           "/thrift");
        server.setHandler(handler);
        servet.start();
    }
}

/**
 * Set up your servlet implementation like this. It is important to use the
 * constructor to be able to keep the instance of the HttpServletRequest. It
 * is also possible to do more of the work in the makeProcessor call below
 * instead of in the constructor, and rather pass processed arguments, injected
 * systems etc.
 */
public class MyServletImpl implements MyServlet.IFace {
    public MyServletImpl(HttpServletRequest request) {
        this.request = request;
        // ... initialize handler instance
    }

    @Override
    public MyFirstResponse doSomething(MyFirstRequest request) {
        if (request.getAttribute("bar") != null) {
            // Do something by reacting information set by the BarFilter
            // request filter. Se below.
        }

        return MyFirstResponse.builder()
                              .setSomething("Something else")
                              .build();
    }

    public static PProcessor makeProcessor(HttpServletRequest request) {
        return new MyServlet.Processor(new MyServletImpl(request));
    }
}
```
