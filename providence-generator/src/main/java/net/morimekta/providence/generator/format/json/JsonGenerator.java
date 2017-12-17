/*
 * Copyright 2015 Providence Authors
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
package net.morimekta.providence.generator.format.json;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

/**
 * Generate a simple JSON model of the program structure.
 */
public class JsonGenerator extends Generator {
    private final JsonSerializer serializer;

    public JsonGenerator(FileManager fileManager) {
        super(fileManager);
        serializer = new JsonSerializer().pretty();
    }

    @Override
    public void generate(ProgramTypeRegistry registry) throws IOException, GeneratorException {
        ProgramType doc = registry.getProgramType();
        if (doc.hasIncludes()) {
            doc = doc.mutate()
                     .setIncludes(doc.getIncludes()
                                     .stream()
                                     .map(path -> path.replaceAll("(\\.thrift)$", ".json"))
                                     .collect(Collectors.toList()))
                     .build();
        }

        OutputStream out = getFileManager().create(null, doc.getProgramName() + ".json");
        try {
            serializer.serialize(out, doc);
            out.write('\n');
        } catch (SerializerException e) {
            throw new GeneratorException("Unable to serialize document.", e);
        }

        getFileManager().finalize(out);
    }
}
