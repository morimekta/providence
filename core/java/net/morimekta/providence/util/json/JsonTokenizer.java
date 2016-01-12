package net.morimekta.providence.util.json;

import net.morimekta.providence.util.PStringUtils;
import net.morimekta.providence.util.io.Utf8StreamReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonTokenizer {
    private static final int CONSOLIDATE_LINE_ON = 1 << 7;  // 128

    private final InputStream           reader;
    private final ArrayList<String>     lines;
    private final ByteBuffer            lineBuffer;
    private final ByteArrayOutputStream stringBuffer;

    private int line;
    private int linePos;
    private int lastByte;

    private StringBuilder lineBuilder;
    private JsonToken     unreadToken;

    public JsonTokenizer(InputStream in) throws IOException {
        this.reader = in;
        this.line = 1;
        this.linePos = 0;
        this.lastByte = 0;

        this.lineBuffer = ByteBuffer.allocate(1 << 16);  // 64k
        this.stringBuffer = new ByteArrayOutputStream(1 << 12);  // 4k
        this.lines = new ArrayList<>(1024);

        this.lineBuilder = new StringBuilder();
        this.unreadToken = null;
    }

    public JsonToken expect(String message) throws JsonException, IOException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file while " + message);
        }
        return next();
    }

    public int expectSymbol(String message, char... symbols) throws IOException, JsonException {
        if (!hasNext()) {
            throw newParseException(String.format("Unexpected end of stream while %s", message));
        } else {
            for (int i = 0; i < symbols.length; ++i) {
                if (unreadToken.isSymbol(symbols[i])) {
                    unreadToken = null;
                    return i;
                }
            }

            throw new JsonException(String.format("Expected one of \"%s\", but found \"%s\" while %s",
                                                  PStringUtils.join("", symbols),
                                                  unreadToken.toString(),
                                                  message), this, unreadToken);
        }
    }

    public boolean hasNext() throws IOException, JsonException {
        if (unreadToken == null) {
            unreadToken = next();
        }
        return unreadToken != null;
    }

    public JsonToken next() throws IOException, JsonException {
        if (unreadToken != null) {
            JsonToken tmp = unreadToken;
            unreadToken = null;
            return tmp;
        }

        while (lastByte >= 0) {
            if (lastByte == 0) {
                if (lineBuffer.position() == (lineBuffer.capacity() - CONSOLIDATE_LINE_ON)) {
                    flushLineBuffer();
                }

                lastByte = reader.read();
                if (lastByte < 0) {
                    break;
                }
                lineBuffer.put((byte) lastByte);
                ++linePos;
            }

            if (lastByte == '\n') {
                // New line
                flushLineBuffer();

                lines.add(lineBuilder.toString());
                ++line;
                linePos = 0;
                lastByte = 0;

                lineBuilder = new StringBuilder();
            } else if (lastByte == ' ' || lastByte == '\t') {
                lastByte = 0;
            } else if (lastByte == '\"') {
                return nextString();
            } else if (lastByte == '-' ||
                       (lastByte >= '0' && lastByte <= '9')) {
                return nextNumber();
            } else if (
                    lastByte == '[' || lastByte == ']' ||
                    lastByte == '{' || lastByte == '}' ||
                    lastByte == ':' || lastByte == ',') {
                return nextSymbol();
            } else if (lastByte < 32 ||
                       (127 <= lastByte && lastByte < 160) ||
                       (8192 <= lastByte && lastByte < 8448)) {
                throw newParseException(String.format(
                        "Illegal character in JSON structure: '\\u%04x'", lastByte));
            } else {
                return nextToken();
            }
        }

        return null;
    }

    private JsonToken nextSymbol() {
        lastByte = 0;
        return new JsonToken(JsonToken.Type.SYMBOL,
                             lineBuffer.array(),
                             lineBuffer.position() - 1,
                             1,
                             line,
                             linePos);
    }

    private JsonToken nextToken() throws IOException {
        int startPos = linePos;
        int startOffset = lineBuffer.position() - 1;
        int len = 0;
        while (lastByte == '_' || lastByte == '.' ||
               (lastByte >= '0' && lastByte <= '0') ||
               (lastByte >= 'a' && lastByte <= 'z') ||
               (lastByte >= 'A' && lastByte <= 'Z')) {
            ++len;
            lastByte = reader.read();
            if (lastByte < 0) {
                break;
            }
            lineBuffer.put((byte) lastByte);
            ++linePos;
        }

        return new JsonToken(JsonToken.Type.TOKEN,
                             lineBuffer.array(),
                             startOffset,
                             len,
                             line,
                             startPos);
    }

    private JsonToken nextNumber() throws IOException {
        int startPos = linePos;
        int startOffset = lineBuffer.position() - 1;
        // number (any type).
        int len = 0;
        while (lastByte == '+' ||
               lastByte == '-' ||
               lastByte == 'x' ||
               lastByte == 'e' ||
               lastByte == 'E' ||
               lastByte == '.' ||
               (lastByte >= '0' && lastByte <= '9')) {
            ++len;
            // numbers are terminated by first non-numeric character.
            lastByte = reader.read();
            if (lastByte < 0) {
                break;
            }
            lineBuffer.put((byte) lastByte);
            ++linePos;
        }

        return new JsonToken(JsonToken.Type.NUMBER,
                             lineBuffer.array(),
                             startOffset,
                             len,
                             line,
                             startPos);
    }

    private JsonToken nextString() throws IOException, JsonException {
        // string literals may be longer than 128 bytes. We may need to build it.
        stringBuffer.reset();
        stringBuffer.write(lastByte);

        int startPos = linePos;
        int startOffset = lineBuffer.position() - 1;

        boolean consolidated = false;
        boolean esc = false;
        for (;;) {
            if (lineBuffer.position() >= (lineBuffer.capacity() - 1)) {
                stringBuffer.write(lineBuffer.array(), startOffset, lineBuffer.position() - startOffset);
                startOffset = 0;
                consolidated = true;
                flushLineBuffer();
            }

            lastByte = reader.read();
            if (lastByte < 0) {
                throw newParseException("Unexpected end of stream in string literal.");
            }

            lineBuffer.put((byte) lastByte);
            ++linePos;

            if (esc) {
                esc = false;
            } else if (lastByte == '\\') {
                esc = true;
            } else if (lastByte == '\"') {
                break;
            }
        }

        lastByte = 0;
        if (consolidated) {
            stringBuffer.write(lineBuffer.array(), 0, lineBuffer.position());
            return new JsonToken(JsonToken.Type.LITERAL,
                                 stringBuffer.toByteArray(),
                                 0,
                                 stringBuffer.size(),
                                 line,
                                 startPos);
        } else {
            return new JsonToken(JsonToken.Type.LITERAL,
                                 lineBuffer.array(),
                                 startOffset,
                                 lineBuffer.position() - startOffset,
                                 line,
                                 startPos);
        }
    }

    public String getLine(int line) throws IOException {
        if (line < 1)
            throw new IllegalArgumentException("Oops!!!");
        if (lines.size() >= line) {
            return lines.get(line - 1);
        } else {
            flushLineBuffer();
            lineBuilder.append(PStringUtils.readString(new Utf8StreamReader(reader), '\n'));
            String ln = lineBuilder.toString();
            lines.add(ln);
            return ln;
        }
    }

    private void flushLineBuffer() {
        lineBuilder.append(new String(lineBuffer.array(), 0, lineBuffer.position()));
        lineBuffer.clear();
    }

    private JsonException newParseException(String s) throws IOException, JsonException {
        return new JsonException(s, getLine(line), line, linePos, 0);
    }
}
