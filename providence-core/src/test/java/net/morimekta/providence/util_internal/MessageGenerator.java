package net.morimekta.providence.util_internal;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableList;
import io.codearte.jfairy.Fairy;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static java.lang.Math.abs;
import static org.junit.Assert.assertNotNull;

/**
 * Providence message serializer that can be used as a junit rule.
 *
 * <pre>{@code
 * class MyTest {
 *    {@literal @}Rule
 *     public MessageGenerator gen = new MessageGenerator()
 *             .dumpOnFailure()
 *             .addFactory(f -> f.getName().endsWith("uuid") ? () -> UUID.randomUUID().toString() : null);
 *
 *    {@literal @}Test
 *     public testSomething() {
 *         gen.addFactory(f -> f.equals(MyMessage._Field.NAME) ? () -> "name" : null);
 *         MyMessage msg = gen.generate(MyMessage.kDescriptor);
 *         sut.doSomething(msg);
 *
 *         assertThat(sut.state() is(SystemToTest.CORRECT));
 *     }
 * }
 * }</pre>
 */
public class MessageGenerator extends TestWatcher {
    /**
     * Factory for value suppliers. The generator can hold any number of value supplier
     */
    @FunctionalInterface
    public interface ValueSupplierFactory {
        Supplier<Object> get(PField field);
    }

    /**
     * Make a simple default message generator.
     */
    public MessageGenerator() {
        this.globalFairy              = this.fairy              = Fairy.create(Locale.ENGLISH);
        this.globalRandom             = this.random             = new Random();
        this.globalOutputSerializer   = this.outputSerializer   = new PrettySerializer().config();
        this.globalMaxCollectionItems = this.maxCollectionItems = 10;
        this.globalFactories          = this.factories          = new ArrayList<>();
        this.globalDumpOnFailure      = this.dumpOnFailure      = false;
        this.globalReader             = this.reader             = null;
        this.globalWriter             = this.writer             = null;
        this.globalFillRate           = this.fillRate           = 1.0;

        this.defaultFactory = this::getDefaultValueSupplier;
        this.generated = new ArrayList<>();
        this.started = false;
    }

    /**
     * Generate a message with random content.
     *
     * @param descriptor Message descriptor to generate message from.
     * @param <M> The message type.
     * @param <F> The field type.
     * @return The generated message.
     */
    @SuppressWarnings("unchecked")
    public <M extends PMessage<M, F>, F extends PField> M generate(PMessageDescriptor<M, F> descriptor) {
        M instance;
        if (reader != null) {
            try {
                instance = reader.read(descriptor);
            } catch (IOException e) {
                throw new AssertionError(e.getMessage(), e);
            }
        } else {
            instance = generateInternal(descriptor);
        }
        generated.add(instance);
        return instance;
    }

    /**
     * Get all generated messages. It will return the messages that was *requested*
     * to be generated with all contained messages, not all messages generated all
     * over the place.
     *
     * @return The list of generated messages.
     */
    public List<PMessage> getGenerated() {
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
     * Set the random generator being used.
     *
     * @param random The random generator.
     * @return The message generator.
     */
    public MessageGenerator setRandom(Random random) {
        if (started) {
            this.random = random;
        } else {
            this.globalRandom = random;
            this.random = random;
        }
        return this;
    }

    /**
     * Set the feiry data generator being used.
     *
     * @param fairy The fairy data generator.
     * @return The message generator.
     */
    public MessageGenerator setFairy(Fairy fairy) {
        if (started) {
            this.fairy = fairy;
        } else {
            this.globalFairy = fairy;
            this.fairy = fairy;
        }
        return this;
    }

    /**
     * Set the locale to generate values for. Applies to default string
     * values. Known good locales are:
     * <ul>
     *     <li>Engligh (US)
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
    public MessageGenerator setLocale(Locale locale) {
        this.fairy = Fairy.create(locale);
        if (!started) {
            this.globalFairy = this.fairy;
        }
        return this;
    }

    /**
     * Set the field fill rate in the range &lt;0.0 .. 1.0].
     *
     * @param fillRate The new full rate.
     * @return The message generator.
     */
    public MessageGenerator setFillRate(double fillRate) {
        assert fillRate > 0.0 && fillRate <= 1.0 : "Fill rate outside the range < 0.0 .. 1.0 ]: " + fillRate;
        if (started) {
            this.fillRate = fillRate;
        } else {
            this.globalFillRate = fillRate;
            this.fillRate = fillRate;
        }
        return this;
    }

    /**
     * Add a value supplier factory to the generator.
     *
     * @param factory The factory.
     * @return The message generator.
     */
    public MessageGenerator addFactory(ValueSupplierFactory factory) {
        if (started) {
            this.factories.add(factory);
        } else {
            this.globalFactories.add(factory);
        }
        return this;
    }

    /**
     * Add value supplier factories to the generator.
     *
     * @param factories The factories.
     * @return The message generator.
     */
    public MessageGenerator addFactories(ValueSupplierFactory... factories) {
        if (started) {
            Collections.addAll(this.factories, factories);
        } else {
            Collections.addAll(this.globalFactories, factories);
        }
        return this;
    }

    /**
     * Add a collection of value supplier factories to the generator.
     *
     * @param factories The factory.
     * @return The message generator.
     */
    public MessageGenerator addFactories(Collection<ValueSupplierFactory> factories) {
        if (started) {
            this.factories.addAll(factories);
        } else {
            this.globalFactories.addAll(factories);
        }
        return this;
    }

    /**
     * Set the message writer in case of failure.
     *
     * @param writer The message writer.
     * @return The message generator.
     */
    public MessageGenerator setMessageWriter(MessageWriter writer) {
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
    public MessageGenerator setMessageReader(MessageReader reader) {
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
    public MessageGenerator setResourceReader(String resource) {
        return setResourceReader(resource, new PrettySerializer());
    }

    /**
     * Read messages from the given resource.
     *
     * @param resource The resource path.
     * @param serializer Serializer to use for reading resource.
     * @return The message generator.
     */
    public MessageGenerator setResourceReader(String resource,
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
    public MessageGenerator setOutputSerializer(Serializer defaultSerializer) {
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
    public MessageGenerator setMaxCollectionItems(int max) {
        if (!started) {
            this.maxCollectionItems = max;
        } else {
            this.globalMaxCollectionItems = max;
            this.maxCollectionItems = max;
        }
        return this;
    }

    /**
     * Dump all generated messages on failure for this test only.
     */
    public MessageGenerator dumpOnFailure() {
        if (started) {
            this.dumpOnFailure = true;
        } else {
            this.globalDumpOnFailure = true;
            this.dumpOnFailure = true;
        }
        return this;
    }

    /**
     * Get the default value supplier for the given descriptor.
     *
     * @param descriptor The descriptor to make a supplier for.
     * @return The value supplier.
     */
    @SuppressWarnings("unchecked")
    public Supplier<Object> getValueSupplier(PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BOOL:
                return random::nextBoolean;
            case BYTE:
                return () -> (byte) random.nextInt();
            case I16:
                return () -> (short) random.nextInt();
            case I32:
                return random::nextInt;
            case I64:
                return random::nextLong;
            case DOUBLE:
                return random::nextDouble;
            case ENUM: {
                PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                PEnumValue[] values = ed.getValues();
                return () -> values[abs(random.nextInt()) % values.length];
            }
            case BINARY: {
                return () -> {
                    byte[] tmp = new byte[nextCollectionSize()];
                    random.nextBytes(tmp);
                    return Binary.wrap(tmp);
                };
            }
            case STRING:
                return () -> fairy.textProducer()
                                  .sentence();
            case SET: {
                PSet<Object> set = (PSet<Object>) descriptor;
                Supplier<Object> itemSupplier = getValueSupplier(set.itemDescriptor());
                return () -> {
                    int num = nextCollectionSize();
                    // Maps does not necessary allow conflicting keys.
                    HashSet<Object> builder = new HashSet<>();
                    for (int i = 0; i < num; ++i) {
                        builder.add(itemSupplier.get());
                    }
                    return set.builder()
                              .addAll(builder)
                              .build();
                };
            }
            case LIST: {
                PList<Object> list = (PList<Object>) descriptor;
                Supplier<Object> itemSupplier = getValueSupplier(list.itemDescriptor());
                return () -> {
                    int num = nextCollectionSize();
                    // Maps does not necessary allow conflicting keys.
                    List<Object> builder = new ArrayList<>();
                    for (int i = 0; i < num; ++i) {
                        builder.add(itemSupplier.get());
                    }
                    return list.builder()
                               .addAll(builder)
                               .build();
                };
            }
            case MAP: {
                PMap<Object, Object> map = (PMap<Object, Object>) descriptor;
                Supplier<Object> keySupplier = getValueSupplier(map.keyDescriptor());
                Supplier<Object> itemSupplier = getValueSupplier(map.itemDescriptor());
                return () -> {
                    int num = nextCollectionSize();
                    // Maps does not necessary allow conflicting keys.
                    HashMap<Object, Object> builder = new HashMap<>();
                    for (int i = 0; i < num; ++i) {
                        builder.put(keySupplier.get(), itemSupplier.get());
                    }
                    return map.builder()
                              .putAll(builder)
                              .build();
                };
            }
            case MESSAGE:
                return () -> generateInternal((PMessageDescriptor) descriptor);
            case VOID:
                return () -> Boolean.TRUE;
            default:
                throw new IllegalArgumentException(descriptor.getType() + " field in message");
        }
    }

    // -------------- INHERITED --------------

    @Override
    protected void starting(Description description) {
        super.starting(description);
        if (!description.isEmpty() && description.getMethodName() == null) {
            throw new AssertionError("MessageGenerator instantiated as class rule: forbidden");
        }

        random = globalRandom;
        fairy = globalFairy;
        writer = globalWriter;
        reader = globalReader;
        factories = new ArrayList<>(globalFactories);
        dumpOnFailure = globalDumpOnFailure;
        outputSerializer = globalOutputSerializer;
        maxCollectionItems = globalMaxCollectionItems;
        fillRate = globalFillRate;

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
        maxCollectionItems = globalMaxCollectionItems;
        outputSerializer = globalOutputSerializer;
        factories = globalFactories;
        fairy = globalFairy;
        random = globalRandom;
        writer = globalWriter;
        reader = globalReader;
        fillRate = globalFillRate;
    }

    // --- PRIVATE ---
    private final ValueSupplierFactory defaultFactory;
    // --- Global: set before starting(),
    //             and copied below in starting().
    private Random                     globalRandom;
    private Fairy                      globalFairy;
    private Serializer                 globalOutputSerializer;
    private int                        globalMaxCollectionItems;
    private boolean                    globalDumpOnFailure;
    private MessageWriter              globalWriter;
    private MessageReader              globalReader;
    private List<ValueSupplierFactory> globalFactories;
    private double                     globalFillRate;

    // --- Per test: set after starting()
    private Random                     random;
    private Fairy                      fairy;
    private Serializer                 outputSerializer;
    private int                        maxCollectionItems;
    private boolean                    dumpOnFailure;
    private MessageWriter              writer;
    private MessageReader              reader;
    private List<ValueSupplierFactory> factories;
    private double                     fillRate;

    // generated during test.
    private List<PMessage>             generated;
    private boolean                    started;

    private int nextCollectionSize() {
        return abs(random.nextInt() % maxCollectionItems);
    }

    private Supplier<Object> getValueSupplier(PField field) {
        for (ValueSupplierFactory factory : factories) {
            Supplier<Object> supplier = factory.get(field);
            if (supplier != null) {
                return supplier;
            }
        }
        return defaultFactory.get(field);
    }

    private Supplier<Object> getDefaultValueSupplier(PField field) {
        if (field.getRequirement() != PRequirement.REQUIRED) {
            if (fillRate < 1.0 && random.nextDouble() < fillRate) {
                return () -> null;
            }
        }
        return getValueSupplier(field.getDescriptor());
    }

    private <M extends PMessage<M, F>, F extends PField> M generateInternal(PMessageDescriptor<M, F> descriptor) {
        PMessageBuilder<M, F> builder = descriptor.builder();
        if (descriptor.getVariant() == PMessageVariant.UNION) {
            F field = descriptor.getFields()[random.nextInt(descriptor.getFields().length)];
            Supplier<Object> supplier = getValueSupplier(field);
            assertNotNull("No supplier for field: " + descriptor.getQualifiedName() + "." + field.getName(), supplier);

            // Only non-null values are set.
            Object value = supplier.get();
            if (value != null) {
                builder.set(field, value);
            }
        } else {
            for (F field : descriptor.getFields()) {
                Supplier<Object> supplier = getValueSupplier(field);
                assertNotNull("No supplier for field: " + descriptor.getQualifiedName() + "." + field.getName(),
                              supplier);
                // Only non-null values are set.
                Object value = supplier.get();
                if (value != null) {
                    builder.set(field, value);
                }
            }
        }
        return builder.build();
    }
}
