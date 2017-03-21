package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * TODO(steineldar): Make a proper class description.
 */
public abstract class PMessageDescriptor<T extends PMessage<T, F>, F extends PField> extends PDeclaredDescriptor<T> {
    private final PMessageBuilderFactory<T, F> factory;
    private final boolean                      simple;

    public PMessageDescriptor(String programName,
                              String name,
                              PMessageBuilderFactory<T, F> factory,
                              boolean simple) {
        super(programName, name);

        this.factory = factory;
        this.simple = simple;
    }

    /**
     * @return An unmodifiable list of fields that the struct holds.
     */
    @Nonnull
    public abstract F[] getFields();

    /**
     * @param name Name of field to get.
     * @return The field if present.
     */
    public abstract F getField(String name);

    /**
     * @param key The ID of the field to get.
     * @return The field if present.
     */
    public abstract F getField(int key);

    /**
     * @return The struct variant.
     */
    public abstract PMessageVariant getVariant();

    /**
     * @return True iff the message is simple. A simple message contains no
     *         containers, and no sub-messages.
     */
    public boolean isSimple() {
        return simple;
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.MESSAGE;
    }

    @Override
    public PMessageBuilder<T, F> builder() {
        return factory.builder();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o.getClass().equals(getClass()))) {
            return false;
        }
        PMessageDescriptor<?, ?> other = (PMessageDescriptor<?, ?>) o;
        if (!getQualifiedName().equals(other.getQualifiedName()) ||
            !getVariant().equals(other.getVariant()) ||
            getFields().length != other.getFields().length) {
            return false;
        }
        for (PField field : getFields()) {
            if (!field.equals(other.getField(field.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(getClass(),
                                getQualifiedName(),
                                getVariant());
        for (PField field : getFields()) {
            hash += Objects.hash(hash, field.hashCode());
        }
        return hash;
    }

    /**
     * Get the actual builder factory instance. For contained structs only.
     * @return The builder factory.
     */
    protected PMessageBuilderFactory<T, F> getFactoryInternal() {
        return factory;
    }
}
