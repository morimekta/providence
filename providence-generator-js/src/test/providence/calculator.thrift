namespace java net.morimekta.test.providence.testing.calculator
namespace js pvd.testing

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

/**
 * A real calculator. does calculating stuff.
 */
service Calculator {
    /**
     * This is a real method with a real implementation.
     *
     * @calculating
     */
    Operand calculate(1: Operation op) throws (1: CalculateException ce);

    /**
     * Don't kill me!
     *
     * @deprecated
     */
    oneway void iamalive();

    /**
     * Just a comment, I don't care what.
     */
    void justAVoidMethod(1: string with_param);
}

const Operand PI = {
  "number": 3.141592
};

const map<i32, number.Imaginary> imaginaries = {
    Operator.IDENTITY: {
        'v': 3.141592;
        'i': -2.71828;
    },
}

const set<Operator> kComplexOperands = [
    Operator.MULTIPLY,
    Operator.DIVIDE
];
