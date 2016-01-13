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

package net.morimekta.providence.compiler.format.thrift;

import net.morimekta.providence.Binary;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.util.io.IndentedPrintWriter;
import net.morimekta.providence.util.json.JsonException;
import net.morimekta.providence.util.json.JsonWriter;

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

    public void format(OutputStream out, CDocument document) {
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

    private void appendDocument(IndentedPrintWriter builder, CDocument document) throws IOException, JsonException {
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

        for (PDeclaredDescriptor<?> type : document.getDeclaredTypes()) {
            switch (type.getType()) {
                case ENUM:
                    appendEnum(builder, (PEnumDescriptor<?>) type);
                    break;
                case MESSAGE:
                    appendStruct(builder, (PStructDescriptor<?,?>) type);
                    break;
                default:
                    throw new IllegalStateException("Document " +
                                                    document.getPackageName() +
                                                    ".thrift contains invalid declared type " +
                                                    type.getName());
            }
            builder.newline();
        }

        for (PField<?> constant : document.getConstants()) {
            builder.formatln("const %s %s = ",
                             constant.getDescriptor().getQualifiedName(document.getPackageName()),
                             constant.getName());
            JsonWriter json = new JsonWriter(builder);
            appendTypedValue(json,
                             constant.getDefaultValue(),
                             constant.getDescriptor(),
                             document.getPackageName());
            json.flush();
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

    private void appendStruct(IndentedPrintWriter builder, PStructDescriptor<?,?> type) throws IOException, JsonException {
        if (type.getComment() != null) {
            appendBlockComment(builder, type.getComment(), false);
        }
        builder.formatln("%s %s {", type.getVariant().getName(), type.getName())
               .begin();
        for (PField<?> field : type.getFields()) {
            if (field.getComment() != null) {
                appendBlockComment(builder, field.getComment(), false);
            }
            builder.formatln("%d: ", field.getKey());
            if (field.getRequirement() != PRequirement.DEFAULT) {
                builder.format("%s ", field.getRequirement().label);
            }
            builder.format("%s %s",
                           field.getDescriptor().getQualifiedName(type.getPackageName()),
                           field.getName());
            if (field.getDefaultValue() != null) {
                builder.append(" = ");
                JsonWriter json = new JsonWriter(builder);
                appendTypedValue(json,
                                 field.getDefaultValue(),
                                 field.getDescriptor(),
                                 field.getDescriptor().getPackageName());
                json.flush();
            }
            builder.append(';');
        }
        builder.end()
               .appendln('}');
    }

    private void appendEnum(IndentedPrintWriter builder, PEnumDescriptor<?> type) throws IOException {
        if (type.getComment() != null) {
            appendBlockComment(builder, type.getComment(), false);
        }
        builder.formatln("enum %s {", type.getName())
               .begin();
        int nextValue = mEnumValuePresence.equals(EnumValuePresence.FIRST) ? -1 : PEnumDescriptor.DEFAULT_FIRST_VALUE;
        for (PEnumValue<?> value : type.getValues()) {
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

    // --- Constant values.

    /**
     *
     * @param writer
     * @param value
     * @param type
     * @param packageContext
     * @throws JsonException
     */
    protected void appendTypedValue(JsonWriter writer,
                                    Object value,
                                    PDescriptor type,
                                    String packageContext) throws IOException, JsonException {
        switch (type.getType()) {
            case ENUM:
                writer.valueLiteral(String.format("%s.%s", type.getQualifiedName(packageContext), value.toString()));
                break;
            case LIST:
            case SET:
                PContainer<?, ?> cType = (PContainer<?, ?>) type;
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;

                writer.array();
                for (Object item : collection) {
                    appendTypedValue(writer,
                                     item,
                                     cType.itemDescriptor(),
                                     packageContext);
                }
                writer.endArray();
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                writer.object();
                for (Entry<Object, Object> entry : map.entrySet()) {
                    appendMapKey(writer, entry.getKey());
                    appendTypedValue(writer,
                                     entry.getValue(),
                                     mType.itemDescriptor(),
                                     packageContext);
                }
                writer.endObject();
                break;
            case MESSAGE:
                appendMessage(writer, (PMessage<?>) value, packageContext);
                break;
            default:
                appendPrimitive(writer, value);
                break;
        }
    }

    private void appendMapKey(JsonWriter writer, Object key) throws IOException, JsonException {
        if (key instanceof PEnumValue<?>) {
            PEnumValue<?> ev = (PEnumValue<?>) key;
            writer.keyLiteral(String.format(
                    "%s.%s",
                    ev.descriptor().getName(),
                    ev.toString()));
        } else if (key instanceof Boolean) {
            writer.key((Boolean) key);
        } else if (key instanceof Byte) {
            writer.key((Byte) key);
        } else if (key instanceof Short) {
            writer.key((Short) key);
        } else if (key instanceof Integer) {
            writer.key((Integer) key);
        } else if (key instanceof Long) {
            writer.key((Long) key);
        } else if (key instanceof Double) {
            writer.key((Double) key);
        } else if (key instanceof String) {
            writer.key((String) key);
        } else if (key instanceof Binary) {
            writer.key((Binary) key);
        } else {
            throw new IllegalArgumentException("No such primitive value type: " +
                    key.getClass().getSimpleName());
        }
    }

    private void appendPrimitive(JsonWriter writer, Object value) throws IOException, JsonException {
        if (value instanceof PEnumValue<?>) {
            PEnumValue<?> ev = (PEnumValue<?>) value;
            writer.valueLiteral(String.format(
                    "%s.%s",
                    ev.descriptor().getName(),
                    ev.toString()));
        } else if (value instanceof Boolean) {
            writer.value((Boolean) value);
        } else if (value instanceof Byte) {
            writer.value((Byte) value);
        } else if (value instanceof Short) {
            writer.value((Short) value);
        } else if (value instanceof Integer) {
            writer.value((Integer) value);
        } else if (value instanceof Long) {
            writer.value((Long) value);
        } else if (value instanceof Double) {
            writer.value((Double) value);
        } else if (value instanceof String) {
            writer.value((String) value);
        } else if (value instanceof Binary) {
            writer.value((Binary) value);
        } else {
            throw new IllegalArgumentException("No such primitive value type: " +
                                               value.getClass().getSimpleName());
        }
    }

    private void appendMessage(JsonWriter writer, PMessage<?> message, String packageContext) throws IOException, JsonException {
        writer.object();
        for (PField<?> field : message.descriptor().getFields()) {
            if (message.has(field.getKey())) {
                writer.key(field.getName());
                appendTypedValue(writer,
                                 message.get(field.getKey()),
                                 field.getDescriptor(),
                                 packageContext);
            }
        }
        writer.endObject();
    }
}
