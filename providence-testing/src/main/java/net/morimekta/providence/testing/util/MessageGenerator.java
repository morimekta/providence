package net.morimekta.providence.testing.util;

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
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableList;
import org.jfairy.Fairy;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertNotNull;

/**
 * Providence message serializer that can be used as a junit rule.
 *
 * <pre>{@code
 * class MyTest {
 *    {@literal @}Rule
 *     public MessageGenerator gen = new MessageGenerator.builder()
 *                                                       .dumpOnFailure()
 *                                                       .build();
 *    {@literal @}Test
 *     public testSomething() {
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
        if (!preGenerated.isEmpty()) {
            instance = (M) preGenerated.pollFirst();
        } else if (reader != null) {
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
     * Factory for value suppliers. The generator can hold any number of value supplier
     */
    @FunctionalInterface
    public interface ValueSupplierFactory {
        Supplier<Object> get(PField field);
    }

    /**
     * @return A message generator builder.
     */
    public static Builder builder() {
        return new MessageGenerator.Builder();
    }

    /**
     * Builder to make the MessageGenerator.
     */
    public static class Builder {
        private int                              maxCollectionItems;
        private LinkedList<ValueSupplierFactory> factories;
        private Random                           random;
        private boolean                          globalDumpOnFailure;
        private Fairy                            fairy;
        private List<PMessage>                   preGenerated;
        private Serializer                       serializer;
        private MessageWriter                    writer;
        private MessageReader                    reader;

        public Builder() {
            this.factories = new LinkedList<>();
            this.preGenerated = new LinkedList<>();
            this.random = null;
            this.globalDumpOnFailure = false;
            this.maxCollectionItems = 10;
            this.writer = null;
        }

        public MessageGenerator build() {
            return new MessageGenerator(
                    serializer == null ? new PrettySerializer(true, false) : serializer,
                    random == null ? new Random() : random,
                    fairy == null ? Fairy.create(Locale.ENGLISH) : fairy,
                    preGenerated,
                    factories,
                    writer, reader,
                    maxCollectionItems,
                    globalDumpOnFailure);
        }

        public MessageGenerator.Builder dumpOnFailure() {
            this.globalDumpOnFailure = true;
            return this;
        }

        public MessageGenerator.Builder withMaxCollectionItems(int max) {
            this.maxCollectionItems = max;
            return this;
        }

        public MessageGenerator.Builder withFactory(ValueSupplierFactory factory) {
            this.factories.add(factory);
            return this;
        }

        public MessageGenerator.Builder withFactories(ValueSupplierFactory... factories) {
            Collections.addAll(this.factories, factories);
            return this;
        }

        public MessageGenerator.Builder withFactories(Collection<ValueSupplierFactory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        public MessageGenerator.Builder withRandom(Random random) {
            this.random = random;
            return this;
        }

        public MessageGenerator.Builder withFairy(Fairy fairy) {
            this.fairy = fairy;
            return this;
        }

        public MessageGenerator.Builder withSerializer(Serializer serializer) {
            assert writer == null : "Generator already has a writer.";
            this.serializer = serializer;
            return this;
        }

        public MessageGenerator.Builder withMessageWriter(MessageWriter writer) {
            assert serializer == null : "Generator already has a serializer.";
            this.writer = writer;
            return this;
        }

        public MessageGenerator.Builder withMessageReader(MessageReader messageReader) {
            assert preGenerated.isEmpty() : "Generator already contains pre-generated messages.";
            this.reader = messageReader;
            return this;
        }

        public <M extends PMessage<M, F>, F extends PField> MessageGenerator.Builder withPregenMessage(M message) {
            assert reader == null : "Generator already contains reader for messages.";
            preGenerated.add(message);
            return this;
        }

        public <M extends PMessage<M, F>, F extends PField> MessageGenerator.Builder withPregenResource(String resource, PMessageDescriptor<M,F> descriptor) {
            assert reader == null : "Generator already contains reader for messages.";
            PrettySerializer serializer = new PrettySerializer(true, false);

            try {
                MessageStreams.resource(resource, serializer, descriptor)
                              .forEach(m -> preGenerated.add(m));
                return this;
            } catch (IOException e) {
                throw new AssertionError(e.getMessage(), e);
            }
        }
    }

    private MessageGenerator(Serializer serializer,
                             Random random,
                             Fairy fairy,
                             List<PMessage> preGenerated,
                             List<ValueSupplierFactory> factories,
                             MessageWriter writer,
                             MessageReader messageReader,
                             int maxCollectionItems,
                             boolean globalDumpOnFailure) {
        this.serializer = serializer;
        this.factories = ImmutableList.copyOf(factories);
        this.preGenerated = new LinkedList<>(preGenerated);
        this.fairy = fairy;
        this.random = random;
        this.writer = writer;
        this.globalDumpOnFailure = globalDumpOnFailure;
        this.maxCollectionItems = maxCollectionItems;
        this.reader = messageReader;

        this.generated = null;
        this.dumpOnFailure = globalDumpOnFailure;
    }

    /**
     * Dump all generated messages on failure for this test only.
     */
    public void dumpOnFailure() {
        this.dumpOnFailure = true;
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

    @Override
    protected void starting(Description description) {
        super.starting(description);
        this.generated = new LinkedList<>();
    }

    @Override
    protected void succeeded(Description description) {
        this.generated = null;
        this.dumpOnFailure = this.globalDumpOnFailure;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        if (dumpOnFailure) {
            MessageWriter writer = this.writer;
            if (writer == null) {
                writer = new IOMessageWriter(System.err, serializer);
            }

            try {
                for (PMessage message : generated) {
                    writer.write(message);
                    writer.separator();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                e.addSuppressed(e1);
            }
        }
        this.generated = null;
        this.dumpOnFailure = this.globalDumpOnFailure;
    }

    // --- PRIVATE ---
    private final List<ValueSupplierFactory> factories;
    private final Random                     random;
    private final Fairy                      fairy;
    private final boolean                    globalDumpOnFailure;
    private final LinkedList<PMessage>       preGenerated;
    private final int                        maxCollectionItems;
    private final MessageWriter              writer;
    private final MessageReader              reader;
    private final Serializer                 serializer;

    private List<PMessage>                   generated;
    private boolean                          dumpOnFailure;

    private Supplier<Object> getValueSupplier(PField field) {
        for (ValueSupplierFactory factory : factories) {
            Supplier<Object> supplier = factory.get(field);
            if (supplier != null) {
                return supplier;
            }
        }
        return getValueSupplier(field.getDescriptor());
    }

    @SuppressWarnings("unchecked")
    private Supplier<Object> getValueSupplier(PDescriptor descriptor) {
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
                return () -> values[random.nextInt() % values.length];
            }
            case BINARY: {
                return () -> {
                    byte[] tmp = new byte[random.nextInt() % maxCollectionItems];
                    random.nextBytes(tmp);
                    return Binary.wrap(tmp);
                };
            }
            case STRING:
                return () -> fairy.textProducer().sentence();
            case SET: {
                PSet<Object> set = (PSet<Object>) descriptor;
                Supplier<Object> itemSupplier = getValueSupplier(set.itemDescriptor());
                return () -> {
                    int num = random.nextInt(maxCollectionItems);
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
                    int num = random.nextInt(maxCollectionItems);
                    // Maps does not necessary allow conflicting keys.
                    List<Object> builder = new LinkedList<>();
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
                    int num = random.nextInt(maxCollectionItems);
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

    private <M extends PMessage<M, F>, F extends PField> M generateInternal(PMessageDescriptor<M, F> descriptor) {
        PMessageBuilder<M, F> builder = descriptor.builder();
        if (descriptor.getVariant() == PMessageVariant.UNION) {
            F field = descriptor.getFields()[random.nextInt() % descriptor.getFields().length];
            Supplier<Object> supplier = getValueSupplier(field);
            assertNotNull("No supplier for field: " + descriptor.getQualifiedName() + "." + field.getName(),
                          supplier);

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
