package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;

/**
 * Interface for searching a store for a specific search S.
 *
 * @param <S> Search param, be it single string or struct or union with search parameters.
 * @param <M> PMessage that we search for.
 * @param <F> PField of M.
 */
public interface MessageSearcher<S, M extends PMessage<M, F>, F extends PField> {

    /**
     * Get a list of builders for the search input. Any modifications
     * to the returned builders will not be reflected onto the store.
     *
     * @param search The key to look up.
     * @param <B>    The builder type.
     * @return List of builders that matches search S.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M, F>>
    List<B> searchBuilders(@Nonnull S search) {
        return mutateAll(search(search));
    }

    /**
     * Look up a set of keys from the storage.
     *
     * @param search The search to query.
     * @return List of all M that matches Search S.
     */
    @Nonnull
    List<M> search(@Nonnull S search);

}
