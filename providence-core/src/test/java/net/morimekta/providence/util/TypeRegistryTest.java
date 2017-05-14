package net.morimekta.providence.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

    @Test
    public void testService() {
        TypeRegistry registry = new TypeRegistry();
        registry.putService(Calculator.kDescriptor);

        try {
            registry.putService(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No service to register"));
        }
        try {
            registry.putService(Calculator.kDescriptor);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("Service calculator.Calculator already registered"));
        }

        assertThat(registry.getService("Calculator", "calculator"), is(sameInstance(Calculator.kDescriptor)));
    }

    @Test
    public void testGetDeclaredType() {
        TypeRegistry registry = new TypeRegistry();

        registry.putTypedef("I", "number", "Imaginary");
        registry.putTypedef("real", "number", "double");
        registry.registerRecursively(Operation.kDescriptor);

        assertThat((PDeclaredDescriptor) registry.getDeclaredType("number.I"),
                   is(sameInstance(Imaginary.kDescriptor)));

        try {
            registry.getDeclaredType("FakeNews");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Requesting global typename without package: \"FakeNews\""));
        }
        try {
            registry.getDeclaredType("real.fake.News");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid declared type: \"real.fake.News\""));
        }

        try {
            registry.getDeclaredType("fake.News");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such package \"fake\" exists for type \"News\""));
        }
        try {
            registry.getDeclaredType("number.Fake");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such type \"Fake\" in package \"number\""));
        }

    }
}
