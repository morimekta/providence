/*
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

package org.apache.thrift.j2.util;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.util.io.IndentedPrintWriter;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TContainer;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.json.JSONObject;

/**
 * Pretty printer that can print message content for easily reading
 * and debugging. This is a write only format used in stringifying
 * messages.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TPrettyPrinter {
    private final static String INDENT  = "  ";
    private final static String SPACE   = " ";
    private final static String NEWLINE = "\n";
    private final static String SEP     = ",";

    private final String mIndent;
    private final String mSpace;
    private final String mNewline;
    private final String mSep;

    public TPrettyPrinter() {
        this(INDENT, SPACE, NEWLINE, SEP);
    }

    public TPrettyPrinter(String indent,
                          String space,
                          String newline) {
        this(indent, space, newline, SEP);
    }

    public TPrettyPrinter(String indent,
                          String space,
                          String newline,
                          String sep) {
        mIndent = indent;
        mSpace = space;
        mNewline = newline;
        mSep = sep;
    }

    public String format(TMessage<?> message) {
        StringWriter stringWriter = new StringWriter();
        IndentedPrintWriter builder = new IndentedPrintWriter(stringWriter, mIndent, mNewline);
        try {
            appendMessage(builder, message);
            builder.flush();
        } finally {
            builder.close();
        }
        return stringWriter.toString();
    }

    private void appendMessage(IndentedPrintWriter builder, TMessage<?> message) {
        TStructDescriptor<?> type = message.descriptor();

        builder.append("{")
               .begin();

        boolean first = true;
        for (TField<?> field : type.getFields()) {
            if (message.has(field.getKey())) {
                if (first) first = false;
                else builder.append(mSep);
                Object o = message.get(field.getKey());

                builder.appendln(field.getName())
                       .append(":")
                       .append(mSpace);
                appendTypedValue(builder, field.descriptor(), o);
            }
        }

        builder.end()
               .appendln("}");
    }

    private void appendTypedValue(IndentedPrintWriter writer, TDescriptor descriptor, Object o) {
        switch (descriptor.getType()) {
            case LIST:
            case SET:
                writer.append("[")
                      .begin();

                TContainer<?, ?> containerType = (TContainer<?, ?>) descriptor;
                Collection<?> collection = (Collection<?>) o;

                boolean first = true;
                for (Object i : collection) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',');
                    }
                    writer.appendln();
                    appendTypedValue(writer, containerType.itemDescriptor(), i);
                }

                writer.end()
                      .appendln("]");
                break;
            case MAP:
                TMap<?, ?> mapType = (TMap<?, ?>) descriptor;

                Map<?, ?> map = (Map<?, ?>) o;

                writer.append("{")
                      .begin();

                first = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',');
                    }
                    writer.appendln();
                    appendPrimitive(writer, entry.getKey());
                    writer.append(":")
                          .append(mSpace);
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.end()
                      .appendln("}");
                break;
            case MESSAGE:
                TMessage<?> message = (TMessage<?>) o;
                appendMessage(writer, message);
                break;
            default:
                appendPrimitive(writer, o);
                break;
        }
    }

    private void appendPrimitive(IndentedPrintWriter writer, Object o) {
        if (o instanceof TEnumValue) {
            writer.print(o.toString());
        } else if (o instanceof String) {
            writer.print(JSONObject.quote((String) o));
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[]) o;
            writer.format("b64(%s)", TBase64Utils.encode(bytes));
        } else if (o instanceof Boolean) {
            writer.print(((Boolean) o).booleanValue());
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            writer.print(o.toString());
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d == ((double) d.longValue())) {
                // actually an integer or long value.
                writer.print(d.longValue());
            } else if (d > ((10 << 9) - 1) || (1 / d) > (10 << 6)) {
                // Scientific notation should be used.
                writer.print((new DecimalFormat("0.#########E0")).format(d.doubleValue()));
            } else {
                writer.print(d.doubleValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type class " + o.getClass().getSimpleName());
        }
    }
}
