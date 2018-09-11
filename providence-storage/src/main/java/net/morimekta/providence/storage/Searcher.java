package net.morimekta.providence.storage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

/**
 * Interface for searching a store for a specific search S.
 *
 * @param <Q> Search query param, be it single string or struct or union with search parameters.
 * @param <R> The result type of the search.
 */
public interface Searcher<Q, R> {
    /**
     * Run a query and return the resulting items.
     *
     * @param query The search query.
     * @return List of all R that matches Query Q.
     */
    @Nonnull
    List<R> search(@Nonnull Q query);

    /**
     * Run a query and stream the resulting items.
     *
     * @param query The search query.
     * @return Stream of all R that matches Query Q.
     */
    default Stream<R> stream(@Nonnull Q query) {
        return search(query).stream();
    }
}
