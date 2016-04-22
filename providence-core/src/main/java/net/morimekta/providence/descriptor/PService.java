package net.morimekta.providence.descriptor;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Descriptor for a complete service.
 */
public class PService {
    private final String name;
    private final String packageName;
    private final PServiceProvider extendsService;
    private final Collection<? extends PServiceMethod> methods;

    public PService(String packageName,
             String name,
             PServiceProvider extendsService,
             Collection<? extends PServiceMethod> methods) {
        this.name = name;
        this.packageName = packageName;
        this.extendsService = extendsService;
        this.methods = methods;
    }

    public PService(String packageName,
             String name,
             PServiceProvider extendsService,
             PServiceMethod[] methods) {
        this.name = name;
        this.packageName = packageName;
        this.extendsService = extendsService;
        this.methods = ImmutableList.copyOf(methods);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public PService getExtendsService() {
        return extendsService.getService();
    }

    public Collection<? extends PServiceMethod> getMethods() {
        return methods;
    }

    public Set<String> getMethodNames() {
        Set<String> result = new TreeSet<>();
        if (extendsService != null) {
            result.addAll(extendsService.getService().getMethodNames());
        }
        for (PServiceMethod<?,?,?,?> method : methods) {
            result.add(method.getName());
        }
        return result;
    }

    public PServiceMethod<?,?,?,?> getMethod(String name) {
        for (PServiceMethod<?,?,?,?> method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        if (extendsService != null) {
            return extendsService.getService().getMethod(name);
        }
        return null;
    }

    public String getQualifiedName(String packageContext) {
        if (packageName.equals(packageContext)) {
            return name;
        }
        return packageName + "." + name;
    }
}
