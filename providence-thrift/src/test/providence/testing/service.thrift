namespace java net.morimekta.test.providence.thrift.service


struct Request {
    1: string text;
} (java.public.constructor)

struct Response {
    1: string text;
} (java.public.constructor)

exception Failure {
    1: string text;
} (java.public.constructor)

service MyService {
    oneway void ping();

    Response test(1: Request request) throws (1: Failure f);

    double test2(2: i64 a, 4: byte late);
}

service MyService2 {
    Response testing(1: Request request) throws (1: Failure f);

    double test2(2: i64 a, 4: byte late);
}
