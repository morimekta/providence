package net.morimekta.providence.it.serialization;

import net.morimekta.util.Stringable;

/**
 * Enum for serialization format overview and baseline data.
 */
public enum Format implements Stringable, Comparable<Format> {
    // Common serialization formats.
    binary("bin", "Thrift Binary",
            1.04, 1.00, 0.91, 1.00, 27441),

    // Unique providence formats.
    json_pretty("json", "Prettified JSON",
            8.70, 0.00,11.43, 0.00, 94553),
    json_named("json", "Named JSON",
            6.26, 0.00, 8.91, 0.00, 56309),
    json("json", "Compact JSON",
            5.45, 0.00, 6.28, 0.00, 37911),
    pretty("cfg", "Pretty / Config",
            9.52, 0.00, 9.12, 0.00, 74299),
    fast_binary("bin", "Fast Binary",
            1.43, 0.00, 1.51, 0.00, 19062),

    // Unique thrift formats.
    binary_protocol("bin", "Thrift Binary (wrapper)",
            1.85, 1.00, 2.13, 1.00, 27441),
    json_protocol("json", "Thrift JSON Protocol (wrapper)",
            6.40, 5.25, 7.50, 5.70, 58378),
    compact_protocol("bin", "Thrift \"compact\" Protocol (wrapper)",
            2.00, 1.04, 2.08, 1.15, 18512),
    tuple_protocol("tpl", "Thrift \"tuple\" Protocol (wrapper)",
            1.83, 0.89, 1.87, 0.88, 16568);

    public final String suffix;
    public final String description;
    public final double read;
    public final double read_thrift;  // read using the native thrift implementation.
    public final double write;
    public final double write_thrift; // write using the native thrift implementation.
    public final int output_size;     // the expected output size in bytes.

    Format(String s,
           String desc,
           double r,
           double rt,
           double w,
           double wt,
           int buf) {
        description = desc;
        suffix = s;
        read = r;
        read_thrift = rt;
        write = w;
        write_thrift = wt;
        output_size = buf;
    }

    /**
     * Description of the protocol (for output &amp; help).
     *
     * @return The format description.
     */
    public String description() {
        return description;
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
                    read_thrift + write_thrift,
                    output_size / 1024);
        } else {
            return String.format(
                    "%20s:  %5.2f       -- %5.2f        =  %5.2f        (%3d kB)",
                    name(),
                    read,
                    write,
                    read + write,
                    output_size / 1024);
        }
    }
}
