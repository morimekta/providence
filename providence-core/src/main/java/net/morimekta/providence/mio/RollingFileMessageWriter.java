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

import com.google.common.annotations.Beta;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * A simple rolling file message writer in the same manner that logging
 *
 * TODO: change constructor parameters to be a rollingFilePattern and
 * currentFileName instead of the resolution and prefix-suffix pair.
 * But this require that the rollingFilePattern is parsed into something
 * similar to the constructor params.
 */
@Beta
public class RollingFileMessageWriter implements MessageWriter {
    /**
     * Create a rolling file message writer using local timezone timestamps for
     * the files.
     *
     * @param directory The directory to place the message files into.
     * @param resolution The time resolution to roll over files.
     * @param serializer The message serializer to use.
     * @param filePrefix The file prefix.
     * @param fileSuffix The file suffix.
     */
    public RollingFileMessageWriter(File directory,
                                    TimeUnit resolution,
                                    Serializer serializer,
                                    String filePrefix,
                                    String fileSuffix) {
        this(directory, resolution, serializer, filePrefix, fileSuffix, Clock.systemDefaultZone());
    }

    /**
     * Create a rolling file message writer.
     *
     * @param directory The directory to place the message files into.
     * @param resolution The time resolution to roll over files.
     * @param serializer The message serializer to use.
     * @param filePrefix The file prefix.
     * @param fileSuffix The file suffix.
     * @param clock The clock to use for timestamps.
     */
    public RollingFileMessageWriter(File directory,
                                    TimeUnit resolution,
                                    Serializer serializer,
                                    String filePrefix,
                                    String fileSuffix,
                                    Clock clock) {
        try {
            this.directory = directory.getCanonicalFile().getAbsoluteFile();
            this.resolution = resolution;
            this.serializer = serializer;
            this.filePrefix = filePrefix;
            this.fileSuffix = fileSuffix;
            this.clock = clock;
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int write(Message message) throws IOException {
        FileMessageWriter writer = getWriter();
        int i = writer.write(message);
        writer.separator();
        return i;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int write(PServiceCall<Message, Field> call) throws IOException {
        FileMessageWriter writer = getWriter();
        int i = writer.write(call);
        writer.separator();
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

    private final Serializer serializer;
    private final Clock clock;
    private final File directory;
    private final TimeUnit resolution;
    private final String filePrefix;
    private final String fileSuffix;

    // Last write at the normalized current timestamp.
    private long              lastWriteTs;
    private FileMessageWriter currentWriter;

    private String fileTimestamp(long ts) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), clock.getZone());
        switch (resolution) {
            case DAYS: return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(zdt);
            case HOURS: return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH").format(zdt);
            case MINUTES: return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm").format(zdt);
            default: throw new IllegalStateException("Not a valid log rotation resolution: " + resolution.toString() + ", must be days, hours or minutes.");
        }
    }

    private FileMessageWriter getWriter() throws IOException {
        // This will normalize the timestamp to the first millisecond with the
        // requested resolution.
        long ts = resolution.toMillis(resolution.convert(clock.millis(), TimeUnit.MILLISECONDS));
        if (currentWriter == null || ts != lastWriteTs) {
            close();  // close the old writer, it it was opened.

            File file = new File(directory, filePrefix + "-" + fileTimestamp(ts) + fileSuffix);
            Path link = new File(directory, filePrefix + fileSuffix).toPath();

            currentWriter = new FileMessageWriter(file, serializer, true);
            currentWriter.getOutputStream();  // triggers creation of the file.

            // This should result in an atomic switch from old to new "current" logfile.
            Path tmp = Files.createTempFile(directory.toPath(), ".pvd.", ".link");
            Files.deleteIfExists(tmp);
            Files.createSymbolicLink(tmp, file.toPath());
            Files.move(tmp, link, StandardCopyOption.REPLACE_EXISTING);

            lastWriteTs = ts;
        }
        return currentWriter;
    }
}
