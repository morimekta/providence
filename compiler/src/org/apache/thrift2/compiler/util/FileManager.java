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

package org.apache.thrift2.compiler.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 19.09.15
 */
public class FileManager {
    private final File mRoot;

    private final Set<String> mCreatedFiles;

    public FileManager(File root) {
        mRoot = root;

        mCreatedFiles = new HashSet<>();
    }

    protected String relativePath(String path, String name) {
        if (path == null || path.isEmpty())
            return name;
        return String.format("%s%c%s", path, File.separatorChar, name);
    }

    protected String absolutePath(String path, String name) throws IOException {
        return new File(mRoot, relativePath(path, name)).getCanonicalPath();
    }

    public OutputStream create(String path, String name) throws IOException {
        File file = new File(absolutePath(path, name));

        if (mCreatedFiles.contains(file.getCanonicalPath())) {
            throw new IOException("File " + path + File.separator + name + " already created.");
        }

        file.getParentFile().mkdirs();
        file.createNewFile();

        mCreatedFiles.add(file.getCanonicalPath());

        return new FileOutputStream(file, false);
    }

    public void finalize(OutputStream stream) throws IOException {
        stream.flush();
        stream.close();
    }
}
