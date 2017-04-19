package net.morimekta.providence.it.serialization;

import net.morimekta.console.chr.Char;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;
import net.morimekta.test.providence.Containers;
import net.morimekta.util.Stringable;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Serialization Test result.
 */
public class TestSerialization implements Stringable, Comparable<TestSerialization> {
    /**
     * Which format was tested.
     */
    public final Format format;

    /**
     * Providence serializer.
     */
    public final Serializer serializer;

    /**
     * Thrift factory factory.
     */
    public final TProtocolFactory factory;

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

    public TestSerialization(Format format, Serializer serializer, TProtocolFactory factory) {
        this.format = format;
        this.serializer = serializer;
        this.factory = factory;

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
    public int compareTo(TestSerialization other) {
        int c = Double.compare(totalPvd(), other.totalPvd());
        if (c != 0) {
            return c;
        }
        // If the same, sort DESC after original read + write time.
        c = Double.compare(other.format.read + other.format.write,
                           format.read + format.write);
        return c != 0 ? c : format.compareTo(other.format);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !getClass().equals(o.getClass())) return false;

        TestSerialization other = (TestSerialization) o;
        return format == other.format;
    }

    @Override
    public int hashCode() {
        return Objects.hash(TestSerialization.class, format);
    }

    @Override
    public String asString() {
        if (read_thrift > 0 || write_thrift > 0) {
            return String.format(
                    "%20s:  %5.2f %5.2f -- %5.2f %5.2f  =  %5.2f %5.2f  (%3d kB)",
                    format.name(),
                    read,
                    read_thrift,
                    write,
                    write_thrift,
                    read + write,
                    read_thrift + write_thrift,
                    format.output_size / 1024);

        } else {
            return String.format(
                    "%20s:  %5.2f       -- %5.2f        =  %5.2f        (%3d kB)",
                    format.name(),
                    read,
                    write,
                    read + write,
                    format.output_size / 1024);
        }
    }

    public void verify(TestSerialization rel) {
        if (format == Format.pretty) {
            // This format cannot become too slow, as it's whole purpose is debugging
            // and human readability.
            return;
        }

        double r = read / rel.read_thrift;
        double w = write / rel.write_thrift;

        double ro = (r / format.read) - 1.00;
        double wo = (w / format.write) - 1.00;

        if (ro > 0.10) {
            System.out.format(Locale.ENGLISH,
                              "-- %20s read time increased by %.1f%%, expected %.2fx, seeing %.2fx\n",
                              format.toString(),
                              (ro * 100),
                              format.read,
                              r);
        }
        if (wo > 0.10) {
            System.out.format(Locale.ENGLISH,
                              "-- %20s write time increased by %.1f%%, expected %.2fx, seeing %.2fx\n",
                              format.toString(),
                              (wo * 100),
                              format.write,
                              w);
        }
        if (ro < -0.10) {
            System.out.format(Locale.ENGLISH,
                              "++ %20s read time reduced by %.1f%%, expected %.2fx, seeing %.2fx\n",
                              format.toString(),
                              (-ro * 100),
                              format.read,
                              r);
        }
        if (wo < -0.10) {
            System.out.format(Locale.ENGLISH,
                              "++ %20s write time reduced by %.1f%%, expected %.2fx, seeing %.2fx\n",
                              format.toString(),
                              (-wo * 100),
                              format.write,
                              w);
        }
    }

    public String statistics(TestSerialization rel) {
        double r = read / rel.read_thrift;
        double w = write / rel.write_thrift;
        double rw = (r + w) / 2;

        if (read_thrift > 0 || write_thrift > 0) {
            double rt = read_thrift / rel.read_thrift;
            double wt = write_thrift / rel.write_thrift;
            double rwt = (rt + wt) / 2;

            return String.format(
                    "%20s:  %5.2f %5.2f -- %5.2f %5.2f  =  %5.2f %5.2f  (%3d kB)",
                    format.name(),
                    r,
                    rt,
                    w,
                    wt,
                    rw,
                    rwt,
                    format.output_size / 1024);
        } else {
            return String.format(
                    "%20s:  %5.2f       -- %5.2f        =  %5.2f        (%3d kB)",
                    format.name(),
                    r,
                    w,
                    rw,
                    format.output_size / 1024);
        }
    }

    public void runProvidence(List<Containers> content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(format.output_size);

        long totalTime = 0;
        for (Containers c : content) {
            long start = System.nanoTime();

            serializer.serialize(baos, c);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            PwriteStat.addValue(time);

            baos.write(serializer.binaryProtocol() ? Char.FS : '\n');
        }

        PtotalWriteStat.addValue(totalTime);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        totalTime = 0;

        List<Containers> result = new LinkedList<>();
        while (bais.available() > 0) {
            long start = System.nanoTime();

            Containers c = serializer.deserialize(bais, Containers.kDescriptor);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            PreadStat.addValue(time);

            if (bais.read() != (serializer.binaryProtocol() ? Char.FS : '\n')) {
                throw new AssertionError("");
            }

            result.add(c);
        }

        PtotalReadStat.addValue(totalTime);

        // validate?
        if (baos.size() != format.output_size) {
            System.out.println("Expected output size: " + format.output_size + ", got " + baos.size());
        }
        if (result.size() != content.size()) {
            System.out.println("Number of parsed message " + result.size() + " does not match source " + content.size());
        }
    }

    public void runThrift(List<net.morimekta.test.thrift.Containers> content)
            throws TException {
        if (factory == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(format.output_size);
        TTransport transport = new TIOStreamTransport(baos);
        TProtocol protocol = factory.getProtocol(transport);

        long totalTime = 0;
        for (net.morimekta.test.thrift.Containers c : content) {
            long start = System.nanoTime();

            c.write(protocol);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            TwriteStat.addValue(time);

            baos.write(serializer.binaryProtocol() ? Char.FS : '\n');
        }

        TtotalWriteStat.addValue(totalTime);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        transport = new TIOStreamTransport(bais);
        protocol = factory.getProtocol(transport);

        totalTime = 0;
        List<net.morimekta.test.thrift.Containers> result = new LinkedList<>();
        while (bais.available() > 0) {
            long start = System.nanoTime();

            net.morimekta.test.thrift.Containers c = new net.morimekta.test.thrift.Containers();
            c.read(protocol);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            TreadStat.addValue(time);

            bais.read();

            result.add(c);
        }

        TtotalReadStat.addValue(totalTime);

        if (baos.size() != format.output_size) {
            System.out.println("Expected output size: " + format.output_size + ", got " + baos.size());
        }
        if (result.size() != content.size()) {
            System.out.println("Number of parsed message " + result.size() + " does not match source " + content.size());
        }
    }

    public static TestSerialization forFormat(Format format) {
        Serializer serializer;
        TProtocolFactory factory;

        switch (format) {
            case binary:
                serializer = new BinarySerializer();
                factory = TBinaryProtocol::new;
                break;
            case json_pretty:
                serializer = new JsonSerializer(false).pretty();
                factory = null;
                break;
            case json_named:
                serializer = new JsonSerializer(false).named();
                factory = null;
                break;
            case json:
                serializer = new JsonSerializer(false);
                factory = null;
                break;
            case pretty:
                serializer = new PrettySerializer();
                factory = null;
                break;
            case fast_binary:
                serializer = new FastBinarySerializer(false);
                factory = null;
                break;
            case binary_protocol:
                serializer = new TBinaryProtocolSerializer(false);
                factory = TBinaryProtocol::new;
                break;
            case json_protocol:
                serializer = new TJsonProtocolSerializer(false);
                factory = TJSONProtocol::new;
                break;
            case compact_protocol:
                serializer = new TCompactProtocolSerializer(false);
                factory = TCompactProtocol::new;
                break;
            case tuple_protocol:
                serializer = new TTupleProtocolSerializer(false);
                factory = TTupleProtocol::new;
                break;
            default:
                throw new IllegalStateException("Unhandled format " + format.toString());
        }

        return new TestSerialization(format, serializer, factory);
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
