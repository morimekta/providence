Providence CLI Tool : Generator
===============================

The compiler (or code generator) `pvdgen`, this can be used in place of the maven
plugin for non-java projects. Out of the box, it can compile for:

- `json`: Simply writes out the thrift definitions as JSON files.
- `java`: Writes the standard providence java generated code.

See `pvdgen --help` for more detailed info about the available options.

## Developing Generator Modules

See [providence-js](https://github.com/morimekta/providence-js) for an example of
how to add a simple external generator. Note that because of the tie-in with the
contained descriptions and the type registries the generators may be pretty
dependent on the providence version installed.

**TODO: Document properly how to add extra generators.**
