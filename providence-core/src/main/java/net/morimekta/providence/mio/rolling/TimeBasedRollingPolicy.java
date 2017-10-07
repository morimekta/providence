package net.morimekta.providence.mio.rolling;

import net.morimekta.providence.mio.RollingFileMessageWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeBasedRollingPolicy implements RollingFileMessageWriter.RollingPolicy {
    public TimeBasedRollingPolicy(TimeUnit resolution,
                                  String rollingFilePattern) {
        this(1, resolution, rollingFilePattern);
    }

    public TimeBasedRollingPolicy(int units,
                                  TimeUnit resolution,
                                  String rollingFilePattern) {
        this(units, resolution, rollingFilePattern, Clock.systemDefaultZone());
    }

    public TimeBasedRollingPolicy(TimeUnit resolution,
                                  String rollingFilePattern,
                                  Clock clock) {
        this(1, resolution, rollingFilePattern, clock);
    }

    public TimeBasedRollingPolicy(int units,
                                  TimeUnit resolution,
                                  String rollingFilePattern,
                                  Clock clock) {
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

    @Override
    public void maybeUpdateCurrentFile(@Nonnull RollingFileMessageWriter.CurrentFileUpdater onRollFile,
                                       boolean initialCall) throws IOException {
        // This will normalize the timestamp to the first millisecond with the
        // requested resolution. Note that if the units are not really divisible
        // in the time unit, you may get some wacky timestamps, e.g. having a resolution
        // of 11 minutes.
        long ts = resolution.toMillis(
                units * (resolution.convert(clock.millis(), TimeUnit.MILLISECONDS) / units));
        if (initialCall || ts != lastUpdateTs) {
            // This is the actual roll over.
            onRollFile.updateCurrentFile(filePrefix + fileTimestamp(ts) + fileSuffix);
            lastUpdateTs = ts;
        }
    }

    static final Pattern FILE_PATTERN = Pattern.compile(
            "(.*)[%]d[{]([^{}]*)[}](.*)");

    private final String   filePrefix;
    private final String   fileSuffix;
    private final int      units;
    private final TimeUnit resolution;
    private final DateTimeFormatter formatter;
    private final Clock    clock;

    private long lastUpdateTs;

    private String fileTimestamp(long ts) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), Clock.systemUTC().getZone())
                                         .withZoneSameLocal(clock.getZone());
        return formatter.format(zdt);
    }

    protected static DateTimeFormatter getFormatter(String timestampPattern,
                                                    TimeUnit resolution) {
        if (timestampPattern.length() > 0) {
            return DateTimeFormatter.ofPattern(timestampPattern);
        }

        switch (resolution) {
            case DAYS: return DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case HOURS: return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
            case MINUTES: return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            default: throw new IllegalArgumentException("Not a valid log rotation resolution: " + resolution.toString() + ", must be days, hours or minutes");
        }
    }
}
