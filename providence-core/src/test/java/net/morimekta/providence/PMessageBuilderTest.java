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

import net.morimekta.providence.test_internal.CompactFields;
import net.morimekta.providence.test_internal.Containers;
import net.morimekta.providence.test_internal.OptionalFields;

import org.junit.Test;

import static net.morimekta.providence.test_internal.Containers._Field.STRING_SET;
import static net.morimekta.providence.test_internal.OptionalFields._Field.BOOLEAN_VALUE;
import static net.morimekta.providence.test_internal.OptionalFields._Field.COMPACT_VALUE;
import static net.morimekta.providence.util_internal.EqualToMessage.equalToMessage;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class PMessageBuilderTest {
    @Test
    public void testBuilder() {
        OptionalFields._Builder b = OptionalFields.builder();

        assertThat(b.presentFields(), hasSize(0));
        assertThat(b.modifiedFields(), hasSize(0));

        assertThat(b.mutator(COMPACT_VALUE), isA(PMessageBuilder.class));
        assertThat(b.setBooleanValue(false), is(sameInstance(b)));

        assertThat(b.presentFields(), hasSize(2));
        assertThat(b.presentFields(),
                   hasItems(COMPACT_VALUE, BOOLEAN_VALUE));

        assertThat(b.build(), is(equalToMessage(OptionalFields.builder()
                                                              .setCompactValue(CompactFields.builder().build())
                                                              .setBooleanValue(false)
                                                              .build())));

        assertThat(b.clear(BOOLEAN_VALUE), is(sameInstance(b)));

        assertThat(b.modifiedFields(), hasSize(2));
        assertThat(b.modifiedFields(),
                   hasItems(COMPACT_VALUE, BOOLEAN_VALUE));

        assertThat(b.build(), is(equalToMessage(OptionalFields.builder()
                                                              .setCompactValue(CompactFields.builder().build())
                                                              .build())));
    }

    @Test
    public void testCollections() {
        Containers._Builder b1 = Containers.builder();

        b1.addToBooleanList(true, false, false, true);
        b1.addTo(STRING_SET, "boo");

        Containers._Builder b2 = b1.build().mutate();

        assertThat(b2.modifiedFields(), hasSize(0));
        assertThat(b2.presentFields(), hasSize(2));
    }
}
