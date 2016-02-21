Providence Serialization 
========================

The `providence` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages. It is mainly
based on the Facebook / Apache [thrift](https://thrift.apache.org/) library,
but with some differences and limitations.

## Documentation

Documentation for the various parts and modules.

* The [Generated Java Interface](docs/java-generated.md)
* The [Java Libraries](docs/java-libraries.md)

And documentation of the wire-formats

* The [JSON format](docs/serializer-json.md)
* The [Fast Binary format](docs/serializer-fast-binary.md)
* The [Binary Format](docs/serialzer-binary.md).

Old documentation (Out of date)

* **DEPRECATED**: Compact [binary format](docs/backup/compact-binary.md). This
  was the first binary format made for providence. It is not compatible with
  anything special, and is neither very compact, not especially fast.
* Generated [java code](docs/backup/generated-java.md). Don't look at this, it
  is really out of date.
* Testing [read & write speed](docs/backup/speedtest.md). Testing data used
  during development to ensure the speed was up to standard with the original
  thrift library.

## Setup

In order to compile thrift-j2, you need:

- `bazel` Found at [bazel.io](https://bazel.io/) is used as primary build system.
- `java` Is the compiler and runtime. I recommend using `openjdk8-jdk`.
- `maven` Is

Bazel was chosen as build tool for it's good support for multiple binaries and
projects within the same codebase. This project has at least 2 binaries
(compiler and converter), and lots of JAR libraries to generate.

### Ubuntu Linux Setup

Sadly there are no default APT package for buckbuild and android, so it has to
be installed from source. The location does not matter, as long as it's
available in the PATH.

```
# git clone git@github.com:morimekta/providence.git providence
# cd providence
# bazel build //:release
```

# Differences with Thrift

providence is not *exactly* like thrift. The differences are mostly minute, but
all feature changes are there for a reason. Differences are based on the IDL at
[thrift.apache.org](https://thrift.apache.org/docs/idl).

## Alterations to thrift IDL

Constant data syntax has to follow JSON syntax. So ',' are mandatory in list
item separation, '"' is only string literal char. Exception is that map keys
can be the actual value. Enum values cannot be used as a generic "number"
reference in constants, only to set the enum value itself.

Fields with conflicting but differing names are explicitly disallowed. E.g. 
"my_field" and "myField" is considered conflicting because they would generate
the same names when c_casing or camelCasing the name.

## Added support

There are also extra features added that the original Apache Thrift format does
not support, or is only supported on very limited cases.

### Circular containment

Circular containment is explicitly supported as long as the entire circle is
contained within the same thrift definition file. E.g.:

- struct A contains struct B contains struct A. See
  [calculator.thrift](core/res/definitions/calculator.thrift).
  struct Operation contains union Operand contains struct Operation.

This makes model structures like the calculator possible. Since the model
objects are immutable and created with builders, it is not possible to create
a circular instance containment.

### Compact Messages

A struct may be defined as `compact`. A compact struct must adhere to the
compact criteria:

- Only structs may be compact (not union or exception).
- May have a maximum of 5 fields.
- Fields must be numbered 1 .. N.
- A required field may not come after an optional field.
- For the 'compact' serialization to take effect, the first M fields must be set,
  and no other fields may be set. E.g.:
  * If 1, 2 abd 3 are set, and 4, 5 are not set, then compact is used.
  * If 1, 2 and 4 are set, and 3, 5 are not set, compact is **not** used.

When the compact struct is serialized the serializer may choose to use a
different serialization format that serializes the first M fields of the struct
in order. E.g. in JSON a compact struct may be serialized as an array if (and
only if).

Messages will have a `isCompact()` method that determines if the message is
compact compatible for serialization. Descriptors will have a similar
`compactible()` method which determines if the message can be deserialized with
the compact format.

This mimics the way thrift serializes service method calls, but in a way that
is generic to all messages.

To annotate a struct as compact, add the `@compact` annotation to the struct
comment. This will still allow thrift compiler to parse the .thrift files.

### Simple Messages.

A simple message is one that does not contain nested structures, e.g. no containers,
and no internal messages. Simple messages is easier to use as map keys, and some
serialization formats may require that map key messages to be simple. Note that
the simple definition is on the type, not the message.

### Reserved words.

Each format should make getters, setters and mutators, field names etc that are
compatible with the language using prefixes, suffixes and name modifications.
E.g. the field name 'public' should be allowed for all languages, oncluding
java and C++ where it is a reserved word.

In Java:
- field names are prefixed with 'm'. E.g. 'my_field' becomes mMyField.
- getters and setters are prefixed with 'set', 'addTo', 'get', 'clear', 'has' and 'num'.
  respectively.
  
Or in C++ or python
- field names are suffixed with '\_'. E.g. 'my_field' becomes 'my\_field\_'.
- getters and setters are prefixed with 'set\_', 'get\_', 'mutable\_', 'clear\_' 'add\_to\_', 'has\_' and 'num\_'.
- This breaks the convention that getters should not have any prefix or suffix.

## Removed support

`*_namespace` keywords are not supported. Use `namespace php [package]` instead
for php. XSD is AFAIK a facebook specific namespace, which is also not
supported.
 
None of the facebook specific modifiers are supported. They are also removed
from keyword lists (xsd_nullable, xsd_optional). Any extra annotations has to
be part of the comments.

# Structure

The library packages are:

* `core`  The core thrift message handling. Needed to use thrift objects, and
        contains simple to use serialization and deserialization modules, thrift
        IO, debugging tools (pretty printer).
* `core-jackson` Core jackson helper classes (specifically for serializing and
        deserializing Binary data). Is needed if the '--jackson' option is set
        during source generation. 
* `reflect` Reflective library that reads thrift dynamically. This library
        makes full-fledged in-memory representations of thrift struct types,
        and can be used to parse and print serialized thrift messages.

Extra libraries.
       
* `messageio` Classes for stream-lining IO with messages.
        TODO: Also move this to 'extra'.
* `thrift` Thrift protocol compatibility library. Contains serializers that
        wrap the native TProtocol classes from Apache thrift. 

In addition the are some utility packages.

* `tools` Tools for helping out with providence. Compiler, converter and bazel
        helpers. The converter 
        The compiler app generates code using the libraries. Can currently
        compile:
    * java2 (the format it itself uses).
    * thrift (re-generating thrift files).
    * json (serialized format representing the thrift).

There are other folders with internal utilities, docs, etc.

# Contributing

Import the style settings from the `docs/` folder into Eclipse or IntelliJ.
If you use a simpler editor follow these guidelines:

- 4 spaces indents, no tabs **ever**.
- Indent into columns (makes it more readable).
- Preserve style as much as possible.

Clone [this project](https://github.com/morimekta/thrift-j2), and create a
[pull request](https://github.com/morimekta/thrift-j2/pulls) for your change.

Make sure to name it properly, and describe chat the change does, and assign
the request to [morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
