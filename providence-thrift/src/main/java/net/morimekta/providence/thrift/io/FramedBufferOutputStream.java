package net.morimekta.providence.thrift.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrap an output stream in a framed buffer writer similar to the thrift
 * TFramedTransport.
 */
public class FramedBufferOutputStream extends OutputStream {
    private final ByteArrayOutputStream buffer;
    private final OutputStream out;

    public FramedBufferOutputStream(OutputStream out) {
        this.out = out;
        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public void write(int val) throws IOException {
        buffer.write(val);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.write(bytes);
    }

    @Override
    public void write(byte[] var1, int off, int len) throws IOException {
        buffer.write(var1, off, off);
    }

    @Override
    public void flush() throws IOException {
        int frameSize = buffer.size();
        if (frameSize > 0) {
            out.write(0xff & (frameSize >>> 24));
            out.write(0xff & (frameSize >>> 16));
            out.write(0xff & (frameSize >>> 8));
            out.write(0xff & (frameSize));
            out.write(buffer.toByteArray());
            buffer.reset();
        }
    }
}
