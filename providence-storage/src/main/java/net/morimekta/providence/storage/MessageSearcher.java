package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.util.List;

import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;

/**
 * Interface for searching a store for a specific search S.
 *
 * @param <Q> Search query param, be it single string or struct or union with search parameters.
 * @param <M> PMessage that we search for.
 * @param <F> PField of M.
 */
public interface MessageSearcher<Q, M extends PMessage<M, F>, F extends PField> {

    /**
     * Get a list of builders for the query input. Any modifications
     * to the returned builders will not be reflected onto the store.
     *
     * @param query The key to look up.
     * @param <B>   The builder type.
     * @return List of builders that matches query Q.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M, F>>
    List<B> searchBuilders(@Nonnull Q query) {
        return mutateAll(search(query));
    }

    /**
     * Look up a set of keys from the storage.
     *
     * @param query The search to query.
     * @return List of all M that matches Query Q.
     */
    @Nonnull
    List<M> search(@Nonnull Q query);

}
