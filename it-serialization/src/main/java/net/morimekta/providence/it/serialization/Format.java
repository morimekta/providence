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
import net.morimekta.util.Stringable;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;

/**
 * Enum for serialization format overview and baseline data.
 */
public enum Format implements Stringable, Comparable<Format> {
    // Common serialization formats.
    binary("bin", "Thrift Binary",
            1.18, 1.00, 0.84, 1.00,
           new BinarySerializer(true),
           new TBinaryProtocol.Factory()),

    // Unique providence formats.
    json_pretty("json", "Prettified JSON",
           12.85, 0.00, 7.95, 0.00,
                new JsonSerializer(true).pretty(),
                null),
    json_named("json", "Named JSON",
            8.77, 0.00, 5.90, 0.00,
               new JsonSerializer(true).named(),
               null),
    json("json", "Compact JSON",
            7.48, 0.00, 5.53, 0.00,
         new JsonSerializer(true),
         null),
    pretty("cfg", "Pretty / Config",
           10.11, 0.00, 7.94, 0.00,
           new PrettySerializer(true),
           null),
    fast_binary("bin", "Fast Binary",
            1.46, 0.00, 1.40, 0.00,
                new FastBinarySerializer(true),
                null),

    // Unique thrift formats.
    binary_protocol("bin", "Thrift Binary (wrapper)",
            2.22, 1.00, 2.23, 1.00,
                    new TBinaryProtocolSerializer(true),
                    TBinaryProtocol::new),
    json_protocol("json", "Thrift JSON Protocol (wrapper)",
            6.90, 5.26, 6.67, 5.07,
                  new TJsonProtocolSerializer(true),
                  TJSONProtocol::new),
    compact_protocol("bin", "Thrift \"compact\" Protocol (wrapper)",
            2.42, 1.11, 2.16, 0.92,
                     new TCompactProtocolSerializer(true),
                     TCompactProtocol::new),
    tuple_protocol("tpl", "Thrift \"tuple\" Protocol (wrapper)",
            1.90, 0.91, 1.80, 0.78,
                   new TTupleProtocolSerializer(true),
                   TTupleProtocol::new);

    public final String suffix;
    public final String description;
    public final double read;
    public final double read_thrift;  // read using the native thrift implementation.
    public final double write;
    public final double write_thrift; // write using the native thrift implementation.

    public final Serializer serializer;
    public final TProtocolFactory protocolFactory;

    Format(String s,
           String desc,
           double r,
           double rt,
           double w,
           double wt,

           Serializer ps,
           TProtocolFactory pf) {
        description = desc;
        suffix = s;
        read = r;
        read_thrift = rt;
        write = w;
        write_thrift = wt;

        serializer = ps;
        protocolFactory = pf;
    }

    /**
     * Header string that matches the asString output.
     *
     * @return The asString header.
     */
    public static String header() {
        return
                "                           read          write            SUM\n" +
                "        name        :   pvd   thr  --  pvd   thr   =   pvd   thr";
    }

    @Override
    public String asString() {
        if (read_thrift > 0 || write_thrift > 0) {
            return String.format(
                    "%20s:  %5.2f %5.2f -- %5.2f %5.2f  =  %5.2f %5.2f  (%3d kB)",
                    name(),
                    read,
                    read_thrift,
                    write,
                    write_thrift,
                    read + write,
                    read_thrift + write_thrift);
        } else {
            return String.format(
                    "%20s:  %5.2f       -- %5.2f        =  %5.2f        (%3d kB)",
                    name(),
                    read,
                    write,
                    read + write);
        }
    }
}
