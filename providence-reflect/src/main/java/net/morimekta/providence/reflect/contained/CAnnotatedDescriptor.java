package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.reflect.util.ThriftAnnotation;

import java.util.Set;

/**
 */
public interface CAnnotatedDescriptor {
    /**
     * The type comment is the last block of comment written before the type
     * declaration. Comments on the same line, after the declaration is
     * ignored.
     *
     * @return The comment string containing all formatting (not including the
     *         comment delimiter and the leading space.
     */
    String getDocumentation();

    /**
     * Get set of available annotations.
     * @return The annotation set.
     */
    Set<String> getAnnotations();

    /**
     * Get the given annotation value.
     * @param name Name of annotation.
     * @return If the annotation is present.
     */
    boolean hasAnnotation(String name);

    /**
     * Get the given annotation value.
     * @param name Name of annotation.
     * @return The annotation value or null.
     */
    String getAnnotationValue(String name);

    /**
     * Get the given annotation value.
     * @param annotation The annotation.
     * @return If the annotation is present.
     */
    default boolean hasAnnotation(ThriftAnnotation annotation) {
        return hasAnnotation(annotation.tag);
    }

    /**
     * Get the given annotation value.
     * @param annotation The annotation.
     * @return The annotation value or null.
     */
    default String getAnnotationValue(ThriftAnnotation annotation) {
        return getAnnotationValue(annotation.tag);
    }
}
