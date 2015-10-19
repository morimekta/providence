package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.TTypeUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 19.10.15
 */
public class JsonToken {
    public static final String NULL = "null";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public String getToken() {
        return mToken;
    }

    public enum CH {
        MAP_START('{'),
        MAP_KV_SEP(':'),
        MAP_END('}'),
        LIST_START('['),
        LIST_END(']'),
        LIST_SEP(','),
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

    public static final Pattern RE_LITERAL = Pattern.compile("\".*\"");
    public static final Pattern RE_BOOLEAN = Pattern.compile("(true|false)");
    public static final Pattern RE_INTEGER = Pattern.compile("[0-9]+");
    public static final Pattern RE_DOUBLE = Pattern.compile(
            "([0-9]+[.]?([eE][+-]?[0-9]+)?|([0-9]+)?[.][0-9]+([eE][+-]?[0-9]+)?)");

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

    public boolean isDouble() {
        return RE_DOUBLE.matcher(mToken).matches();
    }

    public CH getSymbol() {
        return CH.valueOf(mToken.charAt(0));
    }

    public String literalValue() {
        return new String(mToken.substring(1, mToken.length() - 1).getBytes(),
                          StandardCharsets.UTF_8);
    }

    public boolean getBoolean() {
        return mToken.equals("true");
    }

    public byte byteValue() {
        return (byte) longValue();
    }

    public short shortValue() {
        return (short) longValue();
    }

    public int intValue() {
        return (int) longValue();
    }

    public long longValue() {
        return Long.parseLong(mToken);
    }

    public float getFloat() {
        return (float) doubleValue();
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
