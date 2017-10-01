package net.morimekta.providence.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for the TypeRegistry class.
 */
public class SimpleTypeRegistryTest {
    private SimpleTypeRegistry registry;

    @Before
    public void setUp() {
        registry = new SimpleTypeRegistry();
        registry.registerRecursively(Operation.kDescriptor);
        registry.registerRecursively(Calculator.kDescriptor);
        registry.registerTypedef("I", "number", "Imaginary");
        registry.registerTypedef("real", "number", "double");
    }

    @Test
    public void testService() {
        assertThat(registry.getService("Calculator", "calculator"), is(sameInstance(Calculator.kDescriptor)));

        try {
            registry.getService("gurba.dot.Calculator");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid identifier: \"gurba.dot.Calculator\""));
        }
        try {
            registry.getService("Calculator");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Requesting global service name without package: \"Calculator\""));
        }
        try {
            registry.getService("gurba.Calculator");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such program \"gurba\" known for service \"Calculator\""));
        }
        try {
            registry.getService("Calculator", "gurba");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such program \"gurba\" known for service \"Calculator\""));
        }

        try {
            registry.getService("calculator.Gurba");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such service \"Gurba\" in program \"calculator\""));
        }
        try {
            registry.getService("Gurba", "calculator");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such service \"Gurba\" in program \"calculator\""));
        }
    }

    @Test
    public void testGetDeclaredType() {
        assertThat((PDeclaredDescriptor) registry.getDeclaredType("number.I"),
                   is(sameInstance(Imaginary.kDescriptor)));

        try {
            registry.getDeclaredType("FakeNews");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Requesting global type name without program name: \"FakeNews\""));
        }
        try {
            registry.getDeclaredType("real.fake.News");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid identifier: \"real.fake.News\""));
        }

        try {
            registry.getDeclaredType("fake.News");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such program \"fake\" known for type \"News\""));
        }
        try {
            registry.getDeclaredType("number.Fake");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such type \"Fake\" in program \"number\""));
        }

        try {
            registry.getDeclaredType("gurba.Imaginary");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such program \"gurba\" known for type \"Imaginary\""));
        }
        try {
            registry.getDeclaredType("Imaginary", "gurba");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such program \"gurba\" known for type \"Imaginary\""));
        }

        try {
            registry.getDeclaredType("number.Gurba");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such type \"Gurba\" in program \"number\""));
        }
        try {
            registry.getDeclaredType("Gurba", "number");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such type \"Gurba\" in program \"number\""));
        }
    }
}
