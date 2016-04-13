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

package net.morimekta.providence.reflect.parser;

import net.morimekta.providence.model.ThriftDocument;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class ThriftParserTest {
    @Test
    public void testParse() throws IOException, ParseException {
        ThriftParser parser = new ThriftParser();

        ThriftDocument calculator = parser.parse(getClass().getResourceAsStream("/parser/calculator/calculator.thrift"),
                                                 "calculator.thrift");

        assertEquals("calculator", calculator.getPackage());
        assertEquals(7, calculator.getDecl().size());
    }
}
