package net.morimekta.providence.jdbi.v2;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Update;

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

import static net.morimekta.providence.jdbi.v2.MessageFieldArgument.getDefaultColumnType;

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
        Update update = handle.createStatement(query);
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

        public Builder(String intoTable) {
            this.intoTable = intoTable;
            this.columnToFieldMap = new LinkedHashMap<>();
            this.columnTypeMap = new HashMap<>();
            this.onDuplicateUpdate = new TreeSet<>();
            this.onDuplicateIgnore = new AtomicBoolean();
        }

        @SafeVarargs
        public final Builder<M,F> set(F... fields) {
            for (F field : fields) {
                set(field.getName(), field, getDefaultColumnType(field));
            }
            return this;
        }

        public final Builder<M,F> set(String column, F field) {
            return set(column, field, getDefaultColumnType(field));
        }

        public final Builder<M,F> set(F field, int type) {
            return set(field.getName(), field, type);
        }

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

        @SafeVarargs
        public final Builder<M,F> onDuplicateKeyUpdate(F... fields) {
            return onDuplicateKeyUpdate(ImmutableList.copyOf(fields));
        }

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

        @SafeVarargs
        public final Builder<M,F> onDuplicateKeyUpdateAllExcept(F... fields) {
            return onDuplicateKeyUpdateAllExcept(ImmutableList.copyOf(fields));
        }

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

        public final Builder<M,F> onDuplicateKeyUpdateAllExcept(String... exceptColumns) {
            TreeSet<String> columns = new TreeSet<>(columnToFieldMap.keySet());
            columns.removeAll(ImmutableList.copyOf(exceptColumns));
            return onDuplicateKeyUpdate(columns.toArray(new String[0]));
        }

        public final Builder<M,F> onDuplicateKeyUpdate(String... columns) {
            if (onDuplicateIgnore.get()) {
                throw new IllegalStateException("Duplicate key behavior already set to ignore");
            }
            Collections.addAll(onDuplicateUpdate, columns);
            return this;
        }

        public final Builder<M,F> onDuplicateKeyIgnore() {
            if (onDuplicateUpdate.size() > 0) {
                throw new IllegalStateException("Duplicate key behavior already set to update");
            }
            onDuplicateIgnore.set(true);
            return this;
        }

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
