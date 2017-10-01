/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.providence.reflect.util;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.test.providence.reflect.calculator.Calculator;
import net.morimekta.test.providence.reflect.number.Imaginary;
import net.morimekta.testing.ResourceUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for the document registry.
 */
public class ProgramRegistryTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProgramRegistry registry;

    @Before
    public void setUp() throws IOException {
        File num = ResourceUtils.copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        File calc = ResourceUtils.copyResourceTo("/parser/calculator/calculator.thrift", tmp.getRoot());

        registry = new ProgramRegistry();

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType pNum = parser.parse(new FileInputStream(num), num, ImmutableSet.of(tmp.getRoot()));
        ProgramType pCalc = parser.parse(new FileInputStream(calc), calc, ImmutableSet.of(tmp.getRoot()));

        ProgramConverter converter = new ProgramConverter(registry);

        registry.putProgram(num.toString(), converter.convert(num.toString(), pNum));
        registry.putProgram(calc.toString(), converter.convert(calc.toString(), pCalc));
    }

    @Test
    public void testService() {
        assertThat(registry.getService("Calculator", "calculator").getQualifiedName(),
                   is(Calculator.kDescriptor.getQualifiedName()));

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
        assertThat(registry.getDeclaredType("number.I").getQualifiedName(),
                   is(Imaginary.kDescriptor.getQualifiedName()));

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

    @Test
    public void testGetProvider_map() {
        PDescriptorProvider p1 = registry.getProvider("map<real,I>", "number", ImmutableMap.of(
                "container", "sorted"
        ));
        assertThat(p1.descriptor().getType(), is(PType.MAP));
        PMap map = (PMap) p1.descriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.DOUBLE));
        assertThat(map.itemDescriptor().getQualifiedName(), is(Imaginary.kDescriptor.getQualifiedName()));

        p1 = registry.getProvider("map<real,map<i32,I>>", "number", ImmutableMap.of(
                "container", "ordered"
        ));
        assertThat(p1.descriptor().getType(), is(PType.MAP));
        map = (PMap) p1.descriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.DOUBLE));
        assertThat(map.itemDescriptor().getType(), is(PType.MAP));
        map = (PMap) map.itemDescriptor();
        assertThat(map.keyDescriptor(), is(PPrimitive.I32));
        assertThat(map.itemDescriptor().getQualifiedName(), is(Imaginary.kDescriptor.getQualifiedName()));
    }

    @Test
    public void testGetProvider_set() {
        PDescriptorProvider p1 = registry.getProvider("set<I>", "number", ImmutableMap.of(
                "container", "sorted"
        ));
        assertThat(p1.descriptor().getType(), is(PType.SET));
        PSet set = (PSet) p1.descriptor();
        assertThat(set.itemDescriptor().getQualifiedName(), is(Imaginary.kDescriptor.getQualifiedName()));

        p1 = registry.getProvider("set<set<i32>>", "number", ImmutableMap.of(
                "container", "ordered"
        ));
        assertThat(p1.descriptor().getType(), is(PType.SET));
        set = (PSet) p1.descriptor();
        assertThat(set.itemDescriptor().getType(), is(PType.SET));
        PSet list = (PSet) set.itemDescriptor();
        assertThat(list.itemDescriptor(), is(PPrimitive.I32));
    }


    @Test
    public void testGetProvider_list() {
        PDescriptorProvider p1 = registry.getProvider("list<I>", "number", null);
        assertThat(p1.descriptor().getType(), is(PType.LIST));
        PList list = (PList) p1.descriptor();
        assertThat(list.itemDescriptor().getQualifiedName(), is(Imaginary.kDescriptor.getQualifiedName()));
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

    @Test
    public void testGetServiceProvider() {
        PServiceProvider srv = registry.getServiceProvider("calculator.Calculator", "number");

        assertThat(srv.getService().getQualifiedName(), is(Calculator.kDescriptor.getQualifiedName()));
    }
}
