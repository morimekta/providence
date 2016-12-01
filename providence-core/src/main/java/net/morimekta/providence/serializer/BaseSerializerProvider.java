package net.morimekta.providence.serializer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default serializer provider for core serializers.
 */
public abstract class BaseSerializerProvider implements SerializerProvider {
    private final Map<String, Serializer> serializerMap;
    private final String defaultContentType;

    /**
     * Get provider with the given default content type.
     *
     * @param defaultContentType The default mime-type.
     */
    public BaseSerializerProvider(String defaultContentType) {
        this.defaultContentType = defaultContentType;
        this.serializerMap = new LinkedHashMap<>();
    }

    @Override
    public Serializer getSerializer(String mediaType) {
        return serializerMap.get(mediaType);
    }

    @Override
    public Serializer getDefault() {
        return getSerializer(defaultContentType);
    }

    /**
     * Register the serializer with a given set of mime-types.
     * @param serializer The serializer to register.
     * @param mediaTypes The media types to register it for.
     */
    protected void register(Serializer serializer, String... mediaTypes) {
        for (String mimeType : mediaTypes) {
            this.serializerMap.put(mimeType, serializer);
        }
    }
}
