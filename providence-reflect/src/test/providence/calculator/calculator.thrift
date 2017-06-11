namespace java net.morimekta.test.providence.reflect.calculator

include "number.thrift"

enum Operator {
    IDENTITY = 1,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}

union Operand {
    1: optional Operation operation;
    2: optional double number;
    3: optional number.Imaginary imaginary;
}

struct Operation {
    1: optional Operator operator;
    2: optional list<Operand> operands;
}

exception CalculateException {
    1: required string message;
    2: optional Operation operation;
}

service Calculator {
    Operand calculate(1: Operation op) throws (1: CalculateException ce);
    oneway void iamalive();
    void ping();
}

service Calculator2 extends Calculator {
    string extra();
}

const Operand PI = {
  "number": 3.141592
};

const set<Operator> kComplexOperands = [
    Operator.MULTIPLY,
    Operator.DIVIDE
];
