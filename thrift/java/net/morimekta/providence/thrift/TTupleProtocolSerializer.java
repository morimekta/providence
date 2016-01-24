package net.morimekta.providence.thrift;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.util.Binary;
import net.morimekta.util.io.CountingOutputStream;

import org.apache.thrift.TException;
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
        extends PSerializer {
    private final boolean          readStrict;
    private final TProtocolFactory protocolFactory;

    public TTupleProtocolSerializer() {
        this(true);
    }

    public TTupleProtocolSerializer(boolean readStrict) {
        this.readStrict = readStrict;
        this.protocolFactory = new TTupleProtocol.Factory();
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message)
            throws IOException, PSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);
            writeMessage(message, protocol);
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new PSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
            throws IOException, PSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);

        try {
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);
            switch (descriptor.getType()) {
                case MESSAGE:
                    writeMessage((PMessage<?>) value, protocol);
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
            throw new PSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> definition)
            throws IOException, PSerializeException {
        T ret;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);

            ret = read(protocol, definition);
        } catch (TTransportException e) {
            throw new PSerializeException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new PSerializeException(e, "Transport exception in protocol");
        }

        return ret;
    }

    protected <T> T read(TTupleProtocol protocol, PDescriptor<T> descriptor)
            throws TException, PSerializeException {
        if (PType.MESSAGE == descriptor.getType()) {
            T ret = cast((Object) readMessage(protocol, (PStructDescriptor<?,?>) descriptor));
            return ret;
        } else {
            protocol.readBitSet(1);  // ignored.
            T ret = readTypedValue(descriptor.getType().id, descriptor, protocol);
            return ret;
        }
    }

    protected void writeMessage(PMessage<?> message, TTupleProtocol protocol) throws TException, PSerializeException {
        TTupleProtocol oprot = protocol;
        PStructDescriptor<?,?> descriptor = message.descriptor();
        BitSet optionals = new BitSet();
        PField<?>[] fields = descriptor.getFields();
        for (int i = 0; i < fields.length; ++i) {
            PField<?> fld = fields[i];
            if (message.has(fld.getKey())) {
                optionals.set(i);
            }
        }
        oprot.writeBitSet(optionals, fields.length);

        for (int i = 0; i < fields.length; ++i) {
            PField<?> fld = fields[i];
            if (optionals.get(i)) {
                writeTypedValue(message.get(fld.getKey()), fld.getDescriptor(), protocol);
            }
        }
    }

    protected <T extends PMessage<T>> T readMessage(TTupleProtocol protocol, PStructDescriptor<T, ?> descriptor)
            throws PSerializeException, TException {
        TTupleProtocol iprot = protocol;
        PField<?>[] fields = descriptor.getFields();
        BitSet optionals = iprot.readBitSet(fields.length);

        PMessageBuilder<T> builder = descriptor.factory().builder();

        for (int i = 0; i < fields.length; ++i) {
            if (optionals.get(i)) {
                PField<?> fld = fields[i];
                builder.set(fld.getKey(), readTypedValue(fld.getType().id, fld.getDescriptor(), protocol));
            }
        }

        if (readStrict && !builder.isValid()) {
            throw new PSerializeException("");
        }

        return builder.build();
    }

    protected <T> T readTypedValue(byte tType, PDescriptor<T> type, TTupleProtocol protocol) throws TException, PSerializeException {
        switch (PType.findById(tType)) {
            case BOOL:
                return cast(protocol.readBool());
            case BYTE:
                return cast(protocol.readByte());
            case I16:
                return cast(protocol.readI16());
            case I32:
                if (PType.ENUM == type.getType()) {
                    PEnumDescriptor<?> et = (PEnumDescriptor<?>) type;
                    PEnumBuilder<?> eb = et.factory().builder();
                    final int value = protocol.readI32();
                    eb.setByValue(value);
                    if (readStrict && !eb.isValid()) {
                        throw new PSerializeException("Invalid enum value " + value + " for " +
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
                if (PPrimitive.BINARY == type) {
                    ByteBuffer buffer = protocol.readBinary();
                    return cast(Binary.wrap(buffer.array()));
                }
                return cast(protocol.readString());
            case MESSAGE:
                return cast((Object) readMessage(protocol, (PStructDescriptor<?,?>) type));
            case LIST:
                int lSize = protocol.readI32();
                PList<?> lDesc = (PList<?>) type;
                PDescriptor liDesc = lDesc.itemDescriptor();

                List<Object> list = new LinkedList<>();
                for (int i = 0; i < lSize; ++i) {
                    list.add(readTypedValue(liDesc.getType().id, liDesc, protocol));
                }

                return cast(list);
            case SET:
                int sSize = protocol.readI32();
                PSet<?> sDesc = (PSet<?>) type;
                PDescriptor siDesc = sDesc.itemDescriptor();

                Set<Object> set = new LinkedHashSet<>();
                for (int i = 0; i < sSize; ++i) {
                    set.add(readTypedValue(siDesc.getType().id, siDesc, protocol));
                }

                return cast(set);
            case MAP:
                int mSize = protocol.readI32();
                PMap<?,?> mDesc = (PMap<?,?>) type;
                PDescriptor mkDesc = mDesc.keyDescriptor();
                PDescriptor miDesc = mDesc.itemDescriptor();

                Map<Object,Object> map = new LinkedHashMap<>();
                for (int i = 0; i < mSize; ++i) {
                    Object key = readTypedValue(mkDesc.getType().id, mkDesc, protocol);
                    Object val = readTypedValue(miDesc.getType().id, miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return cast(map);
            default:
                throw new PSerializeException("Unsupported protocol field type: " + tType);
        }
    }

    protected void writeTypedValue(Object item, PDescriptor type, TTupleProtocol protocol)
            throws TException, PSerializeException {
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
                protocol.writeBinary(((Binary) item).getByteBuffer());
                break;
            case ENUM:
                PEnumValue<?> value = (PEnumValue<?>) item;
                protocol.writeI32(value.getValue());
                break;
            case MESSAGE:
                writeMessage((PMessage<?>) item, protocol);
                break;
            case LIST:
                PList<?> lType = (PList<?>) type;
                List<?> list = (List<?>) item;
                protocol.writeI32(list.size());
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                break;
            case SET:
                PSet<?> sType = (PSet<?>) type;
                Set<?> set = (Set<?>) item;
                protocol.writeI32(set.size());
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
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
