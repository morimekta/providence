package net.morimekta.test.providence.core.calculator;

@javax.annotation.Generated("providence-maven-plugin")
@SuppressWarnings("unused")
public class Calculator_Constants {
    private Calculator_Constants() {}

    public static final net.morimekta.test.providence.core.calculator.Operand PI = net.morimekta.test.providence.core.calculator.Operand.builder()
            .setNumber(3.141592d)
            .build();

    public static final java.util.Set<net.morimekta.test.providence.core.calculator.Operator> kComplexOperands = new net.morimekta.providence.descriptor.PSet.DefaultBuilder<net.morimekta.test.providence.core.calculator.Operator>()
            .add(net.morimekta.test.providence.core.calculator.Operator.DIVIDE)
            .add(net.morimekta.test.providence.core.calculator.Operator.MULTIPLY)
            .build();
}
