package org.apache.thrift.j2.protocol;

import org.apache.thrift.protocol.TBinaryProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TBinaryProtocolSerializer extends TProtocolSerializer {
    public TBinaryProtocolSerializer() {
        super(new TBinaryProtocol.Factory());
    }
}
