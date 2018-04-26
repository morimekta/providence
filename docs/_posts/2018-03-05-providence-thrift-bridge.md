---
layout: page
toc_title: Thrift Bridge
title: "Providence Utils: Thrift Bridge"
date: 2018-03-05 12:00:00
category: util
order: 5
---
The providence - thrift bridge is a module to enable complete compatibility
with `Apache Thrift`. It contains a set of serializers that wrap the thrift
TProtocols, and clients capable of talking with the simple "Server" and the
"NonBlocking" thrift server.

### Serialization

The T* serializers are a number of serializers that wrap the TProtocol
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

This can be found in the `providence-thrift-protocol` module.

### Service Compatibility

This module adds server and client implementations that can task to standard
thrift service implementations. There are both client and server classes
for each type of server.

- `SocketServer` is a simple TCP socket server talking with one client at
  a time. Each connection will bind up a thread and will only handle one
  service call at a time. See `SocketClientHandler` and `SocketServer`
  classes. The providence client is thread safe, and will queue up messages
  to be handled internally.

- `NonblockingSocketServer` is a more advanced TCP socket server that uses
  framed buffer messages between the client and server, and can handle calls
  and responses in parallel and out of order. It uses a more complex socket
  channel system to handle messages internally. The client is thread safe
  and can be handled by multiple threads at a time over the same channel.
  The server has a shared thread pool used to provision workers to handle
  each call. See `NonblockingSocketClientHandler` and `NonblockingSocketServer`
  classes.

This can be found in the `providence-thrift-compat` module.
