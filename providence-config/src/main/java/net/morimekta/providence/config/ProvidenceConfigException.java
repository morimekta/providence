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
package net.morimekta.providence.config;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.pretty.TokenizerException;

/**
 * Providence config exceptions are extensions of the serializer exception (as
 * parsing config can be seen as parsing or de-serializing any serialized
 * message).
 */
public class ProvidenceConfigException extends TokenizerException {
    public ProvidenceConfigException(String format, Object... args) {
        super(format, args);
    }

    public ProvidenceConfigException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    public ProvidenceConfigException(SerializerException cause) {
        super(cause.getMessage());
        setExceptionType(cause.getExceptionType());
        initCause(cause);
    }

    public ProvidenceConfigException(TokenizerException cause) {
        super(cause, null);
        setFile(cause.getFile());
    }

    @Override
    public ProvidenceConfigException setFile(String file) {
        return (ProvidenceConfigException) super.setFile(file);
    }

    @Override
    public ProvidenceConfigException setLength(int len) {
        return (ProvidenceConfigException) super.setLength(len);
    }

    @Override
    public ProvidenceConfigException setLine(String line) {
        return (ProvidenceConfigException) super.setLine(line);
    }

    @Override
    public ProvidenceConfigException setLineNo(int lineNo) {
        return (ProvidenceConfigException) super.setLineNo(lineNo);
    }

    @Override
    public ProvidenceConfigException setLinePos(int linePos) {
        return (ProvidenceConfigException) super.setLinePos(linePos);
    }
}
