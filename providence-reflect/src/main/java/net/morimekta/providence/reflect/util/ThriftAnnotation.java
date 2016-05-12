package net.morimekta.providence.reflect.util;

/**
 * Enum containing known "general" thrift annotations.
 */
public enum ThriftAnnotation {
    NONE(null),
    COLLECTION("collection"),
    COMPACT("compact");

    public final String id;

    ThriftAnnotation(String id) {
        this.id = id;
    }

    public static ThriftAnnotation forIdentifier(String id) {
        switch (id) {
            case "collection": return COLLECTION;
            case "compact": return COMPACT;
        }
        return NONE;
    }
}
