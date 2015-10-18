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

package org.apache.thrift.j2.descriptor;

import org.apache.thrift.j2.TBuilder;
import org.apache.thrift.j2.TBuilderFactory;
import org.apache.thrift.j2.TType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.10.15
 */
public class TDeclaredDescriptorTest {
    private MockDescriptor mockType;

    @Before
    public void setUp() {
        mockType = new MockDescriptor();
    }

    @Test
    public void testGetPackageName() {
        assertEquals("test", mockType.getPackageName());
    }

    @Test
    public void testGetName() {
        assertEquals("MockDescriptor", mockType.getName());
        assertEquals("MockDescriptor", mockType.getQualifiedName("test"));
        assertEquals("test.MockDescriptor", mockType.getQualifiedName("not_test"));
        assertEquals("test.MockDescriptor", mockType.getQualifiedName(null));
    }

    @Test
    public void testGetTypeGroup() {
        assertSame(TType.STRING, mockType.getType());
    }

    private static class MockDescriptor
            extends TDeclaredDescriptor<String> {
        protected MockDescriptor() {
            super("comment", "test", "MockDescriptor");
        }

        @Override
        public TType getType() {
            return TType.STRING;
        }

        @Override
        public TBuilderFactory<String> factory() {
            return new TBuilderFactory<String>() {
                @Override
                public TBuilder<String> builder() {
                    return new TBuilder<String>() {
                        @Override
                        public String build() {
                            return "";
                        }
                    };
                }
            };
        }
    }
}
