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

package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.DefaultFields;
import net.morimekta.test.providence.core.RequiredFields;
import net.morimekta.test.providence.core.calculator.Operand;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 10.09.15.
 */
public class PStructDescriptorTest {
    PStructDescriptor<?, ?> valueType;

    @Before
    public void setUp() {
        valueType = Imaginary.kDescriptor;
    }

    @Test
    public void testToString() {
        // Even though it's a union, it inherits from PStructDescriptor.
        assertEquals("calculator.Operand", Operand.kDescriptor.toString());
        assertEquals("number.Imaginary", Imaginary.kDescriptor.toString());
        assertEquals("calculator.Operation", Operation.kDescriptor.toString());
    }

    @Test
    public void testOverrides() {
        assertEquals(PMessageVariant.STRUCT, Imaginary.kDescriptor.getVariant());
        assertThat(Imaginary.kDescriptor.getBuilderSupplier(), is(notNullValue()));
        assertThat(Imaginary.kDescriptor.isSimple(), is(true));
        assertThat(Containers.kDescriptor.isSimple(), is(false));
    }

    @Test
    public void testEquals() {
        assertEquals(Imaginary.kDescriptor,
                     Imaginary.provider()
                              .descriptor());
        assertEquals(Operation.kDescriptor,
                     Operation.provider()
                              .descriptor());

        assertNotEquals(Operation.kDescriptor, Imaginary.kDescriptor);

        // Use dummies to be able to properly check equals.
        PStructDescriptor base = new Dummy(copyOf(DefaultFields._Field.values()));
        PStructDescriptor same = new Dummy(copyOf(DefaultFields._Field.values()));
        PStructDescriptor size = new Dummy(copyOf(CompactFields._Field.values()));
        PStructDescriptor diff = new Dummy(copyOf(RequiredFields._Field.values()));

        assertThat(base, is(same));
        assertThat(base, not(size));
        assertThat(base, not(diff));
    }

    @Test
    public void testFindField() {
        assertThat(Operation.kDescriptor.fieldForId(1), is(Operation._Field.OPERATOR));
        assertThat(Operation.kDescriptor.fieldForName("operands"), is(Operation._Field.OPERANDS));
        assertThat(Operation.kDescriptor.getField(1), is(Operation._Field.OPERATOR));
        assertThat(Operation.kDescriptor.getField("operands"), is(Operation._Field.OPERANDS));

        try {
            Operation.kDescriptor.fieldForId(5);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No field key 5 in calculator.Operation"));
        }
        try {
            Operation.kDescriptor.fieldForName("bar");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No field \"bar\" in calculator.Operation"));
        }
    }

    @Test
    public void testExtra() {
        assertThat(Operation.kDescriptor.getType(), is(PType.MESSAGE));
        assertThat(Operation.kDescriptor.getVariant(), is(PMessageVariant.STRUCT));
        assertThat(Operation.kDescriptor.isSimple(), is(false));
    }

    private static class Dummy extends  PStructDescriptor {
        private PField[] fields;

        @SuppressWarnings("unchecked")
        Dummy(Collection<PField> fields) {
            super("program", "Type", DefaultFields::builder, false);
            this.fields = fields.toArray(new PField[fields.size()]);
        }

        @Nonnull
        @Override
        public PField[] getFields() {
            return fields;
        }

        @Override
        public PField findFieldByName(String name) {
            for (PField field : fields) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
            return null;
        }

        @Override
        public PField findFieldById(int id) {
            for (PField field : fields) {
                if (field.getId() == id) {
                    return field;
                }
            }
            return null;
        }
    }
}
