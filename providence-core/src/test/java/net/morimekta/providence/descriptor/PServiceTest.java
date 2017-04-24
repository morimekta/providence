package net.morimekta.providence.descriptor;

import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Calculator2;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class PServiceTest {
    @Test
    public void testPService() {
        PService base = Calculator.kDescriptor;

        assertThat(base.getProgramName(), is("calculator"));
        assertThat(base.getName(), is("Calculator"));
        assertThat(base.getQualifiedName(), is("calculator.Calculator"));
        assertThat(base.getQualifiedName("calculator"), is("Calculator"));
        assertThat(base.getExtendsService(), is(nullValue()));

        PService extended = Calculator2.kDescriptor;

        assertThat(extended.getMethods(), hasSize(4));
        assertThat(extended.getExtendsService(), is(base));
        assertThat(extended.getMethod("calculate"), is(notNullValue()));
    }
}
