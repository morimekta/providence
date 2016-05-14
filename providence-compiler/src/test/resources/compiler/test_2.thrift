namespace java net.morimekta.test.compiler

include "ref.thrift"

service MyService {
    oneway void ping();

    ref.Response test(1: ref.Request2 request) throws (1: ref.Failure f);

    double test2(2: i64 a, 4: byte late);
}
