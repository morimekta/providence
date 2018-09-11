package net.morimekta.providence.jdbi.v3;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static net.morimekta.providence.jdbi.v3.MessageFieldArgument.getDefaultColumnType;

/**
 * Helper class to handle inserting content from messages into a table.
 * The helper will only select values form the message itself, not using
 * nested structure or anything like that.
 *
 * The inserter is built in  such a way that you can create the inserter
 * (even as a static field), and use it any number of times with a handle
 * to do the pre-programmed insert. The execute method is thread safe, as
 * long as none of the modification methods are called.
 *
 * <pre>{@code
 * class MyInserter {
 *     private static final MessageInserter&lt;MyMessage,MyMessage._Field&gt; INSERTER =
 *             new MessageInserter.Builder&lt;&gt;("my_message")
 *                     .set(MyMessage.UUID, MyMessage.NAME)
 *                     .set("amount", MyMessage.VALUE, Types.INTEGER)  // DOUBLE -&gt; INTEGER
 *                     .onDuplicateKeyUpdate(MyMessage.VALUE)
 *                     .build();
 *
 *     private final Jdbi dbi;
 *
 *     public MyInserter(Jdbi dbi) {
 *         this.dbi = dbi;
 *     }
 *
 *     int insert(HandleMyMessage... messages) {
 *         try (Handle handle = dbi.open()) {
 *             return INSERTER.execute(handle, messages);
 *         }
 *     }
 * }
 * }</pre>
 *
 * Or it can be handled in line where needed. The building process is pretty cheap,
 * so this should not be a problem unless it is called <i>a lot</i> for very small
 * message.
 *
 * <pre>{@code
 * class MyInserter {
 *     int insert(HandleMyMessage... messages) {
 *         try (Handle handle = dbi.open()) {
 *             return new MessageInserter.Builder&lt;MyMessage,MyMessage._Field&gt;("my_message")
 *                     .set(MyMessage.UUID, MyMessage.NAME)
 *                     .set("amount", MyMessage.VALUE, Types.INTEGER)  // DOUBLE -&gt; INTEGER
 *                     .onDuplicateKeyUpdateAllExcept(MyMessage.UUID)
 *                     .build()
 *                     .execute(handle, messages);
 *         }
 *     }
 * }
 * }</pre>
 *
 * The rules for using this is pretty simple:
 *
 * <ul>
 *     <li>
 *         All fields set must be specified before onDuplicateKey* behavior.
 *     </li>
 *     <li>
 *         Only one of <code>onDuplicateKeyIgnore</code> and <code>onDuplicateKeyUpdate</code>
 *         can be set.
 *     </li>
 *     <li>
 *         <code>execute(...)</code> can be called any number of times, and is thread safe.
 *     </li>
 * </ul>
 */
public class MessageInserter<M extends PMessage<M,F>, F extends PField> {
    private final String                queryPrefix;
    private final String                querySuffix;
    private final Map<String, F>        columnToFieldMap;
    private final Map<String, Integer>  columnTypeMap;
    private final ImmutableList<String> columnOrder;
    private final String                valueMarkers;

    private MessageInserter(String queryPrefix,
                            String querySuffix,
                            List<String> columnOrder,
                            Map<String, F> columnToFieldMap,
                            Map<String, Integer> columnTypeMap) {
        this.queryPrefix = queryPrefix;
        this.querySuffix = querySuffix;
        this.columnOrder = ImmutableList.copyOf(columnOrder);
        this.columnToFieldMap = ImmutableMap.copyOf(columnToFieldMap);
        this.columnTypeMap = ImmutableMap.copyOf(columnTypeMap);
        this.valueMarkers = "(" + columnOrder.stream()
                                             .map(k -> "?")
                                             .collect(Collectors.joining(",")) + ")";
    }

    @SafeVarargs
    public final int execute(Handle handle, M... items) {
        return execute(handle, ImmutableList.copyOf(items));
    }

    public int execute(Handle handle, Collection<M> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Nothing to insert");
        }

        String query = queryPrefix + items.stream()
                                          .map(item -> valueMarkers)
                                          .collect(Collectors.joining(", ")) +
                       querySuffix;
        Update update = handle.createUpdate(query);
        int    offset = 0;
        for (M item : items) {
            for (String column : columnOrder) {
                F field = columnToFieldMap.get(column);
                int type = columnTypeMap.get(column);
                update.bind(offset++, new MessageFieldArgument<>(item, field, type));
            }
        }
        return update.execute();
    }

    public static class Builder<M extends PMessage<M,F>, F extends PField> {
        private final String               intoTable;
        private final Map<String, F>       columnToFieldMap;
        private final Map<String, Integer> columnTypeMap;
        private final Set<String>          onDuplicateUpdate;
        private final AtomicBoolean        onDuplicateIgnore;

        private final PMessageDescriptor<M, F> descriptor;

        /**
         * Create a message inserter builder.
         *
         * @param intoTable The table name to insert info.
         * @deprecated since 1.7.0
         */
        @Deprecated
        public Builder(@Nonnull String intoTable) {
            this(intoTable, null);
        }

        /**
         * Create a message inserter builder.
         *
         * @param descriptor The type descriptor.
         * @param intoTable The table name to insert info.
         */
        public Builder(@Nonnull PMessageDescriptor<M, F> descriptor,
                       @Nonnull String intoTable) {
            this(intoTable, descriptor);
        }

        private Builder(@Nonnull String intoTable, @Nullable PMessageDescriptor<M, F> descriptor) {
            this.descriptor = descriptor;
            this.intoTable = intoTable;
            this.columnToFieldMap = new LinkedHashMap<>();
            this.columnTypeMap = new HashMap<>();
            this.onDuplicateUpdate = new TreeSet<>();
            this.onDuplicateIgnore = new AtomicBoolean();
        }

        /**
         * Set all fields with defaults.
         * @return The builder.
         */
        public final Builder<M,F> setAll() {
            if (descriptor == null) throw new IllegalStateException("No descriptor in builder");
            return set(descriptor.getFields());
        }

        /**
         * Set all fields with defaults.
         *
         * @param except Fields to exclude.
         * @return The builder.
         */
        @SafeVarargs
        public final Builder<M,F> setAllExcept(F... except) {
            if (descriptor == null) throw new IllegalStateException("No descriptor in builder");
            return setAllExcept(ImmutableSet.copyOf(except), descriptor);
        }

        /**
         * Set all fields with defaults.
         *
         * @param except Fields to exclude.
         * @return The builder.
         */
        public Builder<M,F> setAllExcept(Collection<F> except) {
            if (descriptor == null) throw new IllegalStateException("No descriptor in builder");
            return setAllExcept(except, descriptor);
        }

        /**
         * Set all fields with defaults.
         *
         * @param except Fields to exclude.
         * @return The builder.
         * @deprecated since 1.7.0
         */
        @Deprecated
        @SafeVarargs
        public final Builder<M,F> setAllExcept(PMessageDescriptor<M, F> descriptor, F... except) {
            return setAllExcept(ImmutableSet.copyOf(except), descriptor);
        }

        /**
         * Set all fields with defaults.
         *
         * @param except Fields to exclude.
         * @return The builder.
         * @deprecated since 1.7.0
         */
        @Deprecated
        public Builder<M,F> setAllExcept(PMessageDescriptor<M, F> descriptor, Collection<F> except) {
            return setAllExcept(except, descriptor);
        }

        private Builder<M,F> setAllExcept(@Nonnull Collection<F> except,
                                         @Nonnull PMessageDescriptor<M, F> descriptor) {
            for (F field : descriptor.getFields()) {
                if (!except.contains(field)) {
                    set(field.getName(), field, getDefaultColumnType(field));
                }
            }
            return this;
        }

        /**
         * Set the specific fields with default name and type.
         *
         * @param fields The fields to be set.
         * @return The builder.
         */
        @SafeVarargs
        public final Builder<M,F> set(F... fields) {
            return set(ImmutableList.copyOf(fields));
        }

        /**
         * Set the specific fields with default name and type.
         *
         * @param fields The fields to be set.
         * @return The builder.
         */
        public final Builder<M,F> set(Collection<F> fields) {
            for (F field : fields) {
                set(field.getName(), field, getDefaultColumnType(field));
            }
            return this;
        }

        /**
         * Set the specific field with name and default type.
         *
         * @param column The column name to set.
         * @param field The field to be set.
         * @return The builder.
         */
        public final Builder<M,F> set(String column, F field) {
            return set(column, field, getDefaultColumnType(field));
        }

        /**
         * Set the specific field with specific type and default name.
         *
         * @param field The field to be set.
         * @param type The field type to set as.
         * @return The builder.
         */
        public final Builder<M,F> set(F field, int type) {
            return set(field.getName(), field, type);
        }

        /**
         * Set the specific field with specific name and type.
         *
         * @param column The column name to set.
         * @param field The field to be set.
         * @param type The field type to set as.
         * @return The builder.
         */
        public final Builder<M,F> set(String column, F field, int type) {
            if (columnToFieldMap.containsKey(column)) {
                throw new IllegalArgumentException("Column " + column + " already inserted");
            }
            if (onDuplicateIgnore.get() || onDuplicateUpdate.size() > 0) {
                throw new IllegalStateException("Duplicate key behavior already determined");
            }
            this.columnToFieldMap.put(column, field);
            this.columnTypeMap.put(column, type);
            return this;
        }

        /**
         * On duplicate keys update the given fields.
         *
         * @param fields The fields to update.
         * @return The builder.
         */
        @SafeVarargs
        public final Builder<M,F> onDuplicateKeyUpdate(F... fields) {
            return onDuplicateKeyUpdate(ImmutableList.copyOf(fields));
        }

        /**
         * On duplicate keys update the given fields.
         *
         * @param fields The fields to update.
         * @return The builder.
         */
        public final Builder<M,F> onDuplicateKeyUpdate(Collection<F> fields) {
            List<String> columns = new ArrayList<>(fields.size());
            fields.forEach(field -> {
                AtomicBoolean found = new AtomicBoolean();
                columnToFieldMap.forEach((column, f) -> {
                    if (f.equals(field)) {
                        columns.add(column);
                        found.set(true);
                    }
                });
                if (!found.get()) {
                    // TODO: Allow update anyway?
                    throw new IllegalArgumentException("Field " + field + " not inserted");
                }
            });
            return onDuplicateKeyUpdate(columns.toArray(new String[0]));
        }

        /**
         * On duplicate keys update all except the given fields.
         *
         * @param fields The fields to NOT update.
         * @return The builder.
         */
        @SafeVarargs
        public final Builder<M,F> onDuplicateKeyUpdateAllExcept(F... fields) {
            return onDuplicateKeyUpdateAllExcept(ImmutableList.copyOf(fields));
        }

        /**
         * On duplicate keys update all except the given fields.
         *
         * @param fields The fields to NOT update.
         * @return The builder.
         */
        public final Builder<M,F> onDuplicateKeyUpdateAllExcept(Collection<F> fields) {
            List<String> columns = new ArrayList<>(fields.size());
            fields.forEach(field -> {
                AtomicBoolean found = new AtomicBoolean();
                columnToFieldMap.forEach((column, f) -> {
                    if (f.equals(field)) {
                        columns.add(column);
                        found.set(true);
                    }
                });
                if (!found.get()) {
                    // TODO: Allow update anyway?
                    throw new IllegalArgumentException("Field " + field + " not inserted");
                }
            });
            return onDuplicateKeyUpdateAllExcept(columns.toArray(new String[0]));
        }

        /**
         * On duplicate keys update all except the given fields.
         *
         * @param exceptColumns The column names NOT to update.
         * @return The builder.
         */
        public final Builder<M,F> onDuplicateKeyUpdateAllExcept(String... exceptColumns) {
            TreeSet<String> columns = new TreeSet<>(columnToFieldMap.keySet());
            columns.removeAll(ImmutableList.copyOf(exceptColumns));
            return onDuplicateKeyUpdate(columns.toArray(new String[0]));
        }

        /**
         * On duplicate keys update the given columns.
         *
         * @param columns The column names NOT to update.
         * @return The builder.
         */
        public final Builder<M,F> onDuplicateKeyUpdate(String... columns) {
            if (onDuplicateIgnore.get()) {
                throw new IllegalStateException("Duplicate key behavior already set to ignore");
            }
            Collections.addAll(onDuplicateUpdate, columns);
            return this;
        }

        /**
         * On duplicate keys ignore updates.
         *
         * @return The builder.
         */
        public final Builder<M,F> onDuplicateKeyIgnore() {
            if (onDuplicateUpdate.size() > 0) {
                throw new IllegalStateException("Duplicate key behavior already set to update");
            }
            onDuplicateIgnore.set(true);
            return this;
        }

        /**
         * @return The final built inserter.
         */
        public MessageInserter<M,F> build() {
            if (columnToFieldMap.isEmpty()) {
                throw new IllegalStateException("No columns inserted");
            }
            List<String> columnOrder = new ArrayList<>(columnToFieldMap.keySet());

            StringBuilder prefixBuilder = new StringBuilder("INSERT ");
            if (onDuplicateIgnore.get()) {
                prefixBuilder.append("IGNORE ");
            }
            prefixBuilder.append("INTO ")
                   .append(intoTable)
                   .append(" (")
                   .append(columnOrder.stream()
                                      .map(col -> "`" + col + "`")
                                      .collect(Collectors.joining(", ")))
                   .append(") VALUES ");

            StringBuilder suffixBuilder = new StringBuilder();

            if (onDuplicateUpdate.size() > 0) {
                suffixBuilder.append(" ON DUPLICATE KEY UPDATE");
                boolean first = true;
                for (String column : onDuplicateUpdate) {
                    if (first) {
                        first = false;
                    } else {
                        suffixBuilder.append(",");
                    }

                    suffixBuilder.append(" `")
                           .append(column)
                           .append("` = VALUES(`")
                           .append(column)
                           .append("`)");
                }
            }

            return new MessageInserter<>(prefixBuilder.toString(),
                                         suffixBuilder.toString(),
                                         columnOrder,
                                         columnToFieldMap,
                                         columnTypeMap);
        }
    }
}
