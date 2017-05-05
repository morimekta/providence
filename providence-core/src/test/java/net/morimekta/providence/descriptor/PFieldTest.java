package net.morimekta.providence.descriptor;

import net.morimekta.test.providence.core.DefaultFields;
import net.morimekta.test.providence.core.RequiredFields;
import net.morimekta.test.providence.core.calculator.Operation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PFieldTest {
    @Test
    public void testPField() {
        assertThat(Operation._Field.OPERANDS, is(Operation._Field.OPERANDS));
        assertThat(Operation._Field.OPERANDS.toString(),
                   is("field(2: optional list<calculator.Operand> operands)"));
        assertThat(DefaultFields._Field.LONG_VALUE.toString(),
                   is("field(5: i64 longValue)"));
        assertThat(RequiredFields._Field.LONG_VALUE.toString(),
                   is("field(5: required i64 longValue)"));
    }
}
