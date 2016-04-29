package net.morimekta.providence.thrift.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrap an input stream in a framed buffer reader similar to the thrift
 * TFramedTransport.
 */
public class FramedBufferInputSteram extends InputStream {
    private final InputStream in;

    private int pos;
    private byte[] buffer;

    public FramedBufferInputSteram(InputStream in) {
        this.in = in;
        this.pos = -1;
    }

    @Override
    public int read() throws IOException {
        if (pos < 0) {
            byte[] tmp = new byte[4];
            int i = 0;
            while ((i += in.read(tmp, i, 4 - i)) > 0) {
                if (i == 4) break;
            }
            int frameSize = decodeFrameSize(tmp);
            if (frameSize < 1) {
                throw new IOException();
            }
            this.buffer = new byte[frameSize];

            i = 0;
            while ((i += in.read(tmp, i, frameSize - i)) > 0) {
                if (i == frameSize) break;
            }
            if (i < frameSize) {
                throw new IOException();
            }
            pos = 0;
        }
        int ret = buffer[pos];
        if (++pos >= buffer.length) {
            pos = -1;
        }
        return ret;
    }

    private static int decodeFrameSize(final byte[] buf) {
        return  ((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) <<  8) |
                ((buf[3] & 0xff));
    }

}
