package net.morimekta.providence.storage.jdbi;

import com.google.common.collect.ImmutableMap;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.util.Binary;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.result.ResultSetException;
import org.jdbi.v3.core.statement.StatementContext;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class MessageRowMapper<M extends PMessage<M,F>, F extends PField> implements RowMapper<M> {
    private static final BinarySerializer BINARY = new BinarySerializer();
    private static final JsonSerializer   JSON   = new JsonSerializer();

    private final PMessageDescriptor<M, F> descriptor;
    private final Map<String,F>            fieldNameMapping;

    public MessageRowMapper(@Nonnull PMessageDescriptor<M,F> descriptor,
                            @Nonnull Map<String,F> fieldMapping) {
        Map<String, F> mappingBuilder = new HashMap<>();
        for (F field : descriptor.getFields()) {
            mappingBuilder.put(field.getName().toUpperCase(), field);
        }
        fieldMapping.forEach((name, field) -> mappingBuilder.put(name.toUpperCase(), field));

        this.descriptor = descriptor;
        this.fieldNameMapping = ImmutableMap.copyOf(mappingBuilder);
    }

    @Override
    public M map(ResultSet rs, StatementContext ctx) throws SQLException {
        PMessageBuilder<M,F> builder = descriptor.builder();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
            String name = rs.getMetaData().getColumnLabel(i).toUpperCase();
            F field = fieldNameMapping.get(name);
            if (field != null) {
                switch (field.getType()) {
                    case BOOL: {
                        boolean b = rs.getBoolean(i);
                        if (!rs.wasNull()) {
                            builder.set(field, b);
                        }
                        break;
                    }
                    case BYTE: {
                        byte b = rs.getByte(i);
                        if (!rs.wasNull()) {
                            builder.set(field, b);
                        }
                        break;
                    }
                    case I16: {
                        short b = rs.getShort(i);
                        if (!rs.wasNull()) {
                            builder.set(field, b);
                        }
                        break;
                    }
                    case I32: {
                        if (rs.getMetaData().getColumnType(i) == Types.TIMESTAMP) {
                            Timestamp ts = rs.getTimestamp(i);
                            if (ts != null) {
                                builder.set(field, (int) (ts.getTime() / 1000L));
                            }
                        } else {
                            int b = rs.getInt(i);
                            if (!rs.wasNull()) {
                                builder.set(field, b);
                            }
                        }
                        break;
                    }
                    case I64: {
                        if (rs.getMetaData().getColumnType(i) == Types.TIMESTAMP) {
                            Timestamp ts = rs.getTimestamp(i);
                            if (ts != null) {
                                builder.set(field, ts.getTime());
                            }
                        } else {
                            long b = rs.getLong(i);
                            if (!rs.wasNull()) {
                                builder.set(field, b);
                            }
                        }
                        break;
                    }
                    case DOUBLE: {
                        double b = rs.getDouble(i);
                        if (!rs.wasNull()) {
                            builder.set(field, b);
                        }
                        break;
                    }
                    case STRING: {
                        builder.set(field, rs.getString(i));
                        break;
                    }
                    case BINARY: {
                        switch (rs.getMetaData().getColumnType(i)) {
                            case Types.BINARY:
                            case Types.VARBINARY:
                                byte[] ts = rs.getBytes(i);
                                if (ts != null) {
                                    builder.set(field, Binary.copy(ts));
                                }
                                break;
                            case Types.BLOB:
                                Blob blob = rs.getBlob(i);
                                if (blob != null) {
                                    try {
                                        builder.set(field, Binary.read(blob.getBinaryStream(), (int) blob.length()));
                                    } catch (IOException e) {
                                        throw new UncheckedIOException(e.getMessage(), e);
                                    }
                                }
                                break;
                            case Types.CHAR:
                            case Types.VARCHAR:
                            case Types.NCHAR:
                            case Types.NVARCHAR: {
                                String tmp = rs.getString(i);
                                if (tmp != null) {
                                    builder.set(field, Binary.fromBase64(tmp));
                                }
                                break;
                            }
                            case Types.NULL:
                                break;
                            default:
                                throw new ResultSetException("Unknown column type " + rs.getMetaData().getColumnTypeName(i) +
                                                             " for " + descriptor.getType().toString() +
                                                             " field " + name + " in " +
                                                             descriptor.getQualifiedName(),
                                                             null, ctx);
                        }
                        break;
                    }
                    case ENUM: {
                        int val = rs.getInt(i);
                        if (!rs.wasNull()) {
                            PEnumDescriptor ed = (PEnumDescriptor) field.getDescriptor();
                            builder.set(field, ed.findById(val));
                        }
                        break;
                    }
                    case MESSAGE: {
                        try {
                            PMessageDescriptor<?,?> md = (PMessageDescriptor) field.getDescriptor();
                            switch (rs.getMetaData().getColumnType(i)) {
                                case Types.BINARY:
                                case Types.VARBINARY:
                                    byte[] data = rs.getBytes(i);
                                    if (data != null) {
                                        ByteArrayInputStream in = new ByteArrayInputStream(data);
                                        builder.set(field, BINARY.deserialize(in, md));
                                    }
                                    break;
                                case Types.BLOB: {
                                    Blob blob = rs.getBlob(i);
                                    if (blob != null) {
                                        try {
                                            builder.set(field, BINARY.deserialize(blob.getBinaryStream(), md));
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e.getMessage(), e);
                                        }
                                    }
                                    break;
                                }
                                case Types.CHAR:
                                case Types.VARCHAR:
                                case Types.NCHAR:
                                case Types.NVARCHAR: {
                                    String tmp = rs.getString(i);
                                    if (tmp != null) {
                                        StringReader reader = new StringReader(tmp);
                                        builder.set(field, JSON.deserialize(reader, md));
                                    }
                                    break;
                                }
                                case Types.CLOB: {
                                    Clob clob = rs.getClob(i);
                                    if (clob != null) {
                                        try {
                                            builder.set(field, JSON.deserialize(clob.getCharacterStream(), md));
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e.getMessage(), e);
                                        }
                                    }
                                    break;
                                }
                                case Types.NULL:
                                    break;
                                default:
                                    throw new ResultSetException("Unknown column type " + rs.getMetaData().getColumnTypeName(i) +
                                                                 " for " + descriptor.getType().toString() +
                                                                 " field " + name + " in " +
                                                                 descriptor.getQualifiedName(),
                                                                 null, ctx);
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e.getMessage(), e);
                        }
                        break;
                    }
                    case LIST:
                    case SET:
                    case MAP: {
                        // ... woot?
                    }
                    case VOID:
                    default: {
                        throw new ResultSetException("Unhandled column of type " + rs.getMetaData().getColumnTypeName(i) +
                                                     " for " + descriptor.getType().toString() +
                                                     " field " + name + " in " +
                                                     descriptor.getQualifiedName(),
                                                     null, ctx);
                    }
                }
            }
        }
        return builder.build();
    }
}
