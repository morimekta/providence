package net.morimekta.providence.testing.hazelcast;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.Portable;

import java.io.IOException;
import java.util.Set;

/**
 * TBD
 */
public class PortableReader extends PortableBase  implements com.hazelcast.nio.serialization.PortableReader {

    public PortableReader(PortableWriter portableWriter) {
        super(portableWriter);
    }

    @Override
    public int getVersion() {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public boolean hasField(String fieldName) {
        return this.fieldNames.contains(fieldName);
    }

    @Override
    public Set<String> getFieldNames() {
        return this.fieldNames;
    }

    @Override
    public FieldType getFieldType(String fieldName) {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public int getFieldClassId(String fieldName) {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public int readInt(String fieldName) throws IOException {
        return this.integerMap.get(fieldName);
    }

    @Override
    public long readLong(String fieldName) throws IOException {
        return this.longMap.get(fieldName);
    }

    @Override
    public String readUTF(String fieldName) throws IOException {
        return this.stringMap.get(fieldName);
    }

    @Override
    public boolean readBoolean(String fieldName) throws IOException {
        return this.booleanMap.get(fieldName);
    }

    @Override
    public byte readByte(String fieldName) throws IOException {
        return this.byteMap.get(fieldName);
    }

    @Override
    public char readChar(String fieldName) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public double readDouble(String fieldName) throws IOException {
        return this.doubleMap.get(fieldName);
    }

    @Override
    public float readFloat(String fieldName) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public short readShort(String fieldName) throws IOException {
        return this.shortMap.get(fieldName);
    }

    @Override
    public <P extends Portable> P readPortable(String fieldName) throws IOException {
        return (P)this.portableMap.get(fieldName);
    }

    @Override
    public byte[] readByteArray(String fieldName) throws IOException {
        return this.byteArrayMap.get(fieldName);
    }

    @Override
    public boolean[] readBooleanArray(String fieldName) throws IOException {
        return booleanArrayMap.get(fieldName);
    }

    @Override
    public char[] readCharArray(String fieldName) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public int[] readIntArray(String fieldName) throws IOException {
        return this.integerArrayMap.get(fieldName);
    }

    @Override
    public long[] readLongArray(String fieldName) throws IOException {
        return this.longArrayMap.get(fieldName);
    }

    @Override
    public double[] readDoubleArray(String fieldName) throws IOException {
        return this.doubleArrayMap.get(fieldName);
    }

    @Override
    public float[] readFloatArray(String fieldName) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public short[] readShortArray(String fieldName) throws IOException {
        return this.shortArrayMap.get(fieldName);
    }

    @Override
    public String[] readUTFArray(String fieldName) throws IOException {
        return this.stringArrayMap.get(fieldName);
    }

    @Override
    public Portable[] readPortableArray(String fieldName) throws IOException {
        return this.portableArrayMap.get(fieldName);
    }

    @Override
    public ObjectDataInput getRawDataInput() throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

}
