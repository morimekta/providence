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

import net.morimekta.util.Slice;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Thrift token. Returned from the thrift tokenizer for each real "token".
 */
@SuppressFBWarnings(value = {"MS_MUTABLE_ARRAY", "MS_PKGPROTECT"})
public class Token extends Slice {
    // Various symbols.
    public static final char kMessageStart  = '{';
    public static final char kMessageEnd    = '}';
    public static final char kFieldIdSep    = ':';
    public static final char kFieldValueSep = '=';
    public static final char kParamsStart   = '(';
    public static final char kParamsEnd     = ')';
    public static final char kGenericStart  = '<';
    public static final char kGenericEnd    = '>';

    public static final char kLineSep1 = ',';
    public static final char kLineSep2 = ';';

    // Not really 'symbols'.
    public static final char kLiteralEscape      = '\\';
    public static final char kLiteralQuote       = '\'';
    public static final char kLiteralDoubleQuote = '\"';
    public static final char kListStart  = '[';
    public static final char kListEnd    = ']';
    public static final char kJavaCommentStart   = '/';
    public static final char kShellComment       = '#';

    public static final String kSymbols = "{}:=()<>,;#[]";

    public static final byte[] kJavaComment       = new byte[]{'/', '/'};
    public static final byte[] kBlockCommentStart = new byte[]{'/', '*'};
    public static final byte[] kBlockCommentEnd   = new byte[]{'*', '/'};

    // Keyword constants.
    // -- header
    public static final byte[] kInclude   = new byte[]{'i', 'n', 'c', 'l', 'u', 'd', 'e'};
    public static final byte[] kNamespace = new byte[]{'n', 'a', 'm', 'e', 's', 'p', 'a', 'c', 'e'};

    // -- types
    public static final byte[] kEnum      = new byte[]{'e', 'n', 'u', 'm'};
    public static final byte[] kStruct    = new byte[]{'s', 't', 'r', 'u', 'c', 't'};
    public static final byte[] kUnion     = new byte[]{'u', 'n', 'i', 'o', 'n'};
    public static final byte[] kException = new byte[]{'e', 'x', 'c', 'e', 'p', 't', 'i', 'o', 'n'};
    public static final byte[] kService   = new byte[]{'s', 'e', 'r', 'v', 'i', 'c', 'e'};
    public static final byte[] kConst     = new byte[]{'c', 'o', 'n', 's', 't'};
    public static final byte[] kTypedef   = new byte[]{'t', 'y', 'p', 'e', 'd', 'e', 'f'};

    // -- modifiers
    public static final byte[] kRequired = new byte[]{'r', 'e', 'q', 'u', 'i', 'r', 'e', 'd'};
    public static final byte[] kOptional = new byte[]{'o', 'p', 't', 'i', 'o', 'n', 'a', 'l'};
    public static final byte[] kOneway   = new byte[]{'o', 'n', 'e', 'w', 'a', 'y'};
    public static final byte[] kThrows   = new byte[]{'t', 'h', 'r', 'o', 'w', 's'};
    public static final byte[] kExtends  = new byte[]{'e', 'x', 't', 'e', 'n', 'd', 's'};
    public static final byte[] kVoid     = new byte[]{'v', 'o', 'i', 'd'};

    // -- primitives
    public static final byte[] kBool   = new byte[]{'b', 'o', 'o', 'l'};
    public static final byte[] kByte   = new byte[]{'b', 'y', 't', 'e'};
    public static final byte[] kI8     = new byte[]{'i', '8'};
    public static final byte[] kI16    = new byte[]{'i', '1', '6'};
    public static final byte[] kI32    = new byte[]{'i', '3', '2'};
    public static final byte[] kI64    = new byte[]{'i', '6', '4'};
    public static final byte[] kDouble = new byte[]{'d', 'o', 'u', 'b', 'l', 'e'};
    public static final byte[] kString = new byte[]{'s', 't', 'r', 'i', 'n', 'g'};
    public static final byte[] kBinary = new byte[]{'b', 'i', 'n', 'a', 'r', 'y'};

    // -- containers
    public static final byte[] kList = new byte[]{'l', 'i', 's', 't'};
    public static final byte[] kSet  = new byte[]{'s', 'e', 't'};
    public static final byte[] kMap  = new byte[]{'m', 'a', 'p'};

    private static final Pattern RE_IDENTIFIER           = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern RE_QUALIFIED_IDENTIFIER = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern RE_INTEGER              = Pattern.compile("-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");
    private static final Pattern RE_DOUBLE               = Pattern.compile("-?((0|[1-9][0-9]*)[.]|([0-9]*[.][0-9][0-9]*))([eE][-+]?[0-9][0-9]*)?");

    private final int lineNo;
    private final int linePos;

    public Token(byte[] fb, int off, int len, int lineNo, int linePos) {
        super(fb, off, len);
        this.lineNo = lineNo;
        this.linePos = linePos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !getClass().equals(o.getClass())) return false;

        Token other = (Token) o;
        return super.equals(o) &&
               (lineNo == other.lineNo) &&
               (linePos == other.linePos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Token.class, super.hashCode(), lineNo, linePos);
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getLinePos() {
        return linePos;
    }

    public boolean startsLineComment() {
        return isSymbol(kShellComment) || strEquals(kJavaComment);
    }

    public boolean startsBlockComment() {
        return strEquals(kBlockCommentStart);
    }

    public boolean isSymbol(char symbol) {
        return len == 1 && fb[off] == symbol;
    }

    public boolean isStringLiteral() {
        return (length() > 1 &&
                ((charAt(0) == '\"' && charAt(-1) == '\"') ||
                 (charAt(0) == '\'' && charAt(-1) == '\'')));
    }

    public boolean isIdentifier() {
        return RE_IDENTIFIER.matcher(asString())
                            .matches();
    }

    public boolean isQualifiedIdentifier() {
        return RE_QUALIFIED_IDENTIFIER.matcher(asString())
                                      .matches();
    }

    public boolean isInteger() {
        return RE_INTEGER.matcher(asString())
                         .matches();
    }

    public boolean isDouble() {
        return RE_DOUBLE.matcher(asString())
                        .matches();
    }

    /**
     * Get the whole slice as a string.
     *
     * @return Slice decoded as UTF_8 string.
     */
    public String decodeStringLiteral() {
        // This decodes the string from UTF_8 bytes.
        String tmp = substring(1, -1).asString();
        final int l = tmp.length();
        StringBuilder out = new StringBuilder(l);

        boolean esc = false;
        for (int i = 0; i < l; ++i) {
            if (esc) {
                esc = false;

                char ch = tmp.charAt(i);
                switch (ch) {
                    case 'b':
                        out.append('\b');
                        break;
                    case 'f':
                        out.append('\f');
                        break;
                    case 'n':
                        out.append('\n');
                        break;
                    case 'r':
                        out.append('\r');
                        break;
                    case 't':
                        out.append('\t');
                        break;
                    case '\"':
                    case '\'':
                    case '\\':
                        out.append(ch);
                        break;
                    case 'u':
                        if (l < i + 5) {
                            out.append('?');
                        } else {
                            String n = tmp.substring(i + 1, i + 5);
                            try {
                                int cp = Integer.parseInt(n, 16);
                                out.append((char) cp);
                            } catch (NumberFormatException e) {
                                out.append('?');
                            }
                        }
                        i += 4;  // skipping 4 more characters.
                        break;
                    default:
                        out.append('?');
                        break;
                }
            } else if (tmp.charAt(i) == '\\') {
                esc = true;
            } else {
                out.append(tmp.charAt(i));
            }
        }
        return out.toString();
    }

    @Override
    public String toString() {
        return String.format("Token('%s',%d:%d-%d)", asString(), lineNo, linePos, linePos + len);
    }
}
