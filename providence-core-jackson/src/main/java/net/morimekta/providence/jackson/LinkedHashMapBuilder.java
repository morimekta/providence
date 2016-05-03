package net.morimekta.providence.jackson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by morimekta on 5/2/16.
 */
public class LinkedHashMapBuilder<K, V> {
    private final LinkedHashMap<K, V> map;

    public LinkedHashMapBuilder() {
        map = new LinkedHashMap<>();
    }

    public LinkedHashMapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public LinkedHashMapBuilder<K, V> putAll(Map<K, V> values) {
        map.putAll(values);
        return this;
    }

    public LinkedHashMap<K, V> build() {
        return map;
    }
}
