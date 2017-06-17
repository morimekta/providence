namespace java net.morimekta.test.thrift.serialization.messages

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


struct ManyRequiredFields {
    1: required list<i32> integerList;
    2: required list<string> stringList;
    3: required set<i32> integerSet;
    4: required set<string> stringSet;
    5: required map<i32,i32> integerMap;
    6: required map<string,string> stringMap;

    10: required string first;
    11: required string second;
    12: required string third;
    13: required string fourth;
    14: required string fifth;
    15: required string sixth;
    16: required string seventh;
    17: required string eighth;
    18: required string ninth;
    19: required string tenth;

    20: required i32 no_1;
    21: required i32 no_2;
    22: required i32 no_3;
    23: required i32 no_4;
    24: required i32 no_5;
    25: required i32 no_6;
    26: required i32 no_7;
    27: required i32 no_8;
    28: required i32 no_9;
    29: required i32 no_10;

    30: required i64 long_1;
    31: required i64 long_2;
    32: required i64 long_3;
    33: required i64 long_4;
    34: required i64 long_5;
    35: required i64 long_6;
    36: required i64 long_7;
    37: required i64 long_8;
    38: required i64 long_9;
    39: required i64 long_10;

    40: required double dbl_1;
    41: required double dbl_2;
    42: required double dbl_3;
    43: required double dbl_4;
    44: required double dbl_5;
    45: required double dbl_6;
    46: required double dbl_7;
    47: required double dbl_8;
    48: required double dbl_9;
    49: required double dbl_10;

    51: required common.RequiredFields requiredFields;
    52: required common.DefaultFields defaultFields;
    53: required common.OptionalFields optionalFields;
    54: required common.UnionFields unionFields;
    56: required common.DefaultValues defaultValues;
    57: required common.CompactFields compactFields;
}
