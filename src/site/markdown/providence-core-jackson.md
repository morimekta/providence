Providence Core : Jackson
=========================

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
