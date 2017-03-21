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
 */package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.io.IndentedPrintWriter;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonWriter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class ValueBuilder {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public ValueBuilder(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    public void appendDefaultConstants(List<JField> fields) throws GeneratorException {
        boolean hasDefault = false;
        for (JField field : fields) {
            if (field.hasDefault() || field.isPrimitiveJavaValue()) {
                Object defaultValue = helper.getDefaultValue(field.field());
                if (defaultValue != null) {
                    hasDefault = true;
                    writer.formatln("private final static %s %s = ", field.valueType(), field.kDefault())
                          .begin(IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT);
                    appendTypedValue(defaultValue,
                                     field.field()
                                          .getDescriptor());
                    writer.append(';')
                          .end();
                }
            }
        }

        if (hasDefault) {
            writer.newline();
        }
    }

    public void appendTypedValue(Object value, PDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                writer.append(value.toString());
                break;
            case BYTE:
                writer.append("(byte)")
                      .append(value.toString());
                break;
            case I16:
                writer.append("(short)")
                      .append(value.toString());
                break;
            case I32:
                writer.append(value.toString());
                break;
            case I64:
                writer.append(value.toString())
                      .append("L");
                break;
            case DOUBLE:
                writer.append(value.toString())
                      .append("d");
                break;
            case BINARY:
                if (value == null) {
                    writer.format("null");
                    break;
                }

                writer.append("Binary.wrap(new byte[]{");
                byte[] bytes = ((Binary) value).get();
                boolean first = true;
                for (byte b : bytes) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',');
                    }
                    writer.format("(byte)%d", b);
                }
                writer.append("})");
                break;
            case STRING:
                if (value == null) {
                    writer.format("null");
                    break;
                }

                try {
                    JsonWriter json = new JsonWriter(writer);
                    json.value(value.toString());
                    json.flush();
                } catch (JsonException je) {
                    throw new GeneratorException("Unable to appendEnumClass string value");
                }
                break;
            case ENUM:
                writer.format("%s.%s", helper.getValueType(type), JUtils.enumConst((PEnumValue) value));
                break;
            case MESSAGE: {
                if (value == null) {
                    writer.format("null");
                    break;
                }
                writer.format("%s.builder()", helper.getFieldType(type))
                      .begin();
                PMessage message = (PMessage) value;
                int i = 0;
                for (CField field : ((CMessageDescriptor) type).getFields()) {
                    JField fld = new JField(field, helper, i++);
                    if (message.has(field.getKey())) {
                        writer.formatln(".%s(", fld.setter());
                        appendTypedValue(message.get(field.getKey()), field.getDescriptor());
                        writer.append(")");
                    }
                }
                writer.appendln(".build()")
                      .end();
                break;
            }
            case MAP: {
                PMap<?,?> lDesc = (PMap<?,?>) type;
                PDescriptor keyDesc = lDesc.keyDescriptor();
                PDescriptor itemDesc = lDesc.itemDescriptor();

                JField constant = new JField(new CConst("", "", () -> type, () -> value, null), helper, 0);

                writer.format("new %s<%s,%s>()",
                                constant.builderInstanceType(),
                                helper.getFieldType(keyDesc),
                                helper.getFieldType(itemDesc))
                      .begin();

                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                for (Map.Entry<Object,Object> entry : map.entrySet()) {
                    writer.appendln(".put(")
                          .begin("     ");

                    appendTypedValue(entry.getKey(), keyDesc);

                    writer.append(",")
                          .appendln();

                    appendTypedValue(entry.getValue(), itemDesc);

                    writer.end()
                          .append(")");
                }

                writer.formatln(".build()")
                      .end();
                break;
            }
            case LIST:
            case SET: {
                PContainer<?> lDesc = (PContainer<?>) type;
                PDescriptor itemDesc = lDesc.itemDescriptor();

                JField constant = new JField(new CConst("", "", () -> type, () -> value, null), helper, 0);

                writer.format("new %s<%s>()", constant.builderInstanceType(), helper.getFieldType(itemDesc))
                      .begin();

                @SuppressWarnings("unchecked")
                Collection<Object> items = (Collection<Object>) value;
                for (Object item : items) {
                    writer.appendln(".add(")
                          .begin("     ");

                    appendTypedValue(item, itemDesc);

                    writer.end()
                          .append(")");
                }

                writer.formatln(".build()")
                      .end();
                break;
            }
            default:
                throw new GeneratorException("Unhandled value type: " + type.getType());
        }
    }
}
