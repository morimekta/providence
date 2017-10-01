package net.morimekta.providence.util;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for the TypeRegistry class.
 */
public class BaseTypeRegistryTest {
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
    public void testFinalTypename() {
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
    public void testGetProvider() {
        PDescriptorProvider p1 = registry.getProvider("map<real,I>", "number", null);
        assertThat(p1.descriptor().getType(), is(PType.MAP));
        PMap map = (PMap) p1.descriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.DOUBLE));
        assertThat(map.itemDescriptor(), is(Imaginary.kDescriptor));

        p1 = registry.getProvider("map<real,map<i32,I>>", "number", null);
        assertThat(p1.descriptor().getType(), is(PType.MAP));
        map = (PMap) p1.descriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.DOUBLE));
        assertThat(map.itemDescriptor().getType(), is(PType.MAP));
        map = (PMap) map.itemDescriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.I32));
        assertThat(map.itemDescriptor(), is(Imaginary.kDescriptor));
    }

    @Test
    public void testGetProvider_bad() {
        try {
            registry.getProvider("map<real>", "calculator", null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid map generic part \"map<real>\": missing ',' kv separator"));
        }
        try {
            registry.getProvider("map<real,real,number.I>", "calculator", null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid map generic part \"map<real,real,number.I>\": " +
                                          "Invalid atomic type name real,number.I"));
        }

        try {
            registry.getProvider("set<real,number.I>", "calculator", null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid set generic part \"set<real,number.I>\": " +
                                          "Invalid atomic type name real,number.I"));
        }

        try {
            registry.getProvider("list<real,number.I>", "calculator", null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid list generic part \"list<real,number.I>\": " +
                                          "Invalid atomic type name real,number.I"));
        }
    }
}
