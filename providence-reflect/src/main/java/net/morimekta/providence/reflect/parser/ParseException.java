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
package net.morimekta.providence.reflect.parser;

import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.util.Locale;

/**
 * Token specialization for the thrift parser and tokenizer.
 */
public class ParseException extends TokenizerException {
    public ParseException(Throwable cause, String message, Object... params) {
        super(cause, String.format(Locale.US, message, params));
    }

    public ParseException(String message, Object... params) {
        super(String.format(Locale.US, message, params));
    }
}
