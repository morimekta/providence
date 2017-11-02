package net.morimekta.providence.testing.generator;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.testing.generator.defaults.BinaryGenerator;
import net.morimekta.providence.testing.generator.defaults.BoolGenerator;
import net.morimekta.providence.testing.generator.defaults.ByteGenerator;
import net.morimekta.providence.testing.generator.defaults.DoubleGenerator;
import net.morimekta.providence.testing.generator.defaults.EnumGenerator;
import net.morimekta.providence.testing.generator.defaults.IntGenerator;
import net.morimekta.providence.testing.generator.defaults.ListGenerator;
import net.morimekta.providence.testing.generator.defaults.LongGenerator;
import net.morimekta.providence.testing.generator.defaults.MapGenerator;
import net.morimekta.providence.testing.generator.defaults.SetGenerator;
import net.morimekta.providence.testing.generator.defaults.ShortGenerator;
import net.morimekta.providence.testing.generator.defaults.StringGenerator;

import io.codearte.jfairy.Fairy;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Options and context container for managing a set of message generators and
 * associated value generators.
 *
 * Note that all classes who extend the GeneratorBase must have a
 * public copy constructor.
 */
public abstract class GeneratorBase<
        Base extends GeneratorBase<Base, Context>,
        Context extends GeneratorContext<Context>> {
    private static final Fairy DEFAULT_FAIRY = Fairy.create(Locale.ENGLISH);

    private final Map<PMessageDescriptor, MessageGenerator> defaultMessageGenerators;
    private final Map<PEnumDescriptor, Generator>           defaultEnumGenerators;
    private final EnumMap<PType, Generator<Context, ?>> primitiveCache;

    private Fairy  fairy;
    private Random random;
    private double defaultFillRate;
    private int    defaultMaxCollectionSize;

    /**
     * Default generator context.
     */
    public GeneratorBase() {
        this(DEFAULT_FAIRY, new Random());
    }

    /**
     *
     * @param fairy Fairy instance.
     * @param random Random instance.
     */
    public GeneratorBase(Fairy fairy,
                         Random random) {
        this(fairy, random, 1.0, 10);
    }

    /**
     *
     * @param fairy Fairy instance.
     * @param random Random instance.
     * @param defaultFillRate The default fill rate.
     * @param defaultMaxCollectionSize The max collection size.
     */
    public GeneratorBase(Fairy fairy,
                         Random random,
                         double defaultFillRate,
                         int defaultMaxCollectionSize) {
        this(new HashMap<>(), new HashMap<>(), defaultFillRate, defaultMaxCollectionSize, fairy, random);
    }

    private GeneratorBase(Map<PMessageDescriptor, MessageGenerator> defaultMessageGenerators,
                          Map<PEnumDescriptor, Generator> defaultEnumGenerators,
                          double defaultFillRate,
                          int defaultMaxCollectionSize,
                          Fairy fairy,
                          Random random) {
        this.fairy = fairy;
        this.random = random;
        this.primitiveCache = new EnumMap<>(PType.class);

        this.defaultFillRate = defaultFillRate;
        this.defaultMaxCollectionSize = defaultMaxCollectionSize;
        this.defaultMessageGenerators = defaultMessageGenerators;
        this.defaultEnumGenerators = defaultEnumGenerators;
    }

    /**
     * Create the context instance used when generating messages.
     *
     * @return The new context class.
     */
    public abstract Context createContext();

    /**
     * Copy constructor.
     *
     * @param copyOf Instance to make copy of.
     */
    @SuppressWarnings("unchecked")
    public GeneratorBase(GeneratorBase<Base, Context> copyOf) {
        this(new HashMap<>(),
             new HashMap<>(),
             copyOf.defaultFillRate,
             copyOf.defaultMaxCollectionSize,
             copyOf.fairy,
             copyOf.random);

        for (Map.Entry<PMessageDescriptor, MessageGenerator> entry :
                copyOf.defaultMessageGenerators.entrySet()) {
            defaultMessageGenerators.put(entry.getKey(), entry.getValue().deepCopy());
        }
        for (Map.Entry<PEnumDescriptor, Generator> entry :
                copyOf.defaultEnumGenerators.entrySet()) {
            defaultEnumGenerators.put(entry.getKey(), entry.getValue());
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T>
    Generator<Context, T> generatorFor(@Nonnull PDescriptor type) {
        if (type instanceof PMessageDescriptor) {
            return messageGeneratorFor((PMessageDescriptor) type);
        } else if (type instanceof PEnumDescriptor) {
            return enumGeneratorFor((PEnumDescriptor) type);
        } else if (type instanceof PPrimitive) {
            if (!primitiveCache.containsKey(type.getType())) {
                primitiveCache.put(type.getType(), makeGeneratorInternal(type));
            }
            return (Generator<Context, T>) primitiveCache.get(type.getType());
        } else {
            // Containers are not cached.
            return makeGeneratorInternal(type);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <E extends PEnumValue<E>>
    Generator<Context, E> enumGeneratorFor(PEnumDescriptor<E> descriptor) {
        EnumGenerator<Context,E> generator = (EnumGenerator) defaultEnumGenerators.get(descriptor);
        if (generator == null) {
            generator = new EnumGenerator<>(descriptor);
            defaultEnumGenerators.put(descriptor, generator);
        }
        return generator;
    }

    /**
     * Get the default generator for type, or create one if it does not exists.
     * This generator can be modified and is kept around.
     *
     * @param descriptor The message descriptor.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The message generator.
     */
    @Nonnull
    public <M extends PMessage<M, F>, F extends PField>
    MessageGenerator<Context, M, F> messageGeneratorFor(PMessageDescriptor<M, F> descriptor) {
        @SuppressWarnings("unchecked")
        MessageGenerator<Context, M, F> generator = defaultMessageGenerators.get(descriptor);
        if (generator == null) {
            generator = new MessageGenerator<>(descriptor);
            defaultMessageGenerators.put(descriptor, generator);
        }
        return generator;
    }

    /**
     * Set the default enum generator for the enum type.
     *
     * @param descriptor The enum descriptor.
     * @param generator The enum value generator.
     * @param <E> The enum type.
     * @return The generator context.
     */
    @Nonnull
    public <E extends PEnumValue<E>>
    Base withEnumGenerator(PEnumDescriptor<E> descriptor,
                           Generator<Context, E> generator) {
        defaultEnumGenerators.put(descriptor, generator);
        return self();
    }

    /**
     * Set the default message generator for the message type.
     *
     * @param descriptor The message descriptor.
     * @param generator The message value generator.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The generator context.
     */
    @Nonnull
    public <M extends PMessage<M, F>, F extends PField>
    Base withMessageGenerator(PMessageDescriptor<M, F> descriptor,
                              MessageGenerator<Context, M, F> generator) {
        defaultMessageGenerators.put(descriptor, generator);
        return self();
    }

    /**
     * Get the default message generator for the given message type, and
     * apply the closure context on that.
     *
     * @param descriptor The message descriptor.
     * @param closure The closure working on the generator.
     * @param <M> The message type.
     * @param <F> the message field type.
     * @return The generator context.
     */
    @Nonnull
    public <M extends PMessage<M, F>, F extends PField>
    Base withMessageGenerator(PMessageDescriptor<M, F> descriptor,
                              Consumer<MessageGenerator<Context, M, F>> closure) {
        closure.accept(messageGeneratorFor(descriptor));
        return self();
    }

    // -------------------------------------------
    // ----                                   ----
    // ----       GETTERS AND SETTERS         ----
    // ----                                   ----
    // -------------------------------------------

    /**
     * @return The current fairy instance.
     */
    @Nonnull
    public Fairy getFairy() {
        return fairy;
    }

    /**
     * @param fairy The new fairy instance.
     * @return The generator context.
     */
    @Nonnull
    public Base setFairy(Fairy fairy) {
        this.fairy = fairy;
        return self();
    }

    /**
     * @return The current random instance.
     */
    @Nonnull
    public Random getRandom() {
        return random;
    }

    /**
     * @param random The new random instance.
     * @return The generator context.
     */
    @Nonnull
    public Base setRandom(Random random) {
        this.random = random;
        return self();
    }

    /**
     * @return The default max collection size.
     */
    public int getDefaultMaxCollectionSize() {
        return defaultMaxCollectionSize;
    }

    /**
     * @param defaultMaxCollectionSize The new default max collection size.
     * @return The generator context.
     */
    @Nonnull
    public Base setDefaultMaxCollectionSize(int defaultMaxCollectionSize) {
        this.defaultMaxCollectionSize = defaultMaxCollectionSize;
        return self();
    }

    /**
     * @return The current default fill rate.
     */
    public double getDefaultFillRate() {
        return defaultFillRate;
    }

    /**
     * @param defaultFillRate The new default fill rate.
     * @return The generator context.
     */
    @Nonnull
    public Base setDefaultFillRate(double defaultFillRate) {
        this.defaultFillRate = defaultFillRate;
        return self();
    }

    // -------------------------------------------
    // ----                                   ----
    // ----         INTERNAL METHODS          ----
    // ----                                   ----
    // -------------------------------------------

    @SuppressWarnings("unchecked")
    protected Base deepCopy() {
        try {
            Constructor constructor = getClass().getConstructor(getClass());
            return (Base) constructor.newInstance(this);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Base self() {
        return (Base) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private <T>
    Generator<Context, T> makeGeneratorInternal(@Nonnull PDescriptor type) {
        switch (type.getType()) {
            case VOID:
                return ctx -> (T) Boolean.TRUE;
            case BOOL:
                return new BoolGenerator();
            case BYTE:
                return new ByteGenerator();
            case I16:
                return new ShortGenerator();
            case I32:
                return new IntGenerator();
            case I64:
                return new LongGenerator();
            case DOUBLE:
                return new DoubleGenerator();
            case STRING:
                return new StringGenerator();
            case BINARY:
                return new BinaryGenerator();
            case LIST:
                return new ListGenerator((PList<Object>) type);
            case SET:
                return new SetGenerator((PSet<Object>) type);
            case MAP:
                return new MapGenerator((PMap<Object,Object>) type);
            case ENUM:
                return enumGeneratorFor((PEnumDescriptor) type);
            case MESSAGE:
                return messageGeneratorFor((PMessageDescriptor) type);
        }
        throw new IllegalArgumentException("Unhandled default type: " + type.getType());
    }
}
