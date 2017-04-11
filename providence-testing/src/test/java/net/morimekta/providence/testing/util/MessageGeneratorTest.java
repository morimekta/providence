package net.morimekta.providence.testing.util;

import net.morimekta.providence.util.LogFormatter;
import net.morimekta.test.android.CompactFields;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.jfairy.Fairy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class MessageGeneratorTest {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    @Test
    public void testRandom_defaultDump() {
        MessageGenerator generator = MessageGenerator.builder()
                                                     .dumpOnFailure()
                                                     .build();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.hasId(), is(true));

        assertThat(generator.getGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is(new LogFormatter(true).format(compact) + "\n"));
    }

    @Test
    public void testRandom_customDump() {
        Fairy fairy = Fairy.create(Locale.ENGLISH);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        MessageGenerator generator = MessageGenerator.builder()
                                                     .withLogFormatter(new LogFormatter(false))
                                                     .withMaxCollectionItems(2)
                                                     .withFactory(f -> {
                                                         if (f.equals(CompactFields._Field.NAME)) {
                                                             return () -> fairy.textProducer().word(1);
                                                         }
                                                         return null;
                                                     })
                                                     .dumpOnFailure()
                                                     .withWriter(out)
                                                     .build();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.getName(), not(containsString(" ")));
        assertThat(compact.hasId(), is(true));

        assertThat(generator.getGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));

        assertThat(new String(baos.toByteArray(), StandardCharsets.UTF_8),
                   is(new LogFormatter(false).format(compact) + "\n"));
    }

    @Test
    public void testRandom_noDump() {
        MessageGenerator generator = MessageGenerator.builder()
                                                     .build();
        generator.starting(Description.EMPTY);

        CompactFields compact = generator.generate(CompactFields.kDescriptor);

        assertThat(compact.getLabel(), is(notNullValue()));
        assertThat(compact.getName(), is(notNullValue()));
        assertThat(compact.hasId(), is(true));
        assertThat(generator.getGenerated(), hasItem(compact));

        generator.failed(new Throwable(), Description.EMPTY);

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));
    }

    @Test
    public void testRandom_withPregenMessage() {
        CompactFields compact = CompactFields.builder()
                                             .setId(123)
                                             .setName("villa")
                                             .setLabel("Sjampanjebrus")
                                             .build();

        MessageGenerator generator = MessageGenerator.builder()
                                                     .dumpOnFailure()
                                                     .withPregenMessage(compact)
                                                     .build();
        generator.starting(Description.EMPTY);

        CompactFields gen = generator.generate(CompactFields.kDescriptor);

        assertThat(gen, is(sameInstance(compact)));
        assertThat(generator.getGenerated(), hasItem(compact));

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

        MessageGenerator generator = MessageGenerator.builder()
                                                     .dumpOnFailure()
                                                     .withPregenResource("/pregen.cfg", CompactFields.kDescriptor)
                                                     .build();
        generator.starting(Description.EMPTY);

        CompactFields gen = generator.generate(CompactFields.kDescriptor);
        CompactFields gen2 = generator.generate(CompactFields.kDescriptor);

        assertThat(gen, notNullValue());
        assertThat(gen2, notNullValue());
        assertThat(generator.getGenerated(), hasItem(compact));
        assertThat(generator.getGenerated(), hasItem(compact2));

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
}
