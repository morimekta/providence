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

package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.util.TypeRegistry;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class JGenerator extends Generator {
    private final JOptions mOptions;
    TypeRegistry mRegistry;
    JHelper      mTypeHelper;

    public JGenerator(FileManager manager, TypeRegistry registry, JOptions options) {
        super(manager);
        mRegistry = registry;
        mOptions = options;

        mTypeHelper = new JHelper(mRegistry, options);
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(CDocument document) throws IOException, GeneratorException {
        String javaPackage = JUtils.getJavaPackage(document);
        JMessageFormat messageFormatter = new JMessageFormat(mTypeHelper, mOptions);
        JEnumFormat enumFormatter = new JEnumFormat(mTypeHelper, mOptions);

        String path = JUtils.getPackageClassPath(javaPackage);

        if (document.getConstants()
                    .size() > 0) {
            String file = mTypeHelper.getConstantsClassName(document) + ".java";
            OutputStream out = getFileManager().create(path, file);
            try {
                JConstantsFormat constFormat = new JConstantsFormat(mTypeHelper, mOptions);
                IndentedPrintWriter writer = new IndentedPrintWriter(out);
                constFormat.format(writer, document);
                writer.flush();
            } finally {
                try {
                    getFileManager().finalize(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (PDeclaredDescriptor<?> type : document.getDeclaredTypes()) {
            String file = mTypeHelper.getInstanceClassName(type) + ".java";
            OutputStream out = getFileManager().create(path, file);
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);
                switch (type.getType()) {
                    case MESSAGE:
                        messageFormatter.format(writer, (PStructDescriptor<?, ?>) type);
                        break;
                    case ENUM:
                        enumFormatter.format(writer, (PEnumDescriptor<?>) type);
                        break;
                    default:
                        throw new GeneratorException("Unhandled declaration type.");

                }
                writer.flush();
            } finally {
                getFileManager().finalize(out);
            }
        }
    }
}
