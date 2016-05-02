Providence Core
===============

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

* **BinarySerializer**: A native providence version of the thrift's
  **TBinaryProtocol** binary format. It should generate the same serialized
  data as TBinaryProtocol, and be able to parse data back from the same. It is
  the default serializer in the `pvdrpc` tool. See
  [binary serializer spec](../serializer-binary.html) for details.
* **FastBinarySerializer**: A compact and efficient serialization format. The
  serializer is designed to have a balance of computational and data
  efficiency. See [fast binary serializer spec](../serializer-fast-binary.html)
  for details.
* **JsonSerializer**: A general purpose JSON serializer. This should be able
  to read most generic JSON formats into providence structs that match with
  name to field type mapping. See
  [json serializer spec](../serializer-json.html) for details.

It also contains a `PPrettyPrinter` class that similar to the serializers
can serialize messages to an easy to read, and easy to compare (e.g. line by
line) format. But it does not have any deserializer yet, so is not a full
serializer format (yet).

**TODO(morimekta):** Make the PPrettyPrinter a full-fledged serializer with
both serialization and deserialization. Note that it's OK if this is a slightly
lossy serialization / deserialization, and not fast at all, as it is meant
purely for testing and debugging.

## Streams

Convenience methods for creating java 8 streams from an input stream containing
providence data.

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
