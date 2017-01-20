package net.morimekta.providence.util;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.test.calculator.Operand;
import net.morimekta.test.calculator.Operation;
import net.morimekta.test.calculator.Operator;
import net.morimekta.test.number.Imaginary;
import net.morimekta.test.providence.Containers;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static org.junit.Assert.assertEquals;

/**
 * Tests for reading json resources.
 */
public class ProvidenceHelperTest {
    @Test
    public void testFromJsonResource_compact() throws SerializerException, IOException {
        Operation op = net.morimekta.providence.util.ProvidenceHelper.fromJsonResource("/json/calculator/compact.json", Operation.kDescriptor);

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

        assertEquals(op, expected);
    }

    @Test
    public void testFromJsonResource_named() throws SerializerException, IOException {
        Operation op = net.morimekta.providence.util.ProvidenceHelper.fromJsonResource("/json/calculator/named.json", Operation.kDescriptor);

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

        assertEquals(op, expected);
    }

    @Test
    public void testFromJsonResource_pretty() throws SerializerException, IOException {
        Operation op = net.morimekta.providence.util.ProvidenceHelper.fromJsonResource("/json/calculator/pretty.json", Operation.kDescriptor);

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

        assertEquals(op, expected);
    }

    @Test
    public void testArrayListFromJsonResource() throws IOException {
        List<Containers> pretty = net.morimekta.providence.util.ProvidenceHelper.arrayListFromJsonResource("/compat/pretty.json",
                                                                                                           Containers.kDescriptor);
        List<Containers> compact = net.morimekta.providence.util.ProvidenceHelper.arrayListFromJsonResource("/compat/compact.json",
                                                                                                            Containers.kDescriptor);

        assertEquals(10, pretty.size());
        assertEquals(pretty.size(), compact.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(debugString(compact.get(i)), debugString(pretty.get(i)));
        }
    }
}
