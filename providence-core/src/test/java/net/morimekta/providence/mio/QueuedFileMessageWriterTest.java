package net.morimekta.providence.mio;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.CompactFields;

import org.awaitility.Duration;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static org.awaitility.Awaitility.setDefaultPollDelay;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QueuedFileMessageWriterTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testAFewWrites() throws IOException, InterruptedException {
        setDefaultPollDelay(new Duration(10, TimeUnit.MILLISECONDS));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOMessageWriter target = new IOMessageWriter(baos, new BinarySerializer());

        QueuedMessageWriter writer = new QueuedMessageWriter(target);

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        for (int i = 0; i < 10; ++i) {
            executorService.submit(() -> {
                MessageGenerator generator = new MessageGenerator();
                for (int j = 0; j < 10; ++j) {
                    try {
                        if (j > 0) sleep(1L);

                        writer.write(generator.generate(CompactFields.kDescriptor));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e.getMessage(), e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        sleep(10L);

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS));

        sleep(1L);

        writer.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        List<CompactFields> result = MessageStreams.stream(bais, new BinarySerializer(), CompactFields.kDescriptor)
                                                   .collect(Collectors.toList());

        assertThat(result, hasSize(100));
    }
}
