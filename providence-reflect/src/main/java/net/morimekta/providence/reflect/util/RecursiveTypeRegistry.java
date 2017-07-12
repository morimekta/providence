package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.util.BaseTypeRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A registry that can reference each other recursively. Each registry will
 * have a specific package context, and needs to be built up in a recursive
 * manner. This way each type registry will only have access to the described
 * types actually referenced and included in the given thrift program file.
 */
public class RecursiveTypeRegistry extends BaseTypeRegistry {
    private final String                              localProgramContext;
    private final Map<String, PDeclaredDescriptor<?>> declaredTypes;
    private final Map<String, PService>               services;
    private final Map<String, RecursiveTypeRegistry>  includes;

    public RecursiveTypeRegistry(@Nonnull String localProgramContext) {
        this.localProgramContext = localProgramContext;
        this.declaredTypes       = new LinkedHashMap<>();
        this.services            = new HashMap<>();
        this.includes            = new HashMap<>();
    }

    public String getLocalProgramContext() {
        return localProgramContext;
    }

    /**
     * Get the registry to be used for the specific program. Search through
     * all the included registries to find the best one.
     *
     * @param programName The program to find registry for.
     * @return The Recursive type registry or null of not found.
     */
    public RecursiveTypeRegistry getRegistryForProgramName(String programName) {
        if (localProgramContext.equals(programName)) return this;
        for (RecursiveTypeRegistry registry : includes.values()) {
            if (registry.getLocalProgramContext().equals(programName)) {
                return registry;
            }
        }
        for (RecursiveTypeRegistry registry : includes.values()) {
            RecursiveTypeRegistry tmp = registry.getRegistryForProgramName(programName);
            if (tmp != null) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * Register a recursive included registry.
     *
     * @param programName The program to be included.
     * @param registry The registry for the given program.
     */
    public void registerInclude(String programName, RecursiveTypeRegistry registry) {
        if (includes.containsKey(programName)) {
            return;
        }
        includes.put(programName, registry);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String typeName,
                                                                @Nonnull String programContext) {
        String finalName = finalTypename(typeName, programContext);

        String program = finalName.replaceAll("\\..*", "");
        String name = finalName.replaceAll(".*\\.", "");

        if (declaredTypes.containsKey(name)) {
            return (T) declaredTypes.get(name);
        }
        if (localProgramContext.equals(program)) {
            throw new IllegalArgumentException(
                    "No such type \"" + name + "\" in program \"" + program + "\"");
        }
        if (includes.containsKey(program)) {
            return includes.get(program).getDeclaredType(name, program);
        }
        throw new IllegalArgumentException(
                "No such program \"" + program + "\" known for type \"" + typeName + "\"");
    }

    @Nonnull
    @Override
    public PService getService(String serviceName, String programContext) {
        String finalName = qualifiedNameFromIdAndContext(serviceName, programContext);

        String program = finalName.replaceAll("\\..*", "");
        String name = finalName.replaceAll(".*\\.", "");

        if (services.containsKey(name)) {
            return services.get(name);
        }
        if (localProgramContext.equals(program)) {
            throw new IllegalArgumentException("No such service \"" + name + "\" in program \"" + program + "\"");
        }
        if (services.containsKey(finalName)) {
            return services.get(finalName);
        }
        throw new IllegalArgumentException("No such program \"" + program + "\" known for service \"" + serviceName + "\"");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(@Nonnull PService service) {
        String program = service.getProgramName();

        if (!localProgramContext.equals(program)) {
            if (!includes.containsKey(program)) {
                includes.get(program).registerRecursively(service);
            }
            throw new IllegalArgumentException("");
        }
        String serviceName = service.getName();

        if (services.containsKey(serviceName)) {
            return false;
        }
        services.put(serviceName, service);
        return true;
    }

    @Override
    public <T> boolean register(PDeclaredDescriptor<T> declaredType) {
        String program = declaredType.getProgramName();

        if (!localProgramContext.equals(program)) {
            if (includes.containsKey(program)) {
                includes.get(program).registerRecursively(declaredType);
            }
            throw new IllegalArgumentException("");
        }
        String typeName = declaredType.getName();
        if (declaredTypes.containsKey(typeName)) {
            return false;
        }

        declaredTypes.put(typeName, declaredType);
        return true;
    }

    protected boolean isEmpty() {
        return declaredTypes.isEmpty() && services.isEmpty();
    }
}
