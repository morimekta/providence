package net.morimekta.providence.jdbi.v2;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.jdbi.v2.util.NullArgument;

import com.google.common.collect.ImmutableMap;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.NamedArgumentFinder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * A {@link NamedArgumentFinder} implementation that uses a message
 * and finds values based on the thrift declared field names. This
 * supports chained calls to any depth as long as each level is a
 * single message field.
 *
 * @param <M> The message type.
 * @param <F> The message field type.
 */
public class MessageNamedArgumentFinder<M extends PMessage<M,F>, F extends PField> implements NamedArgumentFinder {
    private final String               prefix;
    private final M                    message;
    private final Map<PField, Integer> fieldTypes;

    /**
     * Create a named argument finder.
     *
     * @param prefix Optional prefix name. E.g. "x" will make for lookup
     *               tags like ":x.my_field".
     * @param message The message to look up fields in.
     * @param fieldTypes Overriding of default field types. This can contain
     *                   fields for any of the contained message types, and
     *                   will be mapped whenever the field is selected.
     */
    public MessageNamedArgumentFinder(@Nullable String prefix,
                                      @Nonnull M message,
                                      @Nonnull Map<PField, Integer> fieldTypes) {
        this.message = message;
        this.prefix = (prefix == null || prefix.isEmpty() ? "" : prefix + ".");
        this.fieldTypes = ImmutableMap.copyOf(fieldTypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Argument find(String name) {
        if (!prefix.isEmpty()) {
            if (name.startsWith(prefix)) {
                name = name.substring(prefix.length());
            } else {
                return null;
            }
        }

        String[]           parts          = name.split("[.]");
        PMessage           leaf           = message;
        PMessageDescriptor leafDescriptor = message.descriptor();

        for (int i = 0; i < parts.length - 1; ++i) {
            String part = parts[i];
            PField field = leafDescriptor.findFieldByName(part);
            if (field == null) return null;
            if (field.getType() != PType.MESSAGE) {
                throw new IllegalArgumentException("");
            }
            leafDescriptor = (PMessageDescriptor) field.getDescriptor();
            if (leaf != null) {
                leaf = (PMessage) leaf.get(field.getId());
            }
        }
        String leafName = parts[parts.length - 1];
        PField field = leafDescriptor.findFieldByName(leafName);
        if (field != null) {
            if (leaf != null) {
                return new MessageFieldArgument(leaf, field, getColumnType(field));
            }
            return new NullArgument(getColumnType(field));
        }
        return null;
    }

    private int getColumnType(PField field) {
        return fieldTypes.getOrDefault(field, MessageFieldArgument.getDefaultColumnType(field));
    }

}
