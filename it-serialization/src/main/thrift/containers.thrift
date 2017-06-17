namespace java net.morimekta.test.thrift.serialization.containers

include "common.thrift"

struct ManyContainers {
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
    31: optional list<common.Value> enumList;
    32: optional set<common.Value> enumSet;
    33: optional map<common.Value,common.Value> enumMap;

    // Using struct as key and value in containers.
    41: optional list<common.DefaultFields> messageList;
    42: optional set<common.DefaultFields> messageSet;
    43: optional map<string,common.DefaultFields> messageMap;

    51: optional common.RequiredFields requiredFields;
    52: optional common.DefaultFields defaultFields;
    53: optional common.OptionalFields optionalFields;
    54: optional common.UnionFields unionFields;
    56: optional common.DefaultValues defaultValues;
}
