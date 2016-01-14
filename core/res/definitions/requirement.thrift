namespace java net.morimekta.test.requirement

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
   THIRTEENTH = 377,
   FOURTEENTH = 610,
   FIFTEENTH = 987,
   SIXTEENTH = 1597,
   SEVENTEENTH = 2584,
   EIGHTEENTH = 4181,
   NINTEENTH = 6765,
   TWENTIETH = 10946
}

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
}

exception ExceptionFields {
    1: bool booleanValue;
    2: byte byteValue,
    3: i16 shortValue
    4: i32 integerValue;
    5: i64 longValue,
    6: double doubleValue
    7: string stringValue;
    8: binary binaryValue,
    9: Value enumValue;
}

// all types as list<x>.
struct Requirements {
    1: required list<RequiredFields>  requiredList;
    2: optional list<OptionalFields>  optionalList;
    3:          list<DefaultFields>   defaultList;
    4:          list<UnionFields>     unionList;
    5:          list<ExceptionFields> exceptionList;
}
