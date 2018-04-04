/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.mio.rolling;

import net.morimekta.providence.mio.RollingFileMessageWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Locale;

/**
 * A rolling policy that moves over to a new file whenever the current
 * file reaches a threshold size.
 */
public class SizeBasedRollingPolicy implements RollingFileMessageWriter.RollingPolicy {
    public SizeBasedRollingPolicy(File directory,
                                  long rollOnFileSizeInBytes,
                                  String fileNameFormat) {
        this.directory = directory;
        this.rollOnFileSize = rollOnFileSizeInBytes;
        this.fileNameFormat = fileNameFormat;
        this.currentFileId = 1;
        this.currentFile = new File(directory, String.format(Locale.US, fileNameFormat, currentFileId));
        try {
            // Initially skip the files 1..N which has already reached the size trigger.
            while (currentFile.exists() &&
                   Files.size(currentFile.toPath()) >= rollOnFileSizeInBytes) {
                ++currentFileId;
                this.currentFile = new File(directory, String.format(Locale.US, fileNameFormat, currentFileId));
            }
        } catch (IOException e) {
            // Must be file system error, almost impossible to test.
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public void maybeUpdateCurrentFile(@Nonnull RollingFileMessageWriter.CurrentFileUpdater onRollFile,
                                       boolean initialCall) throws IOException {
        if (initialCall) {
            onRollFile.updateCurrentFile(currentFile.getName());
        } else if (Files.size(currentFile.toPath()) >= rollOnFileSize) {
            // rely on the file for the file size. This can lead to
            // slow roll checking if a networking file system is used.
            ++currentFileId;
            currentFile = new File(directory, String.format(Locale.US, fileNameFormat, currentFileId));
            // When rolling enforce continuity, and delete possible existing files.
            Files.deleteIfExists(currentFile.toPath());
            onRollFile.updateCurrentFile(currentFile.getName());
        }
    }

    private final long     rollOnFileSize;
    private final File     directory;
    private final String   fileNameFormat;

    private long currentFileId;
    private File currentFile;
}
