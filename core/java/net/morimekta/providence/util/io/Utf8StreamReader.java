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

package net.morimekta.providence.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

/**
 * Similar to java native {@link java.io.InputStreamReader}, but locked to
 * utf-8, and explicitly with no buffering whatsoever. It will only read one
 * byte at a time until it has a valid unicode char.
 *
 * In order to make this reader more efficient, rather wrap the input stream in
 * a BufferedInputStream, which can pass on any buffered bytes to later uses.
 * E.g.:
 *
 * <code>
 *     Reader reader = new Utf8StreamReader(new BufferedInputStream(in));
 * </code>
 */
public class Utf8StreamReader
        extends Reader {
    private final InputStream in;
    private final int[] buffer;

    public Utf8StreamReader(InputStream in) {
        this.in = in;
        this.buffer = new int[5];
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            final int r = in.read();
            if (r < 0) {
                if (i == 0) return -1;
                else return i;
            } else if (r < 0x80) {
                cbuf[off + i] = (char) r;
            } else if ((r & 0xC0) == 0x80) {
                // This byte pattern should not be here.
                cbuf[off + i] = '?';
            } else {
                buffer[0] = r;
                int c = 1;

                // 110xxxxx + 1 * 10xxxxxx  = 11 bit
                if ((r & 0xC0) == 0xC0) {
                    buffer[c++] = in.read();
                }
                // 1110xxxx + 2 * 10xxxxxx  = 16 bit
                if ((buffer[0] & 0xE0) == 0xE0) {
                    buffer[c++] = in.read();
                }
                // 11110xxx + 3 * 10xxxxxx  = 21 bit
                if ((buffer[0] & 0xF0) == 0xF0) {
                    buffer[c++] = in.read();
                }
                // 111110xx + 4 * 10xxxxxx  = 26 bit
                if ((buffer[0] & 0xF8) == 0xF8) {
                    buffer[c++] = in.read();
                }
                // 1111110x + 5 * 10xxxxxx  = 31 bit
                if ((buffer[0] & 0xFC) == 0xFC) {
                    buffer[c++] = in.read();
                }

                cbuf[off + i] = convert(buffer, c);
            }
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    protected static char convert(final int[] arr, final int num) throws IOException {
        int cp = 0;
        switch (num) {
            case 1:
                throw new IOException("Not enough bytes for utf-8 encoding");
            case 2:
                cp = (arr[0] & 0x1f);
                break;
            case 3:
                cp = (arr[0] & 0x0f);
                break;
            case 4:
                cp = (arr[0] & 0x07);
                break;
            case 5:
                cp = (arr[0] & 0x03);
                break;
            case 6:
                cp = (arr[0] & 0x01);
                break;
        }
        for (int i = 1; i < num; ++i) {
            if (arr[i] == -1) throw new IOException("Unexpected end of stream inside utf-8 char.");
            if ((arr[i] & 0xC0) != 0x80) {
                throw new IOException(String.format(Locale.ENGLISH,
                                                    "Unexpected non utf-8 char in utf-8 extra bytes: %2x", arr[i]));
            }
            cp = (cp << 6) | (arr[i] & 0x3f);
        }
        return (char) cp;
    }
}
