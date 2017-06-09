package net.morimekta.test.providence.core.calculator;

import net.morimekta.providence.descriptor.PSet;

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
        kComplexOperands = new PSet.DefaultBuilder<Operator>()
                .add(net.morimekta.test.providence.core.calculator.Operator.MULTIPLY)
                .add(net.morimekta.test.providence.core.calculator.Operator.DIVIDE)
                .build();
    }
}