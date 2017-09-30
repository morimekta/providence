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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static java.lang.Math.min;

/**
 * Wrap an input stream in a framed buffer reader similar to the thrift
 * TFramedTransport. The input stream will read one whole frame from the
 * byte channel regardless of how many bytes are read, but can be reset
 * at the end
 */
public class FramedBufferInputStream extends InputStream {
    private static Logger LOGGER = LoggerFactory.getLogger(FramedBufferInputStream.class.getName());

    private static final int MAX_BUFFER_SIZE = 16384000;  // 16M.

    private final ByteBuffer          frameSizeBuffer;
    private final ReadableByteChannel in;
    private final ByteBuffer          buffer;

    public FramedBufferInputStream(ReadableByteChannel in) {
        this(in, MAX_BUFFER_SIZE);
    }

    public FramedBufferInputStream(ReadableByteChannel in, int maxFrameSize) {
        this.in = in;
        this.frameSizeBuffer = ByteBuffer.allocate(Integer.BYTES);
        this.buffer = ByteBuffer.allocateDirect(maxFrameSize);
        this.buffer.limit(0);
    }

    @Override
    public int read() throws IOException {
        if (buffer.limit() == 0) {
            if (readFrame() < 0) {
                return -1;
            }
        }
        return intValue(buffer.get());
    }

    private static int intValue(byte b) {
        if (b < 0) return b + 0x100;
        return b;
    }

    @Override
    public int read(@Nonnull byte[] data) throws IOException {
        return read(data, 0, data.length);
    }

    @Override
    public int read(@Nonnull byte[] data, int off, int len) throws IOException {
        if (off < 0 || len < 0) {
            throw new IOException();
        }
        if (off + len > data.length) {
            throw new IOException();
        }

        int pos = 0;
        while (pos < len) {
            // nothing is read yet.
            if (buffer.limit() == 0) {
                if (readFrame() < 0) {
                    return pos;
                }
            }
            if (buffer.remaining() == 0) {
                return pos;
            }

            int remaining = buffer.remaining();
            int readLen = min(len - pos, remaining);
            buffer.get(data, off + pos, readLen);
            pos += readLen;
        }

        return pos;
    }

    /**
     * Skip the rest of the current frame, regardless of how much has bean read / used.
     */
    public void nextFrame() {
        buffer.rewind();
        buffer.limit(0);
    }

    private int readFrame() throws IOException {
        frameSizeBuffer.rewind();

        in.read(frameSizeBuffer);
        if (frameSizeBuffer.position() == 0) {
            return -1;
        }
        if (frameSizeBuffer.position() < Integer.BYTES) {
            throw new IOException("Not enough bytes for frame size: " + frameSizeBuffer.position());
        }

        int frameSize = TFramedTransport.decodeFrameSize(frameSizeBuffer.array());
        if (frameSize < 1) {
            throw new IOException("Invalid frame size " + frameSize);
        } else if (frameSize > buffer.capacity()) {
            IOException ex = new IOException("Frame size too large " + frameSize + " > " + buffer.capacity());
            try {
                // Try to consume the frame so we can continue with the next.
                while (frameSize > 0) {
                    buffer.rewind();
                    buffer.limit(Math.max(frameSize, buffer.capacity()));

                    int r = in.read(buffer);
                    if (r > 0) {
                        frameSize -= r;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                ex.addSuppressed(e);
            }

            throw ex;
        }

        buffer.rewind();
        buffer.limit(frameSize);

        while (in.read(buffer) > 0) {
            if (buffer.position() == frameSize) {
                break;
            }
            LOGGER.debug("still not enough:  "+ buffer.position() + " of " + frameSize);
        }
        if (buffer.position() < frameSize) {
            throw new IOException();
        }

        buffer.flip();
        return frameSize;
    }
}
