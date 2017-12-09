Code Generator for Java
=======================

The java generator is the core of providence, and should act as the source of
truth for all other generated code. The generated code is described at depth
in [Providence Models](../models.html) and in [Providence Services](../services.html).

Options available in the Java generator:

- `android`: Adds [android.os.Parcelable](options-android.html)
  to messages.
- `jackson`: Adds [Jackson 2.x](options-jackson.html) serialization
  annotations (tested on Jackson 2.7.2 and later).
- `no_rw_binary`: Removes generated code to serialize and deserialize the
  [binary serializer protocol](../serializer-binary.html). This can be done if
  not needed on limited platforms to reduce code size.
- `hazelcast_portable`: Adds support for
  [hazelcast portable](options-hazelcast.html) and associated functionality.
  This only adapts the annotated part of the declared thrift.
