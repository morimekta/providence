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

package net.morimekta.util;

import net.morimekta.util.io.Utf8StreamReader;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class StringsTest {
    byte[] mArray;
    byte[] mArray_withEscaping;
    byte[] mArray_withNullbyte;
    byte[] mArray_withUtf8;
    String mString;
    String mString_withEscaping;
    String mString_withUtf8;

    @Before
    public void setUp() {
        mArray = new byte[]{'1', '2', '3'};
        mArray_withNullbyte = new byte[]{'1', '2', '3', '\0'};
        mArray_withEscaping = new byte[]{'1', '2', '3', '\t'};
        mArray_withUtf8 = new byte[]{'1', '2', '3', (byte) 0xc3, (byte) 0xa1};

        mString = "123";
        mString_withEscaping = "123\t";
        mString_withUtf8 = "123á";
    }

    private String TSU_readString(byte[] bytes) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return Strings.readString(is);
    }

    @Test
    public void testReadString() throws IOException {
        assertEquals(mString, TSU_readString(mArray));
        assertEquals(mString, TSU_readString(mArray_withNullbyte));
        assertEquals(mString_withEscaping, TSU_readString(mArray_withEscaping));
        assertEquals(mString_withUtf8, TSU_readString(mArray_withUtf8));
    }

    @Test(expected = IOException.class)
    public void testReadString_ioException() throws IOException {
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                // TODO Auto-generated method stub
                throw new IOException();
            }
        };
        fail("unexpected output: " + Strings.readString(is));
    }

    @Test
    public void testReadString_partialRead() throws IOException {
        byte[] buffer = new byte[]{'a', 'b', 'c', '\0', 'x', 'y', 'z'};
        // BufferedInputStream supports marks.
        InputStream is = new ByteArrayInputStream(buffer);

        assertEquals("abc", Strings.readString(is));
        assertEquals("xyz", Strings.readString(is));
    }

    @Test
    public void testReadString_partialReadWithTerminator() throws IOException {
        byte[] buffer = new byte[]{'a', 'b', 'c', '\r', '\n', 'x', 'y', 'z'};
        // BufferedInputStream supports marks.
        InputStream is = new ByteArrayInputStream(buffer);

        assertEquals("abc", Strings.readString(is, "\r\n"));
        assertEquals("xyz", Strings.readString(is, "\r\n"));
    }

    @Test
    public void testReadString_partialReader() throws IOException {
        byte[] buffer = new byte[]{'a', 'b', 'c', '\0', 'x', 'y', 'z'};
        // BufferedInputStream supports marks.
        Reader is = new Utf8StreamReader(new ByteArrayInputStream(buffer));

        assertEquals("abc", Strings.readString(is));
        assertEquals("xyz", Strings.readString(is));
    }

    @Test
    public void testReadString_partialReaderWithTerminator() throws IOException {
        byte[] buffer = new byte[]{'a', 'b', 'c', '\r', '\n', 'x', 'y', 'z'};
        // BufferedInputStream supports marks.
        Reader is = new Utf8StreamReader(new ByteArrayInputStream(buffer));

        assertEquals("abc", Strings.readString(is, "\r\n"));
        assertEquals("xyz", Strings.readString(is, "\r\n"));
    }
}
