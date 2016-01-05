namespace java net.morimekta.test.primitives

enum Value {
   FIRST = 1,
   SECOND
}

struct Primitives {
    1: bool bl;
    2: byte bt,
    3: i16 sh
    4: i32 i;
    5: i64 l,
    6: double d
    7: string s;
    8: binary bn,

    // Enums are stored as primitive values.
    9: Value v;
}
