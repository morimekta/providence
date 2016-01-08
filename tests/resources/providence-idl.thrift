namespace java net.morimekta.speedtest.providence

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
    9: Value enumValue;
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
    12: optional set<byte> byteSet;
    13: optional set<i16> shortSet;
    14: optional set<i32> integerSet;
    15: optional set<i64> longSet;
    16: optional set<double> doubleSet;
    17: optional set<string> stringSet;
    18: optional set<binary> binarySet;

    // all types as map<x,x>.
    21: optional map<bool,bool> booleanMap;
    22: optional map<byte,byte> byteMap;
    23: optional map<i16,i16> shortMap;
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
    41: optional list<Primitives> messageList;
    42: optional set<Primitives> messageSet;
    43: optional map<string,Primitives> messageMap;
}
