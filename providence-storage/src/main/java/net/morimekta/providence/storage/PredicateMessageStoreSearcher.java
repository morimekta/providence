package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Simple store searcher that finds all messages in a given store that matches
 * a simple java predicate. Note that this will always load all the data from
 * the store in order to do the search.
 *
 * @param <M> The message type in the store.
 * @param <F> The message field type.
 */
public class PredicateMessageStoreSearcher<
        M extends PMessage<M,F>,
        F extends PField> implements MessageSearcher<Predicate<M>, M, F> {
    private final MessageStore<Object, M, F> store;

    @SuppressWarnings("unchecked")
    public PredicateMessageStoreSearcher(MessageStore<?, M, F> store) {
        this.store = (MessageStore<Object, M, F>) store;
    }

    @Nonnull
    @Override
    public List<M> search(@Nonnull Predicate<M> query) {
        List<M> result = new ArrayList<>();
        store.getAll(store.keys())
             .values()
             .stream()
             .filter(Objects::nonNull)
             .filter(query)
             .forEach(result::add);
        return result;
    }
}
