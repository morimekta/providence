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
    binary("bin", "Thrift Binary",
           new BinarySerializer(true),
           TBinaryProtocol::new),

    // Unique providence formats.
    json_pretty("json", "Prettified JSON",
                new JsonSerializer(true).pretty(),
                null),
    json_named("json", "Named JSON",
               new JsonSerializer(true).named(),
               null),
    json("json", "Compact JSON",
         new JsonSerializer(true),
         null),
    pretty("cfg", "Pretty / Config",
           new PrettySerializer(true),
           null),
    fast_binary("bin", "Fast Binary",
                new FastBinarySerializer(true),
                null),

    // Unique thrift formats.
    json_protocol("json", "Thrift JSON Protocol (wrapper)",
                  new TJsonProtocolSerializer(true),
                  TJSONProtocol::new),
    compact_protocol("bin", "Thrift \"compact\" Protocol (wrapper)",
                     new TCompactProtocolSerializer(true),
                     TCompactProtocol::new),
    tuple_protocol("tpl", "Thrift \"tuple\" Protocol (wrapper)",
                   new TTupleProtocolSerializer(true),
                   TTupleProtocol::new),
    ;

    public final String suffix;
    public final String description;

    public final Serializer serializer;
    public final TProtocolFactory protocolFactory;

    Format(String s,
           String desc,

           Serializer ps,
           TProtocolFactory pf) {
        description = desc;
        suffix = s;

        serializer = ps;
        protocolFactory = pf;
    }
}
