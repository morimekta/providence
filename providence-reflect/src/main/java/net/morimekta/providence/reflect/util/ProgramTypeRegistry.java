package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.contained.CProgram;

import javax.annotation.Nonnull;

/**
 * A recursive type registry that also knows the program that it
 * represents.
 */
public class ProgramTypeRegistry extends RecursiveTypeRegistry {
    private CProgram program;
    private ProgramType programType;

    public ProgramTypeRegistry(@Nonnull String localProgramContext) {
        super(localProgramContext);
    }

    @Nonnull
    public CProgram getProgramForName(String programName) {
        RecursiveTypeRegistry tmp = getRegistryForProgramName(programName);
        if (tmp instanceof ProgramTypeRegistry) {
            return ((ProgramTypeRegistry) tmp).getProgram();
        }
        throw new IllegalArgumentException("No program for name " + programName);
    }

    public CProgram getProgram() {
        return program;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgram(CProgram program) {
        this.program = program;
        if (isEmpty()) {
            program.getTypedefs().forEach(
                    (id, type) -> registerTypedef(id, getLocalProgramContext(), type));
            for (PDeclaredDescriptor<?> descriptor : program.getDeclaredTypes()) {
                registerRecursively(descriptor);
            }
            for (PService service : program.getServices()) {
                registerRecursively(service);
            }
            program.getConstants().forEach(
                    c -> registerConstant(c.getName(), getLocalProgramContext(), c.getDefaultValue()));
        }
    }

    public void setProgramType(ProgramType type) {
        this.programType = type;
    }
}
