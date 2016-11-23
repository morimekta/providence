package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CDocument;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public interface BaseConstantsFormatter {
    void appendConstantsClass(CDocument document) throws GeneratorException;
}
