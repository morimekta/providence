The Java Libraries
==================

Overview over the java libraries, and how to use them properly. Most of the
library should **not** be used directly, only the generated interfaces.
But some specific interfaces are useful.

## Providence : Core

The `providence-core` library has libraries to handle the messages themselves.
With the exception of the `PSerializer` class and implementations, you
should usually not need to worry about the library parts themselves. The
serializer interface has 3 noteworthy methods:

* `int serialize(OutputStream os, PMessage m)`: Serialize message with the
  serializer onto the output stream. Since the message is self-describing no
  descriptor is needed. It should return the number of bytes written.
* `int serialize(OutputStream os, PDescriptor<T> t, T value)`: Serialize the
  value described by the descriptor t onto the stream. Note that the value type
  T and descriptor has to match, otherwise an exception will be cast. It should
  return the number of bytes written.
* `T deserialize(InputStream is, PDescriptor<T> t)`: Read an object of type T
  with the descriptor t from the input stream. The object does not have to be a
  Message, ant serializable type should work.

The available serializers are as follows:

* **TBinarySerializer**: **TBD** This class is to be replaced with a serializer
  that replicates the TBinaryProtocol.
* **TFastBinarySerializer**: A compact and efficient serialization format. The
  serializer is designed to have a balance of computational and data
  efficiency.
* **TJsonSerializer**: A general purpose JSON serializer. This should be able
  to read most generic JSON formats into providence structs that match with
  name to field type mapping.

It also contains a `PPrettyPrinter` class that similar to the serializers
can serialize messages to an easy to read, and easy to compare (e.g. line by
line) format. But it does not have any deserializer yet, so is not a full
serializer format (yet).

**TODO(morimekta):** Make the PPrettyPrinter a full-fledged serializer with
both serialization and deserialization. Note that it's OK if this is a slightly
lossy serialization / deserialization, and not fast at all, as it is meant
purely for testing and debugging.

## Providence : Core - Jackson

For jackson serialization, which may be needed for some general purpose
storage systems, e.g. Hibernate, we provide full jackson2 integration.
To enable jackson support, generate source code with `--jackson` option,
and add the `providence-core-jackson` dependency (needed for binary
value support), and `jackson-databind` 2.x itself.

This will annotate the generic constructor, and the field getters with
jackson annotations to locate which values to read and handle as what.
The resulting JSON should be compatible with the JSON serialization
format, though is not as efficient for serialization / deserialization
as the PJsonSerializer.

## Providence : Core - Streams

Java 8 added streams as a way to produce and consume data streams. Since
Android only supports java 1.7 so far, this is separated in it's own library
at `providence-core-streams`. It contains convenience methods like:

* `MessageStreams.file(File f, PStructDescriptor d)`: Consume messages from
  the file one at a time onto the stream. Will use the first byte(s) as an
  indicator of the serialization format.
* `MessageStreams.file(File f, PSerializer s, PDescriptor d)`: Consume
  messages from the file using the given serializer one at a time onto the
  stream.
* `MessageStreams.resource(String r, PSerializer, s, PDescriptor d)`: Consume
  messages from the java resource using the given serializer one at a time
  onto the stream.
* `MessageStreams.stream(InputStream in, PSerializer, s, PDescriptor d)`: Consume
  messages from the input stream using the given serializer one at a time
  onto the stream.
* `MessageCollectors.toFile(File f, PSerializer s)`: Collect messages and write
  them to file with the given serializer.
* `MessageCollectors.toStream(OutputStream os, PSerializer s)`: Collect
  messages and write them to the given output stream with the given serializer.

## Providence : Reflect

The `providence-reflect` library's purpose is to be able to reflect thrift
descriptors without having to invoke generated code. It has purposes like
conversion for debugging tools, and parsing and generating struct descriptors
in order to generate providence code (see `providence-tools-generator`).

## Providence : Testing

The `providence-testing` library is meant for testing and comparing providence
messages. Notable methods are:

* `MessageMatchers.messageEq(PMessage m)`: Creates a matcher to use with
  junit's `Asserts.assertThat(T actual, Matcher<T> matcher)`. Example:
  `Asserts.assertThat(actual, MessageMatchers.messageEq(expected))`. The
  printed output on a mismatch is constructed to be as useful as possible
  when a lower number of differences appear (<10), printing single differences
  per line.

## Providence : Thrift Bridge

The providence - thrift bridge is a module to enable all the TProtocol
formats for use in serializing providence messages. This goes through the
`TProtocolSerializer` base class, extended for each thrift protocol.

* **TBinaryProtocolSerilizer**: The default thrift binary format.
* **TCompactProtocolSerilizer**: The thrift 'compact' binary protocol.
* **TJsonProtocolSerializer**: The thrift-JSON protocol format.
* **TSimpleJsonSerializer**: For completeness only: Can only serialize,
  and not deserialize, to a "simple" JSON format similar to PJsonSerializer.
* **TTupleProtocolSerializer**: A very compact, but non-version-safe
  thrift protocol format. It uses bitmaps with simple order of fields to
  know what is present, and just assumes everything is formatted the
  correct way.

## DEPRECATED LIBRARIES:

* `providence-messageio`: Should not be used now that streams are supported.
  **TODO(morimekta):** Move over to the `providence-external` project if I'd
  like to keep it around at all.
