package net.morimekta.providence.it.serialization;

import net.morimekta.console.chr.Char;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;

import com.google.common.collect.ImmutableList;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by morimekta on 17.06.17.
 */
public class ITRunner<PM extends PMessage<PM, PF>, PF extends PField,
                      TM extends TBase<TM, TF>, TF extends TFieldIdEnum> {
    private final PMessageDescriptor<PM, PF> descriptor;
    private final Supplier<TM>               supplier;
    private final List<PM>                   pvdList;
    private final List<TM>                   thrList;

    public ITRunner(PMessageDescriptor<PM, PF> descriptor,
                    Supplier<TM> supplier,
                    List<PM> pvdList,
                    List<TM> thrList) {
        this.descriptor = descriptor;
        this.supplier = supplier;
        this.pvdList = ImmutableList.copyOf(pvdList);
        this.thrList = ImmutableList.copyOf(thrList);
    }

    public void run(FormatStatistics statistics) throws TException, IOException {
        runProvidence(statistics);
        if (statistics.format.protocolFactory != null) {
            runThrift(statistics);
        }
    }

    private void runProvidence(FormatStatistics statistics) throws IOException {
        Serializer serializer = statistics.format.serializer;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        long totalTime = 0;
        for (PM pvd : pvdList) {
            long start = System.nanoTime();

            serializer.serialize(baos, pvd);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.PwriteStat.addValue(time);

            baos.write(serializer.binaryProtocol() ? Char.FS : '\n');
        }

        statistics.PtotalWriteStat.addValue(totalTime);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        totalTime = 0;

        while (bais.available() > 0) {
            long start = System.nanoTime();

            serializer.deserialize(bais, descriptor);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.PreadStat.addValue(time);

            if (bais.read() != (serializer.binaryProtocol() ? Char.FS : '\n')) {
                throw new AssertionError("Bad serialized data.");
            }
        }

        statistics.PtotalReadStat.addValue(totalTime);
    }

    private void runThrift(FormatStatistics statistics) throws TException {
        TProtocolFactory factory = statistics.format.protocolFactory;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TTransport transport = new TIOStreamTransport(baos);

        long totalTime = 0;
        for (TM thr : thrList) {
            long start = System.nanoTime();

            TProtocol protocol = factory.getProtocol(transport);
            thr.write(protocol);
            transport.flush();

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.TwriteStat.addValue(time);

            baos.write(statistics.format.serializer.binaryProtocol() ? Char.FS : '\n');
        }

        statistics.TtotalWriteStat.addValue(totalTime);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        transport = new TIOStreamTransport(bais);

        totalTime = 0;
        while (bais.available() > 0) {
            long start = System.nanoTime();

            TM thr = supplier.get();
            TProtocol protocol = factory.getProtocol(transport);
            thr.read(protocol);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.TreadStat.addValue(time);

            if (bais.read() != (statistics.format.serializer.binaryProtocol() ? Char.FS : '\n')) {
                throw new AssertionError("Bad serialized data.");
            }
        }

        statistics.TtotalReadStat.addValue(totalTime);
    }
}
