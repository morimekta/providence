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

import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.util.pretty.TokenizerException;
import net.morimekta.providence.util_internal.EqualToMessage;
import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.calculator.Operand;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.calculator.Operator;
import net.morimekta.util.Binary;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Testing that each serializer does it's job. Does not test backward
 * compatibility / specific internal cases. Those are in the respective
 * P*SerializerTest.
 */
public class SerializerTest {
    private static Operation             operation;
    private static ArrayList<Containers> containers;

    @Rule
    public MessageGenerator generator = new MessageGenerator();

    @BeforeClass
    public static void setUpData() throws IOException {
        MessageGenerator gen = new MessageGenerator()
                .addFactory(f -> {
                    if (f.equals(Operand._Field.OPERATION)) {
                        return () -> Operation.builder()
                                              .setOperator(Operator.ADD)
                                              .addToOperands(Operand.withNumber(123))
                                              .addToOperands(Operand.withNumber(321))
                                              .build();
                    }
                    return null;
                });

        if (operation == null) {
            operation = gen.generate(Operation.kDescriptor);
        }
        if (containers == null) {
            containers = new ArrayList<>();
            for (int i = 0; i < 1; ++i) {
                containers.add(gen.generate(Containers.kDescriptor));
            }
        }
    }

    /**
     * Test that the serializer can serialize and deserialize a test-set of
     * random data. This is not testing backward compatibility of the
     * serializer.
     *
     * @param serializer The serializer to test.
     */
    private void testSerializer(Serializer serializer) throws IOException {
        // Just a sanity check.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais;
        int size;

        // simple message.
        {
            baos.reset();

            size = serializer.serialize(baos, operation);
            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());
            Operation actual = serializer.deserialize(bais, Operation.kDescriptor);

            assertEquals(actual, operation);
        }

        // complex message, one at a time.
        for (Containers expected : containers) {
            baos.reset();

            size = serializer.serialize(baos, expected);
            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());
            Containers actual;
            try {
                actual = serializer.deserialize(bais, Containers.kDescriptor);
            } catch (TokenizerException e) {
                System.err.println(new String(baos.toByteArray(), StandardCharsets.UTF_8));
                System.err.println(e.asString());
                fail("oops");
                return;
            }

            assertThat(actual, new EqualToMessage<>(expected));
        }

        // complex message in stream.
        {
            baos.reset();
            boolean first = true;
            size = 0;
            for (Containers c : containers) {
                if (first) {
                    first = false;
                } else {
                    baos.write('\n');
                    size += 1;
                }
                size += serializer.serialize(baos, c);
            }

            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());

            first = true;
            for (Containers expected : containers) {
                if (first) {
                    first = false;
                } else {
                    assertThat(bais.read(), is((int)'\n'));
                }
                Containers actual = serializer.deserialize(bais, Containers.kDescriptor);
                assertThat(actual, new EqualToMessage<>(expected));
            }

            assertEquals(0, bais.available());
        }
    }

    /**
     * Tests the current output. This tests the byte-stream generated by serialising with the current generated code /
     */
    public void testOutput(Serializer serializer, String resource)
            throws IOException {
        Binary expected;
        List<Containers> source;
        try (InputStream r = SerializerTest.class.getResourceAsStream(resource)) {
            if (r == null) {
                File file = new File("src/test/resources" + resource);
                File testing = new File("providence-core");
                if (testing.isDirectory()) {
                    file = new File(testing, file.toString());
                }
                containers.stream()
                          .limit(10)
                          .collect(MessageCollectors.toFile(file, serializer));
                fail("No such resource to compare: " + resource);
                return;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = r.read(buffer, 0, 1024)) > 0) {
                out.write(buffer, 0, len);
            }
            expected = Binary.wrap(out.toByteArray());
            source = MessageStreams
                    .stream(new ByteArrayInputStream(out.toByteArray()), serializer, Containers.kDescriptor)
                    .collect(Collectors.toList());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        source.stream().collect(MessageCollectors.toStream(out, serializer));
        Binary actual = Binary.wrap(out.toByteArray());

        if (serializer.binaryProtocol()) {
            assertEquals("Hex data comparison.", expected.toHexString(), actual.toHexString());
        } else {
            assertThat(new String(expected.get()),
                       is(equalToLines(new String(actual.get()))));
        }
    }

    @Test
    public void testCompactJson() throws IOException {
        Serializer serializer = new JsonSerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/compact.json");
    }

    @Test
    public void testNamedJson() throws IOException {
        Serializer serializer = new JsonSerializer(true).named();
        testSerializer(serializer);
        testOutput(serializer, "/compat/named.json");
    }

    @Test
    public void testPrettyJson() throws IOException {
        Serializer serializer = new JsonSerializer(true).pretty();
        testSerializer(serializer);
        testOutput(serializer, "/compat/pretty.json");
    }

    @Test
    public void testPretty() throws IOException {
        testSerializer(new PrettySerializer(true));
        testSerializer(new PrettySerializer(true).compact());
        testSerializer(new PrettySerializer(true).debug());
        testSerializer(new PrettySerializer(true).string());
        testSerializer(new PrettySerializer(true).config());
        testOutput(new PrettySerializer(true), "/compat/pretty.cfg");

    }

    @Test
    public void testBinary() throws IOException {
        Serializer serializer = new BinarySerializer(true, false);
        testSerializer(serializer);
        testOutput(serializer, "/compat/binary.data");
    }

    @Test
    public void testFastBinary() throws IOException {
        Serializer serializer = new FastBinarySerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/fast-binary.data");
    }
}
