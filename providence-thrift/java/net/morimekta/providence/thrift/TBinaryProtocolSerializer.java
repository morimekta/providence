package net.morimekta.providence.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TBinaryProtocolSerializer extends TProtocolSerializer {
    public TBinaryProtocolSerializer() {
        this(true);
    }

    public TBinaryProtocolSerializer(boolean readStrict) {
        super(readStrict, new TBinaryProtocol.Factory(readStrict, true));
    }
}
