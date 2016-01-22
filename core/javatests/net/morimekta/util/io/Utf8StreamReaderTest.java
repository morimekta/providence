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

package net.morimekta.util.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 28.12.15.
 */
public class Utf8StreamReaderTest {
    @Test
    public void testRead_ASCII() throws IOException {
        byte[] data = new byte[]{'a', 'b', 'b', 'a', '\t', 'x'};

        Utf8StreamReader reader = new Utf8StreamReader(new ByteArrayInputStream(data));

        char[] out = new char[6];
        assertEquals(6, reader.read(out));
        assertEquals("abba\tx", String.valueOf(out));
    }

    @Test
    public void testRead_UTF8_longString() throws IOException {
        String original = "ü$Ѹ~OӐW| \\rBֆc}}ӂဂG3>㚉EGᖪǙ\\t;\\tၧb}H(πи-ˁ&H59XOqr/,?DרB㡧-Үyg9i/?l+ႬЁjZr=#DC+;|ԥ'f9VB5|8]cOEሹrĐaP.ѾҢ/^nȨޢ\\\"u";
        byte[] data = original.getBytes(StandardCharsets.UTF_8);
        char[] out = new char[original.length()];

        Utf8StreamReader reader = new Utf8StreamReader(new ByteArrayInputStream(data));

        assertEquals(out.length, reader.read(out));
        assertEquals(original, String.valueOf(out));
    }

    @Test
    public void testRead_UTF8_singleRead() throws IOException {
        String original = "ü$Ѹ~";
        byte[] data = original.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        assertEquals('ü', (char) new Utf8StreamReader(bais).read());
        assertEquals('$', (char) new Utf8StreamReader(bais).read());
        assertEquals('Ѹ', (char) new Utf8StreamReader(bais).read());
        assertEquals('~', (char) new Utf8StreamReader(bais).read());
    }
}
