package net.morimekta.providence.jdbi.v3;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.util.Binary;

import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.result.ResultSetException;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Smart mapping of message fields to SQL bound argument. It will
 * map the type to whichever type is default or selected (if supported)
 * for most field types.
 *
 * @param <M> The message type.
 * @param <F> The field type.
 */
public class MessageFieldArgument<M extends PMessage<M,F>, F extends PField> implements Argument {
    private static final BinarySerializer BINARY = new BinarySerializer();
    private static final JsonSerializer   JSON   = new JsonSerializer().named();

    private final M   message;
    private final F   field;
    private final int type;

    /**
     * Create a message field argument.
     *
     * @param message The message to get the field from.
     * @param field The field to select.
     */
    public MessageFieldArgument(M message, F field) {
        this(message, field, getDefaultColumnType(field));
    }

    /**
     * Create a message field argument.
     *
     * @param message The message to get the field from.
     * @param field The field to select.
     * @param type The SQL type. See {@link Types}.
     */
    public MessageFieldArgument(M message, F field, int type) {
        this.message = message;
        this.field = field;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
        if (message.has(field)) {
            switch (field.getType()) {
                case BOOL: {
                    boolean value = message.get(field);
                    if (type == Types.BOOLEAN || type == Types.BIT) {
                        statement.setBoolean(position, value);
                    } else {
                        statement.setInt(position, value ? 1 : 0);
                    }
                    break;
                }
                case BYTE: {
                    statement.setByte(position, message.get(field));
                    break;
                }
                case I16: {
                    statement.setShort(position, message.get(field));
                    break;
                }
                case I32: {
                    if (type == Types.TIMESTAMP) {
                        Timestamp timestamp = new Timestamp(1000L * (int) message.get(field));
                        statement.setTimestamp(position, timestamp);
                    } else {
                        statement.setInt(position, message.get(field));
                    }
                    break;
                }
                case I64: {
                    if (type == Types.TIMESTAMP) {
                        Timestamp timestamp = new Timestamp(message.get(field));
                        statement.setTimestamp(position, timestamp);
                    } else {
                        statement.setLong(position, message.get(field));
                    }
                    break;
                }
                case DOUBLE: {
                    statement.setDouble(position, message.get(field));
                    break;
                }
                case STRING: {
                    statement.setString(position, message.get(field));
                    break;
                }
                case BINARY: {
                    Binary binary = message.get(field);
                    switch (type) {
                        case Types.BINARY:
                        case Types.VARBINARY: {
                            statement.setBytes(position, binary.get());
                            break;
                        }
                        case Types.BLOB: {
                            statement.setBlob(position, binary.getInputStream());
                            break;
                        }
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR: {
                            statement.setString(position, binary.toBase64());
                            break;
                        }
                        default:
                            throw new ResultSetException("Unknown binary field type: " + type + " for " + field, null, ctx);
                    }
                    break;
                }
                case ENUM: {
                    PEnumValue value = message.get(field);
                    statement.setInt(position, value.asInteger());
                    break;
                }
                case MESSAGE: {
                    PMessage value = message.get(field);
                    switch (type) {
                        case Types.BINARY:
                        case Types.VARBINARY: {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            try {
                                BINARY.serialize(out, value);
                                statement.setBytes(position, out.toByteArray());
                            } catch (IOException e) {
                                throw new ResultSetException(e.getMessage(), e, ctx);
                            }
                            break;
                        }
                        case Types.BLOB: {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            try {
                                BINARY.serialize(out, value);
                                statement.setBlob(position, new ByteArrayInputStream(out.toByteArray()));
                            } catch (IOException e) {
                                throw new ResultSetException(e.getMessage(), e, ctx);
                            }
                            break;
                        }
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR: {
                            StringWriter writer = new StringWriter();
                            try {
                                JSON.serialize(new PrintWriter(writer), value);
                                statement.setString(position, writer.getBuffer().toString());
                            } catch (IOException e) {
                                throw new ResultSetException(e.getMessage(), e, ctx);
                            }
                            break;
                        }
                        case Types.CLOB: {
                            StringWriter writer = new StringWriter();
                            try {
                                JSON.serialize(new PrintWriter(writer), value);
                                statement.setClob(position, new StringReader(writer.getBuffer().toString()));
                            } catch (IOException e) {
                                throw new ResultSetException(e.getMessage(), e, ctx);
                            }
                            break;
                        }
                        default:
                            throw new ResultSetException("Unknown message field type: " + type + " for " + field, null, ctx);
                    }
                    break;
                }
                default:
                    throw new ResultSetException("Unhandled field type in SQL: " + field, null, ctx);
            }
        } else {
            statement.setNull(position, type);
        }
    }

    static int getDefaultColumnType(PField field) {
        switch (field.getType()) {
            case BOOL: return Types.BIT;
            case BYTE: return Types.TINYINT;
            case I16: return Types.SMALLINT;
            case I32: return Types.INTEGER;
            case I64: return Types.BIGINT;
            case DOUBLE: return Types.DOUBLE;
            case STRING: return Types.VARCHAR;
            case BINARY: return Types.VARBINARY;
            case ENUM: return Types.INTEGER;
            case MESSAGE: return Types.VARCHAR;  // JSON string.
            default: {
                throw new IllegalArgumentException("No default column type for " + field.toString());
            }
        }
    }
}
