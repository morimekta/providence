Providence Generator Options
============================

Options available in the java generator.

- `android`: Adds [android.os.Parcelable](options-android.html)
  to messages.
- `jackson`: Adds [Jackson 2.x](options-jackson.html) serialization
  annotations (tested on Jackson 2.7.2 and later).
- `rw_binary`: Adds generated code to serialize and deserialize the
  [binary serializer protocol](serializer-binary.html).
- `hazelcast_portable`: Adds support for
  [hazelcast portable](options-hazelcast.html) and associated functionality.
  This only adapts the annotated part of the declared thrift.
