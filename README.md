Immutable Thrift Java library
=============================

The `thrift-j2` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages.

# Documentation

* Compact [binary format](docs/compact-binary.md).
* Compact [JSON format](docs/compact-json.md).
* Generated [java code](docs/generated-java.md).
* Testing [read & write speed](docs/speedtest.md).

# Setup

In order to compile thrift-j2, you need:

- `bazel` Found at [bazel.io](https://bazel.io/) is used as build system.
- `android-sdk` Found at
  [developer.android.com](https://developer.android.com/sdk/installing/index.html?pkg=tools)
  is used for binding of android builds.

Bazel was chosen as build tool for it's good support for multiple binaries and
projects within the same codebase. This project has at least 2 binaries
(compiler and converter), and lots of JAR libraries to generate.

## Ubuntu Linux Setup

Sadly there are no default APT package for buckbuild and android, so it has to
be installed from source. The location does not matter, as long as it's
available in the PATH, and android SDK it located at the `ANDROID_HOME`
location (you may have to set up the env variable yourself).

Make sure to install android API 16 / 4.4 (JELLYBEAN).

```
# git clone git@github.com:morimekta/thrift-j2.git thrift-j2
# cd thrift-j2
# bazel build //...
```

# Differences

thrift-j2 is not *exactly* like thrift. The differences are mostly minute, but
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

Messages will have a `compact()` method that determines if the message is
compact compatible for serialization. Descriptors will have a similar
`compactible()` method which determines if the message can be deserialized with
the compact format.

This mimics the way thrift serializes service method calls, but in a way that
is generic to all messages.

To annotate a struct as compact, add the `@compact` annotation to the struct
comment.

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
       contains simple to use serialization and deserialization modules, thrift IO,
       debugging tools (pretty printer).
* `reflect` Reflective library that reads thrift dynamically. This library
       makes full-fledged in-memory representations of thrift struct types, and can
       be used to parse and print serialized thrift messages.
* `client` Client libraries for Client-Server service handling. With core is
       the only parts needed for a client side application using thrift service
       API. Contains default client transport and protocol handlers.
* `server` Server libraries for Client-Server service handling.
* `jax-rs` REST wrappers for javax.ws.rs (used by dropwizard etc.).

In addition the are some utility packages.

* `converter` A tool to convert serialized thrift data from one format to
       another or read serialized thrift files to command line.
* `compiler` The compiler app that generates code using the libraries. Can
       currently compile:
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
