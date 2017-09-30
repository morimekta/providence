/*
 * Copyright 2016 Providence Authors
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
package net.morimekta.providence.thrift.io;

import org.apache.thrift.transport.TFramedTransport;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Wrap an output stream in a framed buffer writer similar to the thrift
 * TFramedTransport. The output stream will write everything in one single
 * block when it is closed.
 */
public class FramedBufferOutputStream extends OutputStream {
    private static final int MAX_BUFFER_SIZE = 16384000;  // 16M.

    private final byte[]              frameSizeBuffer;
    private final ByteBuffer          buffer;
    private final WritableByteChannel out;

    public FramedBufferOutputStream(WritableByteChannel out) {
        this(out, MAX_BUFFER_SIZE);
    }

    public FramedBufferOutputStream(WritableByteChannel out, int maxBufferSize) {
        this.out = out;
        this.frameSizeBuffer = new byte[4];
        this.buffer = ByteBuffer.allocateDirect(maxBufferSize);
        this.buffer.limit(maxBufferSize);
    }

    @Override
    public void write(int val) throws IOException {
        if (!buffer.hasRemaining()) {
            throw new IOException(String.format("Frame size exceeded: 1 needed, 0 remaining, %d total",
                                                buffer.capacity()));
        }
        buffer.put((byte) val);
    }

    @Override
    public void write(@Nonnull byte[] bytes) throws IOException {
        if (buffer.remaining() < bytes.length) {
            throw new IOException(String.format("Frame size exceeded: %d needed, %d remaining, %d total",
                                                bytes.length, buffer.remaining(), buffer.capacity()));
        }
        buffer.put(bytes);
    }

    @Override
    public void write(@Nonnull byte[] var1, int off, int len) throws IOException {
        if (buffer.remaining() < len) {
            throw new IOException(String.format("Frame size exceeded: %d needed, %d remaining, %d total",
                                                len, buffer.remaining(), buffer.capacity()));
        }
        buffer.put(var1, off, off);
    }

    /**
     * Write the frame at the current state, and reset the buffer to be able to
     * generate a new frame.
     *
     * @throws IOException On failed write.
     */
    public void completeFrame() throws IOException {
        int frameSize = buffer.position();
        if (frameSize > 0) {
            TFramedTransport.encodeFrameSize(frameSize, frameSizeBuffer);
            out.write(ByteBuffer.wrap(frameSizeBuffer));

            buffer.flip();
            while (buffer.hasRemaining()) {
                out.write(buffer);
            }
            buffer.rewind();
            buffer.limit(buffer.capacity());
        }
    }

    @Override
    public void close() throws IOException {
        completeFrame();
    }
}
