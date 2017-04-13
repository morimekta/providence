package net.morimekta.providence.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the TypeRegistry class.
 */
public class TypeRegistryTest {
    @Test
    public void testFinalTypename() {
        TypeRegistry registry = new TypeRegistry();

        registry.putTypedef("I", "number", "Imaginary");
        registry.putTypedef("real", "number", "double");
        registry.registerRecursively(Operation.kDescriptor);

        assertEquals("double", registry.finalTypename("real", "number"));
        assertEquals("double", registry.finalTypename("number.real", "calculator"));

        assertEquals("list<double>", registry.finalTypename("list<real>", "number"));
        assertEquals("set<double>", registry.finalTypename("set<number.real>", "calculator"));

        assertEquals("list<number.Imaginary>", registry.finalTypename("list<I>", "number"));
        assertEquals("set<number.Imaginary>", registry.finalTypename("set<number.I>", "calculator"));

        assertEquals((PDeclaredDescriptor) Imaginary.kDescriptor,
                     registry.getDeclaredType("I", "number"));
        assertEquals((PDeclaredDescriptor) Imaginary.kDescriptor,
                     registry.getDeclaredType("number.I", "calculator"));
    }
}
