Providence Serialization
========================

The `providence` project was made in order to make an immutable model java
library for thrift. It is mostly separate from the thrift library, but can use
the standard thrift protocols to serialize and serialize messages. It is mainly
based on the Facebook / Apache [thrift](https://thrift.apache.org/) library,
but with some differences and limitations.

## Setup

In order to compile providence, you need `java` and `javac` (I recommend
`openjdk8-jdk`), and `maven` (3.3). Check out
`git@github.com:morimekta/providence.git` and build with
`mvn clean verify install`.

## Differences with Thrift

providence is not *exactly* like thrift. The differences are mostly minute, but
all feature changes are there for a reason. Differences are based on the IDL at
[thrift.apache.org](https://thrift.apache.org/docs/idl).

### Alterations to thrift IDL

Constant data syntax has to follow JSON syntax. So ',' are mandatory in list
item separation, '"' is only string literal char. Exception is that map keys
can be the actual value. Enum values cannot be used as a generic "number"
reference in constants, only to set the enum value itself.

Fields with conflicting but differing names are explicitly disallowed. E.g. 
"my_field" and "myField" are considered conflicting because they would generate
the same names when c_casing or camelCasing the name.

### Added support

There are also extra features added that the original Apache Thrift format does
not support, or is only supported on very limited cases.

#### Annotations

Annotations are actually supported by the thrift compiler, but is not defined in
the IDL. Annotations follow the syntax of:

```
ANNOTATIONS :== '(' ANNOTATION [[,;] ANNOTATION]* ')'

ANNOTATION  :== IDENTIFIER '=' LITERAL
```

Which is put _after_ the actual definition, but _before_ any list separator. The
annotation key can be *any* legal identifier, and the value can be any string
literal. E.g.:

```thrift
struct MyStruct {
  1: i32 something
  (something.anno = "value")
  2: i32 field (field.anno = "value");
} (struct.anno = "value")
```

Annotations are there for the compiler only, and should not be saved in the
generated code. Currently the recognized annotations are:
 
* `json.compact = ""`: On structs only, see the (Compact Messages)[#compact-messages] section.
* `container = "ORDERED"`: On fields with set or map type only, will replace the
  default hash-based container with an order-preserving container.
* `container = "SORTED"`: On fields with set or map type only, will replace the
  default hash-based container with a sorted container.
* `java.implements` Each java message (union, struct, exception) can implement
  additional interfaces specified by this annotation. Full package and class name.
  Note that the message is still a full implementation, so the interface methods
  need to be implemented (declared) by the generated code, or have default
  implementation.
* `java.exception.class` Which exception class to inherit from. This must be the
  full class path of an exception. Note that whether it is an exception is not
  checked in the generator, it is plainly trusted as the exception class.
* `java.service.methods.throws` Which replaces the declared exceptions with the
  given exception class on the **service interface only**. Also note that:

    - The property is **not** inherited, and only applies to the methods declared on
      the service with the annotation.
    - All the declared exceptions **must** extend the given exception.
    - The exception class **must** be available at compile time, and have a
      constructor that takes the message string only.
    - Any exception that not declared, including those that extend the base exception
      class will be handled as an application level failure, throwing
      `PApplicationException`.
    - The client will still only throw the declared exceptions (and `IOException`).

#### Circular containment

Circular containment is explicitly supported as long as the entire circle is
contained within the same thrift definition file. E.g.:

- struct A contains struct B contains struct A.

```thrift
namespace java net.morimekta.test.calculator

enum Operator {
    IDENTITY = 1,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}

union Operand {
    1: Operation operation;
    2: double number;
}

struct Operation {
    1: Operator operator;
    2: list<Operand> operands;
}
```

This makes model structures like the calculator possible. Since the model
objects are immutable and created with builders, it is not possible to create
a circular instance containment.

#### Compact JSON Messages

A struct may be defined as **compact** for json using the `json.compact`
annotation. A compact struct must adhere to the compact criteria:

- Only structs may be compact (not union or exception).
- May have a maximum of 10 fields.
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

Compactible messages will have a `jsonCompact()` method that determines if the
message is compact for serialization. The descriptor will have a similar
`isJsonCompactible()` method which determines if the message can be
deserialized with the compact format.

To make a struct compact, add the `json.compact = ""` annotation to the struct.
This will still allow thrift compiler to parse the .thrift files. This applies
both to using the `JsonSerializer` and using the `jackson` java generator
option with a jackson JSON serializer and deserializer.

#### Simple Messages.

A simple message is one that does not contain nested structures, e.g. no containers,
and no internal messages. Simple messages are easier to use as map keys, and some
serialization formats may require that map key messages to be simple. Note that
the simple definition is on the type, not the message.

#### Reserved words.

Each format should make getters, setters and mutators, field names etc that are
compatible with the language using prefixes, suffixes and name modifications.
E.g. the field name 'public' should be allowed for all languages, oncluding
java and C++ where it is a reserved word.

In Java:

- field names are prefixed with `m` and camel cased. E.g. `my_field` becomes
  `mMyField`.
- getters and setters are prefixed with `set`, `addTo`, `get`, `clear`,
  `mutable`, `has` and `num`. respectively.

Or in C++ or python. All the names are initially lowercase c_cased before included in the code.

- __C++__: field names are suffixed with `_`. E.g. `myField` becomes `my_field_`.
- __python__: field names are prefixed with `__`. E.g. `myField` becomes `__my_field`.
- getters and setters are prefixed with `set_`, `get_`, `mutable_`, `clear_` `add_to_`, `has_` and `num_`.
- This breaks the somewhat spread convention that C++ getters should not have any prefix or suffix, but
  it makes it possible to use otherwise reserved words as field names.

### Removed support

`*_namespace` keywords are not supported. Use `namespace php [package]` instead
for php. None of the facebook specific modifiers are supported. They are also removed
from keyword lists (xsd_nullable, xsd_optional and the xsd namespace), but annotations
should be able to fill that gap.

## Contributing

You can send me an [email](mailto:oss@morimekta.net) to suggest a feature. Or
you can make it yourself by cloning
[the project](https://github.com/morimekta/providence), and create a
[pull request](https://github.com/morimekta/providence/pulls) for your change.

Make sure to name it properly, and describe what the change does, and assign
the request to [@morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
