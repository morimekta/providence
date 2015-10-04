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

package org.apache.thrift2.compiler.generator;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.thrift2.compiler.format.java2.Java2EnumFormatter;
import org.apache.thrift2.compiler.format.java2.Java2MessageFormatter;
import org.apache.thrift2.compiler.format.java2.Java2TypeHelper;
import org.apache.thrift2.compiler.format.java2.Java2Utils;
import org.apache.thrift2.compiler.util.FileManager;
import org.apache.thrift2.util.io.IndentedPrintWriter;
import org.apache.thrift2.descriptor.TDeclaredDescriptor;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.reflect.contained.TContainedDocument;
import org.apache.thrift2.reflect.util.TTypeRegistry;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 05.09.15
 */
public class Java2Generator
        extends Generator {
    TTypeRegistry mRegistry;
    Java2TypeHelper mTypeHelper;
    boolean mAndroid;

    public Java2Generator(FileManager manager,
                          TTypeRegistry registry,
                          boolean android) {
        super(manager);
        mRegistry = registry;
        mAndroid = android;

        mTypeHelper = new Java2TypeHelper(mRegistry);
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(TContainedDocument document) throws IOException, GeneratorException {
        String javaPackage = Java2Utils.getJavaPackage(document);
        Java2MessageFormatter messageFormatter =
                new Java2MessageFormatter(mTypeHelper, mAndroid);
        Java2EnumFormatter enumFormatter
                = new Java2EnumFormatter(mTypeHelper);

        String path = Java2Utils.getPackageClassPath(javaPackage);

        for (TDeclaredDescriptor<?> type : document.getDeclaredTypes()) {
            String file = mTypeHelper.getSimpleClassName(type) + ".java";
            OutputStream out = getFileManager().create(path, file);
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);
                switch (type.getType()) {
                    case MESSAGE:
                        messageFormatter.format(writer, (TStructDescriptor<?>) type);
                        break;
                    case ENUM:
                        enumFormatter.format(writer, (TEnumDescriptor<?>) type);
                        break;
                    default:
                        throw new GeneratorException("Unhandled declaration type.");

                }
                writer.flush();
            } finally {
                System.out.println(path + "/" + file + " OK");
                getFileManager().finalize(out);
            }
        }
    }
}
