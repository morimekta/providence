Immutable Thrift Java library
=============================

The `thrift-j2` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages.

# Setup

In order to compile thrift-j2, you need:

- `buck` Found at [buckbuild.com](https://buckbuild.com/) is used as build system.
- `mvn` Found at [maven.org](http://maven.org) is used for dependency resolution.
- `android-sdk` Found at
  [developer.android.com](https://developer.android.com/sdk/installing/index.html?pkg=tools)
  is used for testing of android builds.

Buck was chosen as build tool for it's good support for multiple binaries and
projects within the same codebase. This project has at least 2 binaries
(compiler and converter), and lots of JAR libraries to generate.

## Ubuntu Linux Setup

Sadly there are no default APT package for buckbuild and android, so it has to
be installed from source. The location does not matter, as long as it's
available in the PATH, and android SDK it located at the `ANDROID_HOME`
location (you may have to set up the env variable yourself).

Make sure to install android API 15 / 4.0.3 (ICE_CREAM_SANDWICH_MR1).

```
# sudo apt-get install mvn
# git clone git@github.com:morimekta/thrift-j2.git thrift-j2
# cd thrift-j2
# buck build //...
```

The first build may take some time, as `mvn` has to download a plugin with lots
of dependencies in order to download dependencies (yes, you read it right).

If you find a better solution (using buck), send me a pull request!

# Differences

thrift-j2 is not *exactly* like thrift. The differences are mostly minute, but
all feature changes are there for a reason.

## No supported

`*_namespace` keywords are not supported. Use `namespace php [package]` or
`namespace xsd [namespace]` instead.
 
None of the facebook specific modifiers are supported. They are also removed
from keyword lists.

Fields with conflicting but differing names are explicitly disallowed.

Fields without explicit field IDs. This is true for all of:
- struct, union, exception fields.
- method params, method throws.

## Extra features

There are also extra features added that the original Apache Thrift format does
not support, or is only supported on very limited cases.

### Circular containment

Circular containment is explicitly supported as long as the entire sircle is
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

# Documentation

* Compact [binary format](docs/compact-binary.md).
* Compact [JSON format](docs/compact-json.md).
* Generated [java code](docs/generated-java.md).

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
