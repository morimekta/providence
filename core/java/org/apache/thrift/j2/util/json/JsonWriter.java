package org.apache.thrift.j2.util.json;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonWriter {
    protected enum Mode {
        NONE,
        LIST,
        MAP,
    }
    protected enum Expectation {
        KEY,
        VALUE,
    }
    private final PrintWriter mWriter;

    private static class State {
        Mode mode;
        Expectation expect;
        int num;
    }

    private Stack<State> mStack;
    private State mState;

    public JsonWriter(OutputStream out) {
        this(new PrintWriter(out));
    }

    public JsonWriter(PrintWriter writer) {
        mWriter = writer;
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
        startValue();

        mStack.push(mState);
        mState = new State();
        mState.mode = Mode.MAP;
        mState.expect = Expectation.KEY;
        mState.num = 0;

        mWriter.append('{');

        return this;
    }

    public JsonWriter array() throws JsonException {
        startValue();

        mStack.push(mState);
        mState = new State();
        mState.mode = Mode.LIST;
        mState.expect = Expectation.VALUE;
        mState.num = 0;

        mWriter.append('[');

        return this;
    }

    public JsonWriter endObject() throws JsonException {
        if (!Mode.MAP.equals(mState.mode)) throw new JsonException("Unexpected end, not in object..");
        if (Expectation.VALUE.equals(mState.expect)) throw new JsonException("Expected map value but got end.");
        mWriter.append('}');
        mState = mStack.pop();
        return this;
    }

    public JsonWriter endArray() throws JsonException {
        if (!Mode.LIST.equals(mState.mode)) throw new JsonException("Unexpected end, not in list.");
        mWriter.append(']');
        mState = mStack.pop();
        return this;
    }

    public JsonWriter key(String key) throws JsonException {
        if (!Mode.MAP.equals(mState.mode)) throw new JsonException("Unexpected map key outside map.");
        if (!Expectation.KEY.equals(mState.expect)) throw new JsonException("Unexpected map key, expected value or end");
        if (key == null) throw new JsonException("Expected map key, got null");

        if (mState.num > 0) {
            mWriter.append(',');
        }
        ++mState.num;
        mState.expect = Expectation.VALUE;

        appendQuoted(key);
        mWriter.append(':');
        return this;
    }

    public JsonWriter value(boolean value) throws JsonException {
        startValue();

        mWriter.append(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(long number) throws JsonException {
        startValue();

        mWriter.append(Long.toString(number));
        return this;
    }

    public JsonWriter value(double number) throws JsonException {
        startValue();

        if (number == (double) (long) number) {
            mWriter.append(Long.toString(((Number) number).longValue()));
        } else {
            mWriter.append(Double.toString(number));
        }
        return this;
    }

    public JsonWriter value(CharSequence value) throws JsonException {
        startValue();

        if (value == null) mWriter.append("null");
        else appendQuoted(value.toString());
        return this;
    }

    public JsonWriter value(Object value) throws JsonException {
        if (value == null) {
            startValue();
            mWriter.append("null");
            return this;
        } else if (value instanceof CharSequence) {
            return value((CharSequence) value);
        } else if (value instanceof Boolean) {
            return value(((Boolean) value).booleanValue());
        } else if (value instanceof Double || value instanceof Float) {
            return value(((Number) value).doubleValue());
        } else if (value instanceof Number) {
            return value(((Number) value).longValue());
        } else {
            throw new JsonException("Unknown value type " + value.getClass().getSimpleName());
        }
    }

    protected boolean startValue() throws JsonException {
        if (Expectation.KEY.equals(mState.expect)) throw new JsonException("Unexpected map key, expected value.");
        if (Mode.LIST.equals(mState.mode)) {
            if (mState.num > 0) {
                mWriter.append(',');
            }
            ++mState.num;
            return true;
        } else if (Mode.MAP.equals(mState.mode)){
            mState.expect = Expectation.KEY;
        }
        return false;
    }

    // Copied from org.json JSONObject.quote and modified for local use.
    private void appendQuoted(String string) {
        if(string != null && string.length() != 0) {
            int len = string.length();
            mWriter.append('\"');

            for(int i = 0; i < len; ++i) {
                char c = string.charAt(i);
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
                }

                if(c >= 32 && (c < 127 || c >= 160) && (c < 8192 || c >= 8448)) {
                    mWriter.append(c);
                } else {
                    mWriter.format("\\u%04x", (int) c);
                }
            }

            mWriter.append('\"');
        } else {
            mWriter.append("\"\"");
        }
    }
}
