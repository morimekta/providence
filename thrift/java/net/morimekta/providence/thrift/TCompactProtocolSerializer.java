package net.morimekta.providence.thrift;

import org.apache.thrift.protocol.TCompactProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TCompactProtocolSerializer
        extends TProtocolSerializer {
    public TCompactProtocolSerializer() {
        super(new TCompactProtocol.Factory());
    }
}
