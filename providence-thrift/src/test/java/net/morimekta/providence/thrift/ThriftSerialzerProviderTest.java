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
        assertThat(new ThriftSerializerProvider(BinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(BinarySerializer.ALT_MIME_TYPE).getDefault(), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.JSON_MIME_TYPE).getDefault(), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(FastBinarySerializer.MIME_TYPE).getDefault(), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(TJsonProtocolSerializer.MIME_TYPE).getDefault(), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider(TCompactProtocolSerializer.MIME_TYPE).getDefault(), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer() {
        assertThat(new ThriftSerializerProvider().getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(TJsonProtocolSerializer.MIME_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider().getSerializer(TCompactProtocolSerializer.MIME_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));

        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(BinarySerializer.ALT_MIME_TYPE), is(instanceOf(BinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(JsonSerializer.JSON_MIME_TYPE), is(instanceOf(JsonSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(FastBinarySerializer.MIME_TYPE), is(instanceOf(FastBinarySerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(TJsonProtocolSerializer.MIME_TYPE), is(instanceOf(TJsonProtocolSerializer.class)));
        assertThat(new ThriftSerializerProvider(JsonSerializer.MIME_TYPE).getSerializer(TCompactProtocolSerializer.MIME_TYPE), is(instanceOf(TCompactProtocolSerializer.class)));
    }

    @Test
    public void testGetSerializer_fail() {
        try {
            new ThriftSerializerProvider().getSerializer("text/plain");
            fail("No exception on no serializer");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("No such serializer for media type text/plain"));
        }
    }
}
