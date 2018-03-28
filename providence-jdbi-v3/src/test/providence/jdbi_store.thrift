namespace java net.morimekta.test.providence.storage.jdbc

const i32 FACTORY_ID = 1234;

enum Fibonacci {
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
} (json.compact = "")

struct NormalFields {
    1: required string name
    2:          i32 id,
    3: optional string label;
}

struct OptionalFields {
    1:  required i32           id;       // INT PRIMARY KEY NOT NULL

    10: optional bool          present;  // BIT
    11: optional byte          tiny;     // TINYINT
    12: optional i16           small;    // SMALLINT
    13: optional i32           medium;   // INT
    14: optional i64           large;    // BIGINT
    15: optional double        real;     // DOUBLE
    16: optional string        name;     // VARCHAR(255)
    17: optional binary        data;     // VARBINARY(255)
    18: optional Fibonacci     fib;      // INT
    19: optional CompactFields message;  // VARCHAR(255)

    20: optional i32           timestamp_s;     // TIMESTAMP
    21: optional i64           timestamp_ms;    // TIMESTAMP
    22: optional NormalFields  binary_message;  // VARBINARY(255)
    23: optional NormalFields  blob_message;    // BLOB
    24: optional NormalFields  clob_message;    // TEXT
    25: optional binary        blob_data;
    26: optional binary        base64_data;
}