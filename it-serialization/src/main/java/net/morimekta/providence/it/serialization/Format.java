package net.morimekta.providence.it.serialization;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;

/**
 * Enum for serialization format overview and baseline data.
 */
public enum Format implements Comparable<Format> {
    // Common serialization formats.
    binary(new BinarySerializer(true),
           TBinaryProtocol::new),

    // Unique providence formats.
    json_pretty(new JsonSerializer(true).pretty(),
                null),
    json_named(new JsonSerializer(true).named(),
               null),
    json(new JsonSerializer(true),
         null),
    config(new PrettySerializer(true).config(),
           null),
    pretty(new PrettySerializer(true).compact(),
           null),
    fast_binary(new FastBinarySerializer(true),
                null),

    // Thrift formats.
    binary_protocol(new TBinaryProtocolSerializer(true),
                    null),
    json_protocol(new TJsonProtocolSerializer(true),
                  TJSONProtocol::new),
    compact_protocol(new TCompactProtocolSerializer(true),
                     TCompactProtocol::new),
    tuple_protocol(new TTupleProtocolSerializer(true),
                   TTupleProtocol::new),
    ;

    public final Serializer serializer;
    public final TProtocolFactory protocolFactory;

    Format(Serializer ps,
           TProtocolFactory pf) {
        serializer = ps;
        protocolFactory = pf;
    }
}
