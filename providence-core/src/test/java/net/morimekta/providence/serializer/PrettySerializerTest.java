/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.providence.serializer;

import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.calculator.Operator;
import net.morimekta.test.providence.core.number.Imaginary;
import net.morimekta.testing.ResourceUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.test.providence.core.calculator.Operand.withImaginary;
import static net.morimekta.test.providence.core.calculator.Operand.withNumber;
import static net.morimekta.test.providence.core.calculator.Operand.withOperation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class PrettySerializerTest {
    private Operation mOperation;
    private String    mFormatted;

    @Rule
    public MessageGenerator generator = new MessageGenerator();

    @Before
    public void setUp() {
        mOperation = Operation.builder()
                              .setOperator(Operator.MULTIPLY)
                              .addToOperands(withOperation(Operation.builder()
                                                                    .setOperator(Operator.ADD)
                                                                    .addToOperands(withNumber(1234))
                                                                    .addToOperands(withNumber(4.321))
                                                                    .build()))
                              .addToOperands(withImaginary(new Imaginary(1.7, -2.0)))
                              .build();

        mFormatted = "{\n" +
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
                     "}";
    }

    @Test
    public void testFormat() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrettySerializer serializer = new PrettySerializer();
        serializer.serialize(baos, mOperation);
        assertEquals(mFormatted, new String(baos.toByteArray(), UTF_8));
    }

    @Test
    public void testParse() throws IOException {
        PrettySerializer serializer = new PrettySerializer();

        Operation actual = serializer.deserialize(getClass().getResourceAsStream("/json/calculator/pretty.cfg"), Operation.kDescriptor);

        assertEquals(mOperation, actual);
    }

    @Test
    public void testConfig() throws IOException {
        PrettySerializer serializer = new PrettySerializer().config();
        Containers containers = generator.generate(Containers.kDescriptor);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(out, containers);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Containers res = serializer.deserialize(in, Containers.kDescriptor);

        assertThat(res, is(containers));
    }

    @Test
    public void testConfig_2() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(ResourceUtils.getResourceAsBytes("/compat/config.cfg"));
        PrettySerializer serializer = new PrettySerializer().config();

        Containers c = serializer.deserialize(in, Containers.kDescriptor);
        assertThat(c, is(notNullValue()));
    }

    @Test
    public void testConfig_fails() throws IOException {
        assertConfigFailure("foo",
                            "Expected message start or qualifier, Got 'foo'",
                            "Error on line 1, pos 1: Expected message start or qualifier, Got 'foo'\n" +
                            "foo\n" +
                            "^^^");
        assertConfigFailure("calculator.Operation foo",
                            "Expected message start after qualifier ('{'): but found 'foo'",
                            "Error on line 1, pos 24: Expected message start after qualifier ('{'): but found 'foo'\n" +
                            "calculator.Operation foo\n" +
                            "-----------------------^");
        assertConfigFailure("calculator.Operand {",
                            "Expected qualifier calculator.Operation or message start, Got 'calculator.Operand'",
                            "Error on line 1, pos 1: Expected qualifier calculator.Operation or message start, Got 'calculator.Operand'\n" +
                            "calculator.Operand {\n" +
                            "^^^^^^^^^^^^^^^^^^");
        assertConfigFailure("{\n" +
                            "  1 = 123\n" +
                            "}\n",
                            "Expected field name, got '1'",
                            "Error on line 2, pos 3: Expected field name, got '1'\n" +
                            "  1 = 123\n" +
                            "--^");
    }

    private void assertConfigFailure(String content,
                                     String message,
                                     String output) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                content.getBytes(StandardCharsets.UTF_8));
        PrettySerializer serializer = new PrettySerializer().config();

        try {
            serializer.deserialize(in, Operation.kDescriptor);
            throw new AssertionError("no exception");
        } catch (SerializerException e) {
            assertThat(e.getMessage(), is(message));
            assertThat(e.asString(), is(output));
        }
    }

    @Test
    public void testService_fails() throws IOException {
        assertServiceFailure("foo",
                             "No such call type foo",
                             "Error on line 1, pos 1: No such call type foo\n" +
                             "foo\n" +
                             "^^^");
        assertServiceFailure("1: call bar",
                             "no such method bar on service calculator.Calculator",
                             "Error on line 1, pos 9: no such method bar on service calculator.Calculator\n" +
                             "1: call bar\n" +
                             "--------^^^");

        assertServiceFailure("1: call calculate",
                             "Expected call params start ('('), Got end of file",
                             "Error on line 1, pos 18: Expected call params start ('('), Got end of file\n" +
                             "1: call calculate\n" +
                             "-----------------^");
    }

    private void assertServiceFailure(String content,
                                      String message,
                                      String output) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                content.getBytes(StandardCharsets.UTF_8));
        PrettySerializer serializer = new PrettySerializer().config();

        try {
            serializer.deserialize(in, Calculator.kDescriptor);
            throw new AssertionError("no exception");
        } catch (SerializerException e) {
            assertThat(e.getMessage(), is(message));
            assertThat(e.asString(), is(output));
        }
    }
}
