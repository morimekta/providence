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
