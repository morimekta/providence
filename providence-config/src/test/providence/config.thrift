namespace java net.morimekta.test.config

enum Value {
    FIRST = 1,
    SECOND = 2
}

struct Credentials {
    1: required string username;
    2: required string password;
}

struct Database {
    // The JDBC URI, e.g. "jdbc:mysql:localhost:1364:main_db"
    1: optional string uri;
    2: optional string driver;
    3: optional Credentials credentials;
    4: optional Value value;
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
