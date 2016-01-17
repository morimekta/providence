/*
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

package net.morimekta.providence.serializer;

import net.morimekta.providence.Binary;
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
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.util.io.BinaryReader;
import net.morimekta.providence.util.io.BinaryWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Compact binary serializer using the protocol buffer serialization format.
 *
 * The format is very simple, though has some drawbacks:
 * <ul>
 *     <li>Contained Messages are serialized and written as bytes, requiring
 *         the message writer to serialize the message internally, then write
 *         that to the output stream.
 *         <p/>
 *         TODO: Possible improvement is to use recycled byte buffers instead
 *         of fresh ByteArrayOutputStream each time.
 *     </li>
 *     <li>Lists and sets are handled as repeated fields.</li>
 *     <li>Contained lists and sets are handled as compacted repeated fields.
 *     </li>
 *     <li>Maps are faked as separate messages, making a virtual message
 *         containing the inner fields:
 *         <ul>
 *             <li>required KeyType key = 1;</li>
 *             <li>required ValueType value = 2;</li>
 *         </ul>
 *     </li>
 *     <li>All numbers are assumed to be varints, except double which is
 *         fixed64.</li>
 * </ul>
 * See data definition file <code>docs/proto-serializer.md</code> for format
 * spec and type matching.
 */
public class PProtoSerializer
        extends PSerializer {
    private final boolean strict;

    /**
     * Construct a serializer instance.
     */
    public PProtoSerializer() {
        this(false);
    }

    /**
     * Construct a serializer instance.
     */
    public PProtoSerializer(boolean strict) {
        this.strict = strict;
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message) throws IOException, PSerializeException {
        BinaryWriter writer = new BinaryWriter(output);
        return writeMessage(writer, message);
    }

    @Override
    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
            throws IOException, PSerializeException {
        try {
            BinaryWriter writer = new BinaryWriter(output);
            if (PType.MESSAGE == descriptor.getType()) {
                return writeMessage(writer, (PMessage<?>) value);
            } else {
                int type = getType(descriptor.getType());
                return writeFieldValue(writer, type, descriptor, value);
            }
        } catch (IOException e) {
            throw new PSerializeException(e, "Unable to write to stream");
        }
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> descriptor)
            throws PSerializeException, IOException {
        BinaryReader reader = new BinaryReader(input);
        // Assume it consists of a single field.
        if (PType.MESSAGE == descriptor.getType()) {
            return cast((Object) readMessage(reader, (PStructDescriptor<?, ?>) descriptor));
        } else {
            int tag = getType(descriptor.getType());
            return readFieldValue(reader, tag, descriptor);
        }
    }

    // --- MESSAGE ---

    protected int writeMessage(BinaryWriter writer, PMessage<?> message) throws IOException, PSerializeException {
        int len = 1;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                writeMessageField(writer, field, message);
            }
        } else {
            for (PField<?> field : message.descriptor().getFields()) {
                if (message.has(field.getKey())) {
                    writeMessageField(writer, field, message);
                }
            }
        }
        return len;
    }

    private int writeMessageField(BinaryWriter writer, PField field, PMessage<?> message)
            throws IOException, PSerializeException {
        int len = 0;
        if (field.getType() == PType.SET || field.getType() == PType.LIST) {
            // Encode as repeated field.
            PContainer<?,?> ct = (PContainer<?, ?>) field.getDescriptor();
            int type = getType(ct.itemDescriptor().getType());
            int tag = field.getKey() << 3 | type;
            @SuppressWarnings("unchecked")
            Collection<Object> container = (Collection<Object>) message.get(field.getKey());

            for (Object item : container) {
                writer.writeVarint(tag);
                writeFieldValue(writer,
                                type,
                                ct.itemDescriptor(),
                                item);
            }
        } else {
            int type = getType(field.getType());
            int tag = (field.getKey() << 3) | type;
            len += writer.writeVarint(tag);
            len += writeFieldValue(writer,
                                   type,
                                   field.getDescriptor(),
                                   message.get(field.getKey()));
        }
        return len;
    }

    private <T extends PMessage<T>> T readMessage(BinaryReader input, PStructDescriptor<T,?> descriptor)
            throws PSerializeException, IOException {
        PMessageBuilder<T> builder = descriptor.factory().builder();
        int tag;
        while ((tag = input.readIntVarint()) > 0) {
            int id = tag >>> 3;
            int type = tag & 0x7;
            PField<?> field = descriptor.getField(id);
            if (field != null) {
                if ((field.getType() == PType.LIST || field.getType() == PType.SET) &&
                        type != BINARY) {
                    // Non-packed repeated field.
                    PContainer<?,?> ct = (PContainer<?, ?>) field.getDescriptor();
                    Object value = readFieldValue(input, type, ct.itemDescriptor());
                    builder.addTo(field.getKey(), value);
                } else {
                    Object value = readFieldValue(input, type, field.getDescriptor());
                    builder.set(field.getKey(), value);
                }
            } else {
                if (strict) {
                    throw new PSerializeException(
                            "Unknown field " + id + " for type" + descriptor.getQualifiedName(null));
                }
                readFieldValue(input, type, null);
            }
        }
        return builder.build();
    }

    // --- READ METHODS ---

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param type       The field info about the content.
     * @param descriptor      The type to generate content for.
     * @return The field value, or null if no type.
     * @throws IOException If unable to read from stream or invalid field type.
     */
    protected <T> T readFieldValue(BinaryReader in, int type, PDescriptor<T> descriptor)
            throws IOException, PSerializeException {
        switch (type) {
            case VARINT: {
                if (descriptor != null) {
                    switch (descriptor.getType()) {
                        case BOOL:
                            return cast(in.readIntZigzag() > 0);
                        case BYTE:
                            return cast((byte) in.readIntZigzag());
                        case I16:
                            return cast((short) in.readIntZigzag());
                        case I32:
                            return cast(in.readIntZigzag());
                        case I64:
                            return cast(in.readLongZigzag());
                        case ENUM:
                            PEnumBuilder<?> builder = ((PEnumDescriptor<?>) descriptor).factory().builder();
                            builder.setByValue(in.readIntZigzag());
                            return cast(builder.build());
                        default:
                            throw new PSerializeException("invalid type for varint value: " + descriptor.getName());
                    }
                } else {
                    in.readLongVarint();
                }
                return null;
            }
            case FIXED_64: {
                if (descriptor == null) {
                    if (strict) {
                        throw new PSerializeException("");
                    }
                    in.readLong();
                } else if (descriptor.getType() == PType.DOUBLE) {
                    return cast(in.readDouble());
                } else {
                    throw new PSerializeException("invalid type for fixed_64 value " + descriptor);
                }
                return null;
            }
            case BINARY: {
                final int len = in.readIntVarint();
                final byte[] bytes = in.readBytes(len);
                if (descriptor != null) {
                    switch (descriptor.getType()) {
                        case STRING:
                            return cast(new String(bytes, StandardCharsets.UTF_8));
                        case BINARY:
                            return cast(Binary.wrap(bytes));
                        case MESSAGE: {
                            ByteArrayInputStream tmp = new ByteArrayInputStream(bytes);
                            return deserialize(tmp, descriptor);
                        }
                        case SET:
                        case LIST: {
                            ByteArrayInputStream tmp = new ByteArrayInputStream(bytes);
                            BinaryReader reader = new BinaryReader(tmp);

                            PContainer<?,?> ct = (PContainer<?, ?>) descriptor;
                            PDescriptor<?> it = ct.itemDescriptor();
                            Collection<Object> out = descriptor.getType() == PType.SET ?
                                                     new LinkedHashSet<>() :
                                                     new LinkedList<>();
                            int itag = reader.readIntVarint() & 0x7;
                            while (tmp.available() > 0) {
                                out.add(readFieldValue(reader, itag, it));
                            }
                            return cast(out);
                        }
                        case MAP: {
                            ByteArrayInputStream tmp = new ByteArrayInputStream(bytes);
                            BinaryReader reader = new BinaryReader(tmp);

                            PMap<?,?> ct = (PMap) descriptor;
                            PDescriptor<?> kt = ct.keyDescriptor();
                            PDescriptor<?> vt = ct.itemDescriptor();

                            Map<Object,Object> out = new LinkedHashMap<>();
                            int ktag;
                            while ((ktag = reader.readIntVarint()) > 0) {
                                Object key = readFieldValue(reader, ktag & 0x07, kt);
                                int vTag = reader.readIntVarint();
                                Object value = readFieldValue(reader, vTag & 0x07, vt);
                                out.put(key, value);
                            }
                            return cast(out);
                        }
                        default:
                            throw new PSerializeException("Illegal type for binary encoding: " + descriptor);
                    }
                } else if (strict) {
                    throw new PSerializeException("");
                }
                return null;
            }
            default:
                throw new PSerializeException("Unknown type: " + type);
        }
    }

    // --- WRITE METHODS ---

    /**
     * Write a field value to stream.
     *
     * @param out The stream to write to.
     * @param value The value to write.
     * @return The number of bytes written.
     * @throws IOException
     */
    protected int writeFieldValue(BinaryWriter out, int tag, PDescriptor<?> descriptor, Object value) throws IOException, PSerializeException {
        int len = 0;
        switch (tag) {
            case VARINT:
                if (value instanceof Boolean) {
                    len += out.writeVarint((Boolean) value ? 1 : 0);
                } else if (value instanceof Byte) {
                    len += out.writeZigzag((byte) value);
                } else if (value instanceof Short) {
                    len += out.writeZigzag((short) value);
                } else if (value instanceof Integer) {
                    len += out.writeZigzag((int) value);
                } else if (value instanceof Long) {
                    len += out.writeZigzag((long) value);
                } else if (value instanceof PEnumValue) {
                    len += out.writeZigzag(((PEnumValue) value).getValue());
                } else {
                    throw new PSerializeException("");
                }
                break;
            case FIXED_64:
                if (value instanceof Double) {
                    len += out.writeDouble((double) value);
                } else {
                    throw new PSerializeException("");
                }
                break;
            case BINARY:
                if (value instanceof String) {
                    byte[] data = ((String) value).getBytes(StandardCharsets.UTF_8);
                    len += out.writeVarint(data.length);
                    out.write(data);
                    len += data.length;
                } else if (value instanceof Binary) {
                    Binary binary = (Binary) value;
                    len += out.writeVarint(binary.length());
                    len += out.writeBinary(binary);
                } else if (value instanceof PMessage) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                    len += serialize(baos, (PMessage<?>) value);
                    len += out.writeVarint(baos.size());
                    out.write(baos.toByteArray(), 0, baos.size());
                } else if (value instanceof Collection) {
                    // packed repeated field.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BinaryWriter packedWriter = new BinaryWriter(baos);

                    PContainer<?,?> ct = (PContainer<?,?>) descriptor;
                    int iTag = getType(ct.itemDescriptor().getType());
                    @SuppressWarnings("unchecked")
                    Collection<Object> coll = (Collection<Object>) value;
                    packedWriter.writeVarint(iTag);
                    for (Object item : coll) {
                        writeFieldValue(packedWriter, iTag, ct.itemDescriptor(),  item);
                    }
                    packedWriter.flush();

                    len += out.writeVarint(baos.size());
                    out.write(baos.toByteArray(), 0, baos.size());
                    len += baos.size();
                } else if (value instanceof Map) {
                    // virtual message: key = 1, value = 2;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BinaryWriter mapWriter = new BinaryWriter(baos);

                    PMap<?,?> ct = (PMap<?,?>) descriptor;
                    int kTag = getType(ct.keyDescriptor().getType());
                    int vTag = getType(ct.itemDescriptor().getType());
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) value;
                    for (Map.Entry<Object, Object> entry : map.entrySet()) {
                        mapWriter.writeVarint(1 << 3 | kTag);
                        writeFieldValue(mapWriter, kTag, ct.keyDescriptor(), entry.getKey());
                        mapWriter.writeVarint(2 << 3 | vTag);
                        writeFieldValue(mapWriter, vTag, ct.itemDescriptor(), entry.getValue());
                    }
                    mapWriter.flush();

                    len += out.writeVarint(baos.size());
                    out.write(baos.toByteArray(), 0, baos.size());
                    len += baos.size();
                } else {
                    throw new PSerializeException("");
                }
                break;
            default:
                throw new PSerializeException("");
        }
        return len;
    }

    private static int getType(PType type) throws PSerializeException {
        switch (type) {
            case BOOL:
            case BYTE:
            case I16:
            case I32:
            case I64:
            case ENUM:
                return VARINT;
            case DOUBLE:
                return FIXED_64;
            case STRING:
            case BINARY:
            case MESSAGE:
            case MAP:
            case SET:
            case LIST:
                return BINARY;
            default:
                throw new PSerializeException("");
        }
    }

    private static final int VARINT      = 0x00;  // -> bool, byte, i16, i32, i64, enum
    private static final int FIXED_64    = 0x01;  // -> double
    private static final int BINARY      = 0x02;  // -> varint + n bytes.
    //                       START_GROUP = 0x03;
    //                       END_GROUP   = 0x04;
    //                       FIXED_32    = 0x05;
}
