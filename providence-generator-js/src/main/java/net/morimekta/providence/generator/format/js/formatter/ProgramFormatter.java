/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.generator.format.js.formatter;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * Base class for formatting a complete .js (.as, .ts etc) file. The
 * basic paradigm is that each file contains code matching a whole
 * thrift program, so that
 */
public abstract class ProgramFormatter {
    final JSOptions           options;
    final ProgramTypeRegistry registry;

    protected ProgramFormatter(JSOptions options, ProgramTypeRegistry registry) {
        this.options = options;
        this.registry = registry;
    }

    public void format(IndentedPrintWriter writer, CProgram program) {
        formatHeader(writer, program);

        for (PDeclaredDescriptor descriptor : program.getDeclaredTypes()) {
            if (descriptor instanceof PEnumDescriptor) {
                formatEnum(writer, (CEnumDescriptor) descriptor);
            } else if (descriptor instanceof PMessageDescriptor) {
                formatMessage(writer, (CMessageDescriptor) descriptor);
            } else {
                throw new IllegalArgumentException("Impossible");
            }
        }
        for (CConst constant : program.getConstants()) {
            formatConstant(writer, program, constant);
        }

        formatFooter(writer, program);
    }

    public abstract String getFileName(CProgram program);

    protected abstract void formatHeader(IndentedPrintWriter writer, CProgram program);
    protected abstract void formatEnum(IndentedPrintWriter writer, CEnumDescriptor descriptor);
    protected abstract void formatMessage(IndentedPrintWriter writer, CMessageDescriptor descriptor);
    protected abstract void formatConstant(IndentedPrintWriter writer, CProgram program, CConst constant);
    protected abstract void formatFooter(IndentedPrintWriter writer, CProgram program);
}
