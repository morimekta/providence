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
import org.junit.Ignore;
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
public class PSerializerTest {
    private static Operation             operation;
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws PSerializeException, IOException {
        synchronized (PSerializerTest.class) {
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
    private void testSerializer(PSerializer serializer) throws IOException, PSerializeException {
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
    public void testCompatibility(PSerializer serializer, String resource) throws IOException, PSerializeException {
        ArrayList<Containers> actual = arrayListFromResource(resource, Containers.kDescriptor, serializer);

        assertEquals(containers.size(), actual.size());
        for (int i = 0; i < containers.size(); ++i) {
            assertThat(actual.get(i), messageEq(containers.get(i)));
        }
    }

    /**
     * Tests the current output. This tests the byte-stream generated by serialising with the current generated code /
     */
    public void testOutput(PSerializer serializer, String resource, boolean binary)
            throws IOException, PSerializeException {
        Binary expected;
        try (InputStream r = PSerializerTest.class.getResourceAsStream(resource)) {
            if (r == null) {
                File file = new File("resourcestest" + resource);
                containers.stream().collect(MessageCollectors.toFile(file, serializer));
                fail("No such resource to compat: " + resource);
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
        containers.stream()
                  .collect(MessageCollectors.toStream(out, serializer));
        Binary actual = Binary.wrap(out.toByteArray());

        if (binary) {
            assertEquals("Hex data comparison.", expected.toHexString(128), actual.toHexString(128));
        } else {
            assertEquals(new String(expected.get()), new String(actual.get()));
        }
    }

    @Test
    public void testCompactJson() throws PSerializeException, IOException {
        PSerializer serializer = new PJsonSerializer(true, PJsonSerializer.IdType.ID);
        testSerializer(serializer);
        // testCompatibility(serializer, "/compat/compact.json");
        testOutput(serializer, "/compat/compact.json", false);
    }

    @Test
    public void testNamedJson() throws PSerializeException, IOException {
        PSerializer serializer = new PJsonSerializer(true, PJsonSerializer.IdType.NAME);
        testSerializer(serializer);
        // testCompatibility(serializer, "/compat/named.json");
        testOutput(serializer, "/compat/named.json", false);
    }

    @Test
    public void testPrettyJson() throws PSerializeException, IOException {
        PSerializer serializer = new PJsonSerializer(true,
                                                     PJsonSerializer.IdType.NAME,
                                                     PJsonSerializer.IdType.NAME,
                                                     true);
        testSerializer(serializer);
        // testCompatibility(serializer, "/compat/pretty.json");
        testOutput(serializer, "/compat/pretty.json", false);
    }

    @Test
    public void testBinary() throws IOException, PSerializeException {
        PSerializer serializer = new PBinarySerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/binary.data", false);
    }

    @Test
    public void testFastBinary() throws IOException, PSerializeException {
        PSerializer serializer = new PFastBinarySerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/fast-binary.data", false);
    }

    @Test
    @Ignore("Proto serialization format does not parse.")
    public void testProto() throws IOException, PSerializeException {
        PSerializer serializer = new PProtoSerializer(true);
        testSerializer(serializer);
        testOutput(serializer, "/compat/proto.data", false);
    }
}
