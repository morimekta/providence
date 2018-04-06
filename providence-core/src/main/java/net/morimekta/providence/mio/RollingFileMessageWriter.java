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
package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.serializer.Serializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple rolling file message writer in the same manner that logging
 * often does, e.g. the 'RollingFileAppender' from lockback.
 * <p>
 * the message writer MUST be assigned a rolling policy, and MAY be
 * assigned a cleanup policy. Note that the cleanup policy will only be
 * triggered when the rolling policy triggers a file update.
 * <p>
 * Also note that the RollingFileMessageWriter is NOT thread safe. So
 * if you need to write to the message writer from multiple threads, you
 * will either have to synchronize the calls yourself, or use the
 * {@link QueuedMessageWriter}.
 */
public class RollingFileMessageWriter implements MessageWriter {
    /**
     * Create a rolling file message writer without a cleanup policy.
     *
     * @param directory The directory to place the message files into.
     * @param serializer The message serializer to use.
     * @param currentName The name of the current file symbolic link.
     * @param rollingPolicy The rolling policy.
     */
    public RollingFileMessageWriter(@Nonnull File directory,
                                    @Nonnull Serializer serializer,
                                    @Nonnull String currentName,
                                    @Nonnull RollingPolicy rollingPolicy) {
        this(directory, serializer, currentName, rollingPolicy, null);
    }

    /**
     * Create a rolling file message writer.
     *
     * @param directory The directory to place the message files into.
     * @param serializer The message serializer to use.
     * @param currentName The name of the current file symbolic link.
     * @param rollingPolicy The rolling policy.
     * @param cleanupPolicy Optional cleanup policy.
     */
    public RollingFileMessageWriter(@Nonnull File directory,
                                    @Nonnull Serializer serializer,
                                    @Nonnull String currentName,
                                    @Nonnull RollingPolicy rollingPolicy,
                                    @Nullable CleanupPolicy cleanupPolicy) {
        try {
            this.directory = directory.getCanonicalFile().getAbsoluteFile();
            this.serializer = serializer;
            this.currentName = currentName;
            this.rollingPolicy = rollingPolicy;
            this.cleanupPolicy = cleanupPolicy;

            Files.createDirectories(directory.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * Interface for rolling policy implementations.
     */
    @FunctionalInterface
    public interface RollingPolicy {
        /**
         * Maybe call the current file updater.
         * @param onRollFile The current file updater to call if the current file
         *                   should roll over.
         * @param initialCall If this is the initial call, and the current
         *                    file updater should be called regardless.
         * @throws IOException If the file roll or update check failed.
         */
        void maybeUpdateCurrentFile(@Nonnull CurrentFileUpdater onRollFile,
                                    boolean initialCall) throws IOException;
    }

    /**
     * Interface for calling back to the rolling file message writen when a file roll
     * is supposed to happen.
     */
    @FunctionalInterface
    public interface CurrentFileUpdater {
        void updateCurrentFile(@Nonnull String newFileName) throws IOException;
    }

    /**
     * Interface for cleanup policy implementations.
     */
    @FunctionalInterface
    public interface CleanupPolicy {
        /**
         * Get a list of files that needs to be deleted because of the cleanup policy.
         *
         * @param candidateFiles List of the files that can be cleaned up. This does NOT
         *                       include the currently written files (current file and
         *                       symlink).
         * @param currentFileName The current file name.
         * @return List of files that needs to be deleted.
         */
        @Nonnull List<String> getFilesToDelete(@Nonnull List<String> candidateFiles,
                                               @Nonnull String currentFileName);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> int write(Message message)
            throws IOException {
        FileMessageWriter writer = getWriter();
        int i = writer.write(message);
        i += writer.separator();
        return i;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> int write(PServiceCall<Message, Field> call)
            throws IOException {
        FileMessageWriter writer = getWriter();
        int i = writer.write(call);
        i += writer.separator();
        return i;
    }

    @Override
    public int separator() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
        if (currentWriter != null) {
            try {
                currentWriter.close();
            } finally {
                currentWriter = null;
            }
        }
    }

    private final Serializer        serializer;
    private final File              directory;
    private final String            currentName;
    private final RollingPolicy     rollingPolicy;
    private final CleanupPolicy     cleanupPolicy;
    private       File              currentFile;
    private       FileMessageWriter currentWriter;
    private       boolean           shouldDoCleanup;

    private void updateWriter(String rollToFile) throws IOException {
        if (rollToFile.contains(File.separator)) {
            throw new IllegalArgumentException("rolling file path " + rollToFile + " is not contained in output directory.");
        }

        close();  // close the old writer, it it was opened.

        currentFile = new File(directory, rollToFile);
        Path link = new File(directory, currentName).toPath();

        currentWriter = new FileMessageWriter(currentFile, serializer, true);
        currentWriter.getOutputStream();  // triggers creation of the file.

        if (!rollToFile.equals(currentName)) {
            // This should result in an atomic switch from old to new "current" logfile.
            Path tmp = Files.createTempFile(directory.toPath(), ".pvd.", ".link");
            Files.deleteIfExists(tmp);
            Files.createSymbolicLink(tmp, currentFile.toPath());
            Files.move(tmp, link, StandardCopyOption.REPLACE_EXISTING);
        }

        if (cleanupPolicy != null) {
            shouldDoCleanup = true;
        }
    }

    private FileMessageWriter getWriter() throws IOException {
        rollingPolicy.maybeUpdateCurrentFile(this::updateWriter, currentWriter == null);
        if (currentWriter == null) {
            updateWriter(currentName);
        }
        if (shouldDoCleanup) {
            shouldDoCleanup = false;

            String[] files = directory.list();
            if (files != null && files.length > 2) {
                // More than the currentFile and the symlink.
                List<String> toDelete = cleanupPolicy.getFilesToDelete(
                        Arrays.stream(files)
                              .filter(f -> !f.equals(currentName) && !f.equals(currentFile.getName()))
                              .collect(Collectors.toList()),
                        currentFile.getName());
                toDelete.forEach(del -> {
                    try {
                        // Delete if exists, just in case the file was deleted by someone else
                        // while we figured out.
                        Files.deleteIfExists(new File(directory, del).toPath());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e.getMessage(), e);
                    }
                });
            }
        }
        return currentWriter;
    }
}
