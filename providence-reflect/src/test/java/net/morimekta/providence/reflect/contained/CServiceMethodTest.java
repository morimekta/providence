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
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CServiceMethodTest {
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
    public void testServiceMethod() {
        CService calc = (CService) registry.getService("calculator.Calculator");

        CServiceMethod iamalive = calc.getMethod("iamalive");  // yes, this goes
        assertThat(iamalive, is(notNullValue()));
        CServiceMethod calculate = calc.getMethod("calculate");
        assertThat(calculate, is(notNullValue()));

        CServiceMethod copyOfCalculate = new CServiceMethod(calculate.getDocumentation(),
                                                            calculate.getName(),
                                                            calculate.isOneway(),
                                                            calculate.getRequestType(),
                                                            calculate.getResponseType(),
                                                            ImmutableMap.of());

        assertThat(iamalive.toString(), is("ServiceMethod(oneway void iamalive([calculator.BaseCalculator.iamalive.request])"));
        assertThat(calculate.toString(), is("ServiceMethod(calculator.Operand calculate([calculator.Calculator.calculate.request])"));

        assertThat(iamalive, is(iamalive));
        assertThat(iamalive, is(not(calculate)));
        assertThat(copyOfCalculate, is(calculate));

        assertThat(iamalive.hashCode(), is(not(calculate.hashCode())));

        assertThat(iamalive.getDocumentation(), is("line comment on method."));
        assertThat(iamalive.getAnnotations(), is(Collections.EMPTY_SET));
        assertThat(iamalive.hasAnnotation("foo"), is(false));
        assertThat(iamalive.hasAnnotation(ThriftAnnotation.DEPRECATED), is(false));
        assertThat(iamalive.getAnnotationValue(ThriftAnnotation.DEPRECATED), is(nullValue()));

        assertThat(iamalive.isOneway(), is(true));
        assertThat(iamalive.getRequestType().getQualifiedName(), is("calculator.BaseCalculator.iamalive.request"));
        assertThat(iamalive.getResponseType(), is(nullValue()));

        assertThat(calculate.isOneway(), is(false));
        assertThat(calculate.getRequestType().getQualifiedName(), is("calculator.Calculator.calculate.request"));
        assertThat(calculate.getResponseType(), is(notNullValue()));
        assertThat(calculate.getResponseType().getQualifiedName(), is("calculator.Calculator.calculate.response"));
    }
}
