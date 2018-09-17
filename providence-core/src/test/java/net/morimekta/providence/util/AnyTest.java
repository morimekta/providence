package net.morimekta.providence.util;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.test.providence.core.RequiredFields;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnyTest {
    @Test
    public void testAny() {
        OptionalFields opts = OptionalFields.builder()
                                            .setIntegerValue(1234)
                                            .setStringValue("Kałłavułłin")
                                            .build();

        Any any = Any.wrapMessage(opts);
        OptionalFields unwrapped = any.unwrapMessage(OptionalFields.kDescriptor);

        assertThat(unwrapped, is(opts));
        assertThat(any.getType(), is(opts.descriptor().getQualifiedName()));
        assertThat(any.getMediaType(), is(BinarySerializer.MEDIA_TYPE));
        assertThat(any.wrappedTypeIs(OptionalFields.kDescriptor), is(true));
        assertThat(any.wrappedTypeIs(RequiredFields.kDescriptor), is(false));
    }

    @Test
    public void testAny_json() {
        OptionalFields opts = OptionalFields.builder()
                                            .setIntegerValue(1234)
                                            .setStringValue("Kałłavułłin")
                                            .build();

        Any any = Any.wrapMessage(opts, new JsonSerializer());
        OptionalFields unwrapped = any.unwrapMessage(OptionalFields.kDescriptor);

        assertThat(any.hasData(), is(false));
        assertThat(any.getText(), is("{\"4\":1234,\"7\":\"Kałłavułłin\"}"));
        assertThat(any.getType(), is(opts.descriptor().getQualifiedName()));
        assertThat(any.getMediaType(), is(JsonSerializer.MEDIA_TYPE));
        assertThat(any.wrappedTypeIs(OptionalFields.kDescriptor), is(true));
        assertThat(any.wrappedTypeIs(RequiredFields.kDescriptor), is(false));
        assertThat(unwrapped, is(opts));
    }
}
