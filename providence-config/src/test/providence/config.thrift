namespace java net.morimekta.test.providence.config

enum Value {
    FIRST = 1,
    SECOND = 2
}

struct Credentials {
    1: required string username;
    2: required string password;
} (java.public.constructor = "")

struct Database {
    // The JDBC URI, e.g. "jdbc:mysql:localhost:1364:main_db"
    1: optional string uri;
    2: optional string driver;
    3: optional Credentials credentials;
    4: optional Value value;
    5: optional i32 max_connections;
}

struct ServicePort {
    1: required i16 port;
    2: optional string context;

    3: optional map<string,binary> signature_keys;
    4: optional list<string> signature_override_keys;
    5: optional binary oauth_token_key;
}

struct Service {
    1: optional string name;
    2: optional ServicePort http;
    3: optional ServicePort admin;
    4: optional Database db;
}

struct RefConfig1 {
    1: optional bool bool_value;
    2: optional byte byte_value;
    3: optional i16 i16_value;
    4: optional i32 i32_value;
    5: optional i64 i64_value;
    6: optional double double_value;
    7: optional Value enum_value;
    8: optional binary bin_value;
    9: optional string str_value;
    90: optional string str2_value;
    10: optional Database msg_value;

    11: optional list<string> list_value;
    12: optional set<i16> set_value;
    13: optional map<Value,ServicePort> map_value;

    20: optional map<i32,Value> simple_map;
    21: optional map<i32,map<i32,i32>> complex_map;
}

// different name, so we can have it 'unknown'.
struct RefConfig2 {
    1: optional bool bool_value;
    2: optional byte byte_value;
    3: optional i16 i16_value;
    4: optional i32 i32_value;
    5: optional i64 i64_value;
    6: optional double double_value;
    7: optional Value enum_value;
    8: optional binary bin_value;
    9: optional string str_value;
    10: optional Database msg_value;

    11: optional list<string> list_value;
    12: optional set<i16> set_value;
    13: optional map<Value,ServicePort> map_value;
}

struct RefMerge {
    1: optional RefConfig1 ref1;
    2: optional RefConfig1 ref1_1;
    3: optional RefConfig2 ref2;
    4: optional RefConfig2 ref2_2;
}
