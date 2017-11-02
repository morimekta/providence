package net.morimekta.providence.testing.generator;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;

import com.google.common.collect.ImmutableList;
import io.codearte.jfairy.Fairy;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Providence message serializer that can be used as a junit rule.
 *
 * <pre>{@code
 * class MyTest {
 *    {@literal @}Rule
 *     public SimpleGeneratorWatcher gen = GeneratorWatcher
 *             .create()
 *             .dumpOnFailure()
 *             .withGenerator(MyMessage.kDescriptor, gen -> {
 *                 gen.setAlwaysPresent(MyMessage._Fields.UUID, MyMessage._Fields.NAME);
 *                 gen.setValueGenerator(MyMessage._Fields.UUID, () -> UUID.randomUUID().toString());
 *             });
 *
 *    {@literal @}Test
 *     public testSomething() {
 *         MyMessage msg = gen.generate(MyMessage.kDescriptor);
 *         sut.doSomething(msg);
 *
 *         assertThat(sut.state(), is(SystemToTest.CORRECT));
 *     }
 *
 *    {@literal @}Test
 *     public testSomethingElse() {
 *         gen.generatorFor(MyMessage.kDescriptor)
 *            .setValueGenerator(MyMessage._Field.NAME, () -> "Mi Nome")
 *            .setAlwaysPresent(MyMessage._Field.AGE)
 *            .setValueGenerator(MyMessage._Field.AGE, () -> 35);
 *
 *         MyMessage msg = gen.generate(MyMessage.kDescriptor);
 *         sut.doSomething(msg);
 *
 *         assertThat(sut.state(), is(SystemToTest.CORRECT));
 *     }
 * }
 * }</pre>
 */
public class GeneratorWatcher<
        Base extends GeneratorBase<Base, Context>,
        Context extends GeneratorContext<Context>>
        extends TestWatcher {
    private static final Map<Locale, Fairy> singletonFairyCache = new ConcurrentHashMap<>();

    /**
     * Create a default message generator watcher.
     *
     * @return The watcher instance.
     */
    public static SimpleGeneratorWatcher create() {
        return SimpleGeneratorWatcher.create();
    }

    /**
     * Create a message generator watcher with the given base context.
     *
     * @param base The base generator to use when generating messages.
     * @param <Base> The base generator type.
     * @param <Context> The context type.
     * @return The watcher instance.
     */
    public static
    <Context extends GeneratorContext<Context>, Base extends GeneratorBase<Base,Context>> GeneratorWatcher<Base, Context> create(Base base) {
        return new GeneratorWatcher<>(base);
    }

    /**
     * Make a simple default message generator.
     *
     * @param base The base generator to use when generating messages.
     */
    public GeneratorWatcher(Base base) {
        this.globalOutputSerializer = this.outputSerializer = new PrettySerializer().config();
        this.globalDumpOnFailure    = this.dumpOnFailure    = false;
        this.globalReader           = this.reader           = null;
        this.globalWriter           = this.writer           = null;
        this.globalBase             = this.base             = base;

        this.generated              = new ArrayList<>();
        this.started                = false;
    }

    /**
     * Generate a message with random content using the default generator
     * for the message type.
     *
     * @param descriptor Message descriptor to generate message from.
     * @param <M> The message type.
     * @param <F> The field type.
     * @return The generated message.
     */
    @SuppressWarnings("unchecked")
    public <M extends PMessage<M, F>, F extends PField> M generate(
            PMessageDescriptor<M, F> descriptor) {
        return generate(base.createContext(), descriptor);
    }

    /**
     * Generate a message with random content using the default generator
     * for the message type.
     *
     * @param context The specific context to use when generating.
     * @param descriptor Message descriptor to generate message from.
     * @param <M> The message type.
     * @param <F> The field type.
     * @return The generated message.
     */
    @SuppressWarnings("unchecked")
    public <M extends PMessage<M, F>, F extends PField> M generate(
            Context context,
            PMessageDescriptor<M, F> descriptor) {
        M instance;
        if (reader != null) {
            try {
                instance = reader.read(descriptor);
            } catch (IOException e) {
                throw new AssertionError(e.getMessage(), e);
            }
        } else {
            instance = base.messageGeneratorFor(descriptor).generate(context);
        }
        generated.add(instance);
        return instance;
    }

    /**
     * Get the modifiable message generator for descriptor.
     *
     * @param descriptor the message descriptor constant.
     * @param modificationConsumer Consumer to handle modifications on the generator
     *                             instance.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The message generator watcher.
     */
    public <M extends PMessage<M, F>, F extends PField> GeneratorWatcher<Base, Context> withGenerator(
            PMessageDescriptor<M,F> descriptor,
            Consumer<MessageGenerator<Context,M,F>> modificationConsumer) {
        modificationConsumer.accept(getDefaultGenerator(descriptor));
        return this;
    }

    /**
     * Get the default generator used to generate given message.
     *
     * @param descriptor the message descriptor constant.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The message generator watcher.
     */
    public <M extends PMessage<M, F>, F extends PField>
    MessageGenerator<Context,M,F> getDefaultGenerator(PMessageDescriptor<M,F> descriptor) {
        return getBaseContext().messageGeneratorFor(descriptor);
    }

    /**
     * Create a new non-default generator used to generate given message.
     * This generator will add it's results to the watchers list of generated
     * messages.
     *
     * @param descriptor the message descriptor constant.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The message generator watcher.
     */
    public <M extends PMessage<M, F>, F extends PField>
    MessageGenerator<Context, M, F> newReportingGenerator(PMessageDescriptor<M,F> descriptor) {
        return new MessageGenerator<Context, M, F>(descriptor) {
            @Override
            public M generate(Context context) {
                M message = super.generate(context);
                generated.add(message);
                return message;
            }
        };
    }

    /**
     * Get all generated messages. It will return the messages that was *requested*
     * to be generated with all contained messages, not all messages generated all
     * over the place.
     *
     * @return The list of generated messages.
     */
    public List<PMessage> allGenerated() {
        return ImmutableList.copyOf(generated);
    }

    /**
     * Dump all generated messages.
     *
     * @throws IOException If writing the messages failed.
     */
    @SuppressWarnings("unchecked")
    public void dumpGeneratedMessages() throws IOException {
        MessageWriter writer = this.writer;
        if (writer == null) {
            writer = new IOMessageWriter(System.err, outputSerializer);
        }

        for (PMessage message : generated) {
            writer.write(message);
            writer.separator();
        }
    }

    // --- generator setup ---:

    /**
     * @return The watchers message generator options.
     */
    public Base getBaseContext() {
        if (started) {
            return base;
        } else {
            return globalBase;
        }
    }

    /**
     * Set the random generator being used.
     *
     * @param random The random generator.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setRandom(Random random) {
        getBaseContext().setRandom(random);
        return this;
    }

    /**
     * Set the feiry data generator being used.
     *
     * @param fairy The fairy data generator.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setFairy(Fairy fairy) {
        getBaseContext().setFairy(fairy);
        return this;
    }

    /**
     * Set the locale to generate values for. Applies to default string
     * values. Known good locales are:
     * <ul>
     *     <li>English (US)
     *     <li>German  (DE)
     *     <li>French  (FR)
     *     <li>Italian (IT)
     *     <li>Spanish (ES)
     *     <li>Polish  (PL)
     *     <li>Swedish (SV)
     *     <li>Chinese (ZH)
     * </ul>
     *
     * @param locale The locale to set.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setLocale(Locale locale) {
        Fairy fairy = singletonFairyCache.get(locale);
        if (fairy == null) {
            fairy = Fairy.create(locale);
            singletonFairyCache.put(locale, fairy);
        }
        return setFairy(fairy);
    }

    /**
     * Set the field fill rate in the range &lt;0.0 .. 1.0].
     *
     * @param fillRate The new fill rate.
     * @return The message generator watcher.
     */
    public GeneratorWatcher<Base, Context> setFillRate(double fillRate) {
        assert fillRate > 0.0 && fillRate <= 1.0 : "Fill rate outside the range < 0.0 .. 1.0 ]: " + fillRate;
        getBaseContext().setDefaultFillRate(fillRate);
        return this;
    }

    /**
     * Set the message writer in case of failure.
     *
     * @param writer The message writer.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setMessageWriter(MessageWriter writer) {
        if (started) {
            this.writer = writer;
        } else {
            this.globalWriter = writer;
            this.writer = writer;
        }
        return this;
    }

    /**
     * Set the message reader for the generator.
     *
     * @param reader The message reader. All messages will be read from this
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setMessageReader(MessageReader reader) {
        if (started) {
            this.reader = reader;
        } else {
            assert globalReader == null : "Generator already contains reader for messages.";
            this.globalReader = reader;
            this.reader = reader;
        }
        return this;
    }

    /**
     * Read messages from the given resource (pretty formatted).
     *
     * @param resource The resource path.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setResourceReader(String resource) {
        return setResourceReader(resource, new PrettySerializer());
    }

    /**
     * Read messages from the given resource.
     *
     * @param resource The resource path.
     * @param serializer Serializer to use for reading resource.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setResourceReader(String resource,
                                                             Serializer serializer) {
        return setMessageReader(new IOMessageReader(
                getClass().getResourceAsStream(resource),
                serializer));
    }

    /**
     * Set default serializer to standard output. If test case not started and a
     * writer is already set, this method fails. Not that this will remove any
     * previously set message writer.
     *
     * @param defaultSerializer The new default serializer.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setOutputSerializer(Serializer defaultSerializer) {
        if (started) {
            this.writer = null;
            this.outputSerializer = defaultSerializer;
        } else {
            assert globalWriter == null : "Generator already has a writer.";
            this.outputSerializer = defaultSerializer;
            this.globalOutputSerializer = defaultSerializer;
        }
        return this;
    }

    /**
     * Set the max collection items for default generated collections.
     *
     * @param max The max number of items.
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> setMaxCollectionItems(int max) {
        if (started) {
            this.base.setDefaultMaxCollectionSize(max);
        } else {
            this.globalBase.setDefaultMaxCollectionSize(max);
        }
        return this;
    }

    /**
     * Dump all generated messages on failure for this test only.
     *
     * @return The message generator.
     */
    public GeneratorWatcher<Base, Context> dumpOnFailure() {
        if (started) {
            this.dumpOnFailure = true;
        } else {
            this.globalDumpOnFailure = true;
        }
        return this;
    }

    // -------------- INHERITED --------------

    @Override
    protected void starting(Description description) {
        super.starting(description);
        if (!description.isEmpty() && description.getMethodName() == null) {
            throw new AssertionError("MessageGenerator instantiated as class rule: forbidden");
        }

        writer           = globalWriter;
        reader           = globalReader;
        dumpOnFailure    = globalDumpOnFailure;
        outputSerializer = globalOutputSerializer;
        base = globalBase.deepCopy();

        // Reset content.
        generated = new ArrayList<>();
        started = true;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        if (dumpOnFailure) {
            try {
                dumpGeneratedMessages();
            } catch (IOException e1) {
                e1.printStackTrace();
                e.addSuppressed(e1);
            }
        }
    }

    @Override
    protected void finished(Description description) {
        // generated kept in case of secondary watchers.
        started = false;

        // Set some interesting stated back to be the global.
        dumpOnFailure = globalDumpOnFailure;
        outputSerializer = globalOutputSerializer;
        writer = globalWriter;
        reader = globalReader;
        base = globalBase;
    }

    // --- Global: set before starting(),
    //             and copied below in starting().
    private Serializer    globalOutputSerializer;
    private boolean       globalDumpOnFailure;
    private MessageWriter globalWriter;
    private MessageReader globalReader;
    private Base          globalBase;

    // --- Per test: set after starting()
    private Serializer    outputSerializer;
    private boolean       dumpOnFailure;
    private MessageWriter writer;
    private MessageReader reader;
    private Base          base;

    // generated during test.
    private List<PMessage> generated;
    private boolean        started;
}
