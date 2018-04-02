---
layout: page
toc_title: "Core"
title: "Providence Core"
category: core
date: 2018-02-01 12:00:00
order: 1
---

The core providence library contains classes that should handle and enable
the generated code, except for some special cases. It handles the descriptors,
base classes for the models and model builder themselves and common utilities
for working with the model classes. There are interfaces for using the
services, but not the implementations for working with them, see
[providence-core-client](../providence-core-client/index.html) and
[providence-core-server](../providence-core-server/index.html) modules for more
on using the services.

**PS:** *Note that for the most part, the user should not need to worry about
these libraries, with some exceptions.*

## Base Classes And Utilities

As described in [providence models](../models.html), there are generated
methods for getting each field from the structures, and how to build it. But
as providence is built to be self-reflective / self-describing, these same
classes need more generic methods to be able to build and read the model
content without relying on java reflection. Notable features are following.

### PMessage and PMessageBuilder

All structures implements a base interface called the `PMessage`. It has
utility methods to dynamically check and get the content of the message.
Both classes have a `descriptor()` method that returns the descriptor for
the message. See below for details. Also the message implementation class
will always have a `kDescriptor` constant containing the same.

The functionality of the `PMessage` is basically only the `get(i)`, `has(i)`
methods, and the more notable:

- `mutate()`: Returns a builder that further builds upon / mutates the content
  from the instance.
- `mergeWith(Message other)`: Essentially just `mutate().merge(other).build()`,
  but handy to have in order to quickly make messages by merging two of the
  same type.

The `PMessageBuilder` has a bit more features to be noted. These are added
mainly in order to facilitate "smarter" ways or handling updates and
changes.

- `valid`() and `validate()` checks if, and fails if not (respectively) the
  current content of the message builder constitutes a valid message when
  comparing it to the field requirements of the type.
- `isSet(field)` and `presentFields()` are respectively returning true if
  the specified field has a value, and the list of fields that has a value
  at time of calling.
- `isModified(field)` and `modifiedFields()` are respectively returning true
  if the specified field has been modified since the builder was created, and
  the list of fields that has been modified since the builder was created at
  time of calling.
- `mutator(field)` returns a contained `PMessageBuilder` for the specified
  field. The returned builder is "contained" in the outer builder so that
  any modifications to this builder will affect the contained message.
  Corresponds to the `mutableMyField()` (if field named 'my_field') method.

### Descriptors

As noted above, providence is self-describing. This is done via a set of
`PDeclaredDescriptor` implementations, one for each generated base class.
As a user, you should usually only need to know that all the generated
classes have a `kDescriptor` constant, which is passed into serializers
and other utilities to represent the type.

The descriptors also have some niceties that can be used to represent
the generated class.

## Serialization

Serialization and deserialization of providence models are done through the
`Serializer` interface. It's main interface are the `serialize` and
`deserialize` methods. Both methods will to it's job and return if and
*only if* the read or write operations succeeded.

* `int serialize(OutputStream os, PMessage m)`: Serialize message with the
  serializer onto the output stream. Since the message is self-describing no
  descriptor is needed. It should return the number of bytes written.
* `T deserialize(InputStream is, PDescriptor<T> t)`: Read an object of type T
  with the descriptor t from the input stream. The object does not have to be a
  Message, any serializable type should work.

The available serializers are as follows:

* **BinarySerializer**: A native providence version of thrift's
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
* **PrettySerializer**: A serializer format for making and reading a pure human
  readable format. The format is also used in the providence config, but with
  some extra features. See [providence-config](../providence-config/index.html)
  for details on the pretty config format.

### Serializer Providers

Often when using serializers a system may need to be able to select a fitting
serializer for the current task. This is done with the `SerializerProvider`
interface, which simply lets a service get a serializer based on a media
type string.

### A Note on Strictness

All of the base serializers have a `strict` option. And this controls how strict
the parser or generator can be when reading and writing the messages. In essence:

- If strict is `true`, only data accepted by the generated code made by
  `apache thrift` will be accepted. Meaning:
    - If a `required` field in a message is missing, reading it will fail. E.g.
      validation failure.
    - If a map key or set value cannot be resolved, it will be 'null' and fail
      reading. This is mostly only applicable to enums reading an unknown value.
- Otherwise the failures above will be handled as:
    - `required` fields are handled as if they were `optional_in_required_out`,
      a.k.a. `default` requirement.
    - Unresolved enum values for map keys and set values are just ignored.
- If strict is `false`, the examples above will be accepted, but a number of
  other issues will still fail reading a message:
    - If any value does not have a compatible wire format to it's described type,
      the read will fail. This may happen if fields change type, or if two
      separate services have added a field with the same ID (or name for some
      serializers).
    - If any value fails to be parsed (for the requested type), e.g. bad string
      utf-8 entities, too large (or small) number for parsed `i*` types, etc.

Writing a message should only fail if writing to the stream fails. A message
usually *cannot* contain an invalid value, since fields are validated on
construction by the builder.

### Example use of Serialization.

In the simplest form, serializers are used plainly on input and output
streams.

```java
class Example {
    void write(MyMessage message) {
        BinarySerializer serializer = new BinarySerializer();
        FileOutputStream out = new FileOutputStream(new File("test.data"));
        serializer.serialize(out, message);
    }

    MyMessage read() {
        BinarySerializer serializer = new BinarySerializer();
        FileInputStream in = new FileInputStream(new File("test.data"));
        return serializer.deserialize(in, MyMessage.kDescriptor);
    }
}
```

## Message I/O

Since using the Serializers require knowing more about the source of and how to
handle multiple message in one etc, there is a MessageIO library that
simplifies some common tasks. The interface is mostly a simplification of use
of the Serializers, making specialized `MessageReader` and `MessageWriter`
classes.

- `IOMessageReader` and `IOMessageWriter` are simply wrappers around input stream
  and output stream that handles streaming multiple messages, with optional
  internal separators (e.g. for JSON).
- `FileMessageReader` and `FileMessageWriter` writes to and reads from a single
  file that is appended to and read from as a stream of messages, also with optional
  internal separators.

### Streams

Convenience methods for creating java 8 streams from an input stream containing
providence data. These are mostly for convenience, but with added handling of
a stream of content, meaning multiple messages in a row in the same stream. It is
mostly just wrappers around the two `MessageReader` & `MessageWriter` implementations.

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

### Storage

If you need a simple data storage layer for providence messages, take a look at
the [providence-storage](../providence-storage/index.html) module.
