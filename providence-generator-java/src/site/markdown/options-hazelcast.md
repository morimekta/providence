Providence Hazelcast Portable
=============================

I  the code generation there is an option available for generating
<a href="http://docs.hazelcast.org/docs/3.5/manual/html/portableserialization.html">
Hazelcast Portable</a> interfaces in the builder. It is added in the builder as
it won't work with the immutable thought for setting and reading in methods of the
class.

The choice fell to Hazelcast Portable of the available serializer implemented as
it supports the same logic required for thrift. Where you need to be able to add
and remove fields between versions and still make it work with rolling out new
versions of the code. With the restriction that any objects written in the new
format will loose the old fields for that specific data structure.

## Generate sources

The tool is part of the [providence code generator](providence-tools.html#Code_Generator).
And takes an argument `hazelcast_portable=true` that will generate the required
code. This also require a few annotations and a constant for each thrift file
that should be generated. Here is an example:

### Example thrift file

```thrift
namespace java net.morimekta.test.hazelcast.v1

const i32 FACTORY_ID = 1;

enum Value {
   FIRST = 1,
   SECOND = 2,
   THIRD = 3,
   FOURTH = 5,
   FIFTH = 8,
   SIXTH = 13,
   SEVENTH = 21,
   EIGHTH = 34,
   NINTH = 55,
   TENTH = 89,
   ELEVENTH = 144,
   TWELWETH = 233,
   /** @Deprecated */
   THIRTEENTH = 377,
   FOURTEENTH = 610,
   FIFTEENTH = 987,
   SIXTEENTH = 1597,
   SEVENTEENTH = 2584,
   EIGHTEENTH = 4181,
   NINTEENTH = 6765,
   TWENTIETH = 10946
}

struct CompactFields {
    1: required string name
    2: required i32 id,
    3: string label;
} (compact = "", hazelcast.class.id = "1")

const list<CompactFields> kDefaultCompactFields = [
  {"name": "Tut-Ankh-Amon", "id": 1333, "label": "dead"},
  {"name": "Ramses II", "id": 1279}
];

struct OptionalFields {
    1: optional bool booleanValue;
    2: optional byte byteValue;
    3: optional i16 shortValue;
    4: optional i32 integerValue;
    5: optional i64 longValue;
    6: optional double doubleValue;
    7: optional string stringValue;
    8: optional binary binaryValue;
    9: optional Value enumValue;
    10: optional CompactFields compactValue;
} (hazelcast.class.id = "2")

struct OptionalListFields {
    1: optional list<bool> booleanValues;
    2: optional list<byte> byteValues;
    3: optional list<i16> shortValues;
    4: optional list<i32> integerValue;
    5: optional list<i64> longValue;
    6: optional list<double> doubleValue;
    7: optional list<string> stringValue;
    10: optional list<CompactFields> compactValue;
} (hazelcast.class.id = "3")

struct RequiredFields {
    1: required bool booleanValue;
    2: required byte byteValue,
    3: required i16 shortValue
    4: required i32 integerValue;
    5: required i64 longValue,
    6: required double doubleValue
    7: required string stringValue;
    8: required binary binaryValue,
    9: required Value enumValue;
    10: required CompactFields compactValue;
}
```

* Each thrift file needs to have the `FACTORY_ID` declared. This id is an integer
  and needs to be unique for the entire hazelcast cluster you are working on. Be
  it the code generated from providence or manually created factories.
* Each struct that should be part of hazelcast needs to have the annotation
  `hazelcast.class.id` with a unique id in that file (for each builderSupplier).
* Each sub struct used in a builderSupplier also needs to have a unique id assigned to
  them, in this case the CompactFields.

Validation is in place for this to be printed as errors if you provide the
`hazelcast_portable=true` to the code generator without providing the required
fields.

If you don't provide any annotation in a trhift file, no builderSupplier will be created
for that file even though you provide the `hazelcast_portable=true` flag.

## Limitations

All of thrift specs can be serialized into hazelcast `Portable` format, but not all
fields can be indexed and handled natively.

All Maps regardless of content, and Lists and Sets containing any form of container,
are serialized as binary (using BinarySerializer binary format). This makes the field
useless for indexing.