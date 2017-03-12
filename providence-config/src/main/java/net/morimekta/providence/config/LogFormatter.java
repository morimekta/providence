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
package net.morimekta.providence.config;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Pretty printer that can print message content for easily reading and
 * debugging. This is a write only format used in stringifying messages.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class LogFormatter {
    public interface FieldHandler {
        boolean appendFieldValue(IndentedPrintWriter writer, PField field, Object value);
    }

    private final static String INDENT   = "  ";
    private final static String SPACE    = " ";
    private final static String NEWLINE  = "\n";
    private final static String LIST_SEP = ",";

    private final String             indent;
    private final String             space;
    private final String             newline;
    private final String             entrySep;
    private final List<FieldHandler> fieldHandlers;

    public LogFormatter(boolean pretty, FieldHandler... fieldHandlers) {
        this(pretty, ImmutableList.copyOf(fieldHandlers));
    }

    public LogFormatter(boolean pretty, Collection<FieldHandler> fieldHandlers) {
        this.indent = pretty ? INDENT : "";
        this.space = pretty ? SPACE : "";
        this.newline = pretty ? NEWLINE : "";
        this.entrySep = pretty ? "" : LIST_SEP;
        this.fieldHandlers = ImmutableList.copyOf(fieldHandlers);
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    void formatTo(OutputStream out, Message message) {
        IndentedPrintWriter builder = new IndentedPrintWriter(out, indent, newline);
        appendMessage(builder, message);
        builder.flush();
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    String format(Message message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        formatTo(out, message);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private void appendMessage(IndentedPrintWriter writer, PMessage<?,?> message) {
        PMessageDescriptor<?, ?> type = message.descriptor();

        writer.append(Token.kMessageStart)
              .begin();

        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                Object o = message.get(field.getKey());

                writer.appendln()
                      .append(field.getName())
                      .append(space)
                      .append(Token.kFieldValueSep)
                      .append(space);
                appendFieldValue(writer, field, o);
            }
        } else {
            boolean first = true;
            for (PField field : type.getFields()) {
                if (message.has(field.getKey())) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(entrySep);
                    }
                    Object o = message.get(field.getKey());

                    writer.appendln()
                          .append(field.getName())
                          .append(space)
                          .append(Token.kFieldValueSep)
                          .append(space);

                    appendFieldValue(writer, field, o);
                }
            }
        }

        writer.end()
              .appendln(Token.kMessageEnd);
    }

    void appendFieldValue(IndentedPrintWriter writer, PField field, Object value) {
        if (field.getType() != PType.MESSAGE) {
            for (FieldHandler handler : fieldHandlers) {
                if (handler.appendFieldValue(writer, field, value)) {
                    return;
                }
            }
        }
        appendTypedValue(writer, field.getDescriptor(), value);
    }

    private void appendTypedValue(IndentedPrintWriter writer, PDescriptor descriptor, Object o) {
        switch (descriptor.getType()) {
            case LIST:
            case SET: {
                PContainer<?> containerType = (PContainer<?>) descriptor;
                PDescriptor itemType = containerType.itemDescriptor();
                Collection<?> collection = (Collection<?>) o;

                PPrimitive primitive = PPrimitive.findByName(itemType.getName());
                if (primitive != null &&
                    primitive != PPrimitive.STRING &&
                    primitive != PPrimitive.BINARY &&
                    collection.size() <= 10) {
                    // special case if we have simple primitives (numbers and bools) in a "short" list,
                    // print in one single line.
                    writer.append(Token.kListStart);

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(Token.kLineSep1)
                                  .append(space);
                        }
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }
                    writer.append(Token.kListEnd);
                } else {
                    writer.append(Token.kListStart)
                          .begin();

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(Token.kLineSep1);
                        }
                        writer.appendln();
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }

                    writer.end()
                          .appendln(Token.kListEnd);
                }
                break;
            }
            case MAP: {
                PMap<?, ?> mapType = (PMap<?, ?>) descriptor;

                Map<?, ?> map = (Map<?, ?>) o;

                writer.append(Token.kMessageStart)
                      .begin();

                boolean first = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(entrySep);
                    }
                    writer.appendln();
                    appendTypedValue(writer, mapType.keyDescriptor(), entry.getKey());
                    writer.append(Token.kKeyValueSep)
                          .append(space);
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.end()
                      .appendln(Token.kMessageEnd);
                break;
            }
            case VOID:
                writer.print(true);
                break;
            case MESSAGE:
                PMessage<?,?> message = (PMessage<?, ?>) o;
                appendMessage(writer, message);
                break;
            default:
                appendPrimitive(writer, o);
                break;
        }
    }

    private void appendPrimitive(IndentedPrintWriter writer, Object o) {
        if (o instanceof PEnumValue) {
            writer.print(((PEnumValue) o).asString());
        } else if (o instanceof CharSequence) {
            writer.print(Token.kLiteralDoubleQuote);
            writer.print(Strings.escape((CharSequence) o));
            writer.print(Token.kLiteralDoubleQuote);
        } else if (o instanceof Binary) {
            Binary b = (Binary) o;
            writer.append(Token.B64)
                  .append(Token.kMethodStart)
                  .append(b.toBase64())
                  .append(Token.kMethodEnd);
        } else if (o instanceof Boolean) {
            writer.print(((Boolean) o).booleanValue());
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            writer.print(Strings.escape(o.toString()));
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d == ((double) d.longValue())) {
                // actually an integer or long value.
                writer.print(d.longValue());
            } else if (d > ((10 << 9) - 1) || (1 / d) > (10 << 6)) {
                // Scientific notation should be used, this enforces a decimal
                // length that is not too overwhelming.
                writer.print((new DecimalFormat("0.#########E0")).format(d.doubleValue()));
            } else {
                writer.print(d.doubleValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type class " + o.getClass()
                                                                                  .getSimpleName());
        }
    }
}
