package org.apache.thrift.j2.util.json;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonException extends Exception {
    private final String mLine;
    private final int    mLineNo;
    private final int    mPos;
    private final int    mLen;

    public JsonException(String message, Throwable cause) {
        this(message, null, 0, 0, 0);
        initCause(cause);
    }

    public JsonException(String message) {
        this(message, null, 0, 0, 0);
    }

    public JsonException(String message, String line, int lineNo, int pos, int len) {
        super(message);

        mLine = line;
        mLineNo = lineNo;
        mPos = pos;
        mLen = len;
    }

    public JsonException(String message, JsonTokenizer tokenizer, JsonToken token) throws IOException {
        super(message);

        mLine = tokenizer.getLine(token.getLine());
        mLineNo = token.getLine();
        mPos = token.getPos();
        mLen = token.getLen();
    }

    public String getLine() {
        return mLine;
    }

    public int getLineNo() {
        return mLineNo;
    }

    public int getPos() {
        return mPos;
    }

    public int getLen() {
        return mLen;
    }

    @Override
    public String toString() {
        if (mLine != null) {
            return String.format("JsonException(%s,%d:%d,\"%s\")",
                                 getLocalizedMessage(),
                                 getLineNo(),
                                 getPos(),
                                 getLine());
        } else {
            return String.format("JsonException(%s)",
                                 getLocalizedMessage());
        }
    }
}
