package net.morimekta.providence.it.serialization;

import net.morimekta.console.chr.Color;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Kept statistics for a given format.
 */
public class FormatStatistics implements Comparable<FormatStatistics> {
    /**
     * Which format was tested.
     */
    public final Format                format;

    public final DescriptiveStatistics PwriteStat;
    public final DescriptiveStatistics PtotalWriteStat;
    public final DescriptiveStatistics PreadStat;
    public final DescriptiveStatistics PtotalReadStat;
    public final DescriptiveStatistics TwriteStat;
    public final DescriptiveStatistics TtotalWriteStat;
    public final DescriptiveStatistics TreadStat;
    public final DescriptiveStatistics TtotalReadStat;

    public double read;
    public double read_thrift;
    public double write;
    public double write_thrift;
    public int    size;

    public FormatStatistics(Format format) {
        this.format = format;

        PwriteStat = new DescriptiveStatistics();
        PtotalWriteStat = new DescriptiveStatistics();

        PreadStat = new DescriptiveStatistics();
        PtotalReadStat = new DescriptiveStatistics();

        TwriteStat = new DescriptiveStatistics();
        TtotalWriteStat = new DescriptiveStatistics();

        TreadStat = new DescriptiveStatistics();
        TtotalReadStat = new DescriptiveStatistics();
    }

    public double totalPvd() {
        return read + write;
    }

    @Override
    public int compareTo(@Nonnull FormatStatistics other) {
        int c = Double.compare(totalPvd(), other.totalPvd());
        if (c != 0) {
            return c;
        }
        // If the same, sort DESC after original read + write time.
        return format.compareTo(other.format);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !getClass().equals(o.getClass())) return false;

        FormatStatistics other = (FormatStatistics) o;
        return format == other.format;
    }

    @Override
    public int hashCode() {
        return Objects.hash(FormatStatistics.class, format);
    }

    /**
     * Header string that matches the asString output.
     *
     * @return The asString header.
     */
    public static String colorHeader() {
        return  Color.BOLD +
                HEADER_L1 + "\n" +
                new Color(Color.YELLOW, Color.UNDERLINE) +
                HEADER_L2 + "    " +
                Color.CLEAR;
    }

    /**
     * Header string that matches the asString output.
     *
     * @return The asString header.
     */
    public static String header() {
        return  HEADER_L1 + "\n" + HEADER_L2;
    }

    private static final String HEADER_L1 = "                           READ          WRITE            SUM            SIZE";
    private static final String HEADER_L2 = "        name        :   pvd   thr  --  pvd   thr   =   pvd   thr  -- (ratio / size)";

    public String statistics(FormatStatistics rel) {
        double r = read / rel.read_thrift;
        double w = write / rel.write_thrift;
        double rw = (r + w) / 2;
        double size_ratio = ((double) size / (double) rel.size);

        if (read_thrift > 0 || write_thrift > 0) {
            double rt = read_thrift / rel.read_thrift;
            double wt = write_thrift / rel.write_thrift;
            double rwt = (rt + wt) / 2;

            return String.format(
                    "%20s:  %5.2f %5.2f -- %5.2f %5.2f  =  %5.2f %5.2f -- (%5.2f / %s)",
                    format.name(),
                    r,
                    rt,
                    w,
                    wt,
                    rw,
                    rwt,
                    size_ratio,
                    humanReadableByteCount(size, false));
        } else {
            return String.format(
                    "%20s:  %5.2f       -- %5.2f        =  %5.2f       -- (%5.2f / %s)",
                    format.name(),
                    r,
                    w,
                    rw,
                    size_ratio,
                    humanReadableByteCount(size, false));
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public void calculate() {
        final long PReadMs = (long) PtotalReadStat.getSum() / 1000000;
        final long PWriteMs = (long) PtotalWriteStat.getSum() / 1000000;
        final long TReadMs = (long) TtotalReadStat.getSum() / 1000000;
        final long TWriteMs = (long) TtotalWriteStat.getSum() / 1000000;

        read = ((double) PReadMs) / 1000;
        write = ((double) PWriteMs) / 1000;
        read_thrift = ((double) TReadMs) / 1000;
        write_thrift = ((double) TWriteMs) / 1000;
    }
}
