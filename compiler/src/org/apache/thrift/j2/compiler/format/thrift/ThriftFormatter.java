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

package org.apache.thrift.j2.compiler.format.thrift;

import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TContainer;
import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TServiceDescriptor;
import org.apache.thrift.j2.descriptor.TServiceMethod;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;
import org.apache.thrift.j2.util.TBase64Utils;
import org.apache.thrift.j2.util.io.IndentedPrintWriter;
import org.apache.thrift.j2.util.json.JsonException;
import org.apache.thrift.j2.util.json.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Pretty printer for types. Generates content as close to the real thrift files
 * as possible.
 *
 * @author Stein Eldar Johnsen
 * @since 04.09.15
 */
public class ThriftFormatter {
    private static final String BLOCK_COMMENT_START = "/**";
    private static final String BLOCK_COMMENT_LINE  = " * ";
    private static final String BLOCK_COMMENT_END   = " */";

    private static final String REQUIRED = "required";

    private final EnumValuePresence mEnumValuePresence;

    public enum EnumValuePresence {
        ALWAYS,
        FIRST,
        NON_DEFAULT
    }

    public ThriftFormatter() {
        this(EnumValuePresence.NON_DEFAULT);
    }

    public ThriftFormatter(EnumValuePresence presence) {
        mEnumValuePresence = presence;
    }

    public void format(OutputStream out, TContainedDocument document) {
        IndentedPrintWriter writer = new IndentedPrintWriter(out);
        try {
            appendDocument(writer, document);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to stream", e);
        } catch (JsonException e) {
            throw new IllegalStateException("Unable to write json constant", e);
        }
    }

    private void appendDocument(IndentedPrintWriter builder, TContainedDocument document) throws IOException, JsonException {
        boolean first = true;
        if (document.getComment() != null) {
            appendBlockComment(builder, document.getComment(), first);
            first = false;
        }
        if (document.getNamespaces().size() > 0) {
            for (Entry<String, String> namespace : document.getNamespaces().entrySet()) {
                if (first) {
                    builder.format("namespace %s %s",
                                   namespace.getKey(), namespace.getValue());
                    first = false;
                } else {
                    builder.formatln("namespace %s %s",
                                     namespace.getKey(), namespace.getValue());
                }
            }
            builder.newline();
        }
        // The file must have some namespace... So first checking is no longer needed.

        if (document.getIncludes().size() > 0) {
            for (String include : document.getIncludes()) {
                builder.formatln("include \"%s.thrift\"", include);
            }
            builder.newline();
        }

        builder.newline(); // one extra appendln between header and
        // declaration.

        for (TDeclaredDescriptor<?> type : document.getDeclaredTypes()) {
            switch (type.getType()) {
                case ENUM:
                    appendEnum(builder, (TEnumDescriptor<?>) type);
                    break;
                case MESSAGE:
                    appendStruct(builder, (TStructDescriptor<?>) type);
                    break;
                default:
                    throw new IllegalStateException("Document " +
                                                    document.getPackageName() +
                                                    ".thrift contains invalid declared type " +
                                                    type.getName());
            }
            builder.newline();
        }

        for (TServiceDescriptor service : document.getServices()) {
            appendService(builder, document, service);
        }

        for (TField<?> constant : document.getConstants()) {
            builder.formatln("const %s %s = ",
                             constant.getDescriptor().getQualifiedName(document.getPackageName()),
                             constant.getName());
            appendTypedValue(builder,
                             constant.getDefaultValue(),
                             constant.getDescriptor(),
                             document.getPackageName());
            // represent the actual value...
            builder.newline();
        }
    }

    private void appendBlockComment(IndentedPrintWriter builder, String comment, boolean first) throws IOException {
        String[] lines = comment.split("\n");
        if (first) {
            builder.append(BLOCK_COMMENT_START);
        } else {
            builder.appendln(BLOCK_COMMENT_START);
        }
        if (lines.length == 1) {
            builder.append(' ')
                   .append(comment)
                   .append(BLOCK_COMMENT_END);
        } else {
            for (String line : lines) {
                builder.appendln(BLOCK_COMMENT_LINE)
                       .append(line);
            }
            builder.appendln(BLOCK_COMMENT_END);
        }
    }

    // --- Declared Types

    private void appendStruct(IndentedPrintWriter builder, TStructDescriptor<?> type) throws IOException, JsonException {
        if (type.getComment() != null) {
            appendBlockComment(builder, type.getComment(), false);
        }
        builder.formatln("%s %s {", type.getVariant().getName(), type.getName())
               .begin();
        for (TField<?> field : type.getFields()) {
            if (field.getComment() != null) {
                appendBlockComment(builder, field.getComment(), false);
            }
            builder.formatln("%d: ", field.getKey());
            if (field.getRequired()) {
                builder.format("%s ", REQUIRED);
            }
            builder.format("%s %s",
                           field.getDescriptor().getQualifiedName(type.getPackageName()),
                           field.getName());
            if (field.getDefaultValue() != null) {
                builder.append(" = ");
                appendTypedValue(builder,
                                 field.getDefaultValue(),
                                 field.getDescriptor(),
                                 field.getDescriptor().getPackageName());
            }
            builder.append(';');
        }
        builder.end()
               .appendln('}');
    }

    private void appendEnum(IndentedPrintWriter builder, TEnumDescriptor<?> type) throws IOException {
        if (type.getComment() != null) {
            appendBlockComment(builder, type.getComment(), false);
        }
        builder.formatln("enum %s {", type.getName())
               .begin();
        int nextValue = mEnumValuePresence.equals(EnumValuePresence.FIRST) ? -1 : TEnumDescriptor.DEFAULT_FIRST_VALUE;
        for (TEnumValue<?> value : type.getValues()) {
            if (value.getComment() != null) {
                appendBlockComment(builder, value.getComment(), false);
            }
            builder.appendln(value.getName());
            if (value.getValue() != nextValue || mEnumValuePresence.equals(EnumValuePresence.ALWAYS)) {
                builder.format(" = %d", value.getValue());
                nextValue = value.getValue() + 1;
            } else {
                ++nextValue;
            }
            builder.append(';');
        }
        builder.end()
               .appendln('}');
    }

    // --- Services

    private void appendService(IndentedPrintWriter writer, TContainedDocument doc, TServiceDescriptor service) throws IOException {
        writer.formatln("service %s {", service.getName())
              .begin();

        boolean firstMethod = true;
        for (TServiceMethod<?,?,?> method : service.getMethods()) {
            if (firstMethod) firstMethod = false;
            else writer.newline();

            if (method.getComment() != null) {
                appendBlockComment(writer, service.getComment(), false);
            }
            writer.appendln("");
            if (method.isOneway()) {
                writer.append("oneway ");
            }
            if (method.getReturnType() != null) {
                writer.append(method.getReturnType().getQualifiedName(doc.getPackageName()));
            } else {
                writer.append("void");
            }
            writer.format(" %s(", method.getName());
            TStructDescriptor<?> params = method.getParamsDescriptor();
            if (params != null) {
                boolean first = true;
                for (TField<?> field : params.getFields()) {
                    if (first)
                        first = false;
                    else
                        writer.append(", ");
                    writer.format("%d: %s %s",
                                  field.getKey(),
                                  field.getDescriptor().getQualifiedName(doc.getPackageName()),
                                  field.getName());
                }
            }
            writer.append(')');

            TStructDescriptor<?> exceptions = method.getExceptionDescriptor();
            if (exceptions != null) {
                writer.append(" throws (");
                boolean first = true;
                for (TField<?> field : exceptions.getFields()) {
                    if (first) first = false;
                    else writer.append(", ");
                    writer.format("%d: %s %s",
                                  field.getKey(),
                                  field.getDescriptor().getQualifiedName(doc.getPackageName()),
                                  field.getName());
                }
                writer.append(')');
            }
            writer.append(';');
        }

        writer.end()
              .appendln("}")
              .newline();
    }

    // --- Constant values.

    /**
     *
     * @param writer
     * @param value
     * @param type
     * @param packageContext
     * @throws JsonException
     */
    protected void appendTypedValue(IndentedPrintWriter writer,
                                    Object value,
                                    TDescriptor type,
                                    String packageContext) throws IOException, JsonException {
        switch (type.getType()) {
            case ENUM:
                writer.format("%s.%s", type.getQualifiedName(packageContext), value.toString());
                break;
            case LIST:
            case SET:
                TContainer<?, ?> cType = (TContainer<?, ?>) type;
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                writer.append('[')
                      .begin();
                boolean first = true;
                for (Object item : collection) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',');
                    }
                    writer.appendln("");
                    appendTypedValue(writer,
                                     item,
                                     cType.itemDescriptor(),
                                     packageContext);
                }
                writer.end()
                      .appendln(']');
                break;
            case MAP:
                TMap<?, ?> mType = (TMap<?, ?>) type;
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                writer.append('{').begin();
                first = true;
                for (Entry<Object, Object> entry : map.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',');
                    }
                    writer.appendln("");
                    appendMapKey(writer, entry.getKey());
                    writer.append(" : ");
                    appendTypedValue(writer,
                                     entry.getValue(),
                                     mType.itemDescriptor(),
                                     packageContext);
                }
                writer.end()
                      .appendln('}');
                break;
            case MESSAGE:
                appendMessage(writer, (TMessage<?>) value, packageContext);
                break;
            default:
                appendPrimitive(writer, value);
                break;
        }
    }

    private void appendMapKey(IndentedPrintWriter writer, Object value) throws IOException, JsonException {
        if (value instanceof TEnumValue<?>) {
            TEnumValue<?> ev = (TEnumValue<?>) value;
            writer.append('\"')
                  .append(ev.getDescriptor().getName()).append('.').append(ev.toString())
                  .append('\"');
        } else if (value instanceof String) {
            JsonWriter json = new JsonWriter(writer, "");
            json.value(value);
            json.flush();
        } else if (value instanceof Boolean ||
                   value instanceof Double ||
                   value instanceof Byte ||
                   value instanceof Short ||
                   value instanceof Long) {
            writer.append('\"');
            JsonWriter json = new JsonWriter(writer, "");
            json.value(value);
            json.flush();
            writer.append('\"');
        } else if (value instanceof byte[]) {
            JsonWriter json = new JsonWriter(writer, "");
            json.value(TBase64Utils.encode((byte[]) value));
            json.flush();
        } else {
            throw new IllegalArgumentException("No such primitive value type: " +
                    value.getClass().getSimpleName());
        }
    }

    private void appendPrimitive(IndentedPrintWriter writer, Object value) throws IOException, JsonException {
        JsonWriter json = new JsonWriter(writer, "");
        json.value(value);
        json.flush();
    }

    private void appendMessage(IndentedPrintWriter writer, TMessage<?> message, String packageContext) throws IOException, JsonException {
        boolean first = true;

        writer.append('{')
              .begin();
        for (TField<?> field : message.getDescriptor().getFields()) {
            if (message.has(field.getKey())) {
                if (first) first = false;
                else writer.append(',');

                writer.formatln("\"%s\": ", field.getName());
                appendTypedValue(writer,
                                 message.get(field.getKey()),
                                 field.getDescriptor(),
                                 packageContext);
            }
        }
        writer.end()
              .appendln('}');
    }
}
