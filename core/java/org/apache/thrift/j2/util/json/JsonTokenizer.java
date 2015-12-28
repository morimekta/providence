package org.apache.thrift.j2.util.json;

import org.apache.thrift.j2.util.TStringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonTokenizer {
    private final static int MARK_LIMIT = 4096;

    private final InputStream mIn;

    private int mLine;
    private int mPos;

    private int mLastByte;

    private boolean mLiteral;
    private boolean mLiteralExcaped;
    private final ArrayList<String> mLines;
    private       StringBuilder     mLineBuilder;
    private       JsonToken            mNextToken;

    public JsonTokenizer(InputStream in) throws IOException {
        mIn = in;

        mLine = 1;
        mPos = 0;

        mLastByte = 0;
        mLiteral = false;
        mLiteralExcaped = false;

        mLineBuilder = new StringBuilder();
        mLines = new ArrayList<>();
    }

    public JsonToken expect(String message) throws JsonException, IOException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file while " + message);
        }
        return next();
    }

    public void expectSymbol(JsonToken.CH symbol, String message) throws IOException, JsonException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected " + symbol + " while " + message);
        } else if (mNextToken.isSymbol() && mNextToken.getSymbol().equals(symbol)) {
            mNextToken = null;
        } else {
            throw newParseException("Expected " + symbol + " but found " + mNextToken + " while " + message);
        }
    }

    public JsonToken expectLiteral(String message) throws IOException, JsonException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected identifier while " + message);
        } else if (mNextToken.isLiteral()) {
            return next();
        } else {
            throw newParseException("Expected identifier but found " + mNextToken + " while " + message);
        }
    }

    public boolean hasNext() throws IOException, JsonException {
        if (mNextToken == null) {
            mNextToken = next();
        }
        return mNextToken != null;
    }

    public JsonToken next() throws IOException, JsonException {
        if (mNextToken != null) {
            JsonToken tmp = mNextToken;
            mNextToken = null;
            return tmp;
        }

        int startPos = mPos;
        StringBuilder builder = new StringBuilder();

        while (mLastByte >= 0) {
            int b = mLastByte;
            mLastByte = 0;
            if (b == 0) {
                b = mLastByte = mIn.read();
                ++mPos;

                if (b != '\n') {
                    mLineBuilder.append((char) b);
                }
            }
            if (b > 0) {
                if (mLiteral) {
                    mLastByte = 0;

                    builder.append((char) b);

                    if (b == '\n') {
                        throw newParseException("newline in string literal");
                    } else if (b == JsonToken.CH.ESCAPE.c) {
                        mLiteralExcaped = true;
                    } else if (b == JsonToken.CH.QUOTE.c) {
                        mLiteral = false;
                        return mkToken(builder, startPos);
                    } else if (JsonToken.mustUnicodeEscape(b)) {
                        throw newParseException(String.format(
                                "Illegal character in string literal '\\u%04x'", b));
                    } else if (mLiteralExcaped) {
                        mLiteralExcaped = false;
                    }
                    continue;
                }

                JsonToken.CH ct = JsonToken.CH.valueOf((char) b);
                if (ct != null) {
                    JsonToken token = mkToken(builder, startPos);
                    if (token != null) {
                        return token;
                    } else {
                        mLastByte = 0;  // consumed 'this'.
                        if (b == JsonToken.CH.QUOTE.c) {
                            mLiteral = true;
                            mLiteralExcaped = false;
                            builder.append((char) b);
                            continue;
                        }

                        return mkToken(ct, mPos);
                    }
                } else if (b == ' ' || b == '\t' || b == '\r') {
                    mLastByte = 0;
                    if (builder.length() > 0) {
                        return mkToken(builder, startPos);
                    }
                } else if (b == '\n') {
                    mLastByte = 0;
                    mLines.add(mLineBuilder.toString());
                    mLineBuilder = new StringBuilder();

                    JsonToken token = mkToken(builder, startPos);
                    ++mLine;
                    mPos = 0;
                    if (token != null) {
                        return token;
                    }
                } else {
                    builder.append((char) b);
                }
            } else {
                return mkToken(builder, startPos);
            }

            mLastByte = 0;
        }

        return null;
    }

    public String getLine(int line) throws IOException {
        if (line < 1) throw new IllegalArgumentException("Oops!!!");
        if (mLines.size() >= line) {
            return mLines.get(line - 1);
        } else {
            mLineBuilder.append(TStringUtils.readString(mIn, "\n"));
            String ln = mLineBuilder.toString();
            mLines.add(ln);
            return ln;
        }
    }

    private JsonException newParseException(String s) throws IOException, JsonException {
        return new JsonException(s, getLine(mLine), mLine, mPos, 0);
    }

    private JsonToken mkToken(JsonToken.CH ct, int pos) {
        return new JsonToken(ct.toString(), mLine, pos, 1);
    }

    private JsonToken mkToken(StringBuilder builder, int startPos) {
        if (builder.length() > 0) {
            return new JsonToken(builder.toString(),
                              mLine,
                              startPos,
                              mPos - startPos - 1);
        }
        return null;
    }
}
