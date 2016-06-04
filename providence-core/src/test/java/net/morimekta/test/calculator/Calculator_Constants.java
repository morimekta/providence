package net.morimekta.test.calculator;

@SuppressWarnings("unused")
public class Calculator_Constants {
    private Calculator_Constants() {}

    public static final net.morimekta.test.calculator.Operand PI;
    static {
        PI = net.morimekta.test.calculator.Operand.builder()
                .setNumber(3.141592d)
                .build();
    }

    public static final java.util.Set<net.morimekta.test.calculator.Operator> kComplexOperands;
    static {
        kComplexOperands = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<net.morimekta.test.calculator.Operator>()
                .add(net.morimekta.test.calculator.Operator.DIVIDE)
                .add(net.morimekta.test.calculator.Operator.MULTIPLY)
                .build();
    }
}