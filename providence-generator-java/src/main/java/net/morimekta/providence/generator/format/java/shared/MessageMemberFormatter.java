package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Base interface for formatting a providence message class.
 */
public interface MessageMemberFormatter {
    default void appendClassAnnotations(JMessage<?> message) throws GeneratorException {}

    default Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        return new LinkedList<>();
    }

    default void appendConstants(JMessage<?> message) throws GeneratorException {}

    default void appendFields(JMessage<?> message) throws GeneratorException {}

    default void appendConstructors(JMessage<?> message) throws GeneratorException {}

    default void appendMethods(JMessage<?> message) throws GeneratorException {}

    default void appendExtraProperties(JMessage<?> message) throws GeneratorException {}
}
