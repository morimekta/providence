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
package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramConverter;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.testing.ResourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CServiceTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProgramTypeRegistry registry;

    @Before
    public void setUp() throws IOException {
        File numeric = ResourceUtils.copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        File calculator = ResourceUtils.copyResourceTo("/parser/calculator/calculator.thrift", tmp.getRoot());

        ProgramRegistry registry = new ProgramRegistry();
        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramConverter converter = new ProgramConverter(registry);


        ProgramType program = parser.parse(new FileInputStream(numeric),
                                           numeric, ImmutableList.of(tmp.getRoot()));
        registry.putProgram(numeric.getCanonicalPath(), converter.convert(numeric.getCanonicalPath(), program));
        program = parser.parse(new FileInputStream(calculator),
                               calculator, ImmutableList.of(tmp.getRoot()));
        registry.putProgram(calculator.getCanonicalPath(), converter.convert(calculator.getCanonicalPath(), program));

        this.registry = registry.registryForPath(calculator.getCanonicalPath());
    }

    @Test
    public void testService() {
        CService base = (CService) registry.getService("calculator.BaseCalculator");
        assertThat(base.getExtendsService(), is(nullValue()));

        CService calc = (CService) registry.getService("calculator.Calculator");
        assertThat(calc.getExtendsService(), is(base));

        CServiceMethod iamalive = calc.getMethod("iamalive");  // yes, this goes
        assertThat(iamalive, is(notNullValue()));
        CServiceMethod calculate = calc.getMethod("calculate");
        assertThat(calculate, is(notNullValue()));

        assertThat(calc.getMethod("ping"), is(nullValue()));

        assertThat(base.getMethods(), is(ImmutableList.of(iamalive)));
        assertThat(calc.getMethods(), is(ImmutableList.of(calculate)));

        assertThat(base.getAnnotations(), is(ImmutableSet.of("deprecated")));
        assertThat(calc.getAnnotations(), is(ImmutableSet.of()));

        assertThat(base.hasAnnotation("deprecated"), is(true));
        assertThat(base.hasAnnotation(ThriftAnnotation.DEPRECATED), is(true));
        assertThat(base.hasAnnotation("foobar"), is(false));

        assertThat(base.getAnnotationValue("deprecated"), is("Because reasons"));
        assertThat(base.getAnnotationValue(ThriftAnnotation.DEPRECATED), is("Because reasons"));
        assertThat(base.getAnnotationValue("foobar"), is(nullValue()));

        assertThat(base.getDocumentation(), is(nullValue()));
        assertThat(calc.getDocumentation(), is("Block comment on service"));
    }
}
