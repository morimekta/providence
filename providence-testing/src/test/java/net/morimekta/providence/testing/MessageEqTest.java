package net.morimekta.providence.testing;

import net.morimekta.test.providence.calculator.Operand;
import net.morimekta.test.providence.calculator.Operation;
import net.morimekta.test.providence.calculator.Operator;
import net.morimekta.test.providence.number.Imaginary;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class MessageEqTest {
    @Test
    public void testMatches() {
        Operation expected = Operation.builder()
                                      .setOperator(Operator.MULTIPLY)
                                      .addToOperands(Operand.builder()
                                                            .setOperation(Operation.builder()
                                                                                   .setOperator(Operator.ADD)
                                                                                   .addToOperands(Operand.builder()
                                                                                                         .setNumber(1234)
                                                                                                         .build())
                                                                                   .addToOperands(Operand.builder()
                                                                                                         .setNumber(
                                                                                                                 4.321)
                                                                                                         .build())
                                                                                   .build())
                                                            .build())
                                      .addToOperands(Operand.builder()
                                                            .setImaginary(Imaginary.builder()
                                                                                   .setV(1.7)
                                                                                   .setI(-2.0)
                                                                                   .build())
                                                            .build())
                                      .build();
        Operation matches = Operation.builder()
                                     .setOperator(Operator.MULTIPLY)
                                     .addToOperands(Operand.builder()
                                                           .setOperation(Operation.builder()
                                                                                  .setOperator(Operator.ADD)
                                                                                  .addToOperands(Operand.builder()
                                                                                                        .setNumber(1234)
                                                                                                        .build())
                                                                                  .addToOperands(Operand.builder()
                                                                                                        .setNumber(4.321)
                                                                                                        .build())
                                                                                  .build())
                                                           .build())
                                     .addToOperands(Operand.builder()
                                                           .setImaginary(Imaginary.builder()
                                                                                  .setV(1.7)
                                                                                  .setI(-2.0)
                                                                                  .build())
                                                           .build())
                                     .build();

        assertTrue(new MessageEq<>(expected).matches(matches));

        Operation not_matches = Operation.builder()
                                         .setOperator(Operator.MULTIPLY)
                                         .addToOperands(Operand.builder()
                                                               .setOperation(Operation.builder()
                                                                                      .setOperator(Operator.ADD)
                                                                                      .addToOperands(Operand.builder()
                                                                                                            .setNumber(
                                                                                                                    1234)
                                                                                                            .build())
                                                                                      .addToOperands(Operand.builder()
                                                                                                            .setNumber(
                                                                                                                    4.321)
                                                                                                            .build())
                                                                                      .build())
                                                               .build())
                                         .addToOperands(Operand.builder()
                                                               .setImaginary(Imaginary.builder()
                                                                                      .setV(1.8)
                                                                                      .setI(-2.0)
                                                                                      .build())
                                                               .build())
                                         .build();

        assertFalse(new MessageEq<>(expected).matches(not_matches));
    }
}
