package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
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

    public CService(String packageName,
                    String name,
                    PServiceProvider extendsService,
                    Collection<PServiceMethod> methods,
                    Map<String, String> annotations) {
        super(packageName, name, extendsService, methods);
        this.annotations = annotations;
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

}
