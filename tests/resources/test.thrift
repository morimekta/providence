namespace java net.morimekta.test

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

struct Primitives {
    1: bool booleanValue;
    2: byte byteValue,
    3: i16 shortValue
    4: i32 integerValue;
    5: i64 longValue,
    6: double doubleValue
    7: string stringValue;
    8: binary binaryValue,

    // Enums are stored as primitive values.
    9: Value enumValue;
}

struct Containers {
    // all types as list<x>.
    1: list<bool> booleanList;
    2: list<byte> byteList;
    3: list<i16> shortList;
    4: list<i32> integerList;
    5: list<i64> longList;
    6: list<double> doubleList;
    7: list<string> stringList;
    8: list<binary> binaryList;

    // all types as set<x>.
    11: set<bool> booleanSet;
    12: set<byte> byteSet;
    13: set<i16> shortSet;
    14: set<i32> integerSet;
    15: set<i64> longSet;
    16: set<double> doubleSet;
    17: set<string> stringSet;
    18: set<binary> binarySet;

    // all types as map<x,x>.
    21: map<bool,bool> booleanMap;
    22: map<byte,byte> byteMap;
    23: map<i16,i16> shortMap;
    24: map<i32,i32> integerMap;
    25: map<i64,i64> longMap;
    26: map<double,double> doubleMap;
    27: map<string,string> stringMap;
    28: map<binary,binary> binaryMap;

    // Using enum as key and value in containers.
    31: list<Value> enumList;
    32: set<Value> enumSet;
    33: map<Value,Value> enumMap;

    // Using struct as key and value in containers.
    41: list<Primitives> messageList;
    42: set<Primitives> messageSet;
    43: map<string,Primitives> messageMap;
}
