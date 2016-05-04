package net.morimekta.providence.thrift.client;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;

/**
 *
 */
public class ThriftSerializerProvider extends DefaultSerializerProvider {
    public ThriftSerializerProvider() {
        this(BinarySerializer.MIME_TYPE);
    }

    public ThriftSerializerProvider(String mimeType) {
        super(mimeType);
        // The default serializer needs to be
        register(getSerializer(BinarySerializer.MIME_TYPE), TBinaryProtocolSerializer.ALT_MIME_TYPE);
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
    }
}
