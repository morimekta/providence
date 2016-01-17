package net.morimekta.providence.util.json;

import net.morimekta.providence.Binary;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonWriter {
    public static final String kNull = "null";
    public static final String kTrue = "true";
    public static final String kFalse = "false";

    private final PrintWriter        writer;
    private final Stack<JsonContext> stack;

    private JsonContext context;

    public JsonWriter(OutputStream out) {
        this(new PrintWriter(out));
    }

    public JsonWriter(PrintWriter writer) {
        this.writer = writer;
        this.stack = new Stack<>();

        context = new JsonContext(JsonContext.Mode.VALUE);
    }

    public void flush() {
        writer.flush();
    }

    public JsonWriter object() throws JsonException {
        startValue();

        stack.push(context);
        context = new JsonContext(JsonContext.Mode.MAP);
        writer.write('{');

        return this;
    }

    public JsonWriter array() throws JsonException {
        startValue();

        stack.push(context);
        context = new JsonContext(JsonContext.Mode.LIST);
        writer.write('[');

        return this;
    }

    public JsonWriter endObject() throws JsonException {
        if (context == null) throw new JsonException("Ending object on closed writer.");
        if (!context.map()) throw new JsonException("Unexpected end, not in object.");
        if (context.value()) throw new JsonException("Expected map value but got end.");
        writer.write('}');
        context = stack.pop();
        return this;
    }

    public JsonWriter endArray() throws JsonException {
        if (context == null) throw new JsonException("Ending array on closed writer.");
        if (!context.list()) throw new JsonException("Unexpected end, not in list.");
        writer.write(']');
        context = stack.pop();
        return this;
    }

    public JsonWriter key(boolean key) throws JsonException {
        startKey();

        writer.write(key ? "\"true\":" : "\"false\":");
        return this;
    }

    public JsonWriter key(byte key) throws JsonException {
        startKey();

        writer.write('\"');
        writer.print((int) key);
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter key(short key) throws JsonException {
        startKey();

        writer.write('\"');
        writer.print((int) key);
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter key(int key) throws JsonException {
        startKey();

        writer.write('\"');
        writer.print(key);
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter key(long key) throws JsonException {
        startKey();

        writer.write('\"');
        writer.print(key);
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter key(double key) throws JsonException {
        startKey();

        writer.write('\"');
        final long i = (long) key;
        if (key == (double) i) {
            writer.print(i);
        } else {
            writer.print(key);
        }
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter key(CharSequence key) throws JsonException {
        startKey();

        if (key == null) throw new JsonException("Expected map key, got null");

        writeQuoted(key);
        writer.write(':');
        return this;
    }

    public JsonWriter key(Binary key) throws JsonException {
        startKey();

        if (key == null) throw new JsonException("Expected map key, got null");

        writer.write('\"');
        writer.write(key.toBase64());
        writer.write('\"');
        writer.write(':');
        return this;
    }

    public JsonWriter keyLiteral(CharSequence key) throws JsonException {
        startKey();

        if (key == null) throw new JsonException("Expected map key, got null");

        writer.write(key.toString());
        writer.write(':');
        return this;
    }

    public JsonWriter value(boolean value) throws JsonException {
        startValue();

        writer.write(value ? kTrue : kFalse);
        return this;
    }

    public JsonWriter value(byte value) throws JsonException {
        startValue();

        writer.print((int) value);
        return this;
    }

    public JsonWriter value(short value) throws JsonException {
        startValue();

        writer.print((int) value);
        return this;
    }

    public JsonWriter value(int value) throws JsonException {
        startValue();

        writer.print(value);
        return this;
    }

    public JsonWriter value(long number) throws JsonException {
        startValue();

        writer.print(number);
        return this;
    }

    public JsonWriter value(double number) throws JsonException {
        startValue();

        final long i = (long) number;
        if (number == (double) i) {
            writer.print(i);
        } else {
            writer.print(number);
        }
        return this;
    }

    public JsonWriter value(CharSequence value) throws JsonException {
        startValue();

        if (value == null) writer.write(kNull);
        else writeQuoted(value);
        return this;
    }

    public JsonWriter value(Binary value) throws JsonException {
        startValue();

        if (value == null) writer.write(kNull);
        else {
            writer.write('\"');
            writer.write(value.toBase64());
            writer.write('\"');
        }
        return this;
    }

    public JsonWriter valueLiteral(CharSequence value) throws JsonException {
        startValue();

        if (value == null) writer.write(kNull);
        else writer.write(value.toString());
        return this;
    }

    protected void startKey() throws JsonException {
        if (context == null) throw new JsonException("Starting key on closed writer.");
        if (!context.map()) throw new JsonException("Unexpected map key outside map.");
        if (!context.key()) throw new JsonException("Unexpected map key, expected value or end");

        if (context.num > 0) {
            writer.write(',');
        }

        ++context.num;
        context.expect = JsonContext.Expect.VALUE;
    }

    protected boolean startValue() throws JsonException {
        if (context == null) throw new JsonException("Starting value on closed writer.");
        if (context.key()) throw new JsonException("Unexpected map key, expected value.");
        if (context.list()) {
            if (context.num > 0) {
                writer.write(',');
            }
            ++context.num;
            return true;
        } else if (context.map()){
            context.expect = JsonContext.Expect.KEY;
        }
        return false;
    }

    // Copied from org.json JSONObject.quote and modified for local use.
    private void writeQuoted(CharSequence string) {
        if(string != null && string.length() != 0) {
            int len = string.length();
            writer.write('\"');

            for(int i = 0; i < len; ++i) {
                char c = string.charAt(i);
                switch(c) {
                    case '\b':
                        writer.write("\\b");
                        break;
                    case '\t':
                        writer.write("\\t");
                        break;
                    case '\n':
                        writer.write("\\n");
                        break;
                    case '\f':
                        writer.write("\\f");
                        break;
                    case '\r':
                        writer.write("\\r");
                        break;
                    case '\"':
                    case '\\':
                        writer.write('\\');
                        writer.write(c);
                        break;
                    default:
                        if(c < 32 || (127 <= c && c < 160) || (8192 <= c && c < 8448)) {
                            writer.format("\\u%04x", (int) c);
                        } else {
                            writer.write(c);
                        }
                        break;
                }
            }

            writer.write('\"');
        } else {
            writer.write("\"\"");
        }
    }
}
