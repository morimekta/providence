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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

/**
 * Specialization of the 'pretty' tokenizer to make it handle some
 * special cases only applicable when parsing thrift files, but not
 * allowed in pretty format or config files.
 */
public class ThriftTokenizer extends Tokenizer {
    // Various thrift keywords.
    public static final String kNamespace = "namespace";
    public static final String kInclude = "include";
    public static final String kTypedef = "typedef";
    public static final String kEnum = "enum";
    public static final String kStruct = "struct";
    public static final String kUnion = "union";
    public static final String kException = "exception";
    public static final String kConst = "const";
    public static final String kService = "service";

    public static final String kExtends = "extends";
    public static final String kVoid = "void";
    public static final String kOneway = "oneway";
    public static final String kThrows = "throws";
    public static final String kRequired = "required";
    public static final String kOptional = "optional";

    // Not really 'symbols', but basically used as.
    public static final String kLineCommentStart  = "//";
    public static final String kBlockCommentStart = "/*";
    public static final String kBlockCommentEnd   = "*/";

    public ThriftTokenizer(InputStream in) throws IOException {
        super(in, false);
    }

    protected Token nextSymbol(int lastByte) throws TokenizerException {
        int startOffset = readOffset;
        int startLineNo = lineNo;
        int startLinePos = linePos;
        if (lastByte == '/') {
            int next = read();
            if (next < 0) {
                throw failure(startLineNo, startLinePos, 1,
                              "");
            }
            if (next == '/' || next == '*') {
                return token(startOffset, 2, startLinePos);
            }
            throw failure(startLineNo, startLinePos, 2,
                          "");
        }
        return super.nextSymbol(lastByte);
    }

    @Nonnull
    @Override
    protected TokenizerException failure(String format, Object... params) {
        return new ParseException(format, params);
    }
}
