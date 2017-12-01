/*
 * Copyright (c) 2016, Stein Eldar Johnsen
 *
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

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.morimekta.providence.reflect.util.ReflectionUtils.isThriftFile;
import static net.morimekta.providence.reflect.util.ReflectionUtils.longestCommonPrefixPath;
import static net.morimekta.providence.reflect.util.ReflectionUtils.programNameFromPath;
import static net.morimekta.providence.reflect.util.ReflectionUtils.stripCommonPrefix;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for the ReflectionUtil utiltiy class.
 */
public class ReflectionUtilTest {
    @Test
    public void testIsThriftFile() {
        assertThat(isThriftFile("C:\\my\\package\\test.thrift"), is(true));
        assertThat(isThriftFile("my\\package\\test.thrift"), is(true));
        assertThat(isThriftFile(new File("/my/package/test.thrift")), is(true));
        assertThat(isThriftFile(new File("my/package/test.thr")), is(true));
        assertThat(isThriftFile("/my/package/test.providence"), is(true));
        assertThat(isThriftFile("../other/test.pvd"), is(true));
        assertThat(isThriftFile("test.thrift"), is(true));
        assertThat(isThriftFile("TEST.THRIFT"), is(true));

        assertThat(isThriftFile("../other/.pvd"), is(false));
        assertThat(isThriftFile(".thrift"), is(false));
        assertThat(isThriftFile("/my/package/test.txt"), is(false));
        assertThat(isThriftFile("C:\\my\\package\\test.bat"), is(false));
    }

    @Test
    public void testProgramNameFromPath() {
        assertThat(programNameFromPath("C:\\my\\package\\test.thrift"), is("test"));
        assertThat(programNameFromPath("my\\package\\test.other.thrift"), is("test_other"));
        assertThat(programNameFromPath("/my/package/test-not.thrift"), is("test_not"));
        assertThat(programNameFromPath("my/package/test.thr"), is("test"));
        assertThat(programNameFromPath("/my/package/test.providence"), is("test"));
        assertThat(programNameFromPath("../other/test.pvd"), is("test"));

        assertThat(programNameFromPath("../other/.pvd"), is(""));
        assertThat(programNameFromPath(".thrift"), is(""));
        assertThat(programNameFromPath("/my/package/test.txt"), is(""));
        assertThat(programNameFromPath("C:\\my\\package\\test.bat"), is(""));
    }

    @Test
    public void testLongestCommonPrefix() {
        assertThat(longestCommonPrefixPath(ImmutableList.of("/abba/anne", "/abba/annika")),
                   is("/abba/"));
        assertThat(longestCommonPrefixPath(ImmutableList.of("/u2/bono", "/abba/annika")),
                   is("/"));
        assertThat(longestCommonPrefixPath(ImmutableList.of("../u2/bono", "../abba/annika")),
                   is("../"));
        assertThat(longestCommonPrefixPath(ImmutableList.of("foo/bar", "bar/foo")),
                   is(""));
    }

    @Test
    public void testStripCommonPrefix() {
        assertThat(stripCommonPrefix(ImmutableList.of("/abba/anne", "/abba/annika")),
                   is(ImmutableList.of("anne", "annika")));
        assertThat(stripCommonPrefix(ImmutableList.of("/u2/bono", "/abba/annika")),
                   is(ImmutableList.of("u2/bono", "abba/annika")));
        assertThat(stripCommonPrefix(ImmutableList.of("../u2/bono", "../abba/annika")),
                   is(ImmutableList.of("u2/bono", "abba/annika")));
        assertThat(stripCommonPrefix(ImmutableList.of("foo/bar", "bar/foo")),
                   is(ImmutableList.of("foo/bar", "bar/foo")));
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ReflectionUtils> constructor = ReflectionUtils.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible(), is(false));
        try {
            constructor.setAccessible(true);
            ReflectionUtils instance = constructor.newInstance();
            assertThat(instance, is(notNullValue()));
        } finally {
            constructor.setAccessible(false);
        }
    }
}
