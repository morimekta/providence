package net.morimekta.providence.util.pretty;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;

import java.io.File;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class TokenizerException extends SerializerException {
    private int    lineNo;
    private int    linePos;
    private String line;
    private String file;

    public TokenizerException(TokenizerException e, File file) {
        super(e, e.getMessage());
        setLine(e.getLine());
        setLineNo(e.getLineNo());
        setLinePos(e.getLinePos());
        // Keep the specified file, if there is one.
        if (e.getFile() == null) {
            setFile(file.getName());
        } else {
            setFile(e.getFile());
        }
    }

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

    public String getFile() {
        return file;
    }

    public TokenizerException setFile(String file) {
        this.file = file;
        return this;
    }

    public String toString() {
        if (lineNo > 0) {
            String fileSpec = "";
            if (file != null) {
                fileSpec = " in " + file;
            }
            if (line != null) {
                return String.format("Error%s on line %d, pos %d:\n" +
                                     "    %s\n" +
                                     "%s\n" +
                                     "%s^",
                                     fileSpec,
                                     getLineNo(),
                                     getLinePos(),
                                     getMessage(),
                                     getLine(),
                                     Strings.times("-", getLinePos()));
            } else {
                return String.format("Error%s on line %d, pos %d: %s",
                                     fileSpec,
                                     getLineNo(),
                                     getLinePos(),
                                     getMessage());
            }
        } else {
            return getMessage();
        }
    }
}
