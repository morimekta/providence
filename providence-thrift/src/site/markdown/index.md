Providence Utils : Thrift
=========================

The providence - thrift bridge is a module to enable complete compatibility
with `Apache Thrift`. It contains a set of serializers that wrap the thrift
TProtocols, and clients capable of talking with the simple "Server" and the
"NonBlocking" thrift server.

### Serialization

The T* serializer are a number of serializers that wrap the TProtocol
implementations in `libthrift`. This way all of the thrift protocols
**can** be handled in providence, including the `thrift json` protocol
and the `compact` protocol. The `TBinaryProtocolSerializer` generates
the same binary as the `BinarySerizlier`, but since it is wrapped and
reflective is way slower. The available serializers are:

* **TBinaryProtocolSerializer**: The default thrift binary format. Same
  as the `BinarySerializer` format.
* **TCompactProtocolSerializer**: The thrift 'compact' binary protocol.
  This protocol is similar to `binary` but uses zigzag numeric encoding
  to save space.
* **TJsonProtocolSerializer**: The thrift-JSON protocol format. This is
  **NOT** the same as the `JsonSerializer` format, but follows some weird
  type-wrapping to make it compatible with the TProtocol interface and
  follow the generated thrift code parsing expectations.
* **TSimpleJsonSerializer**: For completeness only: Can only serialize,
  and not deserialize, to a "simple" JSON format similar to, but not
  identical to `JsonSerializer`. What this produces can be **read** by the
  `JsonSerializer`.
* **TTupleProtocolSerializer**: A very compact, but non-version-safe
  thrift protocol format. It uses bitmaps with simple order of fields to
  know what is present, and just assumes everything is formatted the
  correct way.

### Service Clients

Two extra service clients are added, corresponding to the `SocketServer`
and the `NonblockingSocketServer` server implementations in `libthrift`.

- `SocketClientHandler` corresponds to the thrift `SocketServer`
  single-port single-protocol server. Except: The providence client handler
  is thread-safe as opposed to the apache thrift client.
- `NonblockingSocketClientHandler` corresponds to the thrift
  `NonblockingSocketServer`, which is "designed" to be able to handle a stream
  of requests without blocking in between on the same port. _**PS:** I believe
  the thrift implementation fails completely, as the apache thrift client is
  not capable of handling parallel requests due to it's sequence number
  handling._
