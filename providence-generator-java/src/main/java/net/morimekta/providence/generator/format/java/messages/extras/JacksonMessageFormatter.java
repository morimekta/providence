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
package net.morimekta.providence.generator.format.java.messages.extras;

import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JacksonMessageFormatter implements MessageMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;
    private final AtomicInteger       atomicInteger;

    public JacksonMessageFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.atomicInteger = new AtomicInteger();
        this.writer = writer;
        this.helper = helper;
    }

    private String tmp(String name) {
        return name + atomicInteger.incrementAndGet();
    }

    public void appendClassAnnotations(JMessage<?> message) {
        writer.formatln("@%s(", JsonSerialize.class.getName())
              .formatln("        using = %s._Serializer.class)", message.instanceType());
        writer.formatln("@%s(", JsonDeserialize.class.getName())
              .formatln("        using = %s._Deserializer.class)", message.instanceType());
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendJacksonDeserializer(message);
        appendJacksonSerializer(message);
    }

    private void appendJsonType(String varName, PDescriptor descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case MAP: {
                String kType = tmp("k");
                String vType = tmp("v");

                PMap map = (PMap) descriptor;
                appendJsonType(kType, map.keyDescriptor());
                appendJsonType(vType, map.itemDescriptor());
                writer.formatln("%s %s = ctxt.getTypeFactory().constructMapType(%s.class, %s, %s);",
                                MapType.class.getName(),
                                varName,
                                LinkedHashMap.class.getName(),
                                kType, vType);
                break;
            }
            case LIST:
            case SET: {
                String iType = tmp("i");
                PContainer container = (PContainer) descriptor;
                appendJsonType(iType, container.itemDescriptor());
                writer.formatln("%s %s = ctxt.getTypeFactory().constructCollectionType(%s.class, %s);",
                                CollectionType.class.getName(),
                                varName,
                                ArrayList.class.getName(),
                                iType);
                break;
            }
            default: {
                writer.formatln("%s %s = ctxt.getTypeFactory().constructSimpleType(%s.class, null);",
                                JavaType.class.getName(),
                                varName,
                                helper.getFieldType(descriptor));
                break;
            }
        }
    }

    private void appendReadValue(JField field) throws GeneratorException {
        switch (field.type()) {
            case MAP:
            case SET:
            case LIST: {
                String cType = tmp("c");
                appendJsonType(cType, field.field().getDescriptor());
                writer.formatln("builder.%s(ctxt.readValue(jp, %s));", field.setter(), cType);
                break;
            }
            case BINARY:
            case STRING:
            case MESSAGE:
            case ENUM:
                writer.formatln("builder.%s(ctxt.readValue(jp, %s.class));",
                                field.setter(),
                                field.instanceType());
                break;
            default:
                writer.formatln("builder.%s(ctxt.readValue(jp, %s.TYPE));",
                                field.setter(),
                                field.instanceType());
                break;
        }
    }

    private void appendWriteValue(JField field) {
        switch (field.type()) {
            case VOID:
                writer.formatln("provider.defaultSerializeField(\"%s\", true, generator);",
                                field.name());
                break;
            default:
                writer.formatln("provider.defaultSerializeField(\"%s\", instance.%s, generator);",
                                field.name(), field.member());
                break;
        }
    }

    private void appendJacksonDeserializer(JMessage<?> message) throws GeneratorException {
        writer.formatln("public static class _Deserializer extends %s<%s> {",
                        JsonDeserializer.class.getName(),
                        message.instanceType())
              .begin();

        writer.appendln("@Override")
              .formatln("public %s deserialize(%s jp,",
                        message.instanceType(),
                        JsonParser.class.getName())
              .formatln("       %s             %s ctxt)",
                        message.instanceType().replaceAll("[\\S]", " "),
                        DeserializationContext.class.getName())
              .formatln("         throws %s,",
                        IOException.class.getName())
              .formatln("                %s {",
                        JsonProcessingException.class.getName())
              .begin();

        writer.appendln("_Builder builder = builder();")
              .newline();

        writer.formatln("if (jp.isExpectedStartObjectToken()) {")
              .begin()
              .formatln("while (jp.nextToken() != %s.END_OBJECT) {", JsonToken.class.getName())
              .begin()
              .formatln("if (jp.getCurrentToken() != %s.FIELD_NAME) {", JsonToken.class.getName())
              .formatln("    throw new %s(jp, \"Invalid field name token \" + jp.getText());",
                        JsonParseException.class.getName())
              .appendln('}')
              .newline()
              .appendln("String field = jp.getCurrentName();")
              .appendln("jp.nextToken();")
              .appendln("switch (field) {")
              .begin();

        for (JField field : message.declaredOrderFields()) {
            writer.formatln("case \"%d\":", field.id())
                  .formatln("case \"%s\": {", field.name())
                  .begin();

            appendReadValue(field);

            writer.appendln("break;")
                  .end()
                  .appendln('}');
        }

        writer.end()
              .appendln("}")
              .end()
              .appendln("}");

        if (message.jsonCompactible()) {
            writer.end()
                  .appendln("} else if (jp.isExpectedStartArrayToken()) {")
                  .begin();

            writer.appendln("int idx = 0;")
                  .formatln("while (jp.nextToken() != %s.END_ARRAY) {", JsonToken.class.getName())
                  .begin()
                  .appendln("switch (idx++) {")
                  .begin();

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %d: {", field.index())
                      .begin();

                appendReadValue(field);

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.appendln("default:")
                  .formatln("    throw new %s(jp, \"Unexpected value: \" + jp.getText());",
                            JsonParseException.class.getName())
                  .end()
                  .appendln('}')
                  .end()
                  .appendln('}');
        }

        writer.end()
              .appendln("} else {")
              .formatln("    throw new %s(jp, \"Invalid token for object deserialization \" + jp.getText());",
                        JsonParseException.class.getName())
              .appendln('}')
              .newline()
              .appendln("return builder.build();");

        writer.end()
              .formatln("}")
              .end()
              .formatln("}")
              .newline();
    }

    private void appendJacksonSerializer(JMessage<?> message) throws GeneratorException {
        writer.formatln("public static class _Serializer extends %s<%s> {",
                        JsonSerializer.class.getName(),
                        message.instanceType())
              .begin()
              .appendln("@Override")
              .formatln("public void serialize(%s instance, %s generator, %s provider)",
                        message.instanceType(),
                        JsonGenerator.class.getName(),
                        SerializerProvider.class.getName())
              .formatln("        throws %s, %s {",
                        IOException.class.getName(), JsonProcessingException.class.getName())
              .begin();

        if (message.isUnion()) {
            writer.appendln("generator.writeStartObject();")
                  .formatln("switch (instance.%s) {", UNION_FIELD)
                  .begin();

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin();

                appendWriteValue(field);

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.end()
                  .appendln('}')
                  .appendln("generator.writeEndObject();");
        } else {
            if (message.jsonCompactible()) {
                writer.formatln("if (instance.jsonCompact()) {")
                      .begin()
                      .formatln("generator.writeStartArray();");

                int ifStack = 0;

                for (JField field : message.numericalOrderFields()) {
                    if (!(field.alwaysPresent() || field.isRequired())) {
                        writer.formatln("if (instance.%s != null) {", field.member())
                              .begin();
                        ++ifStack;
                    }

                    writer.formatln("provider.defaultSerializeValue(instance.%s, generator);",
                                    field.member());
                }

                while (ifStack-- > 0) {
                    writer.end()
                          .appendln('}');
                }

                writer.formatln("generator.writeEndArray();");

                writer.end()
                      .appendln("} else {")
                      .begin();
            }

            writer.formatln("generator.writeStartObject();");

            for (JField field : message.numericalOrderFields()) {
                if (!(field.alwaysPresent() || field.isRequired())) {
                    writer.formatln("if (instance.%s != null) {", field.member())
                          .begin();
                }

                appendWriteValue(field);

                if (!(field.alwaysPresent() || field.isRequired())) {
                    writer.end()
                          .appendln('}');
                }
            }

            writer.appendln("generator.writeEndObject();");

            if (message.jsonCompactible()) {
                writer.end()
                      .appendln('}');
            }
        }

        writer.end()
              .formatln("}")  // end serialize()
              .end()
              .formatln("}")  // end _Serializer
              .newline();
    }

}
