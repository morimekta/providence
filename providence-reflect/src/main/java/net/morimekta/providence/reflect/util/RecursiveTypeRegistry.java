package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.util.BaseTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private final Map<String, Object>                 constants;

    public RecursiveTypeRegistry(@Nonnull String localProgramContext) {
        this.localProgramContext = localProgramContext;
        this.declaredTypes       = new LinkedHashMap<>();
        this.services            = new HashMap<>();
        this.includes            = new HashMap<>();
        this.constants           = new HashMap<>();
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
        if (registry == this) {
            throw new IllegalArgumentException("Registering include back to itself: " + programName);
        }
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

        if (localProgramContext.equals(program)) {
            if (declaredTypes.containsKey(name)) {
                return (T) declaredTypes.get(name);
            }
            throw new IllegalArgumentException(
                    "No such type \"" + name + "\" in program \"" + program + "\"");
        }
        if (includes.containsKey(program)) {
            return includes.get(program).getDeclaredType(name, program);
        }
        throw new IllegalArgumentException(
                "No such program \"" + program + "\" known for type \"" + typeName + "\"");
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConstantValue(@Nonnull String constReference, @Nonnull String programContext) {
        if (!constReference.contains(".")) {
            constReference = programContext + "." + constReference;
        }

        String program = constReference.replaceAll("\\..*", "");
        String name = constReference.replaceAll(".*\\.", "");

        if (localProgramContext.equals(program)) {
            return (T) constants.get(name);
        }
        if (includes.containsKey(program)) {
            return includes.get(program).getConstantValue(name, program);
        }

        return null;
    }

    @Nonnull
    @Override
    public PService getService(String serviceName, String programContext) {
        String finalName = qualifiedNameFromIdAndContext(serviceName, programContext);

        String program = finalName.replaceAll("\\..*", "");
        String name = finalName.replaceAll(".*\\.", "");

        if (localProgramContext.equals(program)) {
            if (services.containsKey(name)) {
                return services.get(name);
            }
            throw new IllegalArgumentException("No such service \"" + name + "\" in program \"" + program + "\"");
        }
        if (includes.containsKey(program)) {
            return includes.get(program).getService(name, program);
        }
        throw new IllegalArgumentException("No such program \"" + program + "\" known for service \"" + serviceName + "\"");
    }

    @Override
    public void registerConstant(@Nonnull String identifier, @Nonnull String program, @Nonnull Object value) {
        String origIdentifier = identifier;
        if (identifier.contains(".")) {
            String[] parts = identifier.split("[.]");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid const identifier");
            }
            identifier = parts[1];
            program = parts[0];
        }

        if (localProgramContext.equals(program)) {
            constants.put(identifier, value);
        } else if (includes.containsKey(program)) {
            includes.get(program).registerConstant(identifier, program, value);
        } else {
            throw new IllegalArgumentException("No such include \"" + program + "\" for const " + origIdentifier + " in " + localProgramContext);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(@Nonnull PService service) {
        String program = service.getProgramName();

        if (!localProgramContext.equals(program)) {
            if (includes.containsKey(program)) {
                includes.get(program).registerRecursively(service);
                return true;
            }
            throw new IllegalArgumentException("No such include \"" + program + "\" for type " + service.getQualifiedName() + " in " + localProgramContext);
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
            throw new IllegalArgumentException("No include for type: " + declaredType.getQualifiedName());
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
