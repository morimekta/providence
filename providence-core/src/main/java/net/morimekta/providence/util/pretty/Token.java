/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.util.pretty;

import net.morimekta.util.Slice;
import net.morimekta.util.Strings;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Pretty token.
 */
public class Token extends Slice {
    // Various symbols.
    public static final char kMessageStart  = '{';
    public static final char kMessageEnd    = '}';
    public static final char kKeyValueSep   = ':';
    public static final char kFieldValueSep = '=';
    public static final char kMethodStart   = '(';
    public static final char kMethodEnd     = ')';
    public static final char kListStart     = '[';
    public static final char kListEnd       = ']';
    public static final char kLineSep1      = ',';
    public static final char kLineSep2      = ';';

    // Not really 'symbols'.
    public static final char kIdentifierSep      = '.';
    public static final char kLiteralEscape      = '\\';
    public static final char kLiteralQuote       = '\'';
    public static final char kLiteralDoubleQuote = '\"';
    public static final char kShellComment       = '#';

    public static final String B64 = "b64";
    public static final String HEX = "hex";

    public static final String kSymbols = "{}:=()<>,;#[]&/%$@^";

    private static final Pattern RE_IDENTIFIER                  = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern RE_QUALIFIED_IDENTIFIER        = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*[.][_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern RE_DOUBLE_QUALIFIED_IDENTIFIER = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*[.][_a-zA-Z][_a-zA-Z0-9]*[.][_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern RE_REFERENCE_IDENTIFIER        = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*([.][_a-zA-Z][_a-zA-Z0-9]*)*");
    private static final Pattern RE_INTEGER                     = Pattern.compile("-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");
    private static final Pattern RE_REAL                        = Pattern.compile("-?(0?\\.[0-9]+|[1-9][0-9]*\\.[0-9]*)([eE][+-]?[0-9][0-9]*)?");

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
        return Objects.hash(Token.class, fb, off, len, lineNo, linePos);
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getLinePos() {
        return linePos;
    }

    public boolean isSymbol(char symbol) {
        return len == 1 && fb[off] == symbol;
    }

    public boolean isStringLiteral() {
        return (length() > 1 && charAt(0) == '\"' && charAt(-1) == '\"');
    }

    public boolean isIdentifier() {
        return RE_IDENTIFIER.matcher(asString()).matches();
    }

    public boolean isQualifiedIdentifier() {
        return RE_QUALIFIED_IDENTIFIER.matcher(asString()).matches();
    }

    public boolean isDoubleQualifiedIdentifier() {
        return RE_DOUBLE_QUALIFIED_IDENTIFIER.matcher(asString()).matches();
    }

    public boolean isReferenceIdentifier() {
        return RE_REFERENCE_IDENTIFIER.matcher(asString()).matches();
    }

    public boolean isInteger() {
        return RE_INTEGER.matcher(asString())
                         .matches();
    }

    public boolean isReal() {
        return RE_REAL.matcher(asString())
                      .matches();
    }


    /**
     * Get the whole slice as a string.
     *
     * @return Slice decoded as UTF_8 string.
     */
    public String decodeLiteral() {
        return decodeLiteral(false);
    }


    /**
     * Get the whole slice as a string.
     *
     * @param strict If it should validate string content strictly.
     * @return Slice decoded as UTF_8 string.
     */
    public String decodeLiteral(boolean strict) {
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
                    case '0':
                    case '1':
                        if (l < (i + 3)) {
                            out.append('?');
                        } else {
                            String n = tmp.substring(i, i + 2);
                            try {
                                int cp = Integer.parseInt(n, 8);
                                out.append((char) cp);
                            } catch (NumberFormatException e) {
                                out.append('?');
                            }
                        }
                        i += 2;  // skipping 2 more characters.
                        break;
                    default:
                        if (strict) {
                            throw new IllegalArgumentException("Invalid escaped char: '\\" +
                                                               Strings.escape(String.valueOf(ch)) +
                                                               "'");
                        }
                        out.append('?');
                        break;
                }
            } else if (tmp.charAt(i) == '\\') {
                esc = true;
            } else if (!Strings.isConsolePrintable(tmp.codePointAt(i))) {
                if (strict) {
                    throw new IllegalArgumentException("Unescaped string char: '" +
                                                       Strings.escape(String.valueOf(tmp.charAt(i))) +
                                                       "'");
                }
                out.append('?');
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
