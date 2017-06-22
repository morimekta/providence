package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThriftOnlySerialzerProviderTest {
    @Test
    public void testDefault() {
        assertThat(new ThriftOnlySerializerProvider().getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(BinarySerializer.MEDIA_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(BinarySerializer.ALT_MEDIA_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(TJsonProtocolSerializer.MEDIA_TYPE).getDefault(), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(TCompactProtocolSerializer.MEDIA_TYPE).getDefault(), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer() {
        assertThat(new ThriftOnlySerializerProvider().getSerializer(BinarySerializer.MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider().getSerializer(BinarySerializer.ALT_MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider().getSerializer(TJsonProtocolSerializer.MEDIA_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftOnlySerializerProvider().getSerializer(TCompactProtocolSerializer.MEDIA_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));

        assertThat(new ThriftOnlySerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(BinarySerializer.MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(BinarySerializer.ALT_MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(TJsonProtocolSerializer.MEDIA_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftOnlySerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(TCompactProtocolSerializer.MEDIA_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer_fail() {
        try {
            new ThriftOnlySerializerProvider().getSerializer(JsonSerializer.MEDIA_TYPE);
            fail("No exception on no serializer");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("No such serializer for media type " + JsonSerializer.MEDIA_TYPE));
        }
    }
}
