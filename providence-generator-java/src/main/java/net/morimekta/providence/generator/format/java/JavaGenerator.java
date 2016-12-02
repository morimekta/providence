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

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseConstantsFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseEnumFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseGenerator;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseServiceFormatter;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class JavaGenerator extends BaseGenerator {
    private final JavaOptions options;

    public JavaGenerator(FileManager manager, ProgramRegistry registry, JavaOptions options) throws GeneratorException {
        super(manager, registry);

        this.options = options;
    }

    @Override
    protected BaseMessageFormatter messageFormatter(IndentedPrintWriter writer) {
        return new JavaMessageFormatter(writer, helper, options);
    }

    @Override
    protected BaseEnumFormatter enumFormatter(IndentedPrintWriter writer) {
        return new JavaEnumFormatter(writer, options);
    }

    @Override
    protected BaseConstantsFormatter constFomatter(IndentedPrintWriter writer) {
        return new JavaConstantsFormatter(writer, helper);
    }

    @Override
    protected BaseServiceFormatter serviceFormatter(IndentedPrintWriter writer) {
        return new JavaServiceFormatter(writer, helper, new JavaMessageFormatter(true, true, writer, helper, options));
    }
}
