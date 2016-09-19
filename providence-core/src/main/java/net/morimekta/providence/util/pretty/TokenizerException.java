package net.morimekta.providence.util.pretty;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class TokenizerException extends SerializerException {
    private int    lineNo;
    private int    linePos;
    private String line;

    public TokenizerException(String format, Object... args) {
        super(format, args);
    }

    public TokenizerException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    public TokenizerException(Token token, String format, Object... args) {
        super(format, args);
        setLinePos(token.getLinePos());
        setLineNo(token.getLineNo());
    }

    public int getLineNo() {
        return lineNo;
    }

    public TokenizerException setLineNo(int lineNo) {
        this.lineNo = lineNo;
        return this;
    }

    public int getLinePos() {
        return linePos;
    }

    public TokenizerException setLinePos(int linePos) {
        this.linePos = linePos;
        return this;
    }

    public String getLine() {
        return line;
    }

    public TokenizerException setLine(String line) {
        this.line = line;
        return this;
    }

    public String toString() {
        if (lineNo > 0) {
            if (line != null) {
                return String.format("Error on line %d, pos %d: %s\n" +
                                     "%s\n" +
                                     "%s^",
                                     getLineNo(),
                                     getLinePos(),
                                     getMessage(),
                                     getLine(),
                                     Strings.times("-", getLinePos()));
            } else {
                return String.format("Error on line %d, pos %d: %s",
                                     getLineNo(),
                                     getLinePos(),
                                     getMessage());
            }
        } else {
            return getMessage();
        }
    }
}
