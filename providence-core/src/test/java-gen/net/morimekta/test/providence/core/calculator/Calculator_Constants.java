package net.morimekta.test.providence.core.calculator;

@SuppressWarnings("unused")
public class Calculator_Constants {
    private Calculator_Constants() {}

    public static final net.morimekta.test.providence.core.calculator.Operand PI;
    static {
        PI = net.morimekta.test.providence.core.calculator.Operand.builder()
                .setNumber(3.141592d)
                .build();
    }

    public static final java.util.Set<net.morimekta.test.providence.core.calculator.Operator> kComplexOperands;
    static {
        kComplexOperands = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<net.morimekta.test.providence.core.calculator.Operator>()
                .add(net.morimekta.test.providence.core.calculator.Operator.MULTIPLY)
                .add(net.morimekta.test.providence.core.calculator.Operator.DIVIDE)
                .build();
    }
}