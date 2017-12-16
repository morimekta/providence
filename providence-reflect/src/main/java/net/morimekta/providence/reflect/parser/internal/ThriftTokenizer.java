/*
 * Copyright 2016 Providence Authors
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
package net.morimekta.providence.reflect.parser.internal;

import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;
import net.morimekta.util.io.Utf8StreamReader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Specialization of the 'pretty' tokenizer to make it handle some
 * special cases only applicable when parsing thrift files, but not
 * allowed in pretty format or config files.
 */
public class ThriftTokenizer extends Tokenizer {
    // Various thrift keywords.
    public static final String kNamespace = "namespace";
    public static final String kInclude   = "include";
    public static final String kTypedef   = "typedef";
    public static final String kEnum      = "enum";
    public static final String kStruct    = "struct";
    public static final String kUnion     = "union";
    public static final String kException = "exception";
    public static final String kConst     = "const";
    public static final String kService   = "service";

    public static final String kExtends  = "extends";
    public static final String kVoid     = "void";
    public static final String kOneway   = "oneway";
    public static final String kThrows   = "throws";
    public static final String kRequired = "required";
    public static final String kOptional = "optional";

    // Not really 'symbols', but basically used as.
    public static final String kLineCommentStart  = "//";
    public static final String kBlockCommentStart = "/*";
    public static final String kBlockCommentEnd   = "*/";

    public ThriftTokenizer(InputStream in) {
        this(new Utf8StreamReader(in));
    }

    public ThriftTokenizer(Reader reader) {
        super(reader, Tokenizer.DEFAULT_BUFFER_SIZE, true);
    }

    @Nonnull
    private Token token(int off, int len, int linePos) {
        return token(off, len, lineNo, linePos);
    }

    @Nonnull
    private Token token(int off, int len, int lineNo, int linePos) {
        return new Token(buffer, off, len, lineNo, linePos);
    }

    @Nonnull
    protected Token nextSymbol() throws IOException {
        if (lastChar == '/') {
            int startOffset = bufferOffset;
            int startLinePos = linePos;

            if (!readNextChar()) {
                throw eof("Expected java-style comment, got end of file");
            }
            if (lastChar == '/' || lastChar == '*') {
                lastChar = 0;
                return token(startOffset, 2, startLinePos);
            }
            throw failure(lineNo, startLinePos, 2,
                          "Expected java-style comment, got '%s' after '/'",
                          Strings.escape((char) lastChar));
        }
        return super.nextSymbol();
    }

    public String parseDocBlock() throws IOException {
        String block = IOUtils.readString(this, ThriftTokenizer.kBlockCommentEnd).trim();
        String[] lines = block.split("\\r?\\n");
        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(RE_BLOCK_LINE.matcher(line).replaceFirst(""));
            builder.append('\n');
        }
        return builder.toString()
                      .trim();
    }

    public Token parseValue() throws IOException {
        Stack<Character> enclosures = new Stack<>();

        int startLineNo = 0;
        int startLinePos = 0;
        int offset = -1;
        while (true) {
            Token token = expect("const value");
            if (offset < 0) {
                offset = token.getOffset();
                startLineNo = token.getLineNo();
                startLinePos = token.getLinePos();
            }

            if (token.strEquals(kBlockCommentStart)) {
                parseDocBlock();  // ignore.
                continue;
            } else if (token.strEquals(kLineCommentStart)) {
                IOUtils.readString(this, Token.kNewLine);
                continue;
            } else if (token.isSymbol(Token.kMessageStart)) {
                enclosures.push(Token.kMessageEnd);
            } else if (token.isSymbol(Token.kListStart)) {
                enclosures.push(Token.kListEnd);
            } else if ((token.isSymbol(Token.kMessageEnd) || token.isSymbol(Token.kListEnd)) &&
                       enclosures.peek().equals(token.charAt(0))) {
                enclosures.pop();
            }

            if (enclosures.isEmpty()) {
                return token(offset,
                             (token.getOffset() - offset) + token.length(),
                             startLineNo,
                             startLinePos);
            }
        }
    }

    private final static Pattern RE_BLOCK_LINE = Pattern.compile("^([\\s]*[*])?[\\s]?");

    @Nonnull
    @Override
    protected TokenizerException failure(String format, Object... params) {
        return new ParseException(format, params);
    }
}
