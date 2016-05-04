package net.morimekta.providence.thrift.client;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TSimpleJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by morimekta on 5/4/16.
 */
public class ThriftOnlySerializerProvider implements SerializerProvider {
    private final Map<String, Serializer> serializerMap;
    private final String                  defaultContentType;

    /**
     * Get the default serializer provider.
     */
    public ThriftOnlySerializerProvider() {
        this(BinarySerializer.MIME_TYPE);
    }

    /**
     * Get provider with the given default content type.
     *
     * @param defaultContentType The default mime-type.
     */
    public ThriftOnlySerializerProvider(String defaultContentType) {
        this.defaultContentType = defaultContentType;
        this.serializerMap = new HashMap<>();

        register(new BinarySerializer(), BinarySerializer.MIME_TYPE, TBinaryProtocolSerializer.ALT_MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TTupleProtocolSerializer(), TTupleProtocolSerializer.MIME_TYPE);
        // Even though it's a write-only protocol.
        register(new TSimpleJsonProtocolSerializer(), TSimpleJsonProtocolSerializer.MIME_TYPE);
    }

    @Override
    public Serializer getSerializer(String mimeType) {
        return serializerMap.get(mimeType);
    }

    @Override
    public Serializer getDefault() {
        return getSerializer(defaultContentType);
    }

    /**
     * Register the serializer with a given set of mime-types.
     * @param serializer The serializer to register.
     * @param mimeTypes The mime types to register it for.
     */
    protected void register(Serializer serializer, String... mimeTypes) {
        for (String mimeType : mimeTypes) {
            this.serializerMap.put(mimeType, serializer);
        }
    }
}
