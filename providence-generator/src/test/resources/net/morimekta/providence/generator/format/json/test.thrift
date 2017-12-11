namespace java net.morimekta.test.json.test

include "included.thrift"

struct Test {
    1: required i32 test;
    15: optional i32 another;
    2: included.Included included;
}
