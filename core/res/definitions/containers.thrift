namespace java net.morimekta.test.containers

include "primitives.thrift"

struct Containers {
    // all types as list<x>.
    1: list<bool> lbl;
    2: list<byte> lbt;
    3: list<i16> lsh;
    4: list<i32> li;
    5: list<i64> ll;
    6: list<double> ld;
    7: list<string> ls;
    8: list<binary> lbn;

    // all types as set<x>.
    11: set<bool> sbl;
    12: set<byte> sbt;
    13: set<i16> ssh;
    14: set<i32> si;
    15: set<i64> sl;
    16: set<double> sd;
    17: set<string> ss;
    18: set<binary> sbn;
     
    // all types as map<x,x>.
    21: map<bool,bool> mbl;
    22: map<byte,byte> mbt;
    23: map<i16,i16> msh;
    24: map<i32,i32> mi;
    25: map<i64,i64> ml;
    26: map<double,double> md;
    27: map<string,string> ms;
    28: map<binary,binary> mbn;

    // Using enum as key and value in containers.
    31: list<primitives.Value> lv;
    32: set<primitives.Value> sv;
    33: map<primitives.Value,primitives.Value> mv;

    // Using struct as key and value in containers.
    41: list<primitives.Primitives> lp;
    42: set<primitives.Primitives> sp;
    43: map<primitives.Primitives,primitives.Primitives> mp;

    // Using containers as values: list, set, map.
    51: list<list<primitives.Primitives>> llp;
    54: list<set<primitives.Primitives>> lsp;
    57: list<map<primitives.Primitives,primitives.Primitives>> lmp;

    // Sets cannot contain containers, as they are not hash indexable, not sortable.

    53: map<primitives.Primitives,list<primitives.Primitives>> mlp;
    56: map<primitives.Primitives,set<primitives.Primitives>> msp;
    59: map<primitives.Primitives,map<primitives.Primitives,primitives.Primitives>> mmp;
}
