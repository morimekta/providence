package net.morimekta.providence.storage.jdbi;

import com.google.common.collect.ImmutableMap;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.NamedArgumentFinder;
import org.jdbi.v3.core.argument.NullArgument;
import org.jdbi.v3.core.statement.StatementContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

import static net.morimekta.providence.storage.jdbi.MessageFieldArgument.getDefaultColumnType;

public class MessageNamedArgumentFinder<M extends PMessage<M,F>, F extends PField> implements NamedArgumentFinder {
    private final String               prefix;
    private final M                    message;
    private final Map<PField, Integer> fieldTypes;

    public MessageNamedArgumentFinder(@Nullable String prefix,
                                      @Nonnull M message,
                                      @Nonnull Map<PField, Integer> fieldTypes) {
        this.message = message;
        this.prefix = (prefix == null || prefix.isEmpty() ? "" : prefix + ".");
        this.fieldTypes = ImmutableMap.copyOf(fieldTypes);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Argument> find(String name, StatementContext ctx) {
        if (!prefix.isEmpty()) {
            if (name.startsWith(prefix)) {
                name = name.substring(prefix.length());
            } else {
                return Optional.empty();
            }
        }

        String[]           parts          = name.split("[.]");
        PMessage           leaf           = message;
        PMessageDescriptor leafDescriptor = message.descriptor();

        for (int i = 0; i < parts.length - 1; ++i) {
            String part = parts[i];
            PField field = leafDescriptor.findFieldByName(part);
            if (field == null) return Optional.empty();
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
                return Optional.of(new MessageFieldArgument(leaf, field, getColumnType(field)));
            }
            return Optional.of(new NullArgument(getColumnType(field)));
        }
        return Optional.empty();
    }

    private int getColumnType(PField field) {
        return fieldTypes.getOrDefault(field, getDefaultColumnType(field));
    }

}
