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
package net.morimekta.providence.generator.format.java.enums.extras;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

/**
 * Formatter for extending for jackson annotated serialization.
 */
public class JacksonEnumFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;

    public JacksonEnumFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void appendClassAnnotations(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("@%s(", JsonDeserialize.class.getName())
              .formatln("        using = %s._Deserializer.class)", simpleClass);
    }

    @Override
    public void appendMethods(CEnumDescriptor type) throws GeneratorException {
        writer.formatln("@%s", JsonValue.class.getName())
              .appendln("public int jsonValue() {")
              .appendln("    return mValue;")
              .appendln('}')
              .newline();
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);
        String spacesClass = Strings.times(" ", simpleClass.length());

        writer.formatln("public static class _Deserializer extends %s<%s> {",
                        JsonDeserializer.class.getName(), simpleClass)
              .appendln("    @Override")
              .formatln("    public %s deserialize(%s jp,",
                        simpleClass, JsonParser.class.getName())
              .formatln("           %s             %s ctxt)",
                        spacesClass, DeserializationContext.class.getName())
              .formatln("            throws %s,", IOException.class.getName())
              .formatln("                   %s {", JsonProcessingException.class.getName())
              .formatln("        if (jp.getCurrentToken() == %s.%s) {",
                        JsonToken.class.getName(),
                        JsonToken.VALUE_NUMBER_INT.name())
              .formatln("            return %s.forValue(jp.getIntValue());", simpleClass)
              .formatln("        } else if (jp.getCurrentToken() == %s.%s) {",
                        JsonToken.class.getName(),
                        JsonToken.VALUE_STRING.name())
              .formatln("            return %s.forName(jp.getText());", simpleClass)
              .appendln("        } else {")
              .formatln("            throw new %s(jp, \"Invalid token for enum %s deserialization \" + jp.getText());",
                        JsonParseException.class.getName(),
                        type.getQualifiedName())
              .appendln("        }")
              .appendln("    }")
              .appendln('}')
              .newline();
    }
}
