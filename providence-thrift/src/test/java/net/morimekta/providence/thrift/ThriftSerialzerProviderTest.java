package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThriftSerialzerProviderTest {
    @Test
    public void testDefault() {
        assertThat(new ThriftSerializerProvider().getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(BinarySerializer.MEDIA_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(BinarySerializer.ALT_MEDIA_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.JSON_MEDIA_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(FastBinarySerializer.MEDIA_TYPE).getDefault(), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(TJsonProtocolSerializer.MEDIA_TYPE).getDefault(), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider(TCompactProtocolSerializer.MEDIA_TYPE).getDefault(), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer() {
        assertThat(new ThriftSerializerProvider().getSerializer(BinarySerializer.MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(BinarySerializer.ALT_MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(JsonSerializer.MEDIA_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(JsonSerializer.JSON_MEDIA_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(FastBinarySerializer.MEDIA_TYPE), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(TJsonProtocolSerializer.MEDIA_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(TCompactProtocolSerializer.MEDIA_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));

        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(BinarySerializer.MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(BinarySerializer.ALT_MEDIA_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(JsonSerializer.MEDIA_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(JsonSerializer.JSON_MEDIA_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(FastBinarySerializer.MEDIA_TYPE), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(TJsonProtocolSerializer.MEDIA_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MEDIA_TYPE).getSerializer(TCompactProtocolSerializer.MEDIA_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer_fail() {
        try {
            new ThriftSerializerProvider().getSerializer("text/plain");
            fail("No exception on no serializer");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("No serializer for media type 'text/plain'"));
        }
    }
}
