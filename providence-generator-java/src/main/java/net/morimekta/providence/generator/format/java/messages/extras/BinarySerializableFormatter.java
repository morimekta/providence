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
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BinarySerializableFormatter implements MessageMemberFormatter {
    public static final String T_SERIALIZE_INSTANCE = "tSerializeInstance";
    private final AtomicInteger nextId = new AtomicInteger(1);

    private final IndentedPrintWriter writer;
    private final JHelper helper;

    public BinarySerializableFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        writer.appendln("// Transient object used during java deserialization.")
              .formatln("private transient %s " + T_SERIALIZE_INSTANCE + ";", message.instanceType())
              .appendln();
    }

    @Override
    public void appendMethods(JMessage<?> message) {
        appendWriteObject();
        appendReadObject(message);
        appendReadResolve();
    }

    private void appendWriteObject() {
        writer.formatln("private void writeObject(%s oos) throws %s {",
                        ObjectOutputStream.class.getName(),
                        IOException.class.getName())
              .appendln("    oos.defaultWriteObject();")
              .formatln("    %s serializer = new %s(false);",
                        BinarySerializer.class.getName(),
                        BinarySerializer.class.getName())
              .appendln("    serializer.serialize(oos, this);")
              .appendln("}")
              .newline();
    }

    private void appendReadObject(JMessage<?> message) {
        writer.formatln("private void readObject(%s ois)",
                        ObjectInputStream.class.getName())
              .formatln("        throws %s, %s {",
                        IOException.class.getName(),
                        ClassNotFoundException.class.getSimpleName())
              .begin()
              .formatln("ois.defaultReadObject();")
              .formatln("%s serializer = new %s(false);",
                        BinarySerializer.class.getName(),
                        BinarySerializer.class.getName())
              .appendln(T_SERIALIZE_INSTANCE + " = serializer.deserialize(ois, kDescriptor);");

        if (message.isException()) {
            // Also transfer exception cause and stack trace.
            writer.appendln("if (getCause() != null) {")
                  .formatln("    " + T_SERIALIZE_INSTANCE + ".initCause(getCause());")
                  .appendln("}")
                  .appendln(T_SERIALIZE_INSTANCE + ".setStackTrace(getStackTrace());");
        }

        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendReadResolve() {
        writer.formatln("private Object readResolve() throws %s {",
                        ObjectStreamException.class.getName())
              .appendln("    return " + T_SERIALIZE_INSTANCE + ";")
              .appendln("}")
              .newline();
    }
}
