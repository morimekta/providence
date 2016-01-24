package net.morimekta.util.io;

import net.morimekta.util.Binary;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test testing the pairing between BinaryWriter and BinaryReader, and that
 * what it writes will be read back exactly the same.
 */
public class BinaryIOTest {
    ByteArrayOutputStream out;
    BinaryWriter          writer;

    @Before
    public void setUp() throws InterruptedException, IOException {
        out = new ByteArrayOutputStream();
        writer = new BinaryWriter(out);
    }

    private BinaryReader getReader() {
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        out.reset();
        return new BinaryReader(in);
    }

    @Test
    public void testBytes() throws IOException {
        byte[] bytes = new byte[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        // test writing and reading bytes.
        writer.write(bytes);

        BinaryReader reader = getReader();

        byte[] read = new byte[bytes.length];
        reader.read(read);

        assertArrayEquals(bytes, read);
    }

    @Test
    public void testByte() throws IOException {
        // test writing and reading bytes.
        writer.writeByte((byte) 1);
        writer.writeByte((byte) 0xff);
        writer.writeByte((byte) '\"');
        writer.writeByte((byte) 0);

        BinaryReader reader = getReader();

        assertEquals((byte) 1, reader.readByte());
        assertEquals((byte) 0xff, reader.readByte());
        assertEquals((byte) '\"', reader.readByte());
        assertEquals((byte) 0, reader.readByte());
    }

    @Test
    public void testShort() throws IOException {
        // test writing and reading shorts.
        writer.writeShort((short) 1);
        writer.writeShort((short) 0xffff);
        writer.writeShort((short) -12345);
        writer.writeShort((short) 0);

        BinaryReader reader = getReader();

        assertEquals((short) 1, reader.readShort());
        assertEquals((short) 0xffff, reader.readShort());
        assertEquals((short) -12345, reader.readShort());
        assertEquals((short) 0, reader.readShort());
    }

    @Test
    public void testInt() throws IOException {
        // test writing and reading shorts.
        writer.writeInt(1);
        writer.writeInt(0xdeadbeef);
        writer.writeInt(0xffffffff);
        writer.writeInt(-1234567890);
        writer.writeInt(0);

        BinaryReader reader = getReader();

        assertEquals(1, reader.readInt());
        assertEquals(0xdeadbeef, reader.readInt());
        assertEquals(0xffffffff, reader.readInt());
        assertEquals(-1234567890, reader.readInt());
        assertEquals(0, reader.readInt());
    }

    @Test
    public void testLong() throws IOException {
        // test writing and reading shorts.
        writer.writeLong(1);
        writer.writeLong(0xdeadbeefcafebabeL);
        writer.writeLong(0xffffffffffffffffL);
        writer.writeLong(-1234567890123456789L);
        writer.writeLong(0);

        BinaryReader reader = getReader();

        assertEquals(1, reader.readLong());
        assertEquals(0xdeadbeefcafebabeL, reader.readLong());
        assertEquals(0xffffffffffffffffL, reader.readLong());
        assertEquals(-1234567890123456789L, reader.readLong());
        assertEquals(0, reader.readLong());
    }

    @Test
    public void testDouble() throws IOException {
        // test writing and reading shorts.
        writer.writeDouble(1);
        writer.writeDouble(6.62607004E-34);
        writer.writeDouble(299792458);
        writer.writeDouble(-123456.123456);
        writer.writeDouble(0.0);

        BinaryReader reader = getReader();

        assertEquals(1.0, reader.readDouble(), 0.0);
        assertEquals(6.62607004E-34, reader.readDouble(), 0.0);
        assertEquals(299792458, reader.readDouble(), 0.0);
        assertEquals(-123456.123456, reader.readDouble(), 0.0);
        assertEquals(0.0, reader.readDouble(), 0.0);
    }

    @Test
    public void testBinary() throws IOException {
        Binary bytes = Binary.wrap(new byte[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 9, 8, 7, 6, 5, 4, 3, 2, 1});
        // test writing and reading bytes.
        writer.writeBinary(bytes);

        BinaryReader reader = getReader();

        Binary read = reader.readBinary(bytes.length());

        assertEquals(bytes, read);
    }

    @Test
    public void testUnsigned() {
        // TODO: implement
    }

    @Test
    public void testSigned() {
        // TODO: implement
    }

    @Test
    public void testZigzag() throws IOException {
        // test integer (32 bit) varints.
        testZigzag(0, 1);
        testZigzag(1, 1);
        testZigzag(-1, 1);
        testZigzag(0xcafe, 3);
        testZigzag(-123456, 3);
        testZigzag(615671317, 5);
        testZigzag(Integer.MIN_VALUE, 5);
        testZigzag(Integer.MAX_VALUE, 5);

        // test long (64 bit) varints.

        testZigzag(0L, 1);
        testZigzag(1L, 1);
        testZigzag(-1L, 1);
        testZigzag(0xcafeL, 3);
        testZigzag(-123456L, 3);

        testZigzag(1234567890123456789L, 9);
        testZigzag(0xcafebabedeadbeefL, 9);
    }

    private void testZigzag(int value, int bytes) throws IOException {
        out.reset();
        writer.writeZigzag(value);
        assertEquals(bytes, out.size());
        BinaryReader reader = getReader();
        assertEquals(value, reader.readIntZigzag());
    }

    private void testZigzag(long value, int bytes) throws IOException {
        out.reset();
        writer.writeZigzag(value);
        assertEquals(bytes, out.size());
        BinaryReader reader = getReader();
        assertEquals(value, reader.readLongZigzag());
    }

    @Test
    public void testVarint() throws IOException {
        // test integer (32 bit) varints.
        testVarint(0, 1);
        testVarint(1, 1);
        testVarint(-1, 5);
        testVarint(0xcafe, 3);
        testVarint(-123456, 5);
        testVarint(Integer.MIN_VALUE, 5);
        testVarint(Integer.MAX_VALUE, 5);

        // test long (64 bit) varints.

        testVarint(0L, 1);
        testVarint(1L, 1);
        testVarint(-1L, 10);
        testVarint(0xcafeL, 3);
        testVarint(-123456L, 10);
        testVarint(Long.MIN_VALUE, 10);
        testVarint(Long.MAX_VALUE, 9);

        testVarint(1234567890123456789L, 9);
        testVarint(0xcafebabedeadbeefL, 10);
    }

    private void testVarint(int value, int bytes) throws IOException {
        out.reset();
        writer.writeVarint(value);
        assertEquals(bytes, out.size());
        BinaryReader reader = getReader();
        assertEquals(value, reader.readIntVarint());
    }

    private void testVarint(long value, int bytes) throws IOException {
        out.reset();
        writer.writeVarint(value);
        assertEquals(bytes, out.size());
        BinaryReader reader = getReader();
        assertEquals(value, reader.readLongVarint());
    }
}
