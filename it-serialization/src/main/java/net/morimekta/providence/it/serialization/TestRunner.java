package net.morimekta.providence.it.serialization;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.terminal.Progress;
import net.morimekta.console.terminal.Terminal;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.morimekta.console.util.Parser.dbl;
import static net.morimekta.console.util.Parser.file;
import static net.morimekta.console.util.Parser.i32;
import static net.morimekta.console.util.Parser.oneOf;

/**
 * Speed test running through:
 * - Read a file for each factory / serialization format.
 * - Write the same file back to a temp file.
 */
public class TestRunner<PM extends PMessage<PM, PF>, PF extends PField,
                        TM extends TBase<TM, TF>, TF extends TFieldIdEnum> {
    private final PMessageDescriptor<PM, PF> descriptor;
    private final Supplier<TM>               supplier;
    private final TestOptions                options;

    public TestRunner(PMessageDescriptor<PM, PF> descriptor,
                      Supplier<TM>               supplier,
                      TestOptions                options) {
        this.descriptor = descriptor;
        this.supplier = supplier;
        this.options = options;
    }

    public void run() throws IOException, TException, InterruptedException {
        ITGenerator<PM, PF, TM, TF> generator = new ITGenerator<>(
                descriptor, supplier, options.generator);
        if (options.file.get() != null) {
            generator.load(options.file.get());
        } else {
            generator.generate(options.generate.get());
        }
        ITRunner<PM, PF, TM, TF> runner = new ITRunner<>(
                descriptor, supplier, generator.getProvidence(), generator.getThrift());

        List<FormatStatistics> formats = Arrays.stream(Format.values())
                                               .map(FormatStatistics::new)
                                               .collect(Collectors.toList());

        final int runs = options.runs.get();
        final int iterations_per_run = 40;

        ExecutorService executor = Executors.newFixedThreadPool(5);
        try (Terminal terminal = new Terminal()) {
            Progress progress = options.no_progress.get() ?
                                null :
                                new Progress(terminal, Progress.Spinner.ASCII, "test<" + descriptor.getQualifiedName()  + ">", formats.size() * runs * iterations_per_run);
            AtomicBoolean stop = new AtomicBoolean();

            terminal.executeAbortable(executor, () -> {
                int k = 1;
                try {
                    for (int i = 0; i < runs; ++i) {
                        Collections.shuffle(formats);
                        for (FormatStatistics test : formats) {
                            for (int j = 0; j < iterations_per_run; ++j) {
                                if (stop.get()) return;
                                runner.run(test);
                                if (progress != null) {
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
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (executor.isShutdown()) {
            System.exit(1);
        }

        System.out.println();

        formats.forEach(FormatStatistics::calculate);

        FormatStatistics relativeTo = formats.stream()
                                             .filter(f -> f.format == Format.binary)
                                             .findFirst().orElseThrow(() -> new IllegalStateException("Oops"));
        formats.sort(FormatStatistics::compareTo);

        System.out.println(Format.header());
        System.out.println();
        formats.forEach(f -> System.out.println(f.statistics(relativeTo)));
        System.out.println();
        formats.forEach(f -> f.verify(relativeTo));
        System.out.println();
    }

    public static void main(String... args) throws InterruptedException, TException, IOException {
        try {
            TestOptions options = new TestOptions();
            ArgumentParser parser = new ArgumentParser("it-serialization",
                                                       "SNAPSHOT",
                                                       "Serialization speed Integration test");
            parser.add(new Flag("--no_progress", "s",
                                "No progress output",
                                options.no_progress::set));
            parser.add(new Option("--runs", "r", "RUNS",
                                  "Number of runs to do",
                                  i32(options.runs::set), "100"));
            parser.add(new Option("--generate", "g", "NUM",
                                  "Number of messages to generate",
                                  i32(options.generate::set), "10"));
            parser.add(new Option("--fill_rate", "f", "FRACT",
                                  "Fill rate, 0.0 - 1.0 of fields in generated classes",
                                  dbl(options.generator::setFillRate), "1.0"));
            parser.add(new Option("--load", null, "FILE",
                                  "File to load data from. Required test to match content",
                                  file(options.file::set)));
            parser.add(new Argument("test", "Which test to run",
                                    oneOf(TestOptions.Test.class, options.test::set), "all"));

            parser.parse(args);
            parser.validate();

            if (options.test.get() == null ||
                options.test.get() == TestOptions.Test.containers) {
                new TestRunner<>(net.morimekta.test.providence.serialization.containers.ManyContainers.kDescriptor,
                                 net.morimekta.test.thrift.serialization.containers.ManyContainers::new,
                                 options).run();
            }
            if (options.test.get() == null ||
                options.test.get() == TestOptions.Test.fields) {
                new TestRunner<>(net.morimekta.test.providence.serialization.messages.ManyFields.kDescriptor,
                                 net.morimekta.test.thrift.serialization.messages.ManyFields::new,
                                 options).run();
            }
            if (options.test.get() == null ||
                options.test.get() == TestOptions.Test.deep) {
                new TestRunner<>(net.morimekta.test.providence.serialization.deep.DeepStructure.kDescriptor,
                                 net.morimekta.test.thrift.serialization.deep.DeepStructure::new,
                                 options).run();
            }
        } catch (TException e) {
            e.printStackTrace();
            System.err.println("TException" + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println();
        System.exit(0);
    }
}
