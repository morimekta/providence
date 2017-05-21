package net.morimekta.providence.it.serialization;

import net.morimekta.util.Stringable;

/**
 * Enum for serialization format overview and baseline data.
 */
public enum Format implements Stringable, Comparable<Format> {
    // Common serialization formats.
    binary("bin", "Thrift Binary",
            1.23, 1.00, 0.86, 1.00),

    // Unique providence formats.
    json_pretty("json", "Prettified JSON",
            9.25, 0.00,14.14, 0.00),
    json_named("json", "Named JSON",
            6.81, 0.00,11.90, 0.00),
    json("json", "Compact JSON",
            6.01, 0.00, 6.84, 0.00),
    pretty("cfg", "Pretty / Config",
           10.38, 0.00, 7.94, 0.00),
    fast_binary("bin", "Fast Binary",
            1.91, 0.00, 1.40, 0.00),

    // Unique thrift formats.
    binary_protocol("bin", "Thrift Binary (wrapper)",
            2.61, 1.00, 2.23, 1.00),
    json_protocol("json", "Thrift JSON Protocol (wrapper)",
            7.18, 5.26, 6.67, 5.07),
    compact_protocol("bin", "Thrift \"compact\" Protocol (wrapper)",
            2.74, 1.11, 2.16, 0.92),
    tuple_protocol("tpl", "Thrift \"tuple\" Protocol (wrapper)",
            2.29, 0.91, 1.80, 0.78);

    public final String suffix;
    public final String description;
    public final double read;
    public final double read_thrift;  // read using the native thrift implementation.
    public final double write;
    public final double write_thrift; // write using the native thrift implementation.

    Format(String s,
           String desc,
           double r,
           double rt,
           double w,
           double wt) {
        description = desc;
        suffix = s;
        read = r;
        read_thrift = rt;
        write = w;
        write_thrift = wt;
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
