package net.morimekta.providence.thrift.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Testing the frame buffered input stream.
 */
public class FramedBufferTest {
    private static final byte[] data = new byte[]{0, 0, 0, 10, 't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a', '\n'};
    private static final byte[] multiData = new byte[]{
            0, 0, 0, 10, 't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a', '\n',
            0, 0, 0, 13, 'n', 'o', 't', ' ', 'a', 'g', 'a', 'i', 'n', '.', '.', '.', '\n'};

    @Test
    public void testRead() throws IOException {
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(data));
        FramedBufferInputStream in = new FramedBufferInputStream(channel);

        byte[] out = new byte[14];
        assertEquals(10, in.read(out));
        assertEquals("this is a\n", new String(out, 0, 10, UTF_8));
    }

    @Test
    public void testReadMultiple() throws IOException {
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(multiData));
        FramedBufferInputStream in = new FramedBufferInputStream(channel);

        in.nextFrame();

        byte[] out = new byte[14];
        assertEquals(10, in.read(out));
        assertEquals("this is a\n", new String(out, 0, 10, UTF_8));

        in.nextFrame();

        out = new byte[14];
        assertEquals(13, in.read(out));
        assertEquals("not again...\n", new String(out, 0, 13, UTF_8));
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(baos);
        FramedBufferOutputStream out = new FramedBufferOutputStream(channel);

        out.write("this is a\n".getBytes(UTF_8));
        out.completeFrame();

        assertArrayEquals(data, baos.toByteArray());
    }

    @Test
    public void testWriteMultiple() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(baos);
        FramedBufferOutputStream out = new FramedBufferOutputStream(channel);

        out.write("this is a\n".getBytes(UTF_8));
        out.completeFrame();

        out.write("not again...\n".getBytes(UTF_8));
        out.completeFrame();

        assertArrayEquals(multiData, baos.toByteArray());
    }
}
