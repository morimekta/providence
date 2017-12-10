namespace java net.morimekta.test.providence.client

struct Request {
    1: string text;
} (java.public.constructor)

struct Response {
    1: string text;
} (java.public.constructor)

exception Failure {
    1: string text;
} (java.public.constructor)

service BaseTestService {
    i32 inBaseService();
}

service TestService extends BaseTestService {
    void voidMethod(1: i32 param) throws (1: Failure fail);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);

    oneway void onewayMethod();
}

service TestService2  {
    void voidMethod(1: i32 param) throws (1: Failure fail);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);
}
