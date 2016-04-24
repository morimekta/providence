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
import net.morimekta.test.calculator.Operation;
import net.morimekta.test.providence.Containers;
import net.morimekta.util.Binary;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static net.morimekta.providence.testing.ProvidenceHelper.arrayListFromJsonResource;
import static net.morimekta.providence.testing.ProvidenceHelper.arrayListFromResource;
import static net.morimekta.providence.testing.ProvidenceHelper.fromJsonResource;
import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testing that each serializer does it's job. Does not test backward
 * compatibility / specific internal cases. Those are in the respective
 * P*SerializerTest.
 */
public class SerializerTest {
    private static Operation             operation;
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws SerializerException, IOException {
        synchronized (SerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (operation == null) {
                operation = fromJsonResource("/json/calculator/compact.json", Operation.kDescriptor);
            }
            if (containers == null) {
                containers = arrayListFromJsonResource("/compat/compact.json", Containers.kDescriptor);
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
    private void testSerializer(Serializer serializer) throws IOException, SerializerException {
        // Just a sanity check.
        assertTrue(containers.size() == 10);

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

            assertThat(actual, messageEq(operation));
        }

        // complex message, one at a time.
        for (int i = 0; i < 10; ++i) {
            baos.reset();

            Containers expected = containers.get(i);
            size = serializer.serialize(baos, expected);
            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());
            Containers actual = serializer.deserialize(bais, Containers.kDescriptor);

            assertThat(actual, messageEq(expected));
        }

        // complex message in stream.
        {
            baos.reset();
            size = 0;
            for (int i = 0; i < 10; ++i) {
                if (i != 0) {
                    baos.write('\n');
                    size += 1;
                }
                size += serializer.serialize(baos, containers.get(i));
            }

            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());

            for (int i = 0; i < 10; ++i) {
                if (i != 0) {
                    assertEquals('\n', bais.read());
                }
                Containers expected = containers.get(i);
                Containers actual = serializer.deserialize(bais, Containers.kDescriptor);

                assertThat(actual, messageEq(expected));
            }

            assertEquals(0, bais.available());
        }
    }

    /**
     * Tests that the serializer can deserialize the given file and still produce the
     */
    public void testCompatibility(Serializer serializer, String resource) throws IOException, SerializerException {
        ArrayList<Containers> actual = arrayListFromResource(resource, Containers.kDescriptor, serializer);

        assertEquals(containers.size(), actual.size());
        for (int i = 0; i < containers.size(); ++i) {
            assertThat(actual.get(i), messageEq(containers.get(i)));
        }
    }

    /**
     * Tests the current output. This tests the byte-stream generated by serialising with the current generated code /
     */
    public void testOutput(Serializer serializer, String resource)
            throws IOException, SerializerException {
        Binary expected;
        try (InputStream r = SerializerTest.class.getResourceAsStream(resource)) {
            if (r == null) {
                File file = new File("src/test/resources" + resource);
                File testing = new File("providence-testing");
                if (testing.isDirectory()) {
                    file = new File(testing, file.toString());
                }
                containers.stream().collect(MessageCollectors.toFile(file, serializer));
                fail("No such resource to compare: " + resource);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = r.read(buffer, 0, 1024)) > 0) {
                out.write(buffer, 0, len);
            }
            expected = Binary.wrap(out.toByteArray());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        containers.stream().collect(MessageCollectors.toStream(out, serializer));
        Binary actual = Binary.wrap(out.toByteArray());

        if (serializer.binaryProtocol()) {
            assertEquals("Hex data comparison.", expected.toHexString(), actual.toHexString());
        } else {
            assertEquals(new String(expected.get()), new String(actual.get()));
        }
    }

    @Test
    public void testCompactJson() throws SerializerException, IOException {
        Serializer serializer = new JsonSerializer(true, JsonSerializer.IdType.ID);
        testSerializer(serializer);
        testOutput(serializer, "/compat/compact.json");
        testCompatibility(serializer, "/compat/compact.json");
    }

    @Test
    public void testNamedJson() throws SerializerException, IOException {
        Serializer serializer = new JsonSerializer(true, JsonSerializer.IdType.NAME);
        testSerializer(serializer);
        testOutput(serializer, "/compat/named.json");
        testCompatibility(serializer, "/compat/named.json");
    }

    @Test
    public void testPrettyJson() throws SerializerException, IOException {
        Serializer serializer = new JsonSerializer(true,
                                                   JsonSerializer.IdType.NAME,
                                                   JsonSerializer.IdType.NAME,
                                                   true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/pretty.json");
        testCompatibility(serializer, "/compat/pretty.json");
    }

    @Test
    public void testBinary() throws IOException, SerializerException {
        Serializer serializer = new BinarySerializer(true, false);
        testSerializer(serializer);
        testOutput(serializer, "/compat/binary.data");
        testCompatibility(serializer, "/compat/binary.data");
    }

    @Test
    public void testFastBinary() throws IOException, SerializerException {
        Serializer serializer = new FastBinarySerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/fast-binary.data");
        testCompatibility(serializer, "/compat/fast-binary.data");
    }
}
