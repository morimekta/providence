package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.io.IndentedPrintWriter;

import java.io.OutputStream;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class PrettyJsonWriter extends JsonWriter {
    private static final String SPACE = " ";

    private final IndentedPrintWriter mIndentedWriter;

    public PrettyJsonWriter(OutputStream out) {
        this(new IndentedPrintWriter(out));
    }

    public PrettyJsonWriter(IndentedPrintWriter writer) {
        super(writer);
        mIndentedWriter = writer;
    }

    @Override
    public PrettyJsonWriter object() throws JsonException {
        super.object();
        mIndentedWriter.begin();
        return this;
    }

    @Override
    public PrettyJsonWriter array() throws JsonException {
        super.array();
        mIndentedWriter.begin();
        return this;
    }

    @Override
    public PrettyJsonWriter endObject() throws JsonException {
        mIndentedWriter.end().appendln();
        super.endObject();
        return this;
    }

    @Override
    public PrettyJsonWriter endArray() throws JsonException {
        mIndentedWriter.end().appendln();
        super.endArray();
        return this;
    }

    @Override
    public PrettyJsonWriter key(String key) throws JsonException {
        mIndentedWriter.appendln();
        super.key(key);
        mIndentedWriter.append(SPACE);
        return this;
    }

    @Override
    public PrettyJsonWriter value(Object value) throws JsonException {
        super.value(value);
        return this;
    }

    @Override
    protected boolean startValue() throws JsonException {
        if (super.startValue()) {
            mIndentedWriter.appendln();
            return true;
        }
        return false;
    }
}
