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
package net.morimekta.providence.reflect.parser;

import net.morimekta.providence.model.ProgramType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Document parser interface.
 */
@FunctionalInterface
public interface ProgramParser {
    /**
     * Parse input stream to document declaration model.
     *
     * @param in The stream to parse.
     * @param file The file that is being parsed.
     * @param includeDirs Included directories that can be referenced directly.
     * @return The declared document model.
     * @throws IOException When the stream was unreadable.
     * @throws ParseException When the document could not be parsed.
     */
    ProgramType parse(InputStream in, File file, Collection<File> includeDirs) throws IOException, ParseException;
}
