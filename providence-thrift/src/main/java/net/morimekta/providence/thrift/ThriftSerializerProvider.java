package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;

/**
 * Created by morimekta on 5/2/16.
 */
public class ThriftSerializerProvider implements SerializerProvider {
    private final SerializerProvider baseProvider;

    public ThriftSerializerProvider() {
        this(new SerializerProvider() {});
    }

    public ThriftSerializerProvider(SerializerProvider base) {
        if (base instanceof ThriftSerializerProvider) {
            throw new IllegalArgumentException();
        }
        baseProvider = base;
    }

    @Override
    public Serializer getSerializer(String contentType) {
        switch (contentType) {
            case TBinaryProtocolSerializer.MIME_TYPE:
            case TBinaryProtocolSerializer.ALT_MIME_TYPE:
                return new TBinaryProtocolSerializer();
            case TCompactProtocolSerializer.MIME_TYPE:
                return new TCompactProtocolSerializer();
            case TJsonProtocolSerializer.MIME_TYPE:
                return new TJsonProtocolSerializer();
            case TSimpleJsonProtocolSerializer.MIME_TYPE:
                return new TSimpleJsonProtocolSerializer();
            default:
                return baseProvider.getSerializer(contentType);
        }
    }
}
