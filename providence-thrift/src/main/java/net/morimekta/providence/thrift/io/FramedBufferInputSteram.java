package net.morimekta.providence.thrift.io;

import org.apache.thrift.transport.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static java.lang.Math.min;

/**
 * Wrap an input stream in a framed buffer reader similar to the thrift
 * TFramedTransport.
 */
public class FramedBufferInputSteram extends InputStream {
    private static Logger LOGGER = LoggerFactory.getLogger(FramedBufferInputSteram.class.getName());

    private static final int MAX_BUFFER_SIZE = 16384000;  // 16M.

    private final ByteBuffer          frameSizeBuffer;
    private final ReadableByteChannel in;
    private final ByteBuffer          buffer;

    public FramedBufferInputSteram(ReadableByteChannel in) {
        this.in = in;
        this.frameSizeBuffer = ByteBuffer.allocate(Integer.BYTES);
        this.buffer = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE);
        this.buffer.limit(0);
    }

    @Override
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
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
    public int read(byte[] data) throws IOException {
        return read(data, 0, data.length);
    }

    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        if (off < 0 || len < 0) {
            throw new IOException();
        }
        if (off + len > data.length) {
            throw new IOException();
        }

        int pos = 0;
        while (pos < len) {
            if (!buffer.hasRemaining()) {
                if (readFrame() < 0) {
                    return pos;
                }
            }
            int remaining = buffer.remaining();
            buffer.get(data, off + pos, min(len - pos, remaining));
            pos += remaining;
        }

        return pos;
    }

    private int readFrame() throws IOException {
        frameSizeBuffer.rewind();
        frameSizeBuffer.limit(Integer.BYTES);

        in.read(frameSizeBuffer);
        if (frameSizeBuffer.position() == 0) {
            return -1;
        }
        if (frameSizeBuffer.position() < Integer.BYTES) {
            throw new IOException();
        }

        int frameSize = TFramedTransport.decodeFrameSize(frameSizeBuffer.array());
        if (frameSize < 1 || frameSize > MAX_BUFFER_SIZE) {
            throw new IOException();
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
