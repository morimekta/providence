package net.morimekta.providence.testing.generator;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PRequirement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Message generator for generating a specific message.
 */
public class MessageGenerator<
        Context extends GeneratorContext<Context>,
        Message extends PMessage<Message, Field>,
        Field extends PField>
        implements Generator<Context, Message> {
    private final PMessageDescriptor<Message, Field> descriptor;

    private final Map<Field, Generator<Context, ?>> fieldValueGenerators;
    private final Map<Field, Boolean>               fieldPresenceOverrides;

    public MessageGenerator(PMessageDescriptor<Message, Field> descriptor) {
        this(descriptor, new HashMap<>(), new HashMap<>());
    }

    private MessageGenerator(PMessageDescriptor<Message, Field> descriptor,
                             Map<Field, Generator<Context, ?>> fieldValueGenerators,
                             Map<Field, Boolean> fieldPresenceOverrides) {
        this.descriptor = descriptor;
        this.fieldValueGenerators = fieldValueGenerators;
        this.fieldPresenceOverrides = fieldPresenceOverrides;
    }

    /**
     * Set which fields must always be generated. Note that unions only
     * allow for one single field to be always present.
     *
     * @param fields The fields that must be generated for each instance.
     * @return The message generator.
     */
    @SafeVarargs
    public final MessageGenerator<Context,Message,Field> setAlwaysPresent(Field... fields) {
        return setAlwaysPresent(Arrays.asList(fields));
    }

    /**
     * Set which fields must always be generated. Note that unions only
     * allow for one single field to be always present.
     *
     * @param fields The fields that must be generated for each instance.
     * @return The message generator.
     */
    public MessageGenerator<Context,Message,Field> setAlwaysPresent(@Nonnull Collection<Field> fields) {
        for (Field field : fields) {
            fieldPresenceOverrides.put(field, Boolean.TRUE);
        }
        return this;
    }

    /**
     * Set which fields must never be generated. If the message is a union
     * then these fields will not be selected when getting a random field
     * to get value for.
     *
     * @param fields The fields that should always be absent.
     * @return The message generator.
     */
    @SafeVarargs
    public final MessageGenerator<Context,Message,Field> setAlwaysAbsent(Field... fields) {
        return setAlwaysAbsent(Arrays.asList(fields));
    }

    /**
     * Set which fields must never be generated. If the message is a union
     * then these fields will not be selected when getting a random field
     * to get value for.
     *
     * @param fields The fields that should always be absent.
     * @return The message generator.
     */
    public MessageGenerator<Context,Message,Field> setAlwaysAbsent(@Nonnull Collection<Field> fields) {
        for (Field field : fields) {
            fieldPresenceOverrides.put(field, Boolean.FALSE);
        }
        return this;
    }

    /**
     * Set default presence probability based on the default fill rate
     * in the generator options instance.
     *
     * @param fields The fields the should have default presence probability.
     * @return The message generator.
     */
    @SafeVarargs
    public final MessageGenerator<Context,Message,Field> setDefaultPresence(Field... fields) {
        return setDefaultPresence(Arrays.asList(fields));
    }

    /**
     * Set default presence probability based on the default fill rate
     * in the generator options instance.
     *
     * @param fields The fields the should have default presence probability.
     * @return The message generator.
     */
    public MessageGenerator<Context,Message,Field> setDefaultPresence(@Nonnull Collection<Field> fields) {
        for (Field field : fields) {
            fieldPresenceOverrides.remove(field);
        }
        return this;
    }

    /**
     * Reset all field presence probabilities to default based on the
     * fill rate of the message generator options.
     *
     * @return The message generator.
     */
    public MessageGenerator<Context,Message,Field> resetDefaultPresence() {
        fieldPresenceOverrides.clear();
        return this;
    }

    @SuppressWarnings("unchecked")
    public MessageGenerator<Context,Message,Field> setValueGenerator(Field field, Generator<Context, ?> generator) {
        fieldValueGenerators.put(field, generator);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Message generate(Context options) {
        PMessageBuilder<Message, Field> builder = descriptor.builder();
        if (descriptor.getVariant() == PMessageVariant.UNION) {
            Field selectedField = null;
            Set<Field> blockedFields = new HashSet<>();
            for (Map.Entry<Field, Boolean> entry : fieldPresenceOverrides.entrySet()) {
                if (entry.getValue()) {
                    if (selectedField != null) {
                        throw new IllegalStateException("More than one required union field");
                    }
                    selectedField = entry.getKey();
                } else {
                    blockedFields.add(entry.getKey());
                }
            }

            // select a random field, and set that, unless the field presence
            // overrides has a single required field, then use that. More than
            // one required field is not allowed with unions.
            if (selectedField == null) {
                ArrayList<Field> allowed = new ArrayList<>(Arrays.asList(descriptor.getFields()));
                allowed.removeAll(blockedFields);

                if (allowed.size() < 1) {
                    throw new IllegalStateException("No remaining fields allowed after " + blockedFields.size() + " was blocked");
                }

                int idx = options.getRandom().nextInt(allowed.size());
                selectedField = allowed.get(idx);
            }
            builder.set(selectedField, makeFieldValue(selectedField, options));
        } else {
            for (Field field : descriptor.getFields()) {
                if (fieldPresenceOverrides.containsKey(field)) {
                    if (fieldPresenceOverrides.get(field)) {
                        builder.set(field, makeFieldValue(field, options));
                    }
                    continue;
                }

                // Default presence calculation.
                if (field.getRequirement() != PRequirement.REQUIRED && options.getDefaultFillRate() < 1.0) {
                    if (options.getRandom()
                               .nextDouble() < options.getDefaultFillRate()) {
                        builder.set(field, makeFieldValue(field, options));
                    }
                } else {
                    // Required or fill rate is 100% (1.0)
                    builder.set(field, makeFieldValue(field, options));
                }
            }
        }
        return builder.build();
    }

    protected MessageGenerator<Context, Message, Field> deepCopy() {
        return new MessageGenerator<>(
                descriptor,
                new HashMap<>(fieldValueGenerators),
                new HashMap<>(fieldPresenceOverrides));
    }

    /**
     * When the field is decided to be present, this will generate the
     * actual value based on a simple algorithm.
     *
     * @param field The field to generate for.
     * @param context The context to build the field around.
     * @return The value to be set for the field.
     */
    @SuppressWarnings("unchecked")
    private Object makeFieldValue(Field field,
                                  Context context) {
        // This will try to make a field value for the given field regardless of access.
        Generator<Context, ?> generator = fieldValueGenerators.get(field);
        if (generator == null) {
            generator = context.generatorFor(field.getDescriptor());
        }
        return generator.generate(context);
    }
}
