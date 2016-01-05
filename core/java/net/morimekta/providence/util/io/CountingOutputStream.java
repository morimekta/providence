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
import java.io.OutputStream;

/**
 * An output stream that counts the number of bytes written.
 *
 * @author Stein Eldar Johnsen
 * @since 06.09.15
 */
public class CountingOutputStream extends OutputStream {
    private final OutputStream mOut;
    private int mByteCount;

    public CountingOutputStream(OutputStream out) {
        mOut = out;
        mByteCount = 0;
    }

    @Override
    public void write(int b) throws IOException {
        mOut.write(b);
        ++mByteCount;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        mOut.write(bytes);
        mByteCount += bytes.length;
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        mOut.write(bytes, off, len);
        mByteCount += len;
    }

    @Override
    public void flush() throws IOException {
        mOut.flush();
    }

    @Override
    public void close() throws IOException {
        mOut.close();
    }

    public int getByteCount() {
        return mByteCount;
    }
}
