package net.morimekta.providence.testing.generator;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.test.android.CompactFields;
import net.morimekta.testing.rules.ConsoleWatcher;
import net.morimekta.util.ExtraStreams;

import io.codearte.jfairy.Fairy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class GeneratorWatcherTest {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    @Test
    public void testRandom_defaultDump() {
        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator =
                GeneratorWatcher.create()
                                .dumpOnFailure();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.hasId(), is(true));

        assertThat(generator.allGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(pretty(compact) + "\n"));
    }

    @Test
    public void testRandom_multipleMessages() {
        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator =
                GeneratorWatcher.create()
                                .setFillRate(0.667)
                                .dumpOnFailure();
        generator.starting(Description.EMPTY);

        LinkedList<CompactFields> list = new LinkedList<>();
        ExtraStreams.range(0, 100).forEach(i -> list.add(generator.generate(CompactFields.kDescriptor)));

        assertThat(list.size(), is(100));
        for (CompactFields compact : list) {
            assertThat(generator.allGenerated(), hasItem(compact));
        }

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        for (CompactFields compact : list) {
            assertThat(console.error(), containsString(pretty(compact) + "\n"));
        }
    }

    @Test
    public void testRandom_customSerializer() throws IOException {
        Random random = new Random();
        Fairy fairy = Fairy.create(Locale.ENGLISH);
        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> watcher = GeneratorWatcher
                .create()
                .setOutputSerializer(new JsonSerializer())
                .setMaxCollectionItems(2)
                .setRandom(random)
                .withGenerator(CompactFields.kDescriptor,
                               gen -> gen.setAlwaysPresent(CompactFields._Field.NAME)
                                         .setAlwaysAbsent(CompactFields._Field.LABEL)
                                         .setValueGenerator(CompactFields._Field.NAME,
                                                            ctx -> fairy.textProducer()
                                                                        .word(1)))
                .setFairy(fairy)
                .dumpOnFailure();

        watcher.starting(Description.EMPTY);

        CompactFields compact = watcher.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(nullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.getName(), not(containsString(" ")));
        assertThat(compact.hasId(), is(true));

        assertThat(watcher.allGenerated(), hasItem(compact));

        watcher.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(json(compact) + "\n"));
    }

    @Test
    public void testRandom_customWriter() throws IOException {
        Fairy fairy = Fairy.create(Locale.ENGLISH);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator = GeneratorWatcher
                .create()
                .setMessageWriter(new IOMessageWriter(baos, new FastBinarySerializer()))
                .setMaxCollectionItems(2)
                .withGenerator(CompactFields.kDescriptor, gen -> {
                    gen.setAlwaysPresent(CompactFields._Field.NAME);
                    gen.setValueGenerator(CompactFields._Field.NAME, ctx -> fairy.textProducer().word(1));
                })
                .dumpOnFailure();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.getName(), not(containsString(" ")));
        assertThat(compact.hasId(), is(true));

        assertThat(generator.allGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));

        IOMessageReader reader = new IOMessageReader(new ByteArrayInputStream(baos.toByteArray()), new FastBinarySerializer());

        assertThat(reader.read(CompactFields.kDescriptor), is(equalToMessage(compact)));
    }

    @Test
    public void testRandom_noDump() {
        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator =
                GeneratorWatcher.create();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.hasId(), is(true));
        assertThat(generator.allGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));
    }

    @Test
    public void testRandom_withReader() {
        ByteArrayInputStream bais = new ByteArrayInputStream((
                "{\n" +
                "  name = \"villa\"\n" +
                "  id = 123\n" +
                "  label = \"Sjampanjebrus\"\n" +
                "}\n").getBytes(StandardCharsets.UTF_8));
        MessageReader reader = new IOMessageReader(bais, new PrettySerializer().debug());

        CompactFields compact = CompactFields.builder()
                                             .setId(123)
                                             .setName("villa")
                                             .setLabel("Sjampanjebrus")
                                             .build();

        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator = GeneratorWatcher
                .create()
                .dumpOnFailure()
                .setMessageReader(reader);
        generator.starting(Description.EMPTY);

        CompactFields gen = generator.generate(CompactFields.kDescriptor);

        assertThat(gen, is(equalToMessage(compact)));
        assertThat(generator.allGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is("android.CompactFields {\n" +
                      "  name = \"villa\"\n" +
                      "  id = 123\n" +
                      "  label = \"Sjampanjebrus\"\n" +
                      "}\n"));
    }

    @Test
    public void testRandom_withPregenResource() {
        CompactFields compact = CompactFields.builder()
                                             .setId(123)
                                             .setName("villa")
                                             .setLabel("Sjampanjebrus")
                                             .build();
        CompactFields compact2 = CompactFields.builder()
                                             .setId(125)
                                             .setName("villa2")
                                             .setLabel("Brus med smak")
                                             .build();

        GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator = GeneratorWatcher
                .create()
                .dumpOnFailure()
                .setResourceReader("/pregen.cfg");
        generator.starting(Description.EMPTY);

        CompactFields gen = generator.generate(CompactFields.kDescriptor);
        CompactFields gen2 = generator.generate(CompactFields.kDescriptor);

        assertThat(gen, notNullValue());
        assertThat(gen2, notNullValue());
        assertThat(generator.allGenerated(), hasItem(compact));
        assertThat(generator.allGenerated(), hasItem(compact2));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is("android.CompactFields {\n" +
                      "  name = \"villa\"\n" +
                      "  id = 123\n" +
                      "  label = \"Sjampanjebrus\"\n" +
                      "}\n" +
                      "android.CompactFields {\n" +
                      "  name = \"villa2\"\n" +
                      "  id = 125\n" +
                      "  label = \"Brus med smak\"\n" +
                      "}\n"));
    }

    private <M extends PMessage<M, F>, F extends PField> String json(M message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new JsonSerializer().serialize(baos, message);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    private <M extends PMessage<M, F>, F extends PField> String pretty(M message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new PrettySerializer().config().serialize(baos, message);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
