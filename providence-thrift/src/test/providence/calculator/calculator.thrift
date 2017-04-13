namespace java net.morimekta.test.providence.thrift.calculator

include "number.thrift"

enum Operator {
    IDENTITY = 1,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}

union Operand {
    1: Operation operation;
    2: double number;
    3: number.Imaginary imaginary;
}

struct Operation {
    1: Operator operator;
    2: list<Operand> operands;
}

exception CalculateException {
    1: required string message;
    2: Operation operation;
}

service Calculator {
    Operand calculate(1: Operation op) throws (1: CalculateException ce);
    oneway void iamalive();
}

const Operand PI = {
  "number": 3.141592
};

const set<Operator> kComplexOperands = [
    Operator.MULTIPLY,
    Operator.DIVIDE
];
