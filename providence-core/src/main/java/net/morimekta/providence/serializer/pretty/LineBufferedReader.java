/*
 * Copyright 2017 Providence Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.providence.serializer.pretty;

import javax.annotation.Nonnull;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that manages a buffer if 1 line, or if requested pre-loads the
 * content of the input reader, and pretends as if it was the same line-buffering
 * reader. The whole point of this class is to be a base for tokenizer classes
 * JSON and other simple text-based syntax parsers.
 *
 * The mix of the Reader and the line buffering enables the tokenizer to be pretty
 * efficient (minimal data conversion) while having access to a stream of
 * characters to be parsed. For this reason all of the internal state fields
 * are made protected, though extending classes should not assign any fields
 * but the 'lastChar'.
 *
 * NOTE: Copy of same class from utils.
 */
class LineBufferedReader extends Reader {
    public LineBufferedReader(Reader reader) {
        this(reader, DEFAULT_LINE_BUFFER_SIZE);
    }

    public LineBufferedReader(Reader reader, int bufferSize) {
        this(reader, bufferSize, false);
    }

    public LineBufferedReader(Reader reader, boolean preLoadAll) {
        this(reader, DEFAULT_LINE_BUFFER_SIZE, preLoadAll);
    }

    public LineBufferedReader(Reader reader, int bufferSize, boolean preLoadAll) {
        this.reader = reader;
        this.lineNo = 0;
        this.linePos = 0;
        this.bufferOffset = -1;
        this.bufferLineEnd = false;
        this.preLoaded = preLoadAll;

        if (preLoaded) {
            try {
                CharArrayWriter writer = new CharArrayWriter();
                char[] tmp = new char[bufferSize];
                int r;
                while ((r = reader.read(tmp)) > 0) {
                    writer.write(tmp, 0, r);
                }
                this.buffer = writer.toCharArray();
                this.bufferLimit = buffer.length;
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        } else {
            this.buffer = new char[bufferSize];
            this.bufferLimit = -1;
        }

        this.lastChar = 0;
    }

    @Override
    public int read() throws IOException {
        if (lastChar > 0 || readNextChar()) {
            int ret = lastChar;
            lastChar = 0;
            return ret;
        }
        return -1;
    }

    @Override
    public int read(@Nonnull char[] chars, int off, int len) throws IOException {
        if (off + len > chars.length) {
            throw new IllegalArgumentException("off: " + off + " len: " + len + " > char[" + chars.length + "]");
        }
        if (off < 0 || len < 0) {
            throw new IllegalArgumentException("off: " + off + " len: " + len);
        }

        int r = 0;
        if (len > 0 && lastChar > 0) {
            chars[off] = (char) lastChar;
            ++r;
        }

        while (r < len && readNextChar()) {
            chars[off + r] = (char) lastChar;
            ++r;
        }
        if (r == len) {
            lastChar = 0;
            // Read whole buffer, last char is consumed.
        }
        return r;
    }

    @Override
    public void close() throws IOException {
        // ignore.
    }

    /**
     * @return The current line number.
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * The position of the current (last read) char in the current line.
     *
     * @return The char position.
     */
    public int getLinePos() {
        return linePos;
    }

    /**
     * Returns the current line in the buffer. Or empty string if not usable.
     *
     * @return The line string, not including the line-break.
     */
    @Nonnull
    public String getLine() {
        if (preLoaded) {
            if (bufferOffset >= 0) {
                int lineStart = bufferOffset;
                if (linePos > 0) {
                    lineStart -= linePos - 1;
                }
                int lineEnd = bufferOffset;
                while (lineEnd < bufferLimit && buffer[lineEnd] != '\n') {
                    ++lineEnd;
                }
                return new String(buffer, lineStart, lineEnd - lineStart);
            }
        } else if (bufferLimit > 0) {
            if (Math.abs((linePos - 1) - bufferOffset) < 2) {
                // only return the line if the line has not been consolidated before the
                // exception. This should avoid showing a bad exception line pointing to
                // the wrong content. This should never be the case in pretty-printed
                // JSON unless some really really long strings are causing the error.
                //
                // Since linePos does not exactly follow offset, we must accept +- 1.
                return new String(buffer, 0, bufferLimit - (bufferLineEnd ? 1 : 0));
            }
        }

        // Otherwise we don't have the requested line, return empty string.
        return "";
    }

    /**
     * Return the rest of the current line. This is handy for handling unwanted content
     * after the last expected token or character.
     *
     * @return The rest of the last read line. Not including leading and ending whitespaces,
     *         since these are allowed.
     * @throws IOException If unable to read the rest of the line.
     */
    @Nonnull
    public String getRestOfLine() throws IOException {
        if (preLoaded) {
            if (bufferOffset < 0 || buffer[bufferOffset] == '\n' ||
                (lastChar == 0 && !readNextChar())) {
                return "";
            }

            int start = bufferOffset;
            int length = 1;
            while (readNextChar()) {
                if (lastChar == '\n') {
                    lastChar = 0;
                    break;
                }
                ++length;
            }
            return new String(buffer, start, length);
        }

        if (bufferOffset < 0 || buffer[bufferOffset] == '\n' ||
            bufferOffset >= (bufferLimit - 1) ||
            (lastChar == 0 && !readNextChar())) {
            return "";
        }

        maybeConsolidateBuffer();
        StringBuilder remainderBuilder = new StringBuilder();
        do {
            int unreadChars = bufferLimit - bufferOffset;
            remainderBuilder.append(buffer, bufferOffset, unreadChars - (bufferLineEnd ? 1 : 0));
            bufferOffset = bufferLimit;
            linePos += unreadChars;

            if (bufferLineEnd) {
                break;
            }
            maybeConsolidateBuffer();
        } while (bufferOffset < (bufferLimit - 1));

        lastChar = 0;
        return remainderBuilder.toString();
    }

    /**
     * Read the rest of input from the reader, and get the lines from there.
     * This will consume the rest of the content of the reader.
     *
     * @param trimAndSkipEmpty If lines should be trimmed and empty lines should
     *                         be skipped.
     * @return List of lines after the current.
     * @throws IOException When failing to read stream to end.
     */
    @Nonnull
    public List<String> getRemainingLines(boolean trimAndSkipEmpty) throws IOException {
        List<String> out = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        while (bufferOffset <= bufferLimit || !bufferLineEnd) {
            if (!readNextChar()) {
                break;
            }
            if (lastChar == '\n') {
                String line = builder.toString();
                if (!trimAndSkipEmpty || !line.trim().isEmpty()) {
                    out.add(trimAndSkipEmpty ? line.trim() : line);
                }
                builder = new StringBuilder();
            } else {
                builder.append((char) lastChar);
            }
        }
        if (builder.length() > 0) {
            String line = builder.toString();
            if (!trimAndSkipEmpty || !line.trim().isEmpty()) {
                out.add(builder.toString());
            }
        }
        return out;
    }

    // -------------------------------
    // --         PROTECTED         --
    // -------------------------------

    protected final Reader  reader;
    protected final char[]  buffer;
    protected final boolean preLoaded;

    protected int     bufferLimit;
    protected boolean bufferLineEnd;
    protected int     bufferOffset;
    protected int     lineNo;
    protected int     linePos;
    protected int     lastChar;

    /**
     * If the char buffer is nearing it's "end" and does not end with a newline
     * (meaning it is a complete line), then take the reast of the current buffer
     * and move it to the front of the buffer, and read until end of buffer, or
     * end of line.
     *
     * @throws IOException On IO errors.
     */
    protected void maybeConsolidateBuffer() throws IOException {
        if (bufferLimit == buffer.length &&
            bufferOffset > 0 &&
            bufferOffset >= (buffer.length - CONSOLIDATE_LINE_ON) &&
            !preLoaded &&
            !bufferLineEnd) {

            // A: copy the remainder to the start of the buffer.
            int len = bufferLimit - bufferOffset;
            if (len > 0) {
                System.arraycopy(buffer, bufferOffset, buffer, 0, len);
            }

            int off = len;
            char[] b = new char[1];
            while (off < buffer.length && reader.read(b, 0, 1) > 0) {
                char ch = b[0];
                buffer[off] = ch;
                ++off;
                if (ch == '\n') {
                    bufferLineEnd = true;
                    break;
                }
            }

            bufferOffset = 0;
            bufferLimit = off;
        }
    }

    protected boolean readNextChar() throws IOException {
        if (lastChar < 0) return false;
        if (preLoaded) {
            if (bufferOffset >= (bufferLimit - 1)) {
                lastChar = -1;
                return false;
            }
            if (bufferOffset < 0 || buffer[bufferOffset] == '\n') {
                ++lineNo;
                linePos = 0;
            }
        } else {
            if (bufferOffset < 0 || bufferOffset >= (bufferLimit - 1)) {
                if (!readNextLine()) {
                    lastChar = -1;
                    // not valid JSON string char.
                    return false;
                }
            }
        }
        ++linePos;
        lastChar = buffer[++bufferOffset];
        return true;
    }

    // -------------------------------
    // --          PRIVATE          --
    // -------------------------------

    private static final int CONSOLIDATE_LINE_ON      = 1 << 6;   //   64 chars
    private static final int DEFAULT_LINE_BUFFER_SIZE = 1 << 11;  // 2048 chars --> 4kb

    private boolean readNextLine() throws IOException {
        boolean newLine = false;
        if (bufferLimit > 0 && !bufferLineEnd) {
            // check for "last line"
            if (bufferLimit < buffer.length) {
                return false;
            }
        } else {
            newLine = true;
        }

        bufferLineEnd = false;

        int off = 0;
        char[] b = new char[1];
        while (off < buffer.length && reader.read(b, 0, 1) > 0) {
            final char ch = b[0];
            buffer[off] = ch;
            ++off;
            if (ch == '\n') {
                bufferLineEnd = true;
                break;
            }
        }
        if (off > 0) {
            if (newLine) {
                ++lineNo;
                linePos = 0;
            }
            if (off < buffer.length) {
                buffer[off] = 0;
            }
            bufferOffset = -1;
            bufferLimit = off;
            return true;
        }
        return false;
    }
}
