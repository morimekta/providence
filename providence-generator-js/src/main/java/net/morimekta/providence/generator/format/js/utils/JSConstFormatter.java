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
package net.morimekta.providence.generator.format.js.utils;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.util.Binary;
import net.morimekta.util.io.IndentedPrintWriter;
import net.morimekta.util.json.JsonWriter;
import net.morimekta.util.json.PrettyJsonWriter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Build const values in javascript.
 */
public class JSConstFormatter {
    private final IndentedPrintWriter writer;
    private final JsonWriter json;

    public JSConstFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
        this.json = new PrettyJsonWriter(writer);
    }

    public void format(Object value) {
        if (value == null) {
            json.value((String) null);
        } else if (value instanceof PMessage) {
            PMessage message = (PMessage) value;
            try {
                CharArrayWriter literal = new CharArrayWriter();
                JsonWriter innerJson = new JsonWriter(new PrintWriter(literal));
                literal.write(String.format("new %s.%s(",
                                            message.descriptor()
                                                   .getProgramName(),
                                            JSUtils.getClassName(message.descriptor())));
                formatValue(innerJson, message);
                innerJson.flush();
                literal.write(")");

                json.valueLiteral(literal.toString());
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        } else if (value instanceof PEnumValue) {
            PEnumValue ev = (PEnumValue) value;
            json.valueLiteral(String.format("%s.%s.%s",
                                            ev.descriptor().getProgramName(),
                                            JSUtils.getClassName((PEnumDescriptor) ev.descriptor()),
                                            JSUtils.enumConst(ev)));
        } else if (value instanceof List || value instanceof Set){
            json.array();

            ((Collection<?>) value).forEach(this::format);

            json.endArray();
        } else if (value instanceof Map){
            json.object();

            ((Map<?,?>) value).forEach((key, item) -> {
                formatKey(json, key);
                format(item);
            });

            json.endObject();
        } else {
            formatValue(json, value);
        }
    }

    private void formatValue(JsonWriter json, Object value) {
        if (value == null || value instanceof String) {
            json.value((String) value);
        } else if (value instanceof Boolean) {
            json.value((boolean) value);
        } else if (value instanceof Byte) {
            json.value((byte) value);
        } else if (value instanceof Short) {
            json.value((short) value);
        } else if (value instanceof Integer) {
            json.value((int) value);
        } else if (value instanceof Long) {
            json.value((long) value);
        } else if (value instanceof Double) {
            json.value((double) value);
        } else if (value instanceof PEnumValue) {
            json.value(((PEnumValue)value).getId());
        } else if (value instanceof Binary) {
            json.value((Binary)value);
        } else if (value instanceof PMessage) {
            json.object();

            PMessage message = (PMessage) value;
            for (PField field : message.descriptor().getFields()) {
                if (message.has(field.getId())) {
                    json.key(field.getName());
                    formatValue(json, message.get(field.getId()));
                }
            }

            json.endObject();
        } else if (value instanceof List || value instanceof Set) {
            json.array();

            Collection list = (Collection) value;
            for (Object item : list) {
                format(item);
            }
            json.endArray();
        } else if (value instanceof Map) {
            json.object();

            Map<?,?> map = (Map) value;
            map.forEach((key, val) -> {
                formatKey(json, key);
                formatValue(json, val);
            });

            json.endObject();
        } else {
            throw new IllegalArgumentException("Bad type: " + value.getClass().getName());
        }
    }

    private void formatKey(JsonWriter json, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("no key");
        } else if (value instanceof Boolean) {
            json.key((boolean) value);
        } else if (value instanceof Byte) {
            json.key((byte) value);
        } else if (value instanceof Short) {
            json.key((short) value);
        } else if (value instanceof Integer) {
            json.key((int) value);
        } else if (value instanceof Long) {
            json.key((long) value);
        } else if (value instanceof Double) {
            json.key((double) value);
        } else if (value instanceof PEnumValue) {
            json.key(((PEnumValue)value).getId());
        } else if (value instanceof String) {
            json.key((String) value);
        } else if (value instanceof Binary) {
            json.key((Binary) value);
        } else {
            throw new IllegalArgumentException("Not supported in JS");
        }
    }
}
