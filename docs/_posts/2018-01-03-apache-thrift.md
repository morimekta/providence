---
layout: page
toc_title: "vs Apache Thrift"
title: "Comparison of Providence with Apache Thrift"
category: pvd
date: 2018-01-03 12:00:00
order: 3
---

If you are familiar with `Apache Thrift`, this is an overview over the
main differences in support between the two systems. The differences are
mostly minute, but all feature changes are there for a reason. Differences
are based on the IDL at [thrift.apache.org](https://thrift.apache.org/docs/idl).

Some features of apache thrift has simply been **removed**. This is done
in order to simplify both the thrift IDL parsing, and the resulting code
generator. Note that this is the differences between what is supported
and handled by the *described IDL*. With some few exceptions, all of the
added features are simply ignored by the Apache `thrift` compiler.

## Annotations

Annotations in apache thrift is simply noted as "something internal to
facebook", and should not be used. This includes `xsd` and some other
keywords. Instead of removing annotations, I **explicitly** use
annotations for various type modifications. Note that annotations can
only modify **what** the code generator generates, but is not included
in the type descriptors that are generated.

See details on [annotations](annotations.html) for more.

## Namespaces

Apache Thrift supports a couple of special "namespace" declarations, e.g.
`php_namespace` and `xsi_namespace`. They have been deprecated in thrift,
but are entirely removed from providence, so the keywords are not recognized
at all.

## Circular Containment

This feature is not explicitly supported or not in thrift, but does not
work well in Apache Thrift, meaning that a single struct may contain a
field of it's own type, but you may not have two structs each containing
the other. This is simply a result of the way Apache Thrift parses the
thrift file.

In `providence` you may have circular containment if (and only if) all
of the types referring to each other is in the same thrift definition
file. E.g.:

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

## Naming Conflicts

Each code generator needs make getters, setters and mutators using names that are
compatible with the language **AND** compliant to standard styleguides for that
language. By using prefixes and name modifications consistently this can be
done with some restrictions in order to enable most 'names' to be allowed all
over the place.

E.g. the field name 'public' should be allowed for all languages, including
java and C++ where it is a reserved word.

Since some languages prefer camelCased names, and other prefer c_cased names,
there **should** be no fields in the same struct with names that will conflict
if all '_' are removed, and the entire name is lower-cased. Even though it is
a little more complicated than that.

For this to work in Java, we apply the rules:

- All names are camelCased with a prefix for all methods and fields. E.g.
  value fields are prefixed `mValue`, getters with `getValue`, params with
  `pValue` etc. No suffixes are used.
- getters and setters are prefixed with `set`, `addTo`, `get`, `clear`,
  `mutable`, `has`, `num` etc.

Or in C++ or python. All the names are initially lower c_cased before
included in the code.

- __C++__: field names are suffixed with `_`. E.g. `myField` becomes `my_field_`.
- __python__: field names are prefixed with `__`. E.g. `myField` becomes `__my_field`.
- getters and setters are prefixed with `set_`, `get_`, `mutable_`, `clear_` `add_to_`, `has_` and `num_`.
- This breaks the somewhat spread convention that C++ getters should not have any prefix or suffix, but
  it makes it possible to use otherwise reserved words as field names.
