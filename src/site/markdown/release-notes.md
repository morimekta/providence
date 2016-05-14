Release Notes
=============

Release notes for each release.

## providence 0.1.1 - 14.05.2016

- 'tiny_java' updates:
    - Supports parsing compact JSON messages.
    - Proper handling of unions when serializing.
    - Get the default values correctly based on de-serialized JSON. Proper
      de-serializers. And handle optional fields correctly.
    - Support both named and numeric enum values.
- Better equality handling.
- Generalizing generator copde between java and tiny_java.
- TypeRegistry exception updates and fixes.
- No jackson support in 'java' generator!
- Removed the 'thrift' generator. No point in generating thrift files from thrift files...

## providence 0.1.0 - 07.05.2016

- container builders are properly 'fluent'.
- providence-core-jackson no longer depends on providence-core.
- removed the broken 'proto' format. Use the 'fast' serializer instead.
- MessageReader / Writer to wrap serializers.
- Support providence to native thrift RPC (TServer and TNonblockingServer).
    - New 'core-client' module for RPC related classes. Includes client handlers
      (client's wrap this to get proper RPC). For HTTP, thrift, thirft+nonblocking
      protocols.
- Proper application exception (matching TApplicationException) for
  services.
- Constants expansion.
- Site updates.
- merge java8 streams into main core library, it now compiles with java 8.
- adding a 'tiny' java format.
    - full jackson support.
    - minimal number of methods.
    - still immutable objects + builder.
    - no serialization (except for jackson).
- Tooling updates.
- Proper java-doc on generated classes.

## providence 0.0.2 - 25.04.2016

- BinarySerialize behave as TBinaryProtocol.
- providence-maven-plugin handles test vs compile scope.
- website updates.
- support basic annotations.
- generated code use qualified class names
- using guava for immutable containers.
- no inherited is* methods on structs (because of bool getters).
- support service generation.
- change in fast binary to support field ID 0.
- no more comments in descriptors.

## Providence 0.0.1 - 28.03.2016

Initial development release.
