package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.io.IndentedPrintWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 19.10.15
 */
public class JsonWriter {
    private static final String SPACE = " ";

    private enum Mode {
        NONE,
        LIST,
        MAP,
    }
    private enum Expectation {
        KEY,
        VALUE,
    }
    private final IndentedPrintWriter mWriter;
    private final String mSpace;

    private static class State {
        Mode mode;
        Expectation expect;
        int num;
    }

    private Stack<State> mStack;
    private State mState;

    public JsonWriter(OutputStream out) {
        this(out, false);
    }

    public JsonWriter(OutputStream out, boolean pretty) {
        this(pretty ? new IndentedPrintWriter(out) : new IndentedPrintWriter(new PrintWriter(out), "", ""),
             pretty ? SPACE : "");
    }

    public JsonWriter(IndentedPrintWriter writer, String space) {
        mWriter = writer;
        mSpace = space;
        mStack = new Stack<>();
        mState = new State();
        mState.mode = Mode.NONE;
        mState.expect = Expectation.VALUE;
        mState.num = 0;
    }

    public void flush() {
        mWriter.flush();
    }

    public JsonWriter object() throws JsonException {
        System.out.println("object");

        startValue();

        mStack.push(mState);
        mState = new State();
        mState.mode = Mode.MAP;
        mState.expect = Expectation.KEY;
        mState.num = 0;

        mWriter.append(JsonToken.CH.MAP_START.c)
               .begin();

        return this;
    }

    public JsonWriter array() throws JsonException {
        System.out.println("array");

        startValue();

        mStack.push(mState);
        mState = new State();
        mState.mode = Mode.LIST;
        mState.expect = Expectation.VALUE;
        mState.num = 0;

        mWriter.append(JsonToken.CH.LIST_START.c)
               .begin();

        return this;
    }

    public JsonWriter endObject() throws JsonException {
        System.out.println("endObject");

        if (!Mode.MAP.equals(mState.mode)) throw new JsonException("Unexpected end, not in object..");
        if (Expectation.VALUE.equals(mState.expect)) throw new JsonException("Expected map value but got end.");
        mWriter.end()
               .appendln(JsonToken.CH.MAP_END.c);
        mState = mStack.pop();
        return this;
    }

    public JsonWriter endArray() throws JsonException {
        System.out.println("endArray");

        if (!Mode.LIST.equals(mState.mode)) throw new JsonException("Unexpected end, not in list.");
        mWriter.end()
               .appendln(JsonToken.CH.LIST_END.c);
        mState = mStack.pop();
        return this;
    }

    public JsonWriter key(String key) throws JsonException {
        System.out.println("key");

        if (!Mode.MAP.equals(mState.mode)) throw new JsonException("Unexpected map key outside map.");
        if (!Expectation.KEY.equals(mState.expect)) throw new JsonException("Unexpected map key, expected value or end");
        if (key == null) throw new JsonException("Expected map key, got null");

        if (mState.num > 0) {
            mWriter.append(JsonToken.CH.LIST_SEP.c);
        }
        ++mState.num;
        mState.expect = Expectation.VALUE;

        mWriter.appendln();
        appendQuoted(key);
        mWriter.append(JsonToken.CH.MAP_KV_SEP.c)
               .append(mSpace);
        return this;
    }

    public JsonWriter value(Object value) throws JsonException {
        System.out.println("value");

        startValue();

        if (value == null) {
            mWriter.append(JsonToken.NULL);
        } else if (value instanceof String) {
            appendQuoted((String) value);
        } else if (value instanceof CharSequence) {
            appendQuoted(value.toString());
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                mWriter.append(JsonToken.TRUE);
            } else {
                mWriter.append(JsonToken.FALSE);
            }
        } else if (value instanceof Double || value instanceof Float) {
            Number n = (Number) value;
            if (n.doubleValue() == ((double) n.longValue())) {
                mWriter.append(Long.toString(((Number) value).longValue()));
            } else {
                mWriter.append(Double.toString(((Number) value).doubleValue()));
            }
        } else if (value instanceof Number) {
            mWriter.append(Long.toString(((Number) value).longValue()));
        } else {
            throw new JsonException("Unknown value type " + value.getClass().getSimpleName());
        }

        return this;
    }

    private void startValue() throws JsonException {
        if (Expectation.KEY.equals(mState.expect)) throw new JsonException("Unexpected map key, expected value.");
        if (Mode.LIST.equals(mState.mode)) {
            if (mState.num > 0) {
                mWriter.append(JsonToken.CH.LIST_SEP.c);
            }
            ++mState.num;
            mWriter.appendln();
        } else if (Mode.MAP.equals(mState.mode)){
            mState.expect = Expectation.KEY;
        }
    }

    // Copied from org.json JSONObject.quote and modified for local use.
    private void appendQuoted(String string) {
        if(string != null && string.length() != 0) {
            char c = 0;
            int len = string.length();
            mWriter.append('\"');

            for(int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch(c) {
                    case '\b':
                        mWriter.append("\\b");
                        continue;
                    case '\t':
                        mWriter.append("\\t");
                        continue;
                    case '\n':
                        mWriter.append("\\n");
                        continue;
                    case '\f':
                        mWriter.append("\\f");
                        continue;
                    case '\r':
                        mWriter.append("\\r");
                        continue;
                    case '\"':
                    case '\\':
                        mWriter.append('\\');
                        mWriter.append(c);
                        continue;
                    case '/':
                        if(b == 60) {
                            mWriter.append('\\');
                        }

                        mWriter.append(c);
                        continue;
                }

                if(c >= 32 && (c < 128 || c >= 160) && (c < 8192 || c >= 8448)) {
                    mWriter.append(c);
                } else {
                    String t = "000" + Integer.toHexString(c);
                    mWriter.append("\\u" + t.substring(t.length() - 4));
                }
            }

            mWriter.append('\"');
        } else {
            mWriter.append("\"\"");
        }
    }
}
