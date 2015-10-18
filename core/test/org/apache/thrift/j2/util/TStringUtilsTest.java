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

package org.apache.thrift.j2.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.10.15
 */
public class TStringUtilsTest {
    byte[] mArray;
    byte[] mArray_withEscaping;
    byte[] mArray_withNullbyte;
    byte[] mArray_withUtf8;
    String mString;
    String mString_withEscaping;
    String mString_withUtf8;

    @Before
    public void setUp() {
        mArray = new byte[] { '1', '2', '3' };
        mArray_withNullbyte = new byte[] { '1', '2', '3', '\0' };
        mArray_withEscaping = new byte[] { '1', '2', '3', '\t' };
        mArray_withUtf8 = new byte[] { '1', '2', '3', (byte) 0xc3, (byte) 0xa1 };

        mString = "123";
        mString_withEscaping = "123\t";
        mString_withUtf8 = "123á";
    }

    private String TSU_readString(byte[] bytes) throws IOException{
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return TStringUtils.readString(is);
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
        fail("unexpected output: " + TStringUtils.readString(is));
    }

    @Test
    public void testReadString_partialRead() throws IOException {
        byte[] buffer = new byte[]{
                'a', 'b', 'c', '\0',
                'x', 'y', 'z'
        };
        // BufferedInputStream supports marks.
        InputStream is = new ByteArrayInputStream(buffer);

        assertEquals("abc", TStringUtils.readString(is));
        assertEquals("xyz", TStringUtils.readString(is));
    }
}
