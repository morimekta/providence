package net.morimekta.providence.util.json;

import net.morimekta.providence.util.Slice;

import java.util.Objects;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonToken extends Slice {
    public enum Type {
        SYMBOL,   // on of []{},:
        NUMBER,   // numerical
        LITERAL,  // quoted literal
        TOKEN,    // static token.
    }

    public static final byte[] kNull = new byte[]{'n', 'u', 'l', 'l'};
    public static final byte[] kTrue = new byte[]{'t', 'r', 'u', 'e'};
    public static final byte[] kFalse = new byte[]{'f', 'a', 'l', 's', 'e'};

    public static final char kListStartChar = '[';
    public static final char kListEndChar = ']';
    public static final char kListSepChar = ',';
    public static final char kMapStartChar = '{';
    public static final char kMapEndChar = '}';
    public static final char kKeyValSepChar = ':';

    public final Type type;
    public final int  lineNo;
    public final int  linePos;

    public JsonToken(Type type, byte[] lineBuffer, int offset, int len, int lineNo, int linePos) {
        super(lineBuffer, offset, len);
        this.type = type;
        this.lineNo = lineNo;
        this.linePos = linePos;
    }

    public boolean isNull() {
        return type == Type.TOKEN && strEquals(kNull);
    }

    public boolean isSymbol() {
        return length() == 1 && "{}[],:".indexOf(charAt(0)) >= 0;
    }

    public final boolean isSymbol(char c) {
        return length() == 1 && charAt(0) == c;
    }

    public boolean isLiteral() {
        return type == Type.LITERAL;
    }

    public boolean isBoolean() {
        return type == Type.TOKEN && strEquals(kTrue) || strEquals(kFalse);
    }

    public boolean isInteger() {
        return type == Type.NUMBER && !containsAny((byte) '.', (byte) 'e', (byte) 'E');
    }

    public boolean isReal() {
        return type == Type.NUMBER;
    }

    public boolean booleanValue() {
        return strEquals(kTrue);
    }

    public byte byteValue() {
        return (byte) parseInteger();
    }

    public short shortValue() {
        return (short) parseInteger();
    }

    public int intValue() {
        return (int) parseInteger();
    }

    public long longValue() {
        return parseInteger();
    }

    public double doubleValue() {
        return parseDouble();
    }

    /**
     * Get the whole slice as a string.
     * @return Slice decoded as UTF_8 string.
     */
    public String decodeJsonLiteral() {
        // This decodes the string from UTF_8 bytes.
        String tmp = substring(1, -1);
        final int l = tmp.length();
        StringBuilder out = new StringBuilder(l);

        boolean esc = false;
        for (int i = 0; i < l; ++i) {
            if (esc) {
                esc = false;

                char ch = tmp.charAt(i);
                switch (ch) {
                    case 'b':
                        out.append('\b');
                        break;
                    case 'f':
                        out.append('\f');
                        break;
                    case 'n':
                        out.append('\n');
                        break;
                    case 'r':
                        out.append('\r');
                        break;
                    case 't':
                        out.append('\t');
                        break;
                    case '\"':
                    case '\'':
                    case '\\':
                        out.append(ch);
                        break;
                    case 'u':
                        if (l < i + 5) {
                            out.append('?');
                        } else {
                            String n = tmp.substring(i + 1, i + 5);
                            int cp = Integer.parseInt(n, 16);
                            out.append((char) cp);
                        }
                        i += 4;  // skipping 4 more characters.
                        break;
                    default:
                        out.append('?');
                        break;
                }
            } else if (tmp.charAt(i) == '\\') {
                esc = true;
            } else {
                out.append(tmp.charAt(i));
            }
        }
        return out.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString(), type, lineNo);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof JsonToken)) return false;
        JsonToken other = (JsonToken) o;

        return super.equals(o) &&
               Objects.equals(lineNo, other.lineNo);
    }

    @Override
    public String toString() {
        return String.format("%s('%s',%d:%d-%d)",
                             type.toString(),
                             asString(),
                             lineNo,
                             linePos,
                             linePos + length());
    }
}
