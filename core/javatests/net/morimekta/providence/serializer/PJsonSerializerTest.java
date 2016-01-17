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

import net.morimekta.test.calculator.Operation;
import net.morimekta.test.providence.CompactFields;
import net.morimekta.test.providence.Containers;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.util.PStringUtils.readString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class PJsonSerializerTest {
    private String compact_json;
    private String named_json;
    private String pretty_json;

    private Operation operation;

    private CompactFields cat1;
    private CompactFields cat2;

    private ArrayList<Containers> testData;

    @Before
    public void setUp() throws PSerializeException, IOException {
        PJsonSerializer serializer = new PJsonSerializer(false);

        try (InputStream in = getClass().getResourceAsStream("/json/calculator/compact.json")) {
            compact_json = readString(in).trim();
        }
        try (InputStream in = getClass().getResourceAsStream("/json/calculator/named.json")) {
            named_json = readString(in).trim();
        }
        try (InputStream in = getClass().getResourceAsStream("/json/calculator/pretty.json")) {
            pretty_json = readString(in).trim();
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(compact_json.getBytes(UTF_8));
        operation = serializer.deserialize(bais, Operation.kDescriptor);

        cat1 = CompactFields.builder()
                            .setName("my_category")
                            .setId(44)
                            .build();
        cat2 = CompactFields.builder()
                            .setName("my_category")
                            .setId(44)
                            .setLabel("My Category")
                            .build();
    }

    @Test
    public void testSerialize_compactIdJson() throws PSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PJsonSerializer serializer = new PJsonSerializer();

        int expectedLength = serializer.serialize(baos, operation);
        assertEquals(compact_json, new String(baos.toByteArray(), UTF_8));
        assertEquals(expectedLength, baos.toByteArray().length);
    }

    @Test
    public void testSerialize_compactNamedJson() throws PSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PJsonSerializer serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);

        int expectedLength = serializer.serialize(baos, operation);
        assertEquals(named_json, new String(baos.toByteArray(), UTF_8));
        assertEquals(expectedLength, baos.toByteArray().length);
    }

    @Test
    public void testDeserialize_compactIdJson() throws PSerializeException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compact_json.getBytes());
        // Deserializing can deserialize both formats regardless of serializer
        // type.
        PJsonSerializer serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);
        Operation operation = serializer.deserialize(bais, Operation.kDescriptor);

        assertEquals(operation, operation);
    }

    @Test
    public void testDeserialize_compactNamedJson() throws PSerializeException {
        ByteArrayInputStream bais = new ByteArrayInputStream(named_json.getBytes());
        // Deserializing can deserialize both formats regardless of serializer
        // type.
        PJsonSerializer serializer = new PJsonSerializer();
        Operation operation = serializer.deserialize(bais, Operation.kDescriptor);

        assertEquals(operation, operation);
    }

    @Test
    public void testSerialize_compactStruct() throws PSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PJsonSerializer serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);

        String expectedOutput = "[\"my_category\",44]";
        int expectedLength = serializer.serialize(baos, cat1);

        assertEquals(expectedOutput, new String(baos.toByteArray(), UTF_8));
        assertEquals(18, expectedLength);

        baos.reset();

        expectedOutput = "[\"my_category\",44,\"My Category\"]";
        expectedLength = serializer.serialize(baos, cat2);

        assertEquals(expectedOutput, new String(baos.toByteArray(), UTF_8));
        assertEquals(32, expectedLength);
    }

    @Test
    public void testDeserialize_compactStruct() throws PSerializeException {
        ByteArrayInputStream bais = new ByteArrayInputStream("[\"my_category\",44]".getBytes(UTF_8));
        PJsonSerializer serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);

        CompactFields category = serializer.deserialize(bais, CompactFields.kDescriptor);

        assertEquals("my_category", category.getName());
        assertEquals(44, category.getId());
        assertNull(category.getLabel());

        bais = new ByteArrayInputStream("[\"my_category\",44,\"My Category\"]".getBytes(UTF_8));
        category = serializer.deserialize(bais, CompactFields.kDescriptor);

        assertEquals("my_category", category.getName());
        assertEquals(44, category.getId());
        assertEquals("My Category", category.getLabel());
    }
}
