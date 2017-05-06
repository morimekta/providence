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
package net.morimekta.providence.serializer;

import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.util.Strings;
import net.morimekta.util.json.JsonException;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Wrapper for a JsonException into a SerializerException.
 */
public class JsonSerializerException extends SerializerException {
    private final static long serialVersionUID = 1493883783445793582L;

    public JsonSerializerException(JsonException e) {
        super(e, e.getMessage());
    }

    @Override
    public synchronized JsonException getCause() {
        return (JsonException) super.getCause();
    }

    public String getLine() {
        return getCause().getLine();
    }

    public int getLineNo() {
        return getCause().getLineNo();
    }

    public int getLinePos() {
        return getCause().getLinePos();
    }

    public int getLen() {
        return getCause().getLen();
    }

    @Override
    public synchronized Throwable initCause(Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(getClass())
                                                    .omitNullValues()
                                                    .addValue(getMessage());
        if (getExceptionType() != PApplicationExceptionType.PROTOCOL_ERROR) {
            tsh.add("e", getExceptionType());
        }
        if (!isNullOrEmpty(getLine())) {
            tsh.add("line", getLineNo())
               .add("pos", getLinePos());
        }
        if (!isNullOrEmpty(getMethodName())) {
            tsh.add("method", getMethodName())
               .add("type", getCallType())
               .add("seq", getSequenceNo());
        }
        return tsh.toString();
    }

    @Override
    public String asString() {
        if (getLine() != null) {
            return String.format("JSON Error%s on line %d: %s%n" +
                                 "# %s%n" +
                                 "#%s%s",
                                 getMethodName().isEmpty() ? "" : " in " + getMethodName(),
                                 getLineNo(),
                                 getLocalizedMessage(),
                                 getLine(),
                                 Strings.times("-", getLinePos()),
                                 Strings.times("^", getLen()));
        } else {
            return String.format("JSON Error: %s", getLocalizedMessage());
        }
    }
}
