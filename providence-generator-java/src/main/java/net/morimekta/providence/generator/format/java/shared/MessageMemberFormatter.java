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
package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Base interface for formatting a providence message class.
 */
public interface MessageMemberFormatter {
    default void appendClassAnnotations(JMessage<?> message) throws GeneratorException {}

    default Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        return new LinkedList<>();
    }

    default void appendConstants(JMessage<?> message) throws GeneratorException {}

    default void appendFields(JMessage<?> message) throws GeneratorException {}

    default void appendConstructors(JMessage<?> message) throws GeneratorException {}

    default void appendMethods(JMessage<?> message) throws GeneratorException {}

    default void appendExtraProperties(JMessage<?> message) throws GeneratorException {}
}
