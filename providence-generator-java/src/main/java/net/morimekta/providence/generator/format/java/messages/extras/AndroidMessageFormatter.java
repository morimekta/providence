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

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class AndroidMessageFormatter implements MessageMemberFormatter {
    private final IndentedPrintWriter writer;

    public AndroidMessageFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        return ImmutableList.of(
                "android.os.Parcelable"
        );
    }

    @Override
    public void appendMethods(JMessage message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public int describeContents() {")
              .begin()
              .appendln("return 0;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public void writeToParcel(android.os.Parcel dest, int flags) {")
              .begin()
              .formatln("%s baos = new %s();",
                        ByteArrayOutputStream.class.getName(),
                        ByteArrayOutputStream.class.getName())
              .formatln("%s serializer = new %s();", Serializer.class.getName(), BinarySerializer.class.getName())
              .appendln("try {")
              .begin()
              .appendln("serializer.serialize(baos, this);")
              .appendln("dest.writeByteArray(baos.toByteArray());")
              .end()
              .formatln("} catch (%s e) {", IOException.class.getName())
              .formatln("    throw new %s(e);", UncheckedIOException.class.getName())
              .appendln("}")
              .end()
              .appendln('}');
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        writer.formatln("public static final android.os.Parcelable.Creator<%s> CREATOR = new android.os.Parcelable.Creator<%s>() {",
                        message.instanceType(),
                        message.instanceType())
              .begin();

        writer.appendln("@Override")
              .formatln("public %s createFromParcel(android.os.Parcel source) {", message.instanceType())
              .begin()
              .formatln("%s bais = new %s(source.createByteArray());", ByteArrayInputStream.class.getName(), ByteArrayInputStream.class.getName())
              .formatln("%s serializer = new %s();",
                        Serializer.class.getName(),
                        BinarySerializer.class.getName())
              .appendln("try {")
              .begin()
              .formatln("return serializer.deserialize(bais, %s.kDescriptor);",
                        message.instanceType())
              .end()
              .formatln("} catch (%s e) {", IOException.class.getName())
              .formatln("    throw new %s(e);", UncheckedIOException.class.getName())
              .appendln("}")
              .end()
              .appendln('}');

        writer.appendln("@Override")
              .formatln("public %s[] newArray(int size) {", message.instanceType())
              .begin()
              .formatln("return new %s[size];", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln("};")
              .newline();
    }

}
