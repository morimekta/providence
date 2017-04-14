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
    8: optional list<binary> binaryValue;
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
