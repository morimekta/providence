namespace java org.apache.test.alltypes


typedef double real

enum Values {
  FIRST = 5,
  SECOND = 0x03;
  THIRD
  FOURTH = 1
  FIFTH
}

struct Other {
    1: Values v;
}

struct Empty {
}

struct AllTypes {
    1: bool bl = true;
    2: byte bt = -125,
    3: i16 sh = 0x1fb5
    4: i32 i = 1234567890;
    5: i64 l = 1234567890123456789,
    6: real d = 2.99792458e+8
    7: string s = "test\twith escapes\nand\u00a0unicode.";
    8: binary bn = "dGVzdCAgICB3aXRoIGVzY2FwZXMNCmFuZCB1bmljb2RlLg==",
    9: Values v = Values.SECOND;
    10: Other o
    11: AllTypes self;
}
