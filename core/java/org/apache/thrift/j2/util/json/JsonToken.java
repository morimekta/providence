package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.TTypeUtils;

import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonToken {
    public static final String NULL = "null";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public enum CH {
        LIST_START('['),
        LIST_END(']'),
        LIST_SEP(','),
        MAP_START('{'),
        MAP_END('}'),
        MAP_KV_SEP(':'),
        QUOTE('"'),
        ESCAPE('\\'),
        ;

        protected char c;

        CH(char c) {
            this.c = c;
        }

        public static CH valueOf(char c) {
            for (CH ch : values()) {
                if (ch.c == c) {
                    return ch;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return new String(new char[]{c});
        }
    }

    private static final Pattern RE_LITERAL = Pattern.compile("\".*\"");
    private static final Pattern RE_BOOLEAN = Pattern.compile("(true|false)");
    private static final Pattern RE_INTEGER = Pattern.compile(
            "-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");
    private static final Pattern RE_DOUBLE = Pattern.compile(
            "-?([0-9]+[.]?([eE][+-]?[0-9]+)?|-?([0-9]+)?[.][0-9]+([eE][+-]?[0-9]+)?)");

    private final String mToken;
    private final int mLine;
    private final int mPos;
    private final int mLen;

    public JsonToken(String token, int line, int pos, int len) {
        mToken = token;
        mLine = line;
        mPos = pos;
        mLen = len;
    }

    public static boolean mustUnicodeEscape(int b) {
        return b < 32 || (127 <= b && b < 160) || (8192 <= b && b < 8448);
    }

    public String getToken() {
        return mToken;
    }

    public int getLine() {
        return mLine;
    }

    public int getPos() {
        return mPos;
    }

    public int getLen() {
        return mLen;
    }

    public boolean isNull() {
        return mToken.equals(NULL);
    }

    public boolean isSymbol() {
        return mToken.length() == 1 && CH.valueOf(mToken.charAt(0)) != null;
    }

    public boolean isLiteral() {
        return RE_LITERAL.matcher(mToken).matches();
    }

    public boolean isBoolean() {
        return RE_BOOLEAN.matcher(mToken).matches();
    }

    public boolean isInteger() {
        return RE_INTEGER.matcher(mToken).matches();
    }

    public boolean isReal() {
        return RE_DOUBLE.matcher(mToken).matches();
    }

    public CH getSymbol() {
        return CH.valueOf(mToken.charAt(0));
    }

    public String literalValue() throws JsonException {
        if (!isLiteral()) {
            throw new JsonException(mToken + " is not a string literal.");
        }

        String literal = mToken.substring(1, mToken.length() - 1);
        StringBuilder builder = new StringBuilder();

        boolean escape = false;
        for (int i = 0; i < literal.length(); ++i) {
            char c = literal.charAt(i);
            if (escape) {
                switch (c) {
                    case 'b':
                        builder.append('\b');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case '\"':
                    case '\\':
                        builder.append(c);
                        break;
                    case 'u':
                        if (literal.length() < i + 5) {
                            throw new JsonException("Incomplete unicode at end of literal", mToken, mLine, mPos, mLen);
                        }
                        String unicode = literal.substring(i + 1, i + 5);
                        try {
                            int cp = Integer.parseInt(unicode, 16);
                            builder.append((char) cp);
                        } catch (NumberFormatException nfe) {
                            throw new JsonException("Unable to parse unicode value from " + unicode, nfe);
                        }
                        i += 4;  // skip 4 more characters.
                        break;
                    default:
                        throw new JsonException("Unknown escape sequence: '\\" + c + "'");
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public boolean booleanValue() {
        return mToken.equals(TRUE);
    }

    public byte byteValue() {
        if (mToken.startsWith("0x")) {
            return Byte.parseByte(mToken.substring(2), 16);
        } else if (mToken.startsWith("0") && mToken.length() > 1) {
            return Byte.parseByte(mToken.substring(1), 8);
        }
        return Byte.parseByte(mToken);
    }

    public short shortValue() {
        if (mToken.startsWith("0x")) {
            return Short.parseShort(mToken.substring(2), 16);
        } else if (mToken.startsWith("0") && mToken.length() > 1) {
            return Short.parseShort(mToken.substring(1), 8);
        }
        return Short.parseShort(mToken);
    }

    public int intValue() {
        if (mToken.startsWith("0x")) {
            return Integer.parseInt(mToken.substring(2), 16);
        } else if (mToken.startsWith("0") && mToken.length() > 1) {
            return Integer.parseInt(mToken.substring(1), 8);
        }
        return Integer.parseInt(mToken);
    }

    public long longValue() {
        if (mToken.startsWith("0x")) {
            return Long.parseLong(mToken.substring(2), 16);
        } else if (mToken.startsWith("0") && mToken.length() > 1) {
            return Long.parseLong(mToken.substring(1), 8);
        }
        return Long.parseLong(mToken);
    }

    public float floatValue() {
        return Float.parseFloat(mToken);
    }

    public double doubleValue() {
        return Double.parseDouble(mToken);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof JsonToken)) return false;
        JsonToken other = (JsonToken) o;
        return TTypeUtils.equals(mToken, other.mToken) &&
                TTypeUtils.equals(mLine, other.mLine) &&
                TTypeUtils.equals(mPos, other.mPos) &&
                TTypeUtils.equals(mLen, other.mLen);
    }

    @Override
    public String toString() {
        return String.format("Token('%s',%d:%d-%d)", mToken, mLine, mPos, mPos + mLen);
    }
}
