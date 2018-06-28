package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Simple store searcher that finds all messages in a given store that matches
 * a simple java bi-predicate. Note that this will always load all the data from
 * the store in order to do the search.
 *
 * @param <K> The store key type.
 * @param <M> The message type in the store.
 * @param <F> The message field type.
 */
public class BiPredicateMessageStoreSearcher<
        K,
        M extends PMessage<M,F>,
        F extends PField> implements MessageSearcher<BiPredicate<K, M>, M, F> {
    private final MessageStore<K, M, F> store;

    public BiPredicateMessageStoreSearcher(MessageStore<K, M, F> store) {
        this.store = store;
    }

    @Nonnull
    @Override
    public List<M> search(@Nonnull BiPredicate<K, M> query) {
        List<M> result = new ArrayList<>();
        store.getAll(store.keys())
             .entrySet()
             .stream()
             .filter(e -> query.test(e.getKey(), e.getValue()))
             .forEach(e -> result.add(e.getValue()));
        return result;
    }
}
