package net.morimekta.providence.util;

import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.DefaultFields;
import net.morimekta.test.providence.core.calculator.Operand;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.calculator.Operator;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for reading json resources.
 */
public class ProvidenceHelperTest {
    @Test
    public void testFromJsonResource_compact() throws IOException {
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
    public void testFromJsonResource_named() throws IOException {
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
    public void testFromJsonResource_pretty() throws IOException {
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

    private Operation mOperation;

    @Before
    public void setUp() {
        mOperation = Operation.builder()
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
    }

    @Test
    public void testDebugString() {
        assertEquals("{\n" +
                     "  operator = MULTIPLY\n" +
                     "  operands = [\n" +
                     "    {\n" +
                     "      operation = {\n" +
                     "        operator = ADD\n" +
                     "        operands = [\n" +
                     "          {\n" +
                     "            number = 1234\n" +
                     "          },\n" +
                     "          {\n" +
                     "            number = 4.321\n" +
                     "          }\n" +
                     "        ]\n" +
                     "      }\n" +
                     "    },\n" +
                     "    {\n" +
                     "      imaginary = {\n" +
                     "        v = 1.7\n" +
                     "        i = -2\n" +
                     "      }\n" +
                     "    }\n" +
                     "  ]\n" +
                     "}", ProvidenceHelper.debugString(mOperation));
    }

    @Test
    public void testParseDebugString() {
        assertEquals(mOperation, ProvidenceHelper.parseDebugString(
                "{\n" +
                "  operator = MULTIPLY\n" +
                "  operands = [\n" +
                "    {\n" +
                "      operation = {\n" +
                "        operator = ADD\n" +
                "        operands = [\n" +
                "          {\n" +
                "            number = 1234\n" +
                "          },\n" +
                "          {\n" +
                "            number = 4.321\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      imaginary = {\n" +
                "        v = 1.7\n" +
                "        i = -2\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}", Operation.kDescriptor));
    }

    @Test
    public void testOptionalInMessage() {
        DefaultFields defaultFields = DefaultFields
                .builder()
                .setCompactValue(CompactFields.builder()
                                              .setId(1234)
                                              .setLabel("bar")
                                              .build())
                .build();

        String label = ProvidenceHelper
                .<String>optionalInMessage(defaultFields,
                                           DefaultFields._Field.COMPACT_VALUE,
                                           CompactFields._Field.LABEL).orElseThrow(() -> new AssertionError(
                "No label"));
        assertThat(label, is("bar"));
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ProvidenceHelper> constructor = ProvidenceHelper.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible(), is(false));
        try {
            constructor.setAccessible(true);
            assertThat(constructor.newInstance(), is(instanceOf(ProvidenceHelper.class)));
        } finally {
            constructor.setAccessible(false);
        }
    }
}
