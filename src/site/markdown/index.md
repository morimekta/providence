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
`mvn clean compile`.

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

#### Compact Messages

A struct may be defined as `compact`. A compact struct must adhere to the
compact criteria:

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

Messages will have a `isCompact()` method that determines if the message is
compact compatible for serialization. Descriptors will have a similar
`compactible()` method which determines if the message can be deserialized with
the compact format.

This mimics the way thrift serializes service method calls, but in a way that
is generic to all messages.

To annotate a struct as compact, add the `@compact` annotation to the struct
comment. This will still allow thrift compiler to parse the .thrift files.

#### Simple Messages.

A simple message is one that does not contain nested structures, e.g. no containers,
and no internal messages. Simple messages is easier to use as map keys, and some
serialization formats may require that map key messages to be simple. Note that
the simple definition is on the type, not the message.

#### Reserved words.

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

### Removed support

`*_namespace` keywords are not supported. Use `namespace php [package]` instead
for php. XSD is AFAIK a facebook specific namespace, which is also not
supported.

None of the facebook specific modifiers are supported. They are also removed
from keyword lists (xsd_nullable, xsd_optional). Any extra annotations has to
be part of the comments.

## Contributing

Clone [this project](https://github.com/morimekta/providence), and create a
[pull request](https://github.com/morimekta/providence/pulls) for your change.

Make sure to name it properly, and describe chat the change does, and assign
the request to [morimekta](https://github.com/morimekta). That should send me
an email, so I know it's there and take care of it.
