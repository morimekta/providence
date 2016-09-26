package net.morimekta.providence.it;

import net.morimekta.util.Stringable;

/**
 * Enum for serialization format overview and baseline data.
 */
public enum Format implements Stringable, Comparable<Format> {
    // Common serialization formats.
    binary("bin", "Thrift Binary",
            0.48, 0.49, 0.61, 0.39, 27441),

    // Unique providence formats.
    json_pretty("json", "Prettified JSON",
            3.13, 0.00, 3.80, 0.00, 94490),
    json_named("json", "Named JSON",
            1.97, 0.00, 2.63, 0.00, 56246),
    json("json", "Compact JSON",
            1.71, 0.00, 1.54, 0.00, 37848),
    pretty("pvd", "Pretty Readable",
            0.00, 0.00, 0.00, 0.00, 76080),
    fast_binary("bin", "Fast Binary",
            0.49, 0.00, 0.41, 0.00, 19062),

    // Unique thrift formats.
    binary_protocol("bin", "Thrift Binary (wrapper)",
            0.62, 0.49, 0.49, 0.39, 27441),
    json_protocol("json", "Thrift JSON Protocol (wrapper)",
            2.01, 2.57, 1.51, 1.48, 58378),
    compact_protocol("bin", "Thrift \"compact\" Protocol (wrapper)",
            0.69, 0.63, 0.62, 0.54, 18512),
    tuple_protocol("tpl", "Thrift \"tuple\" Protocol (wrapper)",
            0.69, 0.38, 0.42, 0.38, 16568);

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
