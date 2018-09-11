package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple store searcher that finds all messages in a given store that matches
 * a simple java predicate. Note that this will always load all the data from
 * the store in order to do the search.
 *
 * @param <M> The message type in the store.
 * @param <F> The message field type.
 */
public class PredicateFilterMessageStoreSearcher<Q, K, M extends PMessage<M,F>, F extends PField>
        implements MessageSearcher<Q, M, F> {
    public interface PredicateFilter<Q, K, M extends PMessage<M,F>, F extends PField> {
        boolean test(K key, M message, Q query);
    }

    private final MessageReadOnlyStore<K, M, F> store;
    private final PredicateFilter<Q, K, M, F> predicate;

    @SuppressWarnings("unchecked")
    public PredicateFilterMessageStoreSearcher(MessageReadOnlyStore<K, M, F> store,
                                               PredicateFilter<Q, K, M, F> predicate) {
        this.store = store;
        this.predicate = predicate;
    }

    @Nonnull
    @Override
    public List<M> search(@Nonnull Q query) {
        List<M> result = new ArrayList<>();
        store.getAll(store.keys())
             .entrySet()
             .stream()
             .filter(entry -> predicate.test(entry.getKey(), entry.getValue(), query))
             .forEach(entry -> result.add(entry.getValue()));
        return result;
    }
}
