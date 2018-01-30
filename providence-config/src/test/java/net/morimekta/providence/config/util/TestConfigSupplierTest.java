package net.morimekta.providence.config.util;

import net.morimekta.test.providence.config.Database;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestConfigSupplierTest {
    @Test
    public void testToString() {
        TestConfigSupplier<Database,Database._Field> supplier =
                new TestConfigSupplier<>(Database.builder().build());

        assertThat(supplier.toString(), is("TestConfig{}"));
        assertThat(supplier.getName(), is("TestConfig"));
    }
}
