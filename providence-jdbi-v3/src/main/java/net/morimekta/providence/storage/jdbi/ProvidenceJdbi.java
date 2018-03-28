package net.morimekta.providence.storage.jdbi;

import com.google.common.collect.ImmutableMap;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class and helper to make mappers and argument helpers for
 * JDBI queries and updates.
 */
public class ProvidenceJdbi {
    /**
     * With column mapped to field.
     *
     * @param name Name of column.
     * @param field Field it is mapped to.
     * @param <F> The message field type.
     * @return The mapped field.
     */
    public static <F extends PField> MappedField<F> withColumn(String name, F field) {
        return new MappedField<>(name, field);
    }

    /**
     * Bind to message using row mapper.
     *
     * @param descriptor The message descriptor.
     * @param fieldMapping Extra field mapping.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The row mapper.
     */
    @SafeVarargs
    public static <M extends PMessage<M,F>, F extends PField>
    MessageRowMapper<M,F> toMessage(@Nonnull PMessageDescriptor<M,F> descriptor,
                                    @Nonnull MappedField<F>... fieldMapping) {
        return new MessageRowMapper<>(descriptor, makeMapping(fieldMapping));
    }

    /**
     * With field mapped to SQL type.
     *
     * @param field The field to be mapped.
     * @param type The SQL type. See {@link java.sql.Types}.
     * @return The field type mapping.
     */
    public static FieldType withType(PField field, int type) {
        return new FieldType(field, type);
    }

    /**
     * Get named argument finder for message.
     *
     * @param message The message to map fields from.
     * @param fieldTypes Field type mappings.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The named argument finder.
     */
    public static <M extends PMessage<M,F>, F extends PField>
    MessageNamedArgumentFinder<M,F> forMessage(@Nonnull M message,
                                               @Nonnull FieldType... fieldTypes) {
        return new MessageNamedArgumentFinder<>(null, message, makeFieldTypes(fieldTypes));
    }

    /**
     * Get named argument finder for message.
     *
     * @param prefix Name prefix for naming distinction.
     * @param message The message to map fields from.
     * @param fieldTypes Field type mappings.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The named argument finder.
     */
    public static <M extends PMessage<M,F>, F extends PField>
    MessageNamedArgumentFinder<M,F> forMessage(@Nonnull String prefix,
                                               @Nonnull M message,
                                               @Nonnull FieldType... fieldTypes) {
        return new MessageNamedArgumentFinder<>(prefix, message, makeFieldTypes(fieldTypes));
    }

    public static class MappedField<F extends PField> {
        private final String name;
        private final F field;

        MappedField(String name, F field) {
            this.name = name;
            this.field = field;
        }
    }

    public static class FieldType {
        private PField field;
        private int type;

        FieldType(PField field, int type) {
            this.field = field;
            this.type = type;
        }
    }

    private static Map<PField, Integer> makeFieldTypes(FieldType... mappings) {
        ImmutableMap.Builder<PField, Integer> builder = ImmutableMap.builder();
        for (FieldType mapping : mappings) {
            builder.put(mapping.field, mapping.type);
        }
        return builder.build();
    }

    @SafeVarargs
    private static <F extends PField> Map<String,F> makeMapping(@Nonnull MappedField<F>... mappedFields) {
        HashMap<String, F> out = new HashMap<>();
        for (MappedField<F> mappedField : mappedFields) {
            out.put(mappedField.name, mappedField.field);
        }
        return out;
    }
}
