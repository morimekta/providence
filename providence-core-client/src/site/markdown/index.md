Providence Core : Client
========================

Code for handling client side of client-server communication. It contains the
actual client handlers. It should only require the google-http-client library
and otherwise rely on the same dependencies as core providence.

### Simple HTTP Client

It should be minimal work setting up a thrift HTTP servlet. See the `Core : Server`
module of the providence project for that. To use the providence client
toward the HTTP servlet you can set up as this:

```java
public class Something {
    public void doSomething() {
        // Setting up a reusable providence client.
        GenericUrl url = new GenericUrl("http://my.domain.com/thrift");
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        MyService.IFace service = new MyService.Client(
                new HttpClientHandler(() -> url,
                                      requestFactory,
                                      new DefaultSerializerProvider()));

        // The client can be used both in parallel and one at a time. It will internally
        // handle sequence incrementing etc.
        MyResponse response = service.doSomething(MyRequest.builder()
                                                           .setArgumet("an argument")
                                                           .build());
    }
}
```

### Other types of clients

Note that there are more compatibility clients available in the `Utils : Thrift Bridge`
module that enable you to interface against pure binary thrift servers provided by
the Apache Thrift codebase.