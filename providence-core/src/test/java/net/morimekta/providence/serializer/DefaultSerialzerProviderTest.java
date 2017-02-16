package net.morimekta.providence.serializer;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultSerialzerProviderTest {
    @Test
    public void testDefault() {
        assertThat(new DefaultSerializerProvider().getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(BinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(BinarySerializer.ALT_MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.JSON_MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(FastBinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(FastBinarySerializer.class)));
    }

    @Test
    public void testGetSerializer() {
        assertThat(new DefaultSerializerProvider().getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider().getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));

        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new DefaultSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));
    }

    @Test
    public void testGetSerializer_fail() {
        try {
            new DefaultSerializerProvider().getSerializer("text/plain");
            fail("No exception on no serializer");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("No such serializer for media type text/plain"));
        }
    }
}
