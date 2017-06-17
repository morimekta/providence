namespace java net.morimekta.test.providence.serialization.messages

include "common.thrift"

struct ManyFields {
    1: optional list<i32> integerList;
    2: optional list<string> stringList;
    3: optional set<i32> integerSet;
    4: optional set<string> stringSet;
    5: optional map<i32,i32> integerMap;
    6: optional map<string,string> stringMap;

    10: optional string first;
    11: optional string second;
    12: optional string third;
    13: optional string fourth;
    14: optional string fifth;
    15: optional string sixth;
    16: optional string seventh;
    17: optional string eighth;
    18: optional string ninth;
    19: optional string tenth;

    20: optional i32 no_1;
    21: optional i32 no_2;
    22: optional i32 no_3;
    23: optional i32 no_4;
    24: optional i32 no_5;
    25: optional i32 no_6;
    26: optional i32 no_7;
    27: optional i32 no_8;
    28: optional i32 no_9;
    29: optional i32 no_10;

    30: optional i64 long_1;
    31: optional i64 long_2;
    32: optional i64 long_3;
    33: optional i64 long_4;
    34: optional i64 long_5;
    35: optional i64 long_6;
    36: optional i64 long_7;
    37: optional i64 long_8;
    38: optional i64 long_9;
    39: optional i64 long_10;

    40: optional double dbl_1;
    41: optional double dbl_2;
    42: optional double dbl_3;
    43: optional double dbl_4;
    44: optional double dbl_5;
    45: optional double dbl_6;
    46: optional double dbl_7;
    47: optional double dbl_8;
    48: optional double dbl_9;
    49: optional double dbl_10;

    51: optional common.RequiredFields requiredFields;
    52: optional common.DefaultFields defaultFields;
    53: optional common.OptionalFields optionalFields;
    54: optional common.UnionFields unionFields;
    56: optional common.DefaultValues defaultValues;
    57: optional common.CompactFields compactFields;
}

