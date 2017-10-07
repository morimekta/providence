package net.morimekta.providence.mio;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.mio.rolling.KeepLastNCleanupPolicy;
import net.morimekta.providence.mio.rolling.SizeBasedRollingPolicy;
import net.morimekta.providence.mio.rolling.TimeBasedCleanupPolicy;
import net.morimekta.providence.mio.rolling.TimeBasedRollingPolicy;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.testing.time.FakeClock;

import com.google.common.collect.ImmutableSortedSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static net.morimekta.providence.mio.RollingFileMessageWriterTest.Tmp.publicGetFormatter;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RollingFileMessageWriterTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public MessageGenerator generator = new MessageGenerator();

    @Test
    public void testTimeBasedRolling() throws IOException {
        FakeClock clock = FakeClock.forCurrentTimeMillis(1234567890000L)
                                   .withZone(ZoneId.of("Europe/Oslo"));

        RollingFileMessageWriter writer = new RollingFileMessageWriter(tmp.getRoot(),
                                                                       new JsonSerializer().named(),
                                                                       "my-log.txt",
                                                                       new TimeBasedRollingPolicy(5,
                                                                                                  TimeUnit.MINUTES,
                                                                                                  "my-log-%d{yyyy-MM-dd_HH-mm}.txt",
                                                                                                  clock),
                                                                       new TimeBasedCleanupPolicy(15,
                                                                                                  TimeUnit.MINUTES,
                                                                                                  "my-log-%d{yyyy-MM-dd_HH-mm}.txt",
                                                                                                  clock));

        writer.write(generator.generate(CompactFields.kDescriptor));

        for (int i = 0; i < 10; ++i) {
            clock.tick(107, TimeUnit.SECONDS);
            writer.write(generator.generate(CompactFields.kDescriptor));
        }

        String[] files = tmp.getRoot()
                            .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-2009-02-13_22-35.txt",
                                            "my-log-2009-02-13_22-40.txt",
                                            "my-log-2009-02-13_22-45.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(), is("my-log-2009-02-13_22-45.txt"));

        for (int i = 0; i < 10; ++i) {
            clock.tick(107, TimeUnit.SECONDS);
            writer.write(generator.generate(CompactFields.kDescriptor));
        }

        files = tmp.getRoot()
                   .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-2009-02-13_22-55.txt",
                                            "my-log-2009-02-13_23-00.txt",
                                            "my-log-2009-02-13_23-05.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(), is("my-log-2009-02-13_23-05.txt"));

        writer = new RollingFileMessageWriter(tmp.getRoot(),
                                              new JsonSerializer().named(),
                                              "my-log.txt",
                                              new TimeBasedRollingPolicy(TimeUnit.HOURS,
                                                                         "my-log-%d{yyyy-MM-dd_HH-mm}.txt",
                                                                         clock));

        writer.write(generator.generate(CompactFields.kDescriptor));

        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(), is("my-log-2009-02-13_23-00.txt"));

    }

    @Test
    public void testTimeBasedRollingFails() {
        try {
            new TimeBasedRollingPolicy(TimeUnit.HOURS, "does-not-have-date");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No timestamp input in rolling file pattern"));
        }

        try {
            new TimeBasedRollingPolicy(TimeUnit.HOURS, "bad-%d{format}");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Unknown pattern letter: f"));
        }

        try {
            new TimeBasedRollingPolicy(TimeUnit.MICROSECONDS, "bad-%d{}-resolution");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Not a valid log rotation resolution: MICROSECONDS, must be days, hours or minutes"));
        }

        try {
            new TimeBasedRollingPolicy(-1, TimeUnit.HOURS, "negative-%d{}-time");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid duration: -1"));
        }

        // ---

        try {
            new TimeBasedCleanupPolicy(1, TimeUnit.HOURS, "does-not-have-date");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No timestamp input in rolling file pattern"));
        }

        try {
            new TimeBasedCleanupPolicy(-1, TimeUnit.HOURS, "negative-%d{}-time");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid duration: -1"));
        }

        // ---
        // and not exception on minimal OK constructor.

        new TimeBasedRollingPolicy(TimeUnit.HOURS, "ok-%d{}-pattern");
        new TimeBasedCleanupPolicy(1, TimeUnit.HOURS, "ok-%d{}-pattern");
    }

    static class Tmp extends TimeBasedRollingPolicy {
        public Tmp(TimeUnit resolution, String rollingFilePattern) {
            super(resolution, rollingFilePattern);
        }

        public static DateTimeFormatter publicGetFormatter(String timestampPattern,
                                                           TimeUnit resolution) {
            return getFormatter(timestampPattern, resolution);
        }
    }

    @Test
    public void testTimeBaseDefaultFormatter() {
        FakeClock clock = FakeClock.forCurrentTimeMillis(1234567890000L);

        assertThat(publicGetFormatter("", TimeUnit.DAYS).format(ZonedDateTime.now(clock)),
                   is("2009-02-13"));
        assertThat(publicGetFormatter("", TimeUnit.HOURS).format(ZonedDateTime.now(clock)),
                   is("2009-02-13T23"));
        assertThat(publicGetFormatter("", TimeUnit.MINUTES).format(ZonedDateTime.now(clock)),
                   is("2009-02-13T23:31"));
    }

    @Test
    public void testSizeBasedRolling() throws IOException {
        RollingFileMessageWriter writer = new RollingFileMessageWriter(tmp.getRoot(),
                                                                       new JsonSerializer().named(),
                                                                       "my-log.txt",
                                                                       new SizeBasedRollingPolicy(tmp.getRoot(),
                                                                                                  1000,
                                                                                                  "my-log-%03d.txt"),
                                                                       new KeepLastNCleanupPolicy(5,
                                                                                                  "my-log-[\\d]{3}.txt"));

        // The CompactFields class should be consistent enough in size to
        // work as test-type with fixed number of iterations. Of course there
        // is a possibility of a blue moon large or small streak of messages
        // failing the test.
        writer.write(generator.generate(CompactFields.kDescriptor));
        for (int i = 0; i < 100; ++i) {
            writer.write(generator.generate(CompactFields.kDescriptor));
        }

        String[] files = tmp.getRoot()
                            .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-005.txt",
                                            "my-log-006.txt",
                                            "my-log-007.txt",
                                            "my-log-008.txt",
                                            "my-log-009.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(),
                   is("my-log-009.txt"));

        writer = new RollingFileMessageWriter(tmp.getRoot(),
                                              new JsonSerializer().named(),
                                              "my-log.txt",
                                              new SizeBasedRollingPolicy(tmp.getRoot(), 1000, "my-log-%03d.txt"),
                                              new KeepLastNCleanupPolicy(5, "my-log-[\\d]{3}.txt"));

        writer.write(generator.generate(CompactFields.kDescriptor));
        for (int i = 0; i < 30; ++i) {
            writer.write(generator.generate(CompactFields.kDescriptor));
        }

        files = tmp.getRoot()
                   .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-001.txt",
                                            "my-log-002.txt",
                                            "my-log-003.txt",
                                            "my-log-008.txt",
                                            "my-log-009.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(),
                   is("my-log-003.txt"));

        writer = new RollingFileMessageWriter(tmp.getRoot(),
                                              new JsonSerializer().named(),
                                              "my-log.txt",
                                              new SizeBasedRollingPolicy(tmp.getRoot(), 1000, "my-log-%03d.txt"),
                                              new KeepLastNCleanupPolicy(5, "my-log-[\\d]{3}.txt"));

        writer.write(generator.generate(CompactFields.kDescriptor));
        for (int i = 0; i < 30; ++i) {
            writer.write(generator.generate(CompactFields.kDescriptor));
        }

        files = tmp.getRoot()
                   .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-002.txt",
                                            "my-log-003.txt",
                                            "my-log-004.txt",
                                            "my-log-005.txt",
                                            "my-log-006.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(),
                   is("my-log-006.txt"));
    }

    @Test
    public void testCallAndSeparator() throws IOException {
        RollingFileMessageWriter writer = new RollingFileMessageWriter(tmp.getRoot(),
                                                                       new JsonSerializer().named(),
                                                                       "my-log.txt",
                                                                       new SizeBasedRollingPolicy(tmp.getRoot(),
                                                                                                  10000,
                                                                                                  "my-log-%03d.txt"));

        writer.write(new PServiceCall<>("test",
                                        PServiceCallType.EXCEPTION,
                                        73,
                                        new PApplicationException("boo", PApplicationExceptionType.BAD_SEQUENCE_ID)));

        assertThat(writer.separator(), is(0));
        assertThat(writer.separator(), is(0));
        assertThat(writer.separator(), is(0));

        writer.close();

        String[] files = tmp.getRoot()
                            .list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files), is(ImmutableSortedSet.of("my-log.txt", "my-log-001.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot()
                                           .toPath()
                                           .resolve("my-log.txt")), is(true));
        assertThat(Files.readSymbolicLink(tmp.getRoot()
                                             .toPath()
                                             .resolve("my-log.txt"))
                        .toFile()
                        .getName(), is("my-log-001.txt"));

        String content = new String(Files.readAllBytes(tmp.getRoot()
                                                          .toPath()
                                                          .resolve("my-log.txt")), StandardCharsets.UTF_8);
        assertThat(content,
                   is("[\"test\",\"exception\",73,{\"message\":\"boo\",\"id\":\"BAD_SEQUENCE_ID\"}]\n"));
    }
}
