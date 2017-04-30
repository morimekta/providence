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

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.test.providence.core.calculator.Operand.withImaginary;
import static net.morimekta.test.providence.core.calculator.Operand.withNumber;
import static net.morimekta.test.providence.core.calculator.Operand.withOperation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

        mFormatted = "operator = MULTIPLY\n" +
                     "operands = [\n" +
                     "  {\n" +
                     "    operation = {\n" +
                     "      operator = ADD\n" +
                     "      operands = [\n" +
                     "        {\n" +
                     "          number = 1234\n" +
                     "        },\n" +
                     "        {\n" +
                     "          number = 4.321\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    imaginary = {\n" +
                     "      v = 1.7\n" +
                     "      i = -2\n" +
                     "    }\n" +
                     "  }\n" +
                     "]";
    }

    @Test
    public void testFormat() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrettySerializer serializer = new PrettySerializer().debug();
        serializer.serialize(baos, mOperation);
        assertEquals(mFormatted, new String(baos.toByteArray(), UTF_8));
    }

    @Test
    public void testParse() throws IOException {
        PrettySerializer serializer = new PrettySerializer().debug();

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
    public void testConfig2() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(ResourceUtils.getResourceAsBytes("/compat/config.cfg"));
        PrettySerializer serializer = new PrettySerializer().config();

        try {
            serializer.deserialize(in, Containers.kDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
