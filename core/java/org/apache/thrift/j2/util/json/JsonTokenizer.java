package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.TStringUtils;
import org.apache.thrift.j2.util.io.Utf8StreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonTokenizer {
    private final ArrayList<String> lines;
    private final Reader            reader;

    private int line;
    private int pos;
    private int lastChInt;

    private StringBuilder lineBuilder;
    private JsonToken     nextToken;

    public JsonTokenizer(InputStream in) throws IOException {
        this(new Utf8StreamReader(in));
    }

    public JsonTokenizer(Reader reader) throws IOException {
        this.reader = reader;
        this.line = 1;
        this.pos = 0;

        this.lastChInt = 0;

        this.lineBuilder = new StringBuilder();
        this.lines = new ArrayList<>(1024);
    }

    public JsonToken expect(String message) throws JsonException, IOException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file while " + message);
        }
        return next();
    }

    public int expectSymbol(String message, char... symbols) throws IOException, JsonException {
        if (!hasNext()) {
            throw newParseException(
                    "Unexpected end of file, expected one of " + Arrays.toString(symbols) + " while " + message);
        } else {
            if (nextToken.value.length() == 1) {
                char ch = nextToken.value.charAt(0);
                for (int i = 0; i < symbols.length; ++i) {
                    if (ch == symbols[i]) {
                        nextToken = null;
                        return i;
                    }
                }
            }

            throw newParseException(
                    "Expected one of " + Arrays.toString(symbols) + " but found " + nextToken + " while " + message);
        }
    }

    public boolean hasNext() throws IOException, JsonException {
        if (nextToken == null) {
            nextToken = next();
        }
        return nextToken != null;
    }

    public JsonToken next() throws IOException, JsonException {
        if (nextToken != null) {
            JsonToken tmp = nextToken;
            nextToken = null;
            return tmp;
        }

        int startPos = pos;

        StringBuilder literal = null;
        StringBuilder number = null;
        StringBuilder token = null;

        boolean escaped = false;

        while (lastChInt >= 0) {
            if (lastChInt == 0) {
                lastChInt = reader.read();
                if (lastChInt < 0) {
                    if (number != null) {
                        return mkToken(JsonToken.Type.NUMBER, number, startPos);
                    } else if (token != null) {
                        return mkToken(JsonToken.Type.TOKEN, token, startPos);
                    }
                    break;
                }

                lineBuilder.append((char) lastChInt);

                ++pos;
            }

            char ch = (char) lastChInt;
            if (literal != null) {
                lastChInt = 0;

                if (JsonToken.mustUnicodeEscape(ch)) {
                    throw newParseException(String.format(
                            "Illegal character in JSON literal: '\\u%04x'", (int) ch));
                }

                if (escaped) {
                    escaped = false;
                    switch (ch) {
                        case 'b':
                            literal.append('\b');
                            break;
                        case 'f':
                            literal.append('\f');
                            break;
                        case 'n':
                            literal.append('\n');
                            break;
                        case 'r':
                            literal.append('\r');
                            break;
                        case 't':
                            literal.append('\t');
                            break;
                        case '\"':
                        case '\'':
                        case '\\':
                            literal.append(ch);
                            break;
                        case 'u':
                            char[] i = new char[4];
                            reader.read(i);
                            int cp = Integer.parseInt(String.valueOf(i), 16);
                            literal.append((char) cp);
                            break;
                        default:
                            throw newParseException(String.format(
                                    "Illegal escape entity in JSON literal: '\\%c'", ch));
                    }
                } else {
                    switch (ch) {
                        case '"':
                            // end of literal.
                            return mkToken(JsonToken.Type.LITERAL, literal, startPos);
                        case '\\':
                            escaped = true;
                            break;
                        default:
                            literal.append(ch);
                            break;
                    }
                }
            } else if (number != null) {
                if (Character.isDigit(ch) || ch == '+' || ch == '-' || ch == 'x' || ch == 'e' || ch == 'E' || ch == '.') {
                    lastChInt = 0;
                    number.append(ch);
                } else {
                    return mkToken(JsonToken.Type.NUMBER, number, startPos);
                }
            } else if (token != null) {
                if (('A' <= ch && ch <= 'Z') ||
                    ('a' <= ch && ch <= 'z') ||
                    ('0' <= ch && ch <= '9') ||
                    ch == '_' || ch == '.') {
                    lastChInt = 0;
                    token.append(ch);
                } else {
                    return mkToken(JsonToken.Type.TOKEN, token, startPos);
                }
            } else if (ch == '\n') {
                // New line
                lastChInt = 0;
                lines.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
                ++line;
                pos = 0;
            } else {
                lastChInt = 0;

                if (ch == ' ' || ch == '\t') {
                    // just spacing.
                } else if (ch < 32 || (127 <= ch && ch < 160) || (8192 <= ch && ch < 8448)) {
                    throw newParseException(String.format(
                            "Illegal character in JSON structure: '\\u%04x'", (int) ch));
                } else {
                    switch (ch) {
                        case '[':
                        case ']':
                        case '{':
                        case '}':
                        case ':':
                        case ',':
                            return mkSymbol(ch, startPos);
                        case '-':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '.':
                            // starting a number.
                            number = new StringBuilder();
                            number.append(ch);
                            break;
                        case '\"':
                            literal = new StringBuilder();
                            escaped = false;
                            break;
                        default:
                            token = new StringBuilder();
                            token.append(ch);
                            break;
                    }
                }
            }
        }

        return null;
    }

    public String getLine(int line) throws IOException {
        if (line < 1)
            throw new IllegalArgumentException("Oops!!!");
        if (lines.size() >= line) {
            return lines.get(line - 1);
        } else {
            lineBuilder.append(TStringUtils.readString(reader, '\n'));
            String ln = lineBuilder.toString();
            lines.add(ln);
            return ln;
        }
    }

    private JsonException newParseException(String s) throws IOException, JsonException {
        return new JsonException(s, getLine(line), line, pos, 0);
    }

    private JsonToken mkSymbol(char ct, int pos) {
        return new JsonToken(JsonToken.Type.SYMBOL, line, pos, 1, String.valueOf(ct));
    }


    private JsonToken mkToken(JsonToken.Type type, StringBuilder builder, int startPos) {
        if (builder.length() > 0) {
            return new JsonToken(type,
                                 line,
                                 startPos,
                                 pos - startPos - 1,
                                 builder.toString());
        }
        return null;
    }
}
