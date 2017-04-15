package net.morimekta.providence.descriptor;

import net.morimekta.test.providence.core.Value;
import net.morimekta.test.providence.core.calculator.Operator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PEnumDescriptorTest {
    @Test
    public void testEnumDescriptor() {
        PEnumDescriptor<Value> vd = Value.kDescriptor;
        PEnumDescriptor<Operator> od = Operator.kDescriptor;

        assertThat(vd, is(not(od)));
        assertThat(vd.equals(null), is(false));
        assertThat(vd.equals(new Object()), is(false));
        assertThat(vd.hashCode(), is(not(od.hashCode())));
        assertThat(vd.getFactoryInternal(), is(notNullValue()));
        assertThat(vd.equals(vd), is(true));
        assertThat(vd.toString(), is("providence.Value"));
    }
}
