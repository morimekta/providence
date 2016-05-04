package net.morimekta.providence.serializer;

/**
 * Provider of serializers based on a string mime-type.
 */
public interface SerializerProvider {
    /**
     * Get serializer for the given mime-type
     *
     * @param mimeType The mime-type to get serializer for.
     * @return The serializer, or null if not found.
     */
    Serializer getSerializer(String mimeType);

    /**
     * @return The default serializer.
     */
    Serializer getDefault();
}
