---
layout: page
toc_title: "RPC Tool"
title: "Providence CLI Tool : RPC Tool"
category: cli
date: 2018-06-03 12:00:00
order: 3
---

The providence RPC tool `pvdrpc` is a program designed to test out thrift and
providence RPC service calls based on the same ad-hoc type parsing as `pvd`
uses. It supports HTTP servlet wrapped RPC (e.g. using the `TServlet`), the
`TSimpleServer` direct socket RPC, and the `TNonblockingServer` that also wraps
the calls in sized frame buffers.

The providence RPC tool connects to a remote service, and sends a service call
(which can be parsed from files the same way as the data files above), and sent
as an actual remote procedure call:

Given a simple thrift service file `test.thrift`:

```thrift
struct MyTest {
    1: optional string       test;
    2: optional i32          seq;
    3: optional list<string> tags;
}

exception MyFailure {
    1: string message;
}

service MyService {
    MyTest test(1: MyTest request) throws (1: MyFailure failure);
}
```

And code that just returns the same object as requested as the implementation. Then
given the request `req.json` file:

```json
[
  "test",
  "call",
  7,
  {
    "request": {
      "test": "my test",
      "seq": 44,
      "tags": [
        "first",
        "second"
      ]
    }
  }
]
```

Which is the RPC call data, and the shell command:

```sh
cat req.json | pvdrpc -I thrift/ test.MyService http://localhost:8080/test_service
```

Which would then:

- Parse the thrift IDL in thrift/ directory, pick out the test.MyService service
  from there.
- Read the req.json request call (which needs to follow the desired input format's
  service call syntax), and use that to send the actual RPC call to the remote
  service at `http://localhost:8000/test_service`.
- Parse the response (in whatever format returned) and print out to standard out
  so the user can read it. E.g.:

```json
[
  "test",
  "reply",
  7,
  {
    "success": {
      "test": "my test",
      "seq": 44,
      "tags": [
        "first",
        "second"
      ]
    }
  }
]
```

Or if a non-200 HTTP response is received will print out the error message
received.

#### Supported Protocols

Short overview over the RPC protocols supported by the RPC tool.

- `http://` and `https://`: Connects to a thrift `TServlet` or similar over `HTTP/1.1`
  and with TLS / SSL handshake and encryption with `https`.
- `thrift://`: Connects to a `TSimpleServer` type thrift server.
- `thrift+nonblocking://`: Connects to a `TNonblockingServer` type thrift server,
  or a similar thrift server that wraps messages in `TFramedTransport`.

**PS:** A note of warning: The `TSimpleServer` and `TNonblockingServer` **will** crash if
try to connect to it with `https://` because of trying to allocate multiple gigabytes of
RAM for the request (TNonblockingServer) or for the method name (TSimpleServer).

#### The Service Call Syntax

Note that in the two json structures shown there is the serialized service call
data in addition to the request and response wrappers around the actual request
and response objects. The format here is:

`["${method}", ${type}, ${sequence}, ${wrapper}]`

Where the type can be 1 / "call", 2 / "reply", 3 / "exception", and 4 / "oneway".
The sequence number is something the transport layer (http, client etc) can
use to match response messages to the correct caller, and the wrapper is
either the `request params wrapper` the `response wrapper`, or the
`application exception`. Oneway calls are calls the does not want a response.

- The `request params wrapper` message is a generated thrift struct that
  contains all the method params as field values. E.g. the method
  `ResponseStruct my_test(1: i32 num, 2: string text)` would become
  equivalent to the struct:

    ```thrift
    struct my_test___params {
      1: i32 num
      2: string text
    }
    ```

- The `response wrapper` is a wrapper of the return type (put in a field
  named `success`), and of each possible exception put in a union. In order
  to make it impossible to conflict with field IDs, the success field gets
  the index `0`, which is not allowed to declare. E.g.:

    ```thrift
    union my_test___response {
      0: ResponseStruct success
      1: MyException ex1
    }
    ```

- The `application exception` is a generic providence exception message that can
  be passed with call type 3 (exception), usually to tell a client about a
  problem that was caused outside of the actual service call, e.g. serialization
  problems, bad method call, etc. It is equivalent to:
  
    ```thrift
    exception ApplicationException {
        1: string message
        2: i32 type
    }
    ```
