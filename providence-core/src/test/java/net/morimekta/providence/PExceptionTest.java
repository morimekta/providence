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

import net.morimekta.test.providence.core.ExceptionFields;
import net.morimekta.test.providence.core.Value;
import net.morimekta.util.Binary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class PExceptionTest {
    @Test
    public void testGetMessage() {
        ExceptionFields ex = ExceptionFields.builder()
                                            .setBooleanValue(true)
                                            .setBinaryValue(Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5}))
                                            .setByteValue((byte) 6)
                                            .setDoubleValue(7.8d)
                                            .setIntegerValue(9)
                                            .setLongValue(10L)
                                            .setStringValue("11")
                                            .setShortValue((short) 12)
                                            .setEnumValue(Value.FIFTEENTH)
                                            .build();

        assertEquals("{" +
                     "booleanValue:true," +
                     "byteValue:6," +
                     "shortValue:12," +
                     "integerValue:9," +
                     "longValue:10," +
                     "doubleValue:7.8," +
                     "stringValue:\"11\"," +
                     "binaryValue:b64(AAECAwQF)," +
                     "enumValue:FIFTEENTH" +
                     "}", ex.getMessage());
    }
}
