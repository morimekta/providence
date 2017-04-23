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
        assertThat(c.getKey(), is(-1));
        assertThat(c.getType(), is(PType.STRING));
        assertThat(c.getDefaultValue(), is("value"));
    }
}
