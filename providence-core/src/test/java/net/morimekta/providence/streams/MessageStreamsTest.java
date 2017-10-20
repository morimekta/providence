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
package net.morimekta.providence.streams;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.test.providence.core.CompactFields;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Stein Eldar Johnsen
 * @since 07.11.15.
 */
public class MessageStreamsTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private List<CompactFields> list;

    @Before
    public void setUp() {
        list = ImmutableList.of(new CompactFields("first", 1234, "The first!"),
                                new CompactFields("second", 4321, null),
                                new CompactFields("third", 5432, "Noop!"));
    }

    @Test
    public void testToPath() throws IOException {
        Path file = tmp.newFile().toPath();

        int size = list.stream()
                       .collect(MessageCollectors.toPath(file, new BinarySerializer()));

        assertThat(Files.size(file), is((long) size));

        List<CompactFields> out = MessageStreams.path(file, new BinarySerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));
    }

    @Test
    public void testToFile() throws IOException {
        File file = tmp.newFile();

        int size = list.stream()
                       .collect(MessageCollectors.toFile(file, new BinarySerializer()));

        assertThat(Files.size(file.toPath()), is((long) size));

        List<CompactFields> out = MessageStreams.file(file, new BinarySerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));

        try {
            list.stream().collect(MessageCollectors.toFile(new File(tmp.getRoot(), "does/not"), new BinarySerializer()));
            fail("no exception");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(UncheckedIOException.class)));
            assertThat(e.getMessage(), is("Unable to open not"));
        }
    }

    @Test
    public void testFileCollector() throws IOException {
        File file = tmp.newFile("tmp");

        Collector<CompactFields, OutputStream, Integer> collector = MessageCollectors.toFile(file, new PrettySerializer().config());

        OutputStream out = mock(OutputStream.class);
        doThrow(new IOException("oops")).when(out).write(MessageStreams.READABLE_ENTRY_SEP);
        doThrow(new IOException("close")).when(out).close();

        assertThat(collector.combiner().apply(out, out), is(sameInstance(out)));

        try {
            collector.accumulator().accept(out, list.get(0));
            fail("no exception");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), is("Unable to write to tmp"));
            assertThat(e.getCause(), is(instanceOf(IOException.class)));
            assertThat(e.getCause().getMessage(), is("oops"));
        }

        try {
            collector.finisher().apply(out);
            fail("no exception");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), is("Unable to close tmp"));
            assertThat(e.getCause(), is(instanceOf(IOException.class)));
            assertThat(e.getCause().getMessage(), is("close"));
        }

        Serializer ms = mock(Serializer.class);
        doThrow(new SerializerException("oops2")).when(ms).serialize(out, list.get(0));
        collector = MessageCollectors.toFile(file, ms);
        try {
            collector.accumulator().accept(out, list.get(0));
            fail("no exception");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), is("Bad data"));
            assertThat(e.getCause(), is(instanceOf(SerializerException.class)));
            assertThat(e.getCause().getMessage(), is("oops2"));
        }
    }

    @Test
    public void testToStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int size = list.stream()
                       .collect(MessageCollectors.toStream(baos, new JsonSerializer()));

        assertThat(baos.size(), is(size));

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List<CompactFields> out = MessageStreams.stream(bais, new JsonSerializer(), CompactFields.kDescriptor)
                                                .collect(Collectors.toList());

        assertThat(out, is(equalTo(list)));
    }

    @Test
    public void testStreamCollector() throws IOException {
        Serializer serializer = mock(Serializer.class);
        OutputStream out = mock(OutputStream.class);

        AtomicInteger i = new AtomicInteger();
        Collector<CompactFields,AtomicInteger,Integer> collector = MessageCollectors.toStream(out, serializer);

        doReturn(false).when(serializer).binaryProtocol();
        doThrow(new IOException("write")).when(out).write(MessageStreams.READABLE_ENTRY_SEP);

        i.set(5);
        AtomicInteger j = new AtomicInteger(6);

        assertThat(collector.combiner().apply(i, j).get(), is(11));

        try {
            collector.accumulator().accept(i, list.get(0));
            fail("no exception");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), is("write"));
        }
    }

    @Test
    public void testResource() {
        try {
            MessageStreams.resource("/no_such_resource.json", new JsonSerializer(), CompactFields.kDescriptor);
            fail("no exception");
        } catch (IOException e) {
            assertThat(e.getMessage(), is("No such resource /no_such_resource.json"));
        }
    }

    @Test
    public void testSpliterator() throws IOException {
        InputStream in = mock(InputStream.class);
        Serializer serializer = mock(Serializer.class);
        Consumer<CompactFields> consumer = mock(Consumer.class);

        MessageSpliterator<CompactFields,CompactFields._Field> spliterator = new MessageSpliterator<>(
                in, serializer, CompactFields.kDescriptor);

        assertThat(spliterator.trySplit(), is(nullValue()));
        assertThat(spliterator.estimateSize(), is(Long.MAX_VALUE));
        assertThat(spliterator.getExactSizeIfKnown(), is(-1L));
        assertThat(spliterator.characteristics(), is(Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE));
        assertThat(spliterator.getComparator(), is(notNullValue()));

        CompactFields cf = CompactFields.builder().build();
        // -- read
        doReturn(cf).when(serializer).deserialize(in, CompactFields.kDescriptor);

        assertThat(spliterator.tryAdvance(consumer), is(true));
        verify(serializer).deserialize(in, CompactFields.kDescriptor);
        verify(in).markSupported();
        verify(consumer).accept(same(cf));

        reset(in, serializer, consumer);
        doReturn(false).when(serializer).binaryProtocol();
        doReturn(-1).when(in).read();

        assertThat(spliterator.tryAdvance(consumer), is(false));
        verify(in).markSupported();
        verifyNoMoreInteractions(in);
        verifyZeroInteractions(consumer);

        // fails.
        reset(in, serializer, consumer);
        spliterator = new MessageSpliterator<>(in, serializer, CompactFields.kDescriptor);

        doReturn(true).when(serializer).binaryProtocol();
        doReturn(true).when(in).markSupported();
        doThrow(new IOException("read")).when(in).read();
        doThrow(new IOException("close")).when(in).close();

        try {
            spliterator.tryAdvance(consumer);
            fail("no exception");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), is("read"));
            assertThat(e.getCause(), is(notNullValue()));
            assertThat(e.getCause().getSuppressed().length, is(1));
            assertThat(e.getCause().getSuppressed()[0].getMessage(), is("close"));
        }

        verify(in).markSupported();
        verify(in).mark(2);
        verify(in).read();
        verify(in).close();
        verifyNoMoreInteractions(in);
        verifyZeroInteractions(serializer, consumer);
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<MessageCollectors> collector = MessageCollectors.class.getDeclaredConstructor();
        assertThat(collector.isAccessible(), is(false));
        try {
            collector.setAccessible(true);
            assertThat(collector.newInstance(), instanceOf(MessageCollectors.class));
        } finally {
            collector.setAccessible(false);
        }

        Constructor<MessageStreams> stream = MessageStreams.class.getDeclaredConstructor();
        assertThat(stream.isAccessible(), is(false));
        try {
            stream.setAccessible(true);
            assertThat(stream.newInstance(), instanceOf(MessageStreams.class));
        } finally {
            stream.setAccessible(false);
        }
    }
}
