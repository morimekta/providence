package org.apache.thrift.j2.util.json;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonToken {
    public enum Type {
        SYMBOL,   // on of []{},:
        NUMBER,   // numerical
        LITERAL,  // quoted literal
        TOKEN,    // static token.
    }

    public static final String kNull = "null";
    public static final String kTrue = "true";
    public static final String kFalse = "false";
    public static final String kListStart = "[";
    public static final String kListEnd = "]";
    public static final String kListSep = ",";
    public static final String kMapStart = "{";
    public static final String kMapEnd = "}";
    public static final String kKeyValSep = ":";
    public static final String kQuote = "\"";

    public static final char kListStartChar = '[';
    public static final char kListEndChar = ']';
    public static final char kListSepChar = ',';
    public static final char kMapStartChar = '{';
    public static final char kMapEndChar = '}';
    public static final char kKeyValSepChar = ':';
    public static final char kQuoteChar = '\"';

    private static final Pattern RE_LITERAL = Pattern.compile("\".*\"");
    private static final Pattern RE_BOOLEAN = Pattern.compile("(true|false)");
    private static final Pattern RE_INTEGER = Pattern.compile(
            "-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");
    private static final Pattern RE_DOUBLE = Pattern.compile(
            "-?([0-9]+[.]?([eE][+-]?[0-9]+)?|-?([0-9]+)?[.][0-9]+([eE][+-]?[0-9]+)?)");

    public final Type   type;
    public final int    line;
    public final int    pos;
    public final int    len;

    public final String value;

    public static boolean mustUnicodeEscape(int b) {
        return b < 32 || (127 <= b && b < 160) || (8192 <= b && b < 8448);
    }

    public JsonToken(Type type, int line, int pos, int len, String value) {
        this.type = type;
        this.line = line;
        this.pos = pos;
        this.len = len;

        this.value = value;
    }

    public boolean isNull() {
        return value.equals(kNull);
    }

    public boolean isSymbol() {
        return value.length() == 1 && "{}[],:".indexOf(value.charAt(0)) >= 0;
    }

    public boolean isLiteral() {
        return type == Type.LITERAL;
    }

    public boolean isBoolean() {
        return type == Type.TOKEN && RE_BOOLEAN.matcher(value).matches();
    }

    public boolean isInteger() {
        return type == Type.NUMBER && RE_INTEGER.matcher(value).matches();
    }

    public boolean isReal() {
        return type == Type.NUMBER && RE_DOUBLE.matcher(value).matches();
    }

    public boolean booleanValue() {
        return value.equals(kTrue);
    }

    public byte byteValue() {
        if (value.startsWith("0x")) {
            return Byte.parseByte(value.substring(2), 16);
        } else if (value.startsWith("0") && value.length() > 1) {
            return Byte.parseByte(value.substring(1), 8);
        }
        return Byte.parseByte(value);
    }

    public short shortValue() {
        if (value.startsWith("0x")) {
            return Short.parseShort(value.substring(2), 16);
        } else if (value.startsWith("0") && value.length() > 1) {
            return Short.parseShort(value.substring(1), 8);
        }
        return Short.parseShort(value);
    }

    public int intValue() {
        if (value.startsWith("0x")) {
            return Integer.parseInt(value.substring(2), 16);
        } else if (value.startsWith("0") && value.length() > 1) {
            return Integer.parseInt(value.substring(1), 8);
        }
        return Integer.parseInt(value);
    }

    public long longValue() {
        if (value.startsWith("0x")) {
            return Long.parseLong(value.substring(2), 16);
        } else if (value.startsWith("0") && value.length() > 1) {
            return Long.parseLong(value.substring(1), 8);
        }
        return Long.parseLong(value);
    }

    public float floatValue() {
        return Float.parseFloat(value);
    }

    public double doubleValue() {
        return Double.parseDouble(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(JsonToken.class, type, line, pos, len, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof JsonToken)) return false;
        JsonToken other = (JsonToken) o;

        return Objects.equals(value, other.value) &&
               Objects.equals(line, other.line) &&
               Objects.equals(pos, other.pos) &&
               Objects.equals(len, other.len);
    }

    @Override
    public String toString() {
        return String.format("%s('%s',%d:%d-%d)", type.toString(), value, line, pos, pos + len);
    }
}
