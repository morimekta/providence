package net.morimekta.providence.serializer.rw;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class BinaryFormatUtils {
    // --- READ METHODS ---

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(BigEndianBinaryReader input,
                        PMessageDescriptor<Message, Field> descriptor,
                        boolean readStrict) throws IOException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();
        if (builder instanceof BinaryReader) {
            ((BinaryReader) builder).readBinary(input, readStrict);
        } else {
            FieldInfo fieldInfo = readFieldInfo(input);
            while (fieldInfo != null) {
                PField field = descriptor.getField(fieldInfo.getId());
                if (field != null) {
                    Object value = readFieldValue(input, fieldInfo, field.getDescriptor(), readStrict);
                    builder.set(field.getKey(), value);
                } else {
                    if (readStrict) {
                        throw new SerializerException("Unknown field " + fieldInfo.getId() + " for type " + descriptor.getQualifiedName());
                    }
                    readFieldValue(input, fieldInfo, null, false);
                }

                fieldInfo = readFieldInfo(input);
            }

            if (readStrict) {
                try {
                    builder.validate();
                } catch (IllegalStateException e) {
                    throw new SerializerException(e, e.getMessage());
                }
            }
        }

        return builder.build();
    }

    /**
     * Consume a message from the stream without parsing the content into a message.
     *
     * @param in Stream to read message from.
     */
    public static void consumeMessage(BigEndianBinaryReader in, boolean readStrict) throws IOException {
        FieldInfo fieldInfo;
        while ((fieldInfo = readFieldInfo(in)) != null) {
            readFieldValue(in, fieldInfo, null, readStrict);
        }
    }

    /**
     * Read field info from stream. If this is the last field (field ID 0)
     * return null.
     *
     * @param in The stream to consume.
     * @return The field info or null.
     */
    private static FieldInfo readFieldInfo(BigEndianBinaryReader in) throws IOException {
        byte type = in.expectByte();
        if (type == PType.STOP.id) {
            return null;
        }
        return new FieldInfo(in.expectShort(), type);
    }

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param fieldInfo The field info about the content.
     * @param type      The type to generate content for.
     * @return The field value, or null if no type.
     *
     * @throws IOException If unable to read from stream or invalid field type.
     */
    public static Object readFieldValue(BigEndianBinaryReader in,
                                        FieldInfo fieldInfo,
                                        PDescriptor type,
                                        boolean readStrict)
            throws IOException {
        if (type == null) {
            if (readStrict) {
                throw new SerializerException("Reading unknown field in strict mode.");
            }
        } else if (type.getType().id != fieldInfo.getType()) {
            if (readStrict) {
                throw new SerializerException("Mismatching field type in strict mode.");
            } else {
                // consume the content.
                readFieldValue(in, fieldInfo, null, readStrict);
                // return 'null', which should clear the field value.
                return null;
            }
        }

        switch (PType.findById(fieldInfo.getType())) {
            case VOID:
                return Boolean.TRUE;
            case BOOL:
                return in.expectByte() != 0;
            case BYTE:
                return in.expectByte();
            case I16:
                return in.expectShort();
            case ENUM:
            case I32:
                int val = in.expectInt();
                if (type != null && type instanceof PEnumDescriptor) {
                    @SuppressWarnings("unchecked")
                    PEnumBuilder builder = ((PEnumDescriptor<?>)type).builder();
                    builder.setByValue(val);
                    return builder.build();
                } else {
                    return val;
                }
            case I64:
                return in.expectLong();
            case DOUBLE:
                return in.expectDouble();
            case STRING:
            case BINARY:
                int len = in.expectUInt32();
                byte[] data = in.expectBytes(len);
                if (type != null && type.getType() == PType.STRING) {
                    return new String(data, StandardCharsets.UTF_8);
                } else {
                    return Binary.wrap(data);
                }
            case MESSAGE: {
                if (type == null) {
                    consumeMessage(in, readStrict);
                    return null;
                }
                return readMessage(in, (PMessageDescriptor<?,?>) type, readStrict);
            }
            case MAP: {
                final byte keyT = in.expectByte();
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor keyType = null;
                PDescriptor valueType = null;
                PMap.Builder<Object, Object> out;
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.MAP)) {
                        throw new SerializerException("Invalid type for map encoding: " + type);
                    }

                    @SuppressWarnings("unchecked")
                    PMap<Object, Object> mapType = (PMap<Object, Object>) type;
                    keyType = mapType.keyDescriptor();
                    valueType = mapType.itemDescriptor();

                    out = mapType.builder();
                } else {
                    out = new PMap.ImmutableMapBuilder<>();
                }

                FieldInfo keyInfo = new FieldInfo(1, keyT);
                FieldInfo itemInfo = new FieldInfo(2, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, keyInfo, keyType, readStrict);
                    Object value = readFieldValue(in, itemInfo, valueType, readStrict);
                    if (key != null && value != null) {
                        out.put(key, value);
                    } else if (readStrict) {
                        throw new SerializerException("Null key or value in map.");
                    }
                }
                return out.build();
            }
            case SET: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PSet.Builder<Object> out;
                if (type != null) {
                    @SuppressWarnings("unchecked")
                    PSet<Object> setType = (PSet<Object>) type;
                    entryType = setType.itemDescriptor();
                    out = setType.builder();
                } else {
                    out = new PSet.ImmutableSetBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, itemInfo, entryType, readStrict);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new SerializerException("Null value in set.");
                    }
                }

                return out.build();
            }
            case LIST: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PList.Builder<Object> out;
                if (type != null) {
                    @SuppressWarnings("unchecked")
                    PList<Object> listType = (PList<Object>) type;
                    entryType = listType.itemDescriptor();
                    out = listType.builder();
                } else {
                    out = new PList.ImmutableListBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, itemInfo, entryType, readStrict);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new SerializerException("Null value in list.");
                    }
                }

                return out.build();
            }
            default:
                throw new SerializerException("unknown data type: " + fieldInfo.getType());
        }
    }

    // --- WRITE METHODS ---

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    int writeMessage(BigEndianBinaryWriter writer, Message message)
            throws IOException {
        if (message instanceof BinaryWriter) {
            return ((BinaryWriter) message).writeBinary(writer);
        }

        int len = 0;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                len += writeFieldSpec(writer, field.getDescriptor().getType().id, field.getKey());
                len += writeFieldValue(writer,
                                       message.get(field.getKey()),
                                       field.getDescriptor());
            }
        } else {
            for (PField field : message.descriptor().getFields()) {
                if (message.has(field.getKey())) {
                    len += writeFieldSpec(writer, field.getDescriptor().getType().id, field.getKey());
                    len += writeFieldValue(writer,
                                           message.get(field.getKey()),
                                           field.getDescriptor());
                }
            }
        }
        len += writer.writeUInt8(PType.STOP.id);
        return len;
    }

    private static int writeFieldSpec(BigEndianBinaryWriter out, byte type, int key) throws IOException {
        out.writeByte(type);
        out.writeShort((short) key);
        return 3;
    }

    /**
     * Write a field value to stream.
     *
     * @param out   The stream to write to.
     * @param value The value to write.
     * @return The number of bytes written.
     */
    private static int writeFieldValue(BigEndianBinaryWriter out, Object value, PDescriptor descriptor) throws IOException {
        switch (descriptor.getType()) {
            case VOID:
                return 0;
            case BOOL:
                return out.writeByte(((Boolean) value) ? (byte) 1 : (byte) 0);
            case BYTE:
                return out.writeByte((Byte) value);
            case I16:
                return out.writeShort((Short) value);
            case I32:
                return out.writeInt((Integer) value);
            case I64:
                return out.writeLong((Long) value);
            case DOUBLE:
                return out.writeDouble((Double) value);
            case BINARY: {
                Binary binary = (Binary) value;
                int len = out.writeUInt32(binary.length());
                return len + out.writeBinary(binary);
            }
            case STRING: {
                Binary binary = Binary.wrap(value.toString().getBytes(StandardCharsets.UTF_8));
                int len = out.writeUInt32(binary.length());
                return len + out.writeBinary(binary);
            }
            case ENUM:
                return out.writeInt(((PEnumValue<?>) value).getValue());
            case MAP: {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                PMap<?,?> pMap = (PMap<?, ?>) descriptor;
                int len = out.writeByte(pMap.keyDescriptor().getType().id);
                len += out.writeByte(pMap.itemDescriptor().getType().id);
                len += out.writeUInt32(map.size());
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    len += writeFieldValue(out, entry.getKey(), pMap.keyDescriptor());
                    len += writeFieldValue(out, entry.getValue(), pMap.itemDescriptor());
                }
                return len;
            }
            case SET:
            case LIST: {
                @SuppressWarnings("unchecked")
                Collection<Object> coll = (Collection<Object>) value;
                PContainer<?> pSet = (PContainer<?>) descriptor;

                int len = out.writeByte(pSet.itemDescriptor().getType().id);
                len += out.writeUInt32(coll.size());

                for (Object item : coll) {
                    len += writeFieldValue(out, item, pSet.itemDescriptor());
                }
                return len;
            }
            case MESSAGE: {
                @SuppressWarnings("unchecked")
                int size = writeMessage(out, (PMessage) value);
                return size;
            }
            default:
                throw new SerializerException("Unhandled field type: " + descriptor.getType());
        }
    }

    /**
     * Field info data holder with convenience methods.
     */
    public static class FieldInfo {
        private final int id;
        private final byte type;

        public FieldInfo(int id, byte type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("field(%d: %s)", id, PType.findById(type));
        }

        public int getId() {
            return id;
        }

        public byte getType() {
            return type;
        }

    }

}
