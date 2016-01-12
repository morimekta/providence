package net.morimekta.providence.util.json;

import net.morimekta.providence.util.PStringUtils;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonException extends Exception {
    private final String line;
    private final int    lineNo;
    private final int    linePos;
    private final int    len;

    public JsonException(String message) {
        this(message, null, 0, 0, 0);
    }

    public JsonException(String message, String line, int lineNo, int linePos, int len) {
        super(message);

        this.line = line;
        this.lineNo = lineNo;
        this.linePos = linePos;
        this.len = len;
    }

    public JsonException(String message, JsonTokenizer tokenizer, JsonToken token) throws IOException {
        super(message);

        line = tokenizer.getLine(token.lineNo);
        lineNo = token.lineNo;
        linePos = token.linePos;
        len = token.length();
    }

    public String getLine() {
        return line;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getLinePos() {
        return linePos;
    }

    public int getLen() {
        return len;
    }

    @Override
    public String toString() {
        if (line != null) {
            return String.format("%s : %d : %d - %d\n# %s\n#%s^",
                                 getLocalizedMessage(),
                                 getLineNo(),
                                 getLinePos(),
                                 getLen(),
                                 getLine(),
                                 PStringUtils.times("-", linePos));
        } else {
            return String.format("JsonException(%s)",
                                 getLocalizedMessage());
        }
    }
}
