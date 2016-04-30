package net.morimekta.providence.thrift.io;

import org.apache.thrift.transport.TFramedTransport;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Wrap an output stream in a framed buffer writer similar to the thrift
 * TFramedTransport.
 */
public class FramedBufferOutputStream extends OutputStream {
    private static final int MAX_BUFFER_SIZE = 16384000;  // 16M.

    private final byte[]              frameSizeBuffer;
    private final ByteBuffer          buffer;
    private final WritableByteChannel out;

    public FramedBufferOutputStream(WritableByteChannel out) {
        this.out = out;
        this.frameSizeBuffer = new byte[4];
        this.buffer = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE);
        this.buffer.limit(MAX_BUFFER_SIZE);
    }

    @Override
    public void write(int val) throws IOException {
        if (!buffer.hasRemaining()) {
            flush();
        }
        buffer.put((byte) val);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        if (buffer.remaining() < bytes.length) {
            flush();
        }
        buffer.put(bytes);
    }

    @Override
    public void write(byte[] var1, int off, int len) throws IOException {
        if (buffer.remaining() < len) {
            flush();
        }
        buffer.put(var1, off, off);
    }

    @Override
    public void flush() throws IOException {
        int frameSize = buffer.position();
        if (frameSize > 0) {
            TFramedTransport.encodeFrameSize(frameSize, frameSizeBuffer);
            out.write(ByteBuffer.wrap(frameSizeBuffer));

            buffer.flip();
            while (buffer.hasRemaining()) {
                out.write(buffer);
            }
            buffer.rewind();
            buffer.limit(MAX_BUFFER_SIZE);
        }
    }
}
