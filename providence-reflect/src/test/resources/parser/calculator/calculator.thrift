namespace java net.morimekta.test.calculator

include "number.thrift"

/**
 * Block comment on type.
 */
enum Operator {
    // line comment on enum
    IDENTITY = 1,
    /**
     * Block comment on enum.
     */
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}

// Line comment on type.
union Operand {
    // Double line
    // comment on field.
    1: Operation operation;
    /**
     * Block comment
     *  - with formatting.
     * On field.
     */
    2: double number;
    3: number.Imaginary imaginary;
}

struct Operation {
    1: Operator operator;
    2: list<Operand> operands;
} (compact = "")

exception CalculateException {
    1: required string message;
    2: Operation operation;
}

service Calculator {
    /**
     * Block comment on method.
     */
    Operand calculate(1: Operation op) throws (1: CalculateException ce);
    // line comment on method.
    oneway void iamalive();
}

/**
 * Block comment on constant.
 */
const Operand PI = {
  "number": 3.141592
};

// Line comment on constant.
const set<Operator> kComplexOperands = [
    Operator.MULTIPLY,
    Operator.DIVIDE
];
