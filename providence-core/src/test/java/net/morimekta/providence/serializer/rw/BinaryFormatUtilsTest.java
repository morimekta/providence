package net.morimekta.providence.serializer.rw;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BinaryFormatUtilsTest {
    @Test
    public void testFieldInfo() {
        BinaryFormatUtils.FieldInfo info = new BinaryFormatUtils.FieldInfo(12, BinaryType.VOID);

        assertThat(info.toString(), is("field(12: void(1))"));
        assertThat(info.getId(), is(12));
        assertThat(info.getType(), is(BinaryType.VOID));
    }
}
