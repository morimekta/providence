package net.morimekta.providence.jdbi.v2;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableMap;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Map a result set to a message based on meta information and the message
 * descriptor.
 *
 * @param <M> The message type.
 * @param <F> The message field type.
 */
public class MessageRowMapper<M extends PMessage<M,F>, F extends PField> implements ResultSetMapper<M> {
    public static final String ALL_FIELDS = "*";

    /**
     * Create a message row mapper.
     *
     * @param descriptor Message descriptor.
     */
    public MessageRowMapper(@Nonnull PMessageDescriptor<M,F> descriptor) {
        this(descriptor, ImmutableMap.of());
    }

    /**
     * Create a message row mapper.
     *
     * @param tableName The name of the table to filter fields for this mapper.
     * @param descriptor Message descriptor.
     */
    public MessageRowMapper(@Nonnull String tableName, @Nonnull PMessageDescriptor<M,F> descriptor) {
        this(tableName, descriptor, ImmutableMap.of());
    }

    /**
     * Create a message row mapper.
     *
     * @param descriptor Message descriptor.
     * @param fieldMapping The field mapping. If empty will map all fields with default names.
     */
    public MessageRowMapper(@Nonnull PMessageDescriptor<M,F> descriptor,
                            @Nonnull Map<String,F> fieldMapping) {
        this("", descriptor, fieldMapping);
    }

    /**
     * Create a message row mapper.
     *
     * @param tableName The name of the table to filter fields for this mapper.
     * @param descriptor Message descriptor.
     * @param fieldMapping The field mapping. If empty will map all fields with default names.
     */
    public MessageRowMapper(@Nonnull String tableName,
                            @Nonnull PMessageDescriptor<M,F> descriptor,
                            @Nonnull Map<String,F> fieldMapping) {
        Map<String, F> mappingBuilder = new HashMap<>();
        if (fieldMapping.isEmpty()) {
            for (F field : descriptor.getFields()) {
                mappingBuilder.put(field.getName().toUpperCase(), field);
            }
        } else {
            fieldMapping.forEach((name, addField) -> {
                if (ALL_FIELDS.equals(name)) {
                    for (F field : descriptor.getFields()) {
                        String fieldName = field.getName().toUpperCase();
                        // To avoid overwriting already specified fields.
                        if (!mappingBuilder.containsKey(fieldName)) {
                            mappingBuilder.put(fieldName, field);
                        }
                    }
                } else {
                    mappingBuilder.put(name.toUpperCase(), addField);
                }
            });
        }

        this.tableName = tableName;
        this.descriptor = descriptor;
        this.fieldNameMapping = ImmutableMap.copyOf(mappingBuilder);
    }

    @Override
    public M map(int idx, ResultSet rs, StatementContext ctx) throws SQLException {
        PMessageBuilder<M,F> builder = descriptor.builder();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
            if (!tableName.isEmpty() &&
                !tableName.equalsIgnoreCase(rs.getMetaData().getTableName(i))) {
                continue;
            }

            String name = rs.getMetaData().getColumnLabel(i).toUpperCase();
            F field = fieldNameMapping.get(name);
            if (field != null) {
                int columnType = rs.getMetaData().getColumnType(i);
                switch (field.getType()) {
                    case BOOL: {
                        if (columnType == Types.BOOLEAN || columnType == Types.BIT) {
                            boolean b = rs.getBoolean(i);
                            if (!rs.wasNull()) {
                                builder.set(field, b);
                            }
                        } else {
                            int b = rs.getInt(i);
                            if (!rs.wasNull()) {
                                builder.set(field, b != 0);
                            }
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
                        if (columnType == Types.TIMESTAMP) {
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
                        if (columnType == Types.TIMESTAMP) {
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
                        switch (columnType) {
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
                                throw new SQLDataException("Unknown column type " + rs.getMetaData().getColumnTypeName(i) +
                                                           " for " + descriptor.getType().toString() +
                                                           " field " + name + " in " +
                                                           descriptor.getQualifiedName());
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
                            switch (columnType) {
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
                                        builder.set(field, BINARY.deserialize(blob.getBinaryStream(), md));
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
                                        builder.set(field, JSON.deserialize(clob.getCharacterStream(), md));
                                    }
                                    break;
                                }
                                case Types.NULL:
                                    break;
                                default:
                                    throw new SQLDataException("Unknown column type " + rs.getMetaData().getColumnTypeName(i) +
                                                               " for " + descriptor.getType().toString() +
                                                               " field " + name + " in " +
                                                               descriptor.getQualifiedName());
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
                        throw new SQLDataException("Unhandled column of type " + rs.getMetaData().getColumnTypeName(i) +
                                                   " for " + descriptor.getType().toString() +
                                                   " field " + name + " in " +
                                                   descriptor.getQualifiedName());
                    }
                }
            }
        }
        return builder.build();
    }

    private static final BinarySerializer BINARY = new BinarySerializer();
    private static final JsonSerializer   JSON   = new JsonSerializer();

    private final PMessageDescriptor<M, F> descriptor;
    private final Map<String,F>            fieldNameMapping;
    private final String                   tableName;
}
