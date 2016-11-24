package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CProgram;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public interface BaseConstantsFormatter {
    void appendConstantsClass(CProgram document) throws GeneratorException;
}
