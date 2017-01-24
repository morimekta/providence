namespace java net.morimekta.test.autoid

exception AutoId {
    string message;
    i32 second;
}

service AutoParam {
    i32 method(i32 a, i32 b) throws (AutoId auto1)
}
