package net.morimekta.providence.serializer.binary;

import net.morimekta.providence.PType;

import javax.annotation.Nonnull;

/**
 * Helper class for having binary type ID constants and getting
 * the right binary type ID for a value type.
 */
public class BinaryType {
    public static final byte STOP   = 0;
    public static final byte VOID   = 1;
    public static final byte BOOL   = 2;
    public static final byte BYTE   = 3;
    public static final byte DOUBLE = 4;
    // unused = 5;
    public static final byte I16    = 6;
    // unused = 7;
    public static final byte I32    = 8;  // i32 and enum
    // unused = 9;
    public static final byte I64    = 10;
    public static final byte STRING = 11;  // string & binary
    public static final byte STRUCT = 12;
    public static final byte MAP    = 13;
    public static final byte SET    = 14;
    public static final byte LIST   = 15;

    /**
     * Get the binary type for the given value type.
     *
     * @param type The type to check.
     * @return The binary type ID.
     */
    public static byte forType(@Nonnull PType type) {
        switch (type) {
            case VOID: return VOID;
            case BOOL: return BOOL;
            case BYTE: return BYTE;
            case I16: return I16;
            case I32: return I32;
            case I64: return I64;
            case DOUBLE: return DOUBLE;
            case STRING: return STRING;
            case BINARY: return STRING;
            case ENUM: return I32;
            case MAP: return MAP;
            case SET: return SET;
            case LIST: return LIST;
            case MESSAGE: return STRUCT;
            default: throw new IllegalArgumentException("Unknown binary type for " + type.toString());
        }
    }

    /**
     * Readable string value for a type ID.
     *
     * @param id The type ID.
     * @return The type string.
     */
    public static String asString(byte id) {
        switch (id) {
            case STOP:
                return "stop(0)";
            case VOID:
                return "void(1)";
            case BOOL:
                return "bool(2)";
            case BYTE:
                return "byte(3)";
            case DOUBLE:
                return "double(4)";
            // case 5:
            case I16:
                return "i16(6)";
            // case 7:
            case I32:
                // ENUM is same as I32.
                return "i32(8)";
            // case 9:
            case I64:
                return "i64(10)";
            case STRING:
                // BINARY is same as STRING.
                return "string(11)";
            case STRUCT:
                return "struct(12)";
            case MAP:
                return "map(13)";
            case SET:
                return "set(14)";
            case LIST:
                return "list(15)";
            default:
                return "unknown(" + id + ")";
        }
    }

    // Defeat instantiation
    private BinaryType() {}
}
