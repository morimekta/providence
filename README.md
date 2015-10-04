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
available in the PATH, and android SDK it located at the `ANDROID_SDK_HOME`
location (you may have to set up the env variable yourself).

```
# sudo apt-get install mvn
# git clone git@github.com:morimekta/thrift-j2.git thrift-j2
# cd thrift-j2
# buck build //...
```

The first build may take some time, as `mvn` has to download a plugin with lots
of dependencies in order to download dependencies (yes, you read it right).

If you find a better solution (using buck), send me a pull request!

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
