package net.morimekta.providence.jackson;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by morimekta on 5/2/16.
 */
public class LinkedHashSetBuilder<T> {
    private final LinkedHashSet<T> set;

    public LinkedHashSetBuilder() {
        set = new LinkedHashSet<>();
    }

    public LinkedHashSetBuilder<T> add(T first, T... values) {
        set.add(first);
        for (T value : values) {
            set.add(value);
        }
        return this;
    }


    public LinkedHashSetBuilder<T> addAll(Collection<T> collection) {
        set.addAll(collection);
        return this;
    }

    public LinkedHashSet<T> build() {
        return set;
    }
}
