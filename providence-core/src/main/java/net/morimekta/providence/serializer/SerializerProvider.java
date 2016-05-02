package net.morimekta.providence.serializer;

/**
 * Provider of serializers based on a string content type.
 */
public interface SerializerProvider {
    default Serializer getSerializer(String contentType) {
        switch (contentType.toLowerCase()) {
            case BinarySerializer.MIME_TYPE:
                return new BinarySerializer();
            case FastBinarySerializer.MIME_TYPE:
                return new FastBinarySerializer();
            case JsonSerializer.JSON_MIME_TYPE:
            case JsonSerializer.MIME_TYPE:
                return new JsonSerializer();
        }
        return null;
    }
}
