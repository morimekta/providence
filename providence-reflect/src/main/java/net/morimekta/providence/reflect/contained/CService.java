package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Descriptor for a complete service.
 */
public class CService extends PService implements CAnnotatedDescriptor {
    private final Map<String, String> annotations;
    private String comment;

    public CService(String comment,
                    String packageName,
                    String name,
                    PServiceProvider extendsService,
                    Collection<CServiceMethod> methods,
                    Map<String, String> annotations) {
        super(packageName, name, extendsService, methods);
        this.comment = comment;
        this.annotations = annotations;
    }

    @SuppressWarnings("unchecked")
    public Collection<CServiceMethod> getMethods() {
        return (Collection<CServiceMethod>) super.getMethods();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotationValue(String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

    @Override
    public String getDocumentation() {
        return comment;
    }
}
