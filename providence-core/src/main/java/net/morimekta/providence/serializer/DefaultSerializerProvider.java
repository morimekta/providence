package net.morimekta.providence.serializer;

/**
 * Default serializer provider for core serializers.
 */
public class DefaultSerializerProvider extends BaseSerializerProvider {
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
        super(defaultContentType);

        register(new BinarySerializer(), BinarySerializer.MIME_TYPE, BinarySerializer.ALT_MIME_TYPE);
        register(new FastBinarySerializer(), FastBinarySerializer.MIME_TYPE);
        register(new JsonSerializer(), JsonSerializer.MIME_TYPE, JsonSerializer.JSON_MIME_TYPE);
    }
}
