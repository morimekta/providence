package net.morimekta.providence.storage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Interface for searching a store for a specific search S.
 *
 * @param <Q> Search query param, be it single string or struct or union with search parameters.
 * @param <R> The result type of the search
 */
public interface Searcher<Q, R> {
    /**
     * Look up a set of keys from the storage.
     *
     * @param query The search to query.
     * @return List of all M that matches Query Q.
     */
    @Nonnull
    List<R> search(@Nonnull Q query);

}
