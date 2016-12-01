package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

/**
 *
 */
public class ThriftSerializerProvider extends DefaultSerializerProvider {
    public ThriftSerializerProvider() {
        this(BinarySerializer.MIME_TYPE);
    }

    public ThriftSerializerProvider(String mimeType) {
        super(mimeType);

        // Just add the thrift-only serializers.
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
        register(new TTupleProtocolSerializer(), TTupleProtocolSerializer.MIME_TYPE);
    }
}
