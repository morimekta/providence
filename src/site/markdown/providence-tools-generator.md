Providence CLI Tool : Generator
===============================

The compiler (or code generator) `pvdgen`, this can be used in place of the maven
plugin for non-java projects. It can compile for:

- `json`: Simply writes out the thrift definition as thrift files.
- `java`: Writes the providence java generated code.
- `js`: Generates various javascript (es, type script, etc) variants.

See `pvdgen --help` for more detailed info about the available options.
