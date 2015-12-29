package org.apache.thrift.j2.protocol;

import org.apache.thrift.TException;
import org.apache.thrift.j2.TBinary;
import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TProtocol serializer specialized for Tuple protocol, just because thrift
 * decided that this protocol should be written in a different way than other
 * protocols.
 */
public class TTupleProtocolSerializer
        extends TSerializer {
    private final TProtocolFactory mProtocolFactory;

    public TTupleProtocolSerializer() {
        mProtocolFactory = new TTupleProtocol.Factory();
    }

    @Override
    public int serialize(OutputStream output, TMessage<?> message)
            throws IOException, TSerializeException {
        if (!message.isValid()) {
            throw new TSerializeException("Message is not valid for serialization.");
        }

        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TTupleProtocol protocol = (TTupleProtocol) mProtocolFactory.getProtocol(transport);
            writeMessage(message, protocol);
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new TSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);

        try {
            TTupleProtocol protocol = (TTupleProtocol) mProtocolFactory.getProtocol(transport);
            switch (descriptor.getType()) {
                case MESSAGE:
                    writeMessage((TMessage<?>) value, protocol);
                    break;
                default:
                    BitSet optionals = new BitSet();
                    optionals.set(0);
                    protocol.writeBitSet(optionals, 1);
                    writeTypedValue(value, descriptor, protocol);
                    break;
            }
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new TSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(InputStream input, TDescriptor<T> definition)
            throws IOException, TSerializeException {
        T ret;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TTupleProtocol protocol = (TTupleProtocol) mProtocolFactory.getProtocol(transport);

            ret = read(protocol, definition);
        } catch (TTransportException e) {
            throw new TSerializeException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new TSerializeException(e, "Transport exception in protocol");
        }

        return ret;
    }

    protected <T> T read(TTupleProtocol protocol, TDescriptor<T> descriptor)
            throws TException, TSerializeException {
        if (TType.MESSAGE == descriptor.getType()) {
            T ret = cast((Object) readMessage(protocol, (TStructDescriptor<?,?>) descriptor));
            return ret;
        } else {
            protocol.readBitSet(1);  // ignored.
            T ret = readTypedValue(descriptor.getType().id, descriptor, protocol);
            return ret;
        }
    }

    protected void writeMessage(TMessage<?> message, TTupleProtocol protocol) throws TException, TSerializeException {
        TTupleProtocol oprot = protocol;
        TStructDescriptor<?,?> descriptor = message.descriptor();
        BitSet optionals = new BitSet();
        TField<?>[] fields = descriptor.getFields();
        for (int i = 0; i < fields.length; ++i) {
            TField<?> fld = fields[i];
            if (message.has(fld.getKey())) {
                optionals.set(i);
            }
        }
        oprot.writeBitSet(optionals, fields.length);

        for (int i = 0; i < fields.length; ++i) {
            TField<?> fld = fields[i];
            if (message.has(fld.getKey())) {
                writeTypedValue(message.get(fld.getKey()), fld.getDescriptor(), protocol);
            }
        }
    }

    protected <T extends TMessage<T>> T readMessage(TTupleProtocol protocol, TStructDescriptor<T, ?> descriptor)
            throws TSerializeException, TException {
        TTupleProtocol iprot = protocol;
        TField<?>[] fields = descriptor.getFields();
        BitSet optionals = iprot.readBitSet(fields.length);

        TMessageBuilder<T> builder = descriptor.factory().builder();

        for (int i = 0; i < fields.length; ++i) {
            if (optionals.get(i)) {
                TField<?> fld = fields[i];
                builder.set(fld.getKey(), readTypedValue(fld.getType().id, fld.getDescriptor(), protocol));
            }
        }

        return builder.build();
    }

    protected <T> T readTypedValue(byte tType, TDescriptor<T> type, TTupleProtocol protocol) throws TException, TSerializeException {
        switch (TType.findById(tType)) {
            case BOOL:
                return cast(protocol.readBool());
            case BYTE:
                return cast(protocol.readByte());
            case I16:
                return cast(protocol.readI16());
            case I32:
                if (TType.ENUM == type.getType()) {
                    TEnumDescriptor<?> et = (TEnumDescriptor<?>) type;
                    TEnumBuilder<?> eb = et.factory().builder();
                    int value = protocol.readI32();
                    eb.setByValue(value);
                    if (!eb.isValid()) {
                        throw new TSerializeException("Invalid enum value " + value + " for " +
                                                      et.getQualifiedName(null));
                    }
                    return cast(eb.build());
                } else {
                    return cast(protocol.readI32());
                }
            case I64:
                return cast(protocol.readI64());
            case DOUBLE:
                return cast(protocol.readDouble());
            case STRING:
                if (TPrimitive.BINARY == type) {
                    ByteBuffer buffer = protocol.readBinary();
                    return cast(TBinary.wrap(buffer.array()));
                }
                return cast(protocol.readString());
            case MESSAGE:
                return cast((Object) readMessage(protocol, (TStructDescriptor<?,?>) type));
            case LIST:
                int lSize = protocol.readI32();
                TList<?> lDesc = (TList<?>) type;
                TDescriptor liDesc = lDesc.itemDescriptor();

                List<Object> list = new LinkedList<>();
                for (int i = 0; i < lSize; ++i) {
                    list.add(readTypedValue(liDesc.getType().id, liDesc, protocol));
                }

                return cast(list);
            case SET:
                int sSize = protocol.readI32();
                TSet<?> sDesc = (TSet<?>) type;
                TDescriptor siDesc = sDesc.itemDescriptor();

                Set<Object> set = new LinkedHashSet<>();
                for (int i = 0; i < sSize; ++i) {
                    set.add(readTypedValue(siDesc.getType().id, siDesc, protocol));
                }

                return cast(set);
            case MAP:
                int mSize = protocol.readI32();
                TMap<?,?> mDesc = (TMap<?,?>) type;
                TDescriptor mkDesc = mDesc.keyDescriptor();
                TDescriptor miDesc = mDesc.itemDescriptor();

                Map<Object,Object> map = new LinkedHashMap<>();
                for (int i = 0; i < mSize; ++i) {
                    Object key = readTypedValue(mkDesc.getType().id, mkDesc, protocol);
                    Object val = readTypedValue(miDesc.getType().id, miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return cast(map);
            default:
                throw new TSerializeException("Unsupported protocol field type: " + tType);
        }
    }

    protected void writeTypedValue(Object item, TDescriptor type, TTupleProtocol protocol)
            throws TException, TSerializeException {
        switch (type.getType()) {
            case BOOL:
                protocol.writeBool((Boolean) item);
                break;
            case BYTE:
                protocol.writeByte((Byte) item);
                break;
            case I16:
                protocol.writeI16((Short) item);
                break;
            case I32:
                protocol.writeI32((Integer) item);
                break;
            case I64:
                protocol.writeI64((Long) item);
                break;
            case DOUBLE:
                protocol.writeDouble((Double) item);
                break;
            case STRING:
                protocol.writeString((String) item);
                break;
            case BINARY:
                protocol.writeBinary(((TBinary) item).getByteBuffer());
                break;
            case ENUM:
                TEnumValue<?> value = (TEnumValue<?>) item;
                protocol.writeI32(value.getValue());
                break;
            case MESSAGE:
                writeMessage((TMessage<?>) item, protocol);
                break;
            case LIST:
                TList<?> lType = (TList<?>) type;
                List<?> list = (List<?>) item;
                protocol.writeI32(list.size());
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                break;
            case SET:
                TSet<?> sType = (TSet<?>) type;
                Set<?> set = (Set<?>) item;
                protocol.writeI32(set.size());
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                break;
            case MAP:
                TMap<?, ?> mType = (TMap<?, ?>) type;
                Map<?, ?> map = (Map<?, ?>) item;
                protocol.writeI32(map.size());

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    writeTypedValue(entry.getKey(), mType.keyDescriptor(), protocol);
                    writeTypedValue(entry.getValue(), mType.itemDescriptor(), protocol);
                }

                protocol.writeMapEnd();
                break;
        }
    }
}
