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
package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.messages.BuilderCommonMemberFormatter;
import net.morimekta.providence.generator.format.java.messages.BuilderCoreOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonBuilderFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonMemberFormatter;
import net.morimekta.providence.generator.format.java.messages.CommonOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter;
import net.morimekta.providence.generator.format.java.messages.extras.BinaryReaderBuilderFormatter;
import net.morimekta.providence.generator.format.java.messages.extras.BinarySerializableFormatter;
import net.morimekta.providence.generator.format.java.messages.extras.BinaryWriterFormatter;
import net.morimekta.providence.generator.format.java.messages.extras.HazelcastPortableMessageFormatter;
import net.morimekta.providence.generator.format.java.messages.extras.JacksonMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Formatter for java messages main class.
 */
public class JavaMessageFormatter extends BaseMessageFormatter {
    public JavaMessageFormatter(IndentedPrintWriter writer,
                                JHelper helper,
                                GeneratorOptions generatorOptions,
                                JavaOptions options) {
        this(false, false, writer, helper, generatorOptions, options);
    }

    public JavaMessageFormatter(boolean inner,
                                boolean makeProtected,
                                IndentedPrintWriter writer,
                                JHelper helper,
                                GeneratorOptions generatorOptions,
                                JavaOptions options) {
        super(inner, makeProtected, writer, helper, getFormatters(writer, helper, generatorOptions, options));
    }

    public String getClassName(JMessage<?> message) {
        return message.instanceType();
    }

    private static List<MessageMemberFormatter> getFormatters(IndentedPrintWriter writer,
                                                              JHelper helper,
                                                              GeneratorOptions generatorOptions,
                                                              JavaOptions javaOptions) {
        ImmutableList.Builder<MessageMemberFormatter> builderFormatters = ImmutableList.builder();
        builderFormatters.add(new BuilderCommonMemberFormatter(writer, helper))
                         .add(new BuilderCoreOverridesFormatter(writer, helper));

        if (javaOptions.hazelcast_portable) {
            builderFormatters.add(new HazelcastPortableMessageFormatter(writer, helper));
        }

        ImmutableList.Builder<MessageMemberFormatter> formatters = ImmutableList.builder();
        formatters.add(new CommonMemberFormatter(writer, helper, generatorOptions, javaOptions))
                  .add(new CoreOverridesFormatter(writer))
                  .add(new CommonOverridesFormatter(writer))
                  .add(new BinarySerializableFormatter(writer, helper));

        if (javaOptions.jackson) {
            formatters.add(new JacksonMessageFormatter(writer, helper));
        }
        if (javaOptions.rw_binary) {
            formatters.add(new BinaryWriterFormatter(writer, helper));
            builderFormatters.add(new BinaryReaderBuilderFormatter(writer, helper));
        }

        formatters.add(new CommonBuilderFormatter(writer, helper, builderFormatters.build()));
        return formatters.build();
    }

    @Override
    public void appendMessageClass(PMessageDescriptor<?, ?> descriptor) throws GeneratorException {
        super.appendMessageClass(descriptor);
        writer.newline();
    }
}
