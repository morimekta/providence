namespace java net.morimekta.test.providence.service

struct AStruct {
    1: optional string message;
    2: optional i32 number;
}

exception AnException {
    1: required string message;
}

service BaseTestService {
    i32 inBaseService();
}

service TestService extends BaseTestService {
    void voidMethod(1: string param) throws (1: AnException e1);

    AStruct structMehod();

    double doubleMethod(1: double p1, 2: double p2);
}
