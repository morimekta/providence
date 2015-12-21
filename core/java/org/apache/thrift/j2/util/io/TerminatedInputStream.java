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

package org.apache.thrift.j2.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * InputStream that terminates read if it encounters a termination byte
 * sequence. The termination sequence is not included in the outputted read
 * bytes, but is read from the passed input stream.
 *
 * @author Stein Eldar Johnsen
 * @since 13.09.15
 */
public class TerminatedInputStream extends InputStream {
    private final InputStream mIn;
    private final byte[] mTerminator;
    private final byte[] mSkip;

    private byte[] mBuffer;
    private int mBufferLen;
    private boolean mTerminated;

    public TerminatedInputStream(InputStream in, byte[] terminator, byte[] skip) {
        mIn = in;
        mTerminator = terminator;
        mSkip = skip == null ? new byte[]{} : skip;
        mTerminated = false;

        Arrays.sort(mSkip);
    }

    private boolean bufferIsTerminator() {
        if (mBufferLen != mTerminator.length) return false;
        return Arrays.equals(mBuffer, mTerminator);
    }

    private void shiftBuffer(byte read) {
        if (mBufferLen == mBuffer.length) {
            System.arraycopy(mBuffer, 1, mBuffer, 0, mBufferLen - 1);
            mBuffer[mBuffer.length - 1] = read;
        } else {
            mBuffer[mBufferLen] = read;
            ++mBufferLen;
        }
    }

    @Override
    public int read() throws IOException {
        if (mTerminated) {
            return -1;
        }
        if (mBuffer == null) {
            mBufferLen = 0;
            mBuffer = new byte[mTerminator.length];
            for (int i = 0; i < mTerminator.length; ++i) {
                int read = mIn.read();
                if (read < 0) {
                    break;
                }
                mBuffer[i] = (byte) read;
                ++mBufferLen;
            }
        }

        while (!mTerminated) {
            if (mBufferLen == 0 || bufferIsTerminator()) {
                mTerminated = true;
                return -1;
            }
            int out = mBuffer[0] % 0x100;
            int read = mIn.read();
            if (read < 0) {
                shiftBuffer((byte) 0);
                --mBufferLen;
            } else {
                shiftBuffer((byte) read);
            }
            if (Arrays.binarySearch(mSkip, (byte) out) < 0) {
                return out;
            }
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        return mTerminated ? 0 : mBufferLen + mIn.available();
    }

    @Override
    public void close() throws IOException {
        mTerminated = true;
        mIn.close();
    }

    @Override
    public void mark(int readLimit) {
        mIn.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        mIn.reset();
    }

    @Override
    public boolean markSupported() {
        return mIn.markSupported();
    }
}
