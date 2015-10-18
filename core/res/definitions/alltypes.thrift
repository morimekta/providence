namespace java org.apache.test.alltypes


typedef double real

enum Values {
  FIRST = 5,
  SECOND = 3;
  THIRD
  FOURTH = 1
  FIFTH
}

struct Other {
    1: Values v;
}

/* @compact */
struct AllTypes {
    1: bool bl;
    2: byte bt,
    3: i16 sh
    4: i32 i;
    5: i64 l,
    6: double d
    7: string s;
    8: binary bn,

    9: Values v;
    10: Other o;
    11: AllTypes self;
}
