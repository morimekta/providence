package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CService;

import java.io.IOException;

/**
 * TODO(steineldar): Make a proper class description.
 */
public interface BaseServiceFormatter {
    @SuppressWarnings("unused")
    void appendServiceClass(CService cs) throws GeneratorException, IOException;
}
