Providence Core : Streams
=========================

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
