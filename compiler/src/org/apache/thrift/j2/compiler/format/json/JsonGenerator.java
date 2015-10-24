/*
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

package org.apache.thrift.j2.compiler.format.json;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.thrift.j2.compiler.generator.GeneratorException;
import org.apache.thrift.j2.compiler.util.FileManager;
import org.apache.thrift.j2.model.ThriftDocument;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;
import org.apache.thrift.j2.compiler.generator.Generator;
import org.apache.thrift.j2.reflect.TTypeLoader;
import org.apache.thrift.j2.serializer.TCompactJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;

/**
 * @author Stein Eldar Johnsen
 * @since 22.09.15
 */
public class JsonGenerator extends Generator {
    private final TTypeLoader                 mLoader;
    private final TCompactJsonSerializer mSerializer;

    public JsonGenerator(FileManager fileManager, TTypeLoader loader) {
        super(fileManager);
        mLoader = loader;
        mSerializer = new TCompactJsonSerializer(TCompactJsonSerializer.IdType.NAME, TCompactJsonSerializer.IdType.NAME);
    }

    @Override
    public void generate(TContainedDocument document) throws IOException, GeneratorException {
        for (ThriftDocument doc : mLoader.loadedDocuments()) {
            if (doc.getPackage().equals(document.getPackageName())) {
                OutputStream out = getFileManager().create("", doc.getPackage() + ".json");
                try {
                    mSerializer.serialize(out, doc);
                } catch (TSerializeException e) {
                    throw new GeneratorException("Unable to serialize document.", e);
                }

                getFileManager().finalize(out);
            }
        }
    }
}
