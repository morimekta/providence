package org.apache.thrift.j2.protocol;

import org.apache.thrift.protocol.TTupleProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TTupleProtocolSerializer
        extends TProtocolSerializer {
    public TTupleProtocolSerializer() {
        super(new TTupleProtocol.Factory());
    }
}
