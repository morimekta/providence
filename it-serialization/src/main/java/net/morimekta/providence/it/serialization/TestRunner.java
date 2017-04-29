package net.morimekta.providence.it.serialization;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.chr.Char;
import net.morimekta.console.terminal.Progress;
import net.morimekta.console.terminal.Terminal;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.test.providence.Containers;

import com.google.common.collect.ImmutableList;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static net.morimekta.providence.it.serialization.TestSerialization.forFormat;

/**
 * Speed test running through:
 * - Read a file for each factory / serialization format.
 * - Write the same file back to a temp file.
 */
public class TestRunner {
    private final List<Containers>                           providence;
    private final List<net.morimekta.test.thrift.Containers> thrift;
    private final AtomicBoolean                              silent;
    private final AtomicReference<File>                      file;

    public TestRunner(String... args) throws IOException, TException {
        silent = new AtomicBoolean();
        file = new AtomicReference<>();

        ImmutableList.Builder<Containers> providenceB = ImmutableList.builder();
        ImmutableList.Builder<net.morimekta.test.thrift.Containers> thriftB = ImmutableList.builder();

        ArgumentParser parser = new ArgumentParser("it-serialization", "SNAPSHOT", "Serialization speed Integration test");
        parser.add(new Flag("--silent", "s", "No progress output", silent::set, null, null));
        parser.add(new Argument("file", "Input file, binary format", Parser.file(file::set), null, null, false, true, false));
        parser.parse(args);
        parser.validate();

        MessageStreams.file(file.get(),
                new BinarySerializer(true),
                Containers.kDescriptor).forEach(providenceB::add);

        try (InputStream in = new FileInputStream(file.get())) {
            TTransport transport = new TIOStreamTransport(in);
            TProtocol protocol = new TBinaryProtocol(transport, true, true);

            while (in.available() > 0) {
                net.morimekta.test.thrift.Containers containers = new net.morimekta.test.thrift.Containers();
                containers.read(protocol);
                thriftB.add(containers);
            }
        }

        providence = providenceB.build();
        thrift = thriftB.build();
    }

    public void run() throws IOException, TException, InterruptedException {
        ArrayList<TestSerialization> formats = new ArrayList<>();
        asList(Format.values()).forEach(f -> formats.add(forFormat(f)));

        final int runs = 100;
        final int tasks = 40;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try (Terminal terminal = new Terminal()) {
            Progress progress = silent.get() ?
                                null :
                                new Progress(terminal, Progress.Spinner.ASCII, "testSerializationSpeed", formats.size() * runs * tasks);
            AtomicBoolean stop = new AtomicBoolean();

            Future<?> task = executor.submit(() -> {
                int k = 1;
                try {
                    for (int i = 0; i < runs; ++i) {
                        Collections.shuffle(formats);
                        for (TestSerialization test : formats) {
                            for (int j = 0; j < tasks; ++j) {
                                if (stop.get()) return;
                                test.runProvidence(providence);
                                test.runThrift(thrift);
                                if (!silent.get()) {
                                    progress.update(k++);
                                }
                            }
                            System.gc();
                        }
                    }
                } catch (IOException | TException e) {
                    throw new RuntimeException(e);
                }
            });
            while (!task.isDone()) {
                Char c = terminal.readIfAvailable();
                if (c != null && c.asInteger() == Char.ABR) {
                    stop.set(true);
                    executor.shutdownNow();
                    task.cancel(true);
                    executor.awaitTermination(100L, TimeUnit.MILLISECONDS);
                    break;
                }
            }
        }
        if (executor.isShutdown()) {
            System.exit(1);
        }

        System.out.println();

        formats.forEach(TestSerialization::calculate);

        Optional<TestSerialization> opt = formats.stream().filter(f -> f.format == Format.binary).findFirst();
        if (!opt.isPresent()) {
            throw new IllegalStateException("Oops");
        }
        TestSerialization rel = opt.get();
        formats.sort(TestSerialization::compareTo);

        System.out.println(Format.header());
        System.out.println();
        formats.forEach(f -> System.out.println(f.statistics(rel)));
        System.out.println();
        formats.forEach(f -> f.verify(rel));
        System.out.println();
    }

    public static void main(String... args) {
        try {
            new TestRunner(args).run();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
