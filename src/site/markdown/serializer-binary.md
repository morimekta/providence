Binary Serializer Format
========================

This is the original thrift binary protocol implementation for providence.
See [TBinaryProtocolSerializer](providence-thrift/java/net/morimekta/providence/thrift/TBinaryProtocolSerializer.java) for now.

## TODO:

Make a pure providence implementation of this protocol. At least in theory
that should be faster than using the wrapped TProtocol.
