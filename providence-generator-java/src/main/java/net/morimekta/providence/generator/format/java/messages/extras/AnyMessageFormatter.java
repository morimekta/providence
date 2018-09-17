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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.util.Any;
import net.morimekta.util.Binary;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * This class annotates specifically the providence.Any type with two utility
 * methods:
 *
 * - Message unwrapMessage(descriptor);
 * - static Any wrapMessage(message);
 */
public class AnyMessageFormatter implements MessageMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public AnyMessageFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        if (isAny(message)) {
            new BlockCommentBuilder(writer)
                    .commentRaw("Check the wrapped message type against the provided message type\n" +
                                "descriptor.")
                    .newline()
                    .param_("descriptor", "The message type to check.")
                    .param_("<M>", "The message type")
                    .param_("<F>", "The message field type")
                    .return_("True if the wrapped message type matches the provided.")
                    .finish();
            // boolean wrappedType(PMessageDescriptor)
            writer.formatln("public <M extends %s<M, F>, F extends %s>",
                            PMessage.class.getName(), PField.class.getName())
                  .formatln("boolean wrappedTypeIs(%s %s<M,F> descriptor) {",
                            JAnnotation.NON_NULL,
                            PMessageDescriptor.class.getName())
                  .appendln("    return descriptor.getQualifiedName().equals(getType());")
                  .appendln("}")
                  .newline();

            new BlockCommentBuilder(writer)
                    .commentRaw("Unwrap a message from this wrapper message. This will use the default\n" +
                                "serializer provider to find a suitable serializer to use to deserialize\n" +
                                "the wrapped message. If no serializer is available, or the message\n" +
                                "cannot be deserialized an unchecked IO exception is thrown.")
                    .newline()
                    .param_("descriptor", "The message type to unpack from the content.")
                    .param_("<M>", "The message type")
                    .param_("<F>", "The message field type")
                    .return_("The unwrapped message.")
                    .finish();
            // Message unwrapMessage(PMessageDescriptor)
            writer.formatln("public <M extends %s<M, F>, F extends %s>",
                            PMessage.class.getName(), PField.class.getName())
                  .formatln("M unwrapMessage(%s %s<M,F> descriptor) {",
                            JAnnotation.NON_NULL,
                            PMessageDescriptor.class.getName())
                  .formatln("    return unwrapMessage(descriptor, new %s());", DefaultSerializerProvider.class.getName())
                  .formatln("}")
                  .newline();

            new BlockCommentBuilder(writer)
                    .commentRaw("Unwrap a message from this wrapper message. This will use the provided\n" +
                                "serializer provider to find a suitable serializer to use to deserialize\n" +
                                "the wrapped message. If no serializer is available, or the message\n" +
                                "cannot be deserialized an unchecked IO exception is thrown.")
                    .newline()
                    .param_("descriptor", "The message type to unpack from the content.")
                    .param_("provider", "Serializer provider to get serializer from.")
                    .param_("<M>", "The message type")
                    .param_("<F>", "The message field type")
                    .return_("The unwrapped message.")
                    .finish();
            // Message unwrapMessage(PMessageDescriptor, SerializerProvider)
            writer.formatln("public <M extends %s<M, F>, F extends %s>",
                            PMessage.class.getName(), PField.class.getName())
                  .formatln("M unwrapMessage(%s %s<M,F> descriptor, %s %s provider) {",
                            JAnnotation.NON_NULL,
                            PMessageDescriptor.class.getName(),
                            JAnnotation.NON_NULL,
                            SerializerProvider.class.getName())
                  .begin()
                  .formatln("if (!descriptor.getQualifiedName().equals(getType())) {")
                  .formatln("    throw new %s(\"Any type \" + getType() + \" does not match requested \" + descriptor.getQualifiedName());",
                            IllegalStateException.class.getSimpleName())
                  .formatln("}")
                  .newline();

            writer.appendln("try {")
                  .begin()
                  .formatln("%s serializer = provider.getSerializer(getMediaType());", Serializer.class.getName())
                  .appendln("if (hasData()) {")
                  .appendln("    return serializer.deserialize(getData().getInputStream(), descriptor);")
                  .appendln("} else if (hasText()) {")
                  .formatln("    %s bais = new %s(getText().getBytes(%s.UTF_8));",
                            ByteArrayInputStream.class.getName(), ByteArrayInputStream.class.getName(),
                            StandardCharsets.class.getName())
                  .formatln("    return serializer.deserialize(bais, descriptor);")
                  .appendln("} else {")
                  .formatln("    throw new %s(\"Neither data, nor text de deserialize.\");", SerializerException.class.getName())
                  .appendln("}");

            writer.end()
                  .formatln("} catch (%s e) {", IOException.class.getName())
                  .formatln("    throw new %s(e.getMessage(), e);", UncheckedIOException.class.getName())
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .newline();
        }
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        if (isAny(message)) {
            new BlockCommentBuilder(writer)
                    .commentRaw("Wrap a message into an <code>Any</code> wrapper message. This\n" +
                                "will serialize the message using the default binary serializer.")
                    .newline()
                    .param_("message", "Wrap this message.")
                    .param_("<M>", "The message type")
                    .param_("<F>", "The message field type")
                    .return_("The wrapped message.")
                    .finish();
            // Any wrapMessage(Message)
            writer.formatln("public static <M extends %s<M, F>, F extends %s>",
                            PMessage.class.getName(), PField.class.getName())
                  .formatln("%s wrapMessage(%s M message) {",
                            message.instanceType(),
                            JAnnotation.NON_NULL)
                  .formatln("    return wrapMessage(message, new %s());", BinarySerializer.class.getName())
                  .formatln("}")
                  .newline();

            new BlockCommentBuilder(writer)
                    .commentRaw("Wrap a message into an <code>Any</code> wrapper message. This\n" +
                                "will serialize the message using the provided serializer.")
                    .newline()
                    .param_("message", "Wrap this message.")
                    .param_("serializer", "Use this serializer.")
                    .param_("<M>", "The message type")
                    .param_("<F>", "The message field type")
                    .return_("The wrapped message.")
                    .finish();
            // Any wrapMessage(Message, Serializer)
            writer.formatln("public static <M extends %s<M, F>, F extends %s>",
                            PMessage.class.getName(), PField.class.getName())
                  .formatln("%s wrapMessage(%s M message, %s %s serializer) {",
                            message.instanceType(),
                            JAnnotation.NON_NULL,
                            JAnnotation.NON_NULL,
                            Serializer.class.getName())
                  .begin();

            writer.appendln("try {")
                  .begin()
                  .appendln("_Builder builder = builder();")
                  .formatln("%s baos = new %s();",
                            ByteArrayOutputStream.class.getName(),
                            ByteArrayOutputStream.class.getName())
                  .appendln("serializer.serialize(baos, message);")
                  .appendln("if (serializer.binaryProtocol()) {")
                  .formatln("    builder.setData(%s.wrap(baos.toByteArray()));", Binary.class.getName())
                  .appendln("} else {")
                  .formatln("    builder.setText(new String(baos.toByteArray(), %s.UTF_8));", StandardCharsets.class.getName())
                  .appendln("}")
                  .appendln("builder.setType(message.descriptor().getQualifiedName());")
                  .appendln("builder.setMediaType(serializer.mediaType());")
                  .appendln("return builder.build();");

            writer.end()
                  .formatln("} catch (%s e) {", IOException.class.getName())
                  .formatln("    throw new %s(e.getMessage(), e);", UncheckedIOException.class.getName())
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .newline();
        }
    }

    private boolean isAny(JMessage<?> message) {
        return message.descriptor().getQualifiedName().equals(Any.kDescriptor.getQualifiedName())
               && helper.getValueType(message.descriptor())
                        .equals(Any.class.getName());
    }
}
