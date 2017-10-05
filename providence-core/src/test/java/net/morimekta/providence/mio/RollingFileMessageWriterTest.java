package net.morimekta.providence.mio;

import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.testing.time.FakeClock;

import com.google.common.collect.ImmutableSortedSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RollingFileMessageWriterTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public MessageGenerator generator = new MessageGenerator();

    @Test
    public void testAFewWrites() throws IOException {
        FakeClock clock = FakeClock.forCurrentTimeMillis(1234567890000L);

        RollingFileMessageWriter writer = new RollingFileMessageWriter(tmp.getRoot(),
                                                                       TimeUnit.MINUTES,
                                                                       new JsonSerializer().named(),
                                                                       "my-log",
                                                                       ".txt",
                                                                       clock);

        writer.write(generator.generate(CompactFields.kDescriptor));

        clock.tick(10, TimeUnit.SECONDS);

        writer.write(generator.generate(CompactFields.kDescriptor));

        clock.tick(100, TimeUnit.SECONDS);

        writer.write(generator.generate(CompactFields.kDescriptor));

        String[] files = tmp.getRoot().list();
        assertThat(files, is(notNullValue()));
        assertThat(ImmutableSortedSet.copyOf(files),
                   is(ImmutableSortedSet.of("my-log.txt",
                                            "my-log-2009-02-13_23-31.txt",
                                            "my-log-2009-02-13_23-33.txt")));
        assertThat(Files.isSymbolicLink(tmp.getRoot().toPath().resolve("my-log.txt")), is(true));
    }
}
