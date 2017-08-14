Annotations in Providence
=========================

The explicit use of annotations is one of the big differences
between `Apache Thrift` and `Providence`, and is used for a fair
bit of type modifications.

## Syntax

Annotations are specified by adding a set of key-value pairs inside
parentheses *after* a type or field is defined, where the key is a
*qualified identifier*, and the value is a *quoted string literal*.
Note that the annotations apply to whatever was defined *before* itself.
E.g.:

```thrift
struct MyStruct {
    1: string field (annotation.on.field = "value")
    2: i32    other (annotation.on.other = "value")
} (annotation.on.struct = "value")
```

Or in IDL syntax:

```
ANNOTATIONS          :== '(' ANNOTATION [ [',' ';'] ANNOTATION ]* ')'

ANNOTATION           :== IDENTIFIER '=' LITERAL

ANNOTATED_DEFINITION :== DEFINITION ANNOTATION?
```

## Annotation Specifications

A number of annotations that modify different subsystems are:

### Container Type

The `set` and `map` container types uses by default an `ImmutableSet` or the
`ImmutableMap` guava classes respectively, which uses a simple `hash` storage.
Two other variants exists, which is controlled by the `container` annotations.

* `container = "SORTED"`: On fields with set or map type only, will replace the
  default hash-based container with a sorted container. In java that is the
  `ImmutableSortedMap` or similar.
* `container = "ORDERED"`: On fields with set or map type only, will replace the
  default hash-based container with an order-preserving container. In java that
  is the `LinkedHashMap` or similar.

### Java Specific Annotations

* `java.implements` Each java message (union, struct, exception) can implement
  additional interfaces specified by this annotation. Full package and class name.
  Note that the message is still a full implementation, so the interface methods
  need to be implemented (declared) by the generated code, or have default
  implementations.
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
* `java.public.constructor` will create a public constructor with all fields as
  params. Note that this used to be default on, but is now default off and triggered
  with this annotation.

### Other

- `deprecated = "<message>"`: Will mark the associated methods, service or class as
  being deprecated (should not be used). Since the syntax for deprecating a class
  is different for each language, it is handled in annotation, and should e.g.
  generate `@Deprecated` annotations in java.
- `json.compact = ""`: Enables the use of the compact syntax in the
  [json serializer](serializer-json.html).
