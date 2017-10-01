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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PPrimitive;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CConstTest {
    @Test
    public void testConst() {
        CConst c = new CConst("comment",
                              "name",
                              PPrimitive.STRING.provider(),
                              () -> "value",
                              ImmutableMap.of());

        assertThat(c.getName(), is("name"));
        assertThat(c.getDocumentation(), is("comment"));
        assertThat(c.getId(), is(-1));
        assertThat(c.getType(), is(PType.STRING));
        assertThat(c.getDefaultValue(), is("value"));
    }
}
