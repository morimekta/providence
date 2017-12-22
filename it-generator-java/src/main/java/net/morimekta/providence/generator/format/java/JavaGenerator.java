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

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import javax.annotation.Nonnull;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class JavaGenerator extends Generator {
    public JavaGenerator(FileManager manager) throws GeneratorException {
        super(manager);
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(@Nonnull ProgramTypeRegistry registry) throws GeneratorException {
        // no-op.
    }
}
