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

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;

import com.google.common.base.MoreObjects;

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
        super(e);
        setLine(e.getLine());
        setLineNo(e.getLineNo());
        setLinePos(e.getLinePos());
        // Keep the specified file, if there is one.
        if (e.getFile() == null && file != null) {
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

    @Override
    public String asString() {
        if (lineNo > 0) {
            String fileSpec = "";
            if (file != null) {
                fileSpec = " in " + file;
            }
            if (line != null) {
                return String.format("Error%s on line %d, pos %d:%n" +
                                     "    %s%n" +
                                     "%s%n" +
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

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(getClass())
                          .omitNullValues()
                          .addValue(getMessage())
                          .add("file", file);
        if (lineNo > 0) {
            helper.add("lineNo", lineNo);
            helper.add("linePos", linePos);
        }
        return helper.toString();
    }
}
