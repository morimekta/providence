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
package net.morimekta.providence.serializer.pretty;

import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;

import com.google.common.base.MoreObjects;

import java.io.File;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Math.max;

/**
 * Exception when totalizing fails to make token or read the expected content.
 */
public class TokenizerException extends SerializerException {
    private int    lineNo;
    private int    linePos;
    private String line;
    private String file;
    private int    length;

    public TokenizerException(TokenizerException e, File file) {
        super(e);
        setLine(e.getLine());
        setLineNo(e.getLineNo());
        setLinePos(e.getLinePos());
        setLength(e.getLength());
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
        setLength(token.asString().length());
    }

    /**
     * @return The 1-indexed line number of the fault.
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * @return The 1-indexed position on the given line.
     */
    public int getLinePos() {
        return linePos;
    }

    /**
     * @return The number of u16 chars representing the fault.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return The whole line of the fault, not including line feed.
     */
    public String getLine() {
        return line;
    }

    /**
     * @return The file that contains the fault.
     */
    public String getFile() {
        return file;
    }

    public TokenizerException setLineNo(int lineNo) {
        this.lineNo = lineNo;
        return this;
    }

    public TokenizerException setLinePos(int linePos) {
        this.linePos = linePos;
        return this;
    }

    public TokenizerException setLength(int len) {
        this.length = len;
        return this;
    }

    public TokenizerException setLine(String line) {
        this.line = line;
        return this;
    }

    public TokenizerException setFile(String file) {
        this.file = file;
        return this;
    }

    @Override
    public TokenizerException initCause(Throwable cause) {
        return (TokenizerException) super.initCause(cause);
    }

    @Override
    public String asString() {
        if (lineNo > 0) {
            String fileSpec = "";
            if (file != null) {
                fileSpec = " in " + file;
            }
            if (line != null) {
                return String.format("Error%s on line %d, pos %d: %s%n" +
                                     "%s%n" +
                                     "%s%s",
                                     fileSpec,
                                     getLineNo(),
                                     getLinePos(),
                                     getMessage(),
                                     getLine(),
                                     Strings.times("-", linePos - 1),
                                     Strings.times("^", max(1, length)));
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
        if (getExceptionType() != PApplicationExceptionType.PROTOCOL_ERROR) {
            helper.add("e", getExceptionType());
        }
        if (!isNullOrEmpty(getLine())) {
            helper.add("line", lineNo);
            helper.add("pos", linePos);
            helper.add("len", length);
        }
        if (!isNullOrEmpty(getMethodName())) {
            helper.add("method", getMethodName());
            helper.add("type", getCallType());
            helper.add("seq", getSequenceNo());
        }
        return helper.toString();
    }
}
