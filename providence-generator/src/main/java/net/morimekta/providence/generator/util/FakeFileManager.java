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
package net.morimekta.providence.generator.util;

import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A fake file manager meant for testing only.
 */
@VisibleForTesting
public class FakeFileManager extends FileManager {
    public FakeFileManager(File root) {
        super(root);
    }

    public OutputStream create(String path, String name) {
        System.out.println();
        System.out.println("### --> " + relativePath(path, name));
        return System.out;
    }

    public void finalize(OutputStream stream) throws IOException {
        stream.flush();
        System.out.println("### <-- END");
    }
}
