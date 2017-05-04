namespace java net.morimekta.test.thrift

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

const list<CompactFields> kDefaultCompactFields = [
  {"name": "Tut-Ankh-Amon", "id": 1333, "label": "dead"},
  {"name": "Ramses II", "id": 1279}
];

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
    10: CompactFields compactValue;
}

typedef double real

struct DefaultValues {
    1:  bool          booleanValue = true;
    2:  byte          byteValue    = -125,
    3:  i16           shortValue   = 13579
    4:  i32           integerValue = 1234567890;
    5:  i64           longValue    = 1234567891,
    6:  real          doubleValue  = 2.99792458e+8
    7:  string        stringValue  = "test\\twith escapes\\nand\\u00a0ũñı©ôðé.";
    8:  binary        binaryValue;
    9:  Value         enumValue    = Value.SECOND;
    10: CompactFields compactValue
}

struct Containers {
    // all types as list<x>.
    1: optional list<bool> booleanList;
    2: optional list<byte> byteList;
    3: optional list<i16> shortList;
    4: optional list<i32> integerList;
    5: optional list<i64> longList;
    6: optional list<double> doubleList;
    7: optional list<string> stringList;
    8: optional list<binary> binaryList;

    // all types as set<x>.
    11: optional set<bool> booleanSet;
    12: optional set<byte> byteSet (container = "SORTED");
    13: optional set<i16> shortSet (container = "ORDERED");
    14: optional set<i32> integerSet;
    15: optional set<i64> longSet;
    16: optional set<double> doubleSet;
    17: optional set<string> stringSet;
    18: optional set<binary> binarySet;

    // all types as map<x,x>.
    21: optional map<bool,bool> booleanMap;
    22: optional map<byte,byte> byteMap (container = "SORTED");
    23: optional map<i16,i16> shortMap (container = "ORDERED");
    24: optional map<i32,i32> integerMap;
    25: optional map<i64,i64> longMap;
    26: optional map<double,double> doubleMap;
    27: optional map<string,string> stringMap;
    28: optional map<binary,binary> binaryMap;

    // Using enum as key and value in containers.
    31: optional list<Value> enumList;
    32: optional set<Value> enumSet;
    33: optional map<Value,Value> enumMap;

    // Using struct as key and value in containers.
    41: optional list<DefaultFields> messageList;
    42: optional set<DefaultFields> messageSet;
    43: optional map<string,DefaultFields> messageMap;

    51: optional RequiredFields requiredFields;
    52: optional DefaultFields defaultFields;
    53: optional OptionalFields optionalFields;
    54: optional UnionFields unionFields;
    55: optional ExceptionFields exceptionFields;
    56: optional DefaultValues defaultValues;
}

