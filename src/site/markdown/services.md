Providence Services
===================

Services (a.k.a. thrift services) have a couple of implicit handling around it.
The way this is organized in providence is by defining as much as possible
around the service call handling in thrift as possible.

#### Service Interface

The service is declare initially as a simple interface which mimics the service declaration
from the thrift file, and then logic is added around that. E.g. the
the `ApplicationException` and `IOException` exceptions are added in the
`java` client implementations.

### Service calls

Service calls is a way to handle a request and a response so that the actual
code can work with a service as if it was "just an implementation" of the generated
interface. The call is defined by a 'call type (id)', grouped as request or response.

There are two types of requests:

- `call` (1): Normal method call request. Requires a response (even for void return types).
- `oneway` (4): A void type call request that does not require a response, and can only
  throw an exception if the error occurs on the client side *before* the actual
  call message is sent.

And there are two types of responses:

- `reply` (2): This handled both the "normal" response of the method using the "return"
  type of the method, or any of the declared exception types in the "throws" section
  of the service call definition.
- `exception` (3): Also called 'application exception' is handling of exceptions from
  either the client handlers, or from the providence (or thrift) server handlers
  themselves.

Each actual service call message must have this information:

- The name of the method called, as declared in the service, used by the service
  handler to call the correct service method, and handle message serialization.
- A call type, must be one of the types above.
- A sequence number, which e.g. may be used in a Muxer/DeMuxer used to route
  responses back the the correct caller. A response's sequence number **must**
  be the same as given by the request.
- And a `request`, `response` or `exception` message, which can **NOT** be null
  itself.

#### Application Exceptions

This is a response type where the object is a non-declared exception. E.g. the service
implementation threw an undeclared runtime exception, e.g. a NullPointerException
or IllegalArgumentException, or serialization failed, or there was IO or network problems.
This is wrapped into the "ApplicationException", which all service calls can throw.

The application exceptions are defined as:

```thrift
enum ApplicationExceptionType {
  # Any exception not matching anything below.
  UNKNOWN                 =  0;
  # the requested method (name) is not known to the service.
  UNKNOWN_METHOD          =  1;
  # the serialized message type is not compatible with the locally declared message.
  INVALID_MESSAGE_TYPE    =  2;
  # the method name in the response does not match the call.
  WRONG_METHOD_NAME       =  3;
  # the response sequence ID does not match the one from the request.
  BAD_SEQUENCE_ID         =  4;
  # The response is empty (or null).
  MISSING_RESULT          =  5;
  # Unknown or unhandled exception from the client or service handler.
  INTERNAL_ERROR          =  6;
  # Serialization error.
  PROTOCOL_ERROR          =  7;
  # TODO: Figure out what this type means.
  # AFAIK it is related to transport data problems.
  INVALID_TRANSFORM       =  8;
  # Protocol mismatch.
  INVALID_PROTOCOL        =  9;
  # The client type (???) is not supported.
  UNSUPPORTED_CLIENT_TYPE = 10;
}

exception ApplicationException {
    # Textual message about the exception
    1: string message
    # The application exception type.
    2: ApplicationExceptionType id
}
```

#### Virtual Messages

Each service method will generate a virtual `params` struct, and each
non-oneway service method will generate a virtual `response` struct.
These structs are used to handle the actual communication for the
service method call.

The `params` struct is essentially mimicking the method params as a struct.
E.g.:

```thrift
service Service {
    void call(1: i32 i, 2: MyStruct s) throws (1: MyFailure fail);
}
```

Will generate the request message definition:

```thrift
struct Service.call_request {
    1: i32 i;
    2: MyStruct s;
}
```

For the response, there is generated a virtual struct containing the
response on `success`, using the field ID 0 (which should not be allowed
to be declared, and will not be used by the non-declared auto-IDs for the
exceptions), and one field for each declared exception. E.g. the example
above will generate this response message definition:

```thrift
union Service.call_response {
    0: i32 success;
    1: MyFailure fail;
}
```

Note that since this is declared as a union, one (and only one) of the fields
**must** be set to a non-null value for the response to be valid.
