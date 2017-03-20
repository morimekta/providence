namespace java net.morimekta.test.providence.service

struct Request {
    1: string text;
    2: bool valid;
}

struct Response {
    1: string text;
}

exception Failure {
    1: string text;
} (java.exception.class = "net.morimekta.providence.testing.util.TestException")

exception Failure2 {
    1: string text;
} (java.exception.class = "net.morimekta.providence.testing.util.TestException")

service BaseTestService {
    i32 inBaseService();
}

service TestService extends BaseTestService {
    void voidMethod(1: i32 param) throws (1: Failure fail);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);
} (java.service.methods.throws = "net.morimekta.providence.testing.util.TestException")

service TestService2  {
    void voidMethod(1: i32 param) throws (1: Failure fail, 2: Failure2 fail2);

    Response test(1: Request request) throws (1: Failure fail);

    double otherTest(1: double p1, 2: double p2);

    // Testing fix for https://github.com/morimekta/providence/issues/55
    map<string,i32> returnamap();
    set<string> returnaset();
    list<i32> returnalist();
}
