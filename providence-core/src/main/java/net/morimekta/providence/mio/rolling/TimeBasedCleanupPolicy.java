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
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import static net.morimekta.providence.mio.rolling.TimeBasedRollingPolicy.FILE_PATTERN;
import static net.morimekta.providence.mio.rolling.TimeBasedRollingPolicy.getFormatter;

/**
 * A cleanup policy that removes files older than a certain date.
 * Uses a date-based file pattern in order to determine the date
 * on the file. the actual file mtime is <b>not</b> checked.
 */
public class TimeBasedCleanupPolicy implements RollingFileMessageWriter.CleanupPolicy {
    /**
     * Create a time based cleanup policy using the system clock for parsing timestampts.
     *
     * @param units The amount of time back to keep files for.
     * @param resolution The unit of time to keep files for.
     * @param rollingFilePattern Pattern matching the files, see {@link TimeBasedRollingPolicy}.
     */
    public TimeBasedCleanupPolicy(int units, TimeUnit resolution, String rollingFilePattern) {
        this(units, resolution, rollingFilePattern, Clock.systemDefaultZone());
    }

    /**
     * Create a time based cleanup policy.
     *
     * @param units The amount of time back to keep files for.
     * @param resolution The unit of time to keep files for.
     * @param rollingFilePattern Pattern matching the files, see {@link TimeBasedRollingPolicy}.
     * @param clock The clock to use.
     */
    public TimeBasedCleanupPolicy(int units, TimeUnit resolution, String rollingFilePattern, Clock clock) {
        Matcher matcher = FILE_PATTERN.matcher(rollingFilePattern);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("No timestamp input in rolling file pattern");
        }
        if (units < 1) {
            throw new IllegalArgumentException("Invalid duration: " + units);
        }

        this.filePrefix = matcher.group(1);
        this.fileSuffix = matcher.group(3);

        this.units = units;
        this.resolution = resolution;
        this.formatter = getFormatter(matcher.group(2), resolution);
        this.clock = clock;
    }

    @Nonnull
    @Override
    public List<String> getFilesToDelete(@Nonnull List<String> candidateFiles, @Nonnull String currentFileName) {
        List<String> out = new ArrayList<>(candidateFiles.size());

        ZonedDateTime removeBefore = ZonedDateTime.ofInstant(Instant.ofEpochMilli(clock.millis()), clock.getZone())
                                                  .minusSeconds(resolution.toSeconds(units));

        for (String candidate : candidateFiles) {
            if (candidate.startsWith(filePrefix) && candidate.endsWith(fileSuffix) &&
                candidate.length() > filePrefix.length() + fileSuffix.length()) {
                try {
                    String date = candidate.substring(filePrefix.length(), candidate.length() - fileSuffix.length());
                    ZonedDateTime ts = ZonedDateTime.ofInstant(Instant.ofEpochMilli(LocalDateTime.parse(date, formatter)
                                                                                                 .toInstant(ZoneOffset.UTC)
                                                                                                 .toEpochMilli()), clock.getZone());
                    if (ts.isBefore(removeBefore)) {
                        out.add(candidate);
                    }
                } catch (Exception ignore) {
                    // Just ignore any errors.
                }
            }
        }
        return out;
    }

    private final String            filePrefix;
    private final String            fileSuffix;
    private final int               units;
    private final TimeUnit          resolution;
    private final DateTimeFormatter formatter;
    private final Clock             clock;
}
