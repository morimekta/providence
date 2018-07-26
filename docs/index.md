---
layout: page
title: "About Providence"
---

The `providence` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages. It is mainly
based on the Facebook / Apache [thrift](https://thrift.apache.org/) library,
but with some differences and limitations.

Note that providence **requires** java >= 8.

## Getting Providence CLI

See [here](http://www.morimekta.net/) for instructions on how to get morimekta.net
produces binaries / CLI.

## Developer Setup

In order to compile providence itself, you need the java 8 `java` and `javac`
commands (I recommend `openjdk8-jdk`), and `maven` (3.3). Then check out
`git@github.com:morimekta/providence.git` and build with:

```bash
mvn clean install
```

There is also a `Makefile` for updating pre-compiled providence files, and
a `thrift.gradle` file that helps with updating pre-compiled thrift files.
These need `make` and `gradle` installed to work (no gradle wrapper here).

## Terms and Definitions

Throughout the providence documentation I use a number of terms that
easily can be confused.

- **[thrift]**: A system of converting files that follow the `thrift` IDL
  specification found [here](https://thrift.apache.org/docs/idl) to some form of
  code or data. I currently know of 5 `thrift` systems, other than `providence`:
    - [Apache Thrift](https://thrift.apache.org): The "main" thrift implementation
      containing lots of languages.
    - [FB Thrift](https://github.com/facebook/fbthrift): A FaceBook managed fork
      of thrift with changes and features that FaceBook wants.
    - [Thrift-Nano](https://github.com/markrileybot/thrift-nano): A version of thrift
      optimized for low memory consumption e.g. for using on low-power embedded systems.
    - [Thrifty](https://github.com/Microsoft/thrifty): A compact and simple java version
      of thrift aimed at mobile platforms.
    - [ThriftPy](https://thriftpy.readthedocs.io/en/latest/): An alternative python
      library and compiler for thrift.
- **[Apache Thrift]**: Is the "official" Apache hosted implementation of thrift.
  This is essentially what is compared with for determining compatibility.
- **[Providence]**: Is all of this project (including some off-repository parts
  like [providence-gradle-plugin](https://www.github.com/morimekta/providence-gradle-plugin)).

Also inside providence, we use a little different set of definitions from
Apache Thrift.

- **[message]**: Message is the base type of all the structured data typed
  defined in a thrift file, including `struct`, `exception` and `union`.
  This is variantly called `struct` and `base type` in Apache Thrift.
- **[service call]**: Is the wrapper structure that is sent with the
  call to and response from a service method call.
  This is what is called a `message` in Apache Thrift.

## Contributing

You can send me an [email](mailto:oss@morimekta.net) to suggest a feature. Or
you can make it yourself by cloning
[the project](https://github.com/morimekta/providence), and create a
[pull request](https://github.com/morimekta/providence/pulls) for your change.

Make sure to name it properly, and describe what the change does, and assign
the request to [@morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
