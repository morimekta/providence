## Providence Extra : Thrift Bridge

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
