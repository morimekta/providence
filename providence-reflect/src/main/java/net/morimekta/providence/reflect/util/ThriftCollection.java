package net.morimekta.providence.reflect.util;

/**
 * Enum containing known "general" thrift annotations.
 */
public enum ThriftCollection {
    DEFAULT,
    ORDERED,
    SORTED;

    public static ThriftCollection forName(String id) {
        if (id == null) return DEFAULT;
        switch (id.toUpperCase()) {
            case "ordered": return ORDERED;
            case "sorted": return SORTED;
        }
        return DEFAULT;
    }
}
