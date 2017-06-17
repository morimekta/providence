namespace java net.morimekta.test.thrift.serialization.common

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
    3: optional string label;
} (compact = "")

struct OptionalFields {
    1: optional bool booleanValue;
    2: optional byte byteValue,
    3: optional i16 shortValue
    4: optional i32 integerValue;
    5: optional i64 longValue,
    6: optional double doubleValue
    7: optional string stringValue;
    8: optional binary binaryValue,
    9: optional Value enumValue;
    10: optional CompactFields compactValue;
}

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

struct DefaultFields {
    1: bool booleanValue;
    2: byte byteValue,
    3: i16 shortValue
    4: i32 integerValue;
    5: i64 longValue,
    6: double doubleValue
    7: string stringValue;
    8: binary binaryValue,
    9: Value enumValue;
    10: CompactFields compactValue;
}

union UnionFields {
    1: bool booleanValue;
    2: byte byteValue,
    3: i16 shortValue
    4: i32 integerValue;
    5: i64 longValue,
    6: double doubleValue
    7: string stringValue;
    8: binary binaryValue,
    9: Value enumValue;
    10: CompactFields compactValue;
}

struct DefaultValues {
    1:  bool          booleanValue = true;
    2:  byte          byteValue    = -125,
    3:  i16           shortValue   = 13579
    4:  i32           integerValue = 1234567890;
    5:  i64           longValue    = 1234567891,
    6:  double        doubleValue  = 2.99792458e+8
    7:  string        stringValue  = "test\\twith escapes\\nand\\u00a0ũñı©ôðé.";
    8:  binary        binaryValue;
    9:  Value         enumValue    = Value.SECOND;
    10: CompactFields compactValue
}
