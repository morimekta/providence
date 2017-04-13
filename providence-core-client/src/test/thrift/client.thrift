namespace java net.morimekta.test.thrift.client

struct Request {
    1: string text;
}

struct Response {
    1: string text;
}

exception Failure {
    1: string text;
}

service BaseTestService {
    i32 inBaseService();
}

service TestService extends BaseTestService {
    void voidMethod(1: i32 param) throws (1: Failure fail);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);
}

service TestService2  {
    void voidMethod(1: i32 param) throws (1: Failure fail);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);
}
