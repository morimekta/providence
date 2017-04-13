package net.morimekta.providence.testing;

import net.morimekta.test.providence.testing.calculator.Operand;
import net.morimekta.test.providence.testing.calculator.Operation;
import net.morimekta.test.providence.testing.calculator.Operator;
import net.morimekta.test.providence.testing.number.Imaginary;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class EqualToMessageTest {
    private EqualToMessage matcher;
    private Operation expected;
    private Operation matches;
    private Operation not_matches;

    @Before
    public void setUp() {
        expected = Operation.builder()
                            .setOperator(Operator.MULTIPLY)
                            .addToOperands(Operand.builder()
                                                  .setOperation(Operation.builder()
                                                                         .setOperator(Operator.ADD)
                                                                         .addToOperands(Operand.withNumber(1234))
                                                                         .addToOperands(Operand.withNumber(
                                                                                 4.321))
                                                                         .build())
                                                  .build())
                            .addToOperands(Operand.withImaginary(Imaginary.builder()
                                                                          .setV(1.7)
                                                                          .setI(-2.0)
                                                                          .build()))
                            .build();
        // Same constructor again to make sure all objects are not the same, but equal.
        matches = Operation.builder()
                                     .setOperator(Operator.MULTIPLY)
                                     .addToOperands(Operand.builder()
                                                           .setOperation(Operation.builder()
                                                                                  .setOperator(Operator.ADD)
                                                                                  .addToOperands(Operand.withNumber(1234))
                                                                                  .addToOperands(Operand.withNumber(4.321))
                                                                                  .build())
                                                           .build())
                                     .addToOperands(Operand.withImaginary(Imaginary.builder()
                                                                                   .setV(1.7)
                                                                                   .setI(-2.0)
                                                                                   .build()))
                                     .build();
        not_matches = Operation.builder()
                                         .setOperator(Operator.MULTIPLY)
                                         .addToOperands(Operand.builder()
                                                               .setOperation(Operation.builder()
                                                                                      .setOperator(Operator.ADD)
                                                                                      .addToOperands(Operand.withNumber(1234))
                                                                                      .addToOperands(Operand.withNumber(4.321))
                                                                                      .build())
                                                               .build())
                                         .addToOperands(Operand.withImaginary(Imaginary.builder()
                                                                                       .setV(1.8)
                                                                                       .setI(-2.0)
                                                                                       .build()))
                                         .build();

        matcher = new EqualToMessage<>(expected);
    }

    @Test
    public void testMatches() {
        assertTrue(matcher.matches(expected));
        assertTrue(matcher.matches(matches));
        assertFalse(matcher.matches(not_matches));
    }

    @Test
    public void testDescribeTo() {
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), is(equalTo(
                "equals({operator:MULTIPLY,operands:[{operation:{operator:ADD,operands:[{number:1234},{number:4.321}]}},{imaginary:{v:...})")));
    }

    @Test
    public void testDescribeMismatch() {
        Description description = new StringDescription();
        matcher.describeMismatch(not_matches, description);
        assertThat(description.toString(), is(equalTo(
                "operands[1].imaginary.v was 1.8, expected 1.7")));
    }
}
