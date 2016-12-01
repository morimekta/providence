package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BaseSerializerProvider;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TSimpleJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

/**
 * Created by morimekta on 5/4/16.
 */
public class ThriftOnlySerializerProvider extends BaseSerializerProvider {
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
        super(defaultContentType);

        // The BinarySerializer is identical to the TBinaryProtocolSerializer,
        // except that it is "native providence".
        register(new BinarySerializer(), BinarySerializer.MIME_TYPE, TBinaryProtocolSerializer.ALT_MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TTupleProtocolSerializer(), TTupleProtocolSerializer.MIME_TYPE);
        // Even though it's a write-only protocol.
        register(new TSimpleJsonProtocolSerializer(), TSimpleJsonProtocolSerializer.MIME_TYPE);
    }
}
