package net.morimekta.providence.it.generator;

import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Option;
import net.morimekta.providence.it.data.MessageGenerator;
import net.morimekta.providence.it.data.RandomGenerator;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.test.providence.Containers;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.morimekta.console.util.Parser.dbl;
import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.console.util.Parser.i32;
import static net.morimekta.console.util.Parser.oneOf;

/**
 * Generate test data for speed test.
 */
public class DataGenerator {
    private RandomGenerator random = new RandomGenerator(new Random());
    private MessageGenerator generator = new MessageGenerator(random);
    private AtomicInteger entries = new AtomicInteger(10000);
    private AtomicReference<File> out = new AtomicReference<>(new File("."));
    private AtomicReference<Format> format = new AtomicReference<>(Format.binary);

    public enum Format {
        pretty("txt"),
        json("json"),
        binary("bin"),
        ;

        String suffix;
        Format(String s) {
            suffix = s;
        }
    }

    public ArgumentParser getArgumentParser() {
        ArgumentParser parser = new ArgumentParser("it-generator", "v0.x", "Data generator for extended testing.");

        parser.add(new Option("--entries", null, "num",
                "Number of entries to make",
                i32(entries::set), String.valueOf(entries.get())));
        parser.add(new Option("--fill", null, "ratio",
                "The field fill ratio, or the likelihood any given field is set",
                dbl(generator::setFillRatio), String.valueOf(generator.getFillRatio())));
        parser.add(new Option("--key-len", null, "num",
                "Byte or char count in map keys",
                i32(generator::setKeyLen), String.valueOf(generator.getKeyLen())));
        parser.add(new Option("--data-len", null, "num",
                "Byte or char count in string or binary values",
                i32(generator::setDataLen), String.valueOf(generator.getDataLen())));
        parser.add(new Option("--format", null, "fmt",
                "Output serialization format",
                oneOf(Format.class, format::set), Format.pretty.name()));
        parser.add(new Option("--out", null, "dir",
                "Directory to output generated files to",
                dir(out::set), "."));

        return parser;
    }

    public void run(String... args) throws IOException, SerializerException {
        ArgumentParser parser = getArgumentParser();
        parser.parse(args);

        Instant start = Instant.now();

        System.out.print("Generating data");

        ArrayList<Containers> data = new ArrayList<>();

        for (int i = 0; i < entries.get(); ++i) {
            if (i % 100 == 0) {
                System.out.print(".");
                System.out.flush();
            }
            data.add(generator.nextContainers());
        }

        System.out.println();
        System.out.flush();

        Instant end = Instant.now();

        System.out.format(Locale.ENGLISH,
                " -- Used %.3f ms\n", ((double) (end.getNano() - start.getNano()) / 1000d));

        Serializer serializer;
        switch (format.get()) {
            case binary:
                serializer = new BinarySerializer();
                break;
            case json:
                serializer = new JsonSerializer(JsonSerializer.IdType.ID);
                break;
            case pretty:
                serializer = new PrettySerializer();
                break;
            default:
                throw new AssertionError("Unreachable code!");
        }

        File outFile = new File(out.get(), String.format("%s.%s", format.get().name(), format.get().suffix));
        if (outFile.exists()) {
            outFile.delete();
        }

        System.out.print("Writing to file: " + outFile.toString());

        start = Instant.now();

        AtomicInteger i = new AtomicInteger();
        Integer size = data.stream().map(m -> {
            if (i.incrementAndGet() % 100 == 0) {
                System.out.print(".");
                System.out.flush();
            }
            return m;
        }).collect(MessageCollectors.toFile(outFile, serializer));

        end = Instant.now();

        System.out.format(Locale.ENGLISH,
                " -- Used %.1f ms for %,dkB\n",
                ((double) (end.getNano() - start.getNano()) / 1000d),
                size / 1024);
    }

    public static void main(String... args) throws SerializerException, IOException {
        new DataGenerator().run(args);
    }
}
