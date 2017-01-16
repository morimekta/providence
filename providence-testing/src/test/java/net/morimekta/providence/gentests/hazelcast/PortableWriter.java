package net.morimekta.providence.gentests.hazelcast;

import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;

import java.io.IOException;

/**
 * TBD
 */
public class PortableWriter extends PortableBase implements com.hazelcast.nio.serialization.PortableWriter {

    public PortableWriter() {
        super();
    }

    @Override
    public void writeInt(String fieldName, int value) throws IOException {
        fieldNames.add(fieldName);
        integerMap.put(fieldName, value);
    }

    @Override
    public void writeLong(String fieldName, long value) throws IOException {
        fieldNames.add(fieldName);
        longMap.put(fieldName, value);
    }

    @Override
    public void writeUTF(String fieldName, String value) throws IOException {
        fieldNames.add(fieldName);
        stringMap.put(fieldName, value);
    }

    @Override
    public void writeBoolean(String fieldName, boolean value) throws IOException {
        fieldNames.add(fieldName);
        booleanMap.put(fieldName, value);
    }

    @Override
    public void writeByte(String fieldName, byte value) throws IOException {
        fieldNames.add(fieldName);
        byteMap.put(fieldName, value);
    }

    @Override
    public void writeChar(String fieldName, int value) throws IOException {
        fieldNames.add(fieldName);
        charMap.put(fieldName, value);
    }

    @Override
    public void writeDouble(String fieldName, double value) throws IOException {
        fieldNames.add(fieldName);
        doubleMap.put(fieldName, value);
    }

    @Override
    public void writeFloat(String fieldName, float value) throws IOException {
        fieldNames.add(fieldName);
        floatMap.put(fieldName, value);
    }

    @Override
    public void writeShort(String fieldName, short value) throws IOException {
        fieldNames.add(fieldName);
        shortMap.put(fieldName, value);
    }

    @Override
    public void writePortable(String fieldName, Portable portable) throws IOException {
        fieldNames.add(fieldName);
        portableMap.put(fieldName, portable);
    }

    @Override
    public void writeNullPortable(String fieldName, int factoryId, int classId) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public void writeByteArray(String fieldName, byte[] bytes) throws IOException {
        fieldNames.add(fieldName);
        byteArrayMap.put(fieldName, bytes);
    }

    @Override
    public void writeBooleanArray(String fieldName, boolean[] booleans) throws IOException {
        fieldNames.add(fieldName);
        booleanArrayMap.put(fieldName, booleans);
    }

    @Override
    public void writeCharArray(String fieldName, char[] chars) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public void writeIntArray(String fieldName, int[] ints) throws IOException {
        fieldNames.add(fieldName);
        integerArrayMap.put(fieldName, ints);
    }

    @Override
    public void writeLongArray(String fieldName, long[] longs) throws IOException {
        fieldNames.add(fieldName);
        longArrayMap.put(fieldName, longs);
    }

    @Override
    public void writeDoubleArray(String fieldName, double[] values) throws IOException {
        fieldNames.add(fieldName);
        doubleArrayMap.put(fieldName, values);
    }

    @Override
    public void writeFloatArray(String fieldName, float[] values) throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }

    @Override
    public void writeShortArray(String fieldName, short[] values) throws IOException {
        fieldNames.add(fieldName);
        shortArrayMap.put(fieldName, values);
    }

    @Override
    public void writeUTFArray(String fieldName, String[] values) throws IOException {
        fieldNames.add(fieldName);
        stringArrayMap.put(fieldName, values);
    }

    @Override
    public void writePortableArray(String fieldName, Portable[] portables) throws IOException {
        fieldNames.add(fieldName);
        portableArrayMap.put(fieldName, portables);
    }

    @Override
    public ObjectDataOutput getRawDataOutput() throws IOException {
        throw new IllegalArgumentException("Not implemented!");
    }
}
