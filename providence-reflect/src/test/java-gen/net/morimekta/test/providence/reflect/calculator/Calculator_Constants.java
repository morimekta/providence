package net.morimekta.test.providence.reflect.calculator;

@javax.annotation.Generated("providence-maven-plugin")
@SuppressWarnings("unused")
public class Calculator_Constants {
    private Calculator_Constants() {}

    public static final net.morimekta.test.providence.reflect.calculator.Operand PI = net.morimekta.test.providence.reflect.calculator.Operand.builder()
            .setNumber(3.141592d)
            .build();

    public static final java.util.Set<net.morimekta.test.providence.reflect.calculator.Operator> kComplexOperands = new net.morimekta.providence.descriptor.PSet.DefaultBuilder<net.morimekta.test.providence.reflect.calculator.Operator>()
            .add(net.morimekta.test.providence.reflect.calculator.Operator.MULTIPLY)
            .add(net.morimekta.test.providence.reflect.calculator.Operator.DIVIDE)
            .build();
}
