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
package net.morimekta.providence.serializer.binary;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BinaryFormatUtilsTest {
    @Test
    public void testFieldInfo() {
        BinaryFormatUtils.FieldInfo info = new BinaryFormatUtils.FieldInfo(12, BinaryType.VOID);

        assertThat(info.toString(), is("field(12: void(1))"));
        assertThat(info.getId(), is(12));
        assertThat(info.getType(), is(BinaryType.VOID));
    }
}
