namespace java net.morimekta.test.providence.testing.service2

struct Request {
    1: string text;
    2: bool valid;
}

service Base2Service {
    i64 inBase2Service()
}
