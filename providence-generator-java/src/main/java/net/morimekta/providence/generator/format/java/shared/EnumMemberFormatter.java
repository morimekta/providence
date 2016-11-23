package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Base interface for formatting a providence message class.
 */
public interface EnumMemberFormatter {
    default void appendClassAnnotations(CEnumDescriptor type) throws GeneratorException {}

    default Collection<String> getExtraImplements(CEnumDescriptor type) throws GeneratorException {
        return new LinkedList<>();
    }

    default void appendMethods(CEnumDescriptor type) throws GeneratorException {}

    default void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {}
}
