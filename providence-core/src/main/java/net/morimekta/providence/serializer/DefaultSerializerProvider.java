package net.morimekta.providence.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Default serializer provider for extra serializers.
 */
public class DefaultSerializerProvider implements SerializerProvider {
    private final Map<String, Serializer> serializerMap;
    private final String defaultContentType;

    /**
     * Get the default serializer provider.
     */
    public DefaultSerializerProvider() {
        this(BinarySerializer.MIME_TYPE);
    }

    /**
     * Get provider with the given default content type.
     *
     * @param defaultContentType The default mime-type.
     */
    public DefaultSerializerProvider(String defaultContentType) {
        this.defaultContentType = defaultContentType;
        this.serializerMap = new HashMap<>();

        register(new BinarySerializer(), BinarySerializer.MIME_TYPE, BinarySerializer.ALT_MIME_TYPE);
        register(new FastBinarySerializer(), FastBinarySerializer.MIME_TYPE);
        register(new JsonSerializer(), JsonSerializer.MIME_TYPE, JsonSerializer.JSON_MIME_TYPE);
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
