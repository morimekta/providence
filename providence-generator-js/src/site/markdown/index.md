Code Generator for JavaScript
=============================

Options available in the JS generator:

- `closure`: Generates google closure annotations and modules.
- `node.js`: Generates node.js module exports and imports.
- `ts`: Generates typescript files instead of javascript (slightly different syntax).
  Should be transpiled with the `tsc -t ES6` command to allow for ECMAScript 6
  additions.
- `es51`: Generates code compatible with ECMA-262 Script 5.1 Edition (2011).
  Disables services (no Promise class for return values), and uses native objects
  instead of the es6 `Map` class.
- `pvd`: Write the service module needed for services alongside the generated code.