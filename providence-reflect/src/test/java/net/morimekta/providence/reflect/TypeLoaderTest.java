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

package net.morimekta.providence.reflect;

import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 12.09.15
 */
public class TypeLoaderTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testLoadServices() throws IOException {
        copyResourceTo("/parser/tests/service2.thrift", temp.getRoot());
        File service = copyResourceTo("/parser/tests/service.thrift", temp.getRoot());

        TypeLoader loader = new TypeLoader(ImmutableList.of());

        ProgramTypeRegistry reg = loader.load(service);

        assertThat(loader.loadedPrograms(), hasSize(2));
        assertThat(reg.getLocalProgramContext(), is("service"));

        ProgramTypeRegistry rep = loader.load( service);

        assertThat(loader.loadedPrograms(), hasSize(2));
        assertThat(rep, sameInstance(reg));
    }

    @Test
    public void testFailures() throws IOException {
        File fail = copyResourceTo("/failure/duplicate_field_id.thrift", temp.getRoot());

        TypeLoader loader = new TypeLoader(ImmutableList.of());

        File folder = temp.newFolder("boo");
        try {
            loader.load(folder);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Unable to load thrift program: " + folder.toString() + " is not a file."));
        }

        File noFile = new File(temp.getRoot(), "boo");
        try {
            loader.load(noFile);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Unable to load thrift program: " + noFile.toString() + " is not a file."));
        }

        try {
            loader.load(fail);
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.getMessage(), is("Field id 1 already exists in T"));
            assertThat(e.getFile(), is(fail.getName()));
        }
    }
}
