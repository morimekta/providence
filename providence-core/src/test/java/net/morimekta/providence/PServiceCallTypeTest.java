/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence;

import net.morimekta.test.providence.core.calculator.Operator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class PServiceCallTypeTest {
    @Test
    public void testValues() {
        assertThat(Operator.MULTIPLY.asInteger(), is(Operator.MULTIPLY.getId()));
        assertThat(Operator.MULTIPLY.asString(), is(Operator.MULTIPLY.getName()));
        assertThat(Operator.MULTIPLY.getValue(), is(Operator.MULTIPLY.getId()));
    }
}
