package net.morimekta.providence.serializer;

/**
 * Provider of serializers based on a string mime-type.
 */
@FunctionalInterface
public interface SerializerProvider {
    /**
     * Get serializer for the given mime-type
     *
     * @param mediaType The media-type to get serializer for.
     * @return The serializer, or null if not found.
     */
    Serializer getSerializer(String mediaType);

    /**
     * @return The default serializer.
     */
    default Serializer getDefault() {
        return getSerializer(BinarySerializer.MIME_TYPE);
    }
}
