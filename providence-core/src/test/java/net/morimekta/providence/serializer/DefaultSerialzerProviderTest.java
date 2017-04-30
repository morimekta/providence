/*
 * Copyright 2017 Providence Authors
 *
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

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultSerialzerProviderTest {
    @Test
    public void testDefault() {
        assertThat(new DefaultSerializerProvider().getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(BinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(BinarySerializer.ALT_MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.JSON_MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(FastBinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(FastBinarySerializer.class)));
    }

    @Test
    public void testGetSerializer() {
        assertThat(new DefaultSerializerProvider().getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));

        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));
    }

    @Test
    public void testGetSerializer_fail() {
        try {
            new DefaultSerializerProvider().getSerializer("text/plain");
            fail("No exception on no serializer");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("No such serializer for media type text/plain"));
        }
    }
}
