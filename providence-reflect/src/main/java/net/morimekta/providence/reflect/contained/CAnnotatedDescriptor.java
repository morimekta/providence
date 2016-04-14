package net.morimekta.providence.reflect.contained;

import java.util.Set;

/**
 */
public interface CAnnotatedDescriptor {
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
    String getAnnotation(String name);
}
