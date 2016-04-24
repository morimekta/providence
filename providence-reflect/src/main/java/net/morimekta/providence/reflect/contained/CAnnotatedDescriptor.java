package net.morimekta.providence.reflect.contained;

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
    String getComment();

    /**
     * Get set of available annotations.
     * @return
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
     * @return The annotation value.
     */
    String getAnnotationValue(String name);
}
