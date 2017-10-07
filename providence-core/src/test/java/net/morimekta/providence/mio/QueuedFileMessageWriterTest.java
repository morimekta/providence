package net.morimekta.providence.mio;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.util_internal.MessageGenerator;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.testing.concurrent.FakeScheduledExecutor;
import net.morimekta.testing.time.FakeClock;

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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        sleep(10L);

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        sleep(1L);

        writer.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        List<CompactFields> result = MessageStreams.stream(bais, new BinarySerializer(), CompactFields.kDescriptor)
                                                   .collect(Collectors.toList());

        assertThat(result, hasSize(100));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAFewWrites_serviceCalls() throws IOException, InterruptedException {
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

                        PServiceCall tmp = new PServiceCall("test",
                                                            PServiceCallType.CALL,
                                                            j,
                                                            generator.generate(CompactFields.kDescriptor));

                        writer.write(tmp);
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
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        sleep(1L);

        writer.close();
    }

    @Test
    public void testClose_noQueue() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOMessageWriter target = new IOMessageWriter(baos, new BinarySerializer());

        ExecutorService executor = mock(ExecutorService.class);

        when(executor.isShutdown()).thenReturn(false);
        when(executor.awaitTermination(1000L, TimeUnit.MILLISECONDS)).thenReturn(false);

        QueuedMessageWriter writer = new QueuedMessageWriter(target, executor);
        writer.close();

        verify(executor).submit(any(Runnable.class));
        verify(executor).isShutdown();
        verify(executor).shutdown();
        verify(executor).awaitTermination(1000L, TimeUnit.MILLISECONDS);
        verify(executor).shutdownNow();

        verifyNoMoreInteractions(executor);

        assertThat(baos.toByteArray(), is(new byte[]{}));
    }

    @Test
    public void testSeparator() throws IOException {
        setDefaultPollDelay(new Duration(10, TimeUnit.MILLISECONDS));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOMessageWriter target = new IOMessageWriter(baos, new BinarySerializer());

        QueuedMessageWriter writer = new QueuedMessageWriter(target);

        assertThat(writer.separator(), is(0));

        writer.close();

        assertThat(baos.size(), is(0));
    }

    @Test
    public void testClose_withQueue() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOMessageWriter target = new IOMessageWriter(baos, new JsonSerializer().named());

        ExecutorService executor = mock(ExecutorService.class);

        when(executor.isShutdown()).thenReturn(false);
        when(executor.awaitTermination(1000L, TimeUnit.MILLISECONDS)).thenReturn(true);

        QueuedMessageWriter writer = new QueuedMessageWriter(target, executor);
        writer.write(CompactFields.builder().setId(1).setName("Name").build());
        writer.write(new PServiceCall<>("name",
                                        PServiceCallType.REPLY,
                                        42,
                                        CompactFields.builder()
                                                     .setId(1)
                                                     .setName("Name")
                                                     .build()));
        writer.close();

        verify(executor).submit(any(Runnable.class));
        verify(executor).isShutdown();
        verify(executor).shutdown();
        verify(executor).awaitTermination(1000L, TimeUnit.MILLISECONDS);
        verifyNoMoreInteractions(executor);

        assertThat(new String(baos.toByteArray()),
                   is("[\"Name\",1]\n" +
                      "[\"name\",\"reply\",42,[\"Name\",1]]\n" +
                      ""));
    }

    @Test
    public void testClose_closed() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOMessageWriter target = new IOMessageWriter(baos, new BinarySerializer());

        ExecutorService executor = mock(ExecutorService.class);

        when(executor.isShutdown()).thenReturn(true);

        QueuedMessageWriter writer = new QueuedMessageWriter(target, executor);
        writer.close();

        verify(executor).submit(any(Runnable.class));
        verify(executor).isShutdown();
        verifyNoMoreInteractions(executor);

        assertThat(baos.toByteArray(), is(new byte[]{}));
    }

    @Test
    public void testClose_exceptions() throws IOException, InterruptedException {
        MessageWriter target = mock(MessageWriter.class);
        ExecutorService executor = mock(ExecutorService.class);

        when(target.write(any(CompactFields.class))).thenThrow(new IOException("fail"));

        when(executor.isShutdown()).thenReturn(false);
        when(executor.awaitTermination(1000L, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());

        QueuedMessageWriter writer = new QueuedMessageWriter(target, executor);
        writer.write(new CompactFields("foo", 42, "bar"));

        try {
            writer.close();
            fail("no exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }

        verify(executor).submit(any(Runnable.class));
        verify(executor).isShutdown();
        verify(executor).shutdown();
        verify(executor).awaitTermination(1000L, TimeUnit.MILLISECONDS);

        verify(target).write(new CompactFields("foo", 42, "bar"));
        verify(target).close();

        verifyNoMoreInteractions(executor, target);
    }

    private static class FakeQueuedMessageWriter extends QueuedMessageWriter {
        private final FakeClock fakeClock;

        public FakeQueuedMessageWriter(MessageWriter writer,
                                       ExecutorService executor,
                                       FakeClock fakeClock) {
            super(writer, executor);
            this.fakeClock = fakeClock;
        }

        @Override
        protected void sleep(long ms) throws InterruptedException {
            fakeClock.tick(ms, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testFailedWrite() throws IOException {
        MessageWriter target = mock(MessageWriter.class);

        FakeClock clock = new FakeClock();
        long start = clock.millis();

        FakeScheduledExecutor executor = new FakeScheduledExecutor(clock);

        QueuedMessageWriter writer = new FakeQueuedMessageWriter(target, executor, clock);
        writer.write(new CompactFields("foo", 42, "bar"));
        writer.write(new CompactFields("foo", 42, "bar"));

        when(target.write(any(CompactFields.class))).thenThrow(new IOException());
        when(target.write(any(CompactFields.class))).thenAnswer(i -> {
            executor.shutdown();
            throw new IOException();
        });

        clock.tick(1);

        assertThat(clock.millis() - start, is(138L));

        verify(target).write(any(CompactFields.class));
        verify(target).write(any(CompactFields.class));
        verifyNoMoreInteractions(target);
    }
}
