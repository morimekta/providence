package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;

import org.apache.thrift.protocol.TBinaryProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TBinaryProtocolSerializer extends TProtocolSerializer {
    public static final String MIME_TYPE = BinarySerializer.MIME_TYPE;
    public static final String ALT_MIME_TYPE = BinarySerializer.ALT_MIME_TYPE;

    public TBinaryProtocolSerializer() {
        this(true);
    }

    public TBinaryProtocolSerializer(boolean readStrict) {
        this(readStrict, false);
    }

    public TBinaryProtocolSerializer(boolean readStrict, boolean versioned) {
        super(readStrict, new TBinaryProtocol.Factory(readStrict && versioned, versioned),
              true, MIME_TYPE);
    }
}
