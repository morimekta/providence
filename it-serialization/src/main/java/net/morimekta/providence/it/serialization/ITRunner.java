package net.morimekta.providence.it.serialization;

import net.morimekta.console.chr.Char;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.jackson.ProvidenceModule;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by morimekta on 17.06.17.
 */
public class ITRunner<PM extends PMessage<PM, PF>, PF extends PField,
                      TM extends TBase<TM, TF>, TF extends TFieldIdEnum> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

        ProvidenceModule.register(MAPPER);
    }

    public void run(FormatStatistics statistics) throws TException, IOException {
        runProvidence(statistics);
        if (statistics.format.protocolFactory != null) {
            runThrift(statistics);
        }
        if (statistics.format.jackson) {
            runJackson(statistics);
        }
    }

    private void runProvidence(FormatStatistics statistics) throws IOException {
        Serializer serializer = statistics.format.serializer;

        ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024 * 1024);

        long totalTime = 0;
        for (PM pvd : pvdList) {
            long start = System.nanoTime();

            serializer.serialize(baos, pvd);
            if (!serializer.binaryProtocol()) {
                baos.write('\n');
            }

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.PwriteStat.addValue(time);

            baos.write(serializer.binaryProtocol() ? Char.FS : '\n');
        }

        statistics.PtotalWriteStat.addValue(totalTime);
        statistics.size = baos.size();

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024 * 1024);
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
        }

        statistics.TtotalReadStat.addValue(totalTime);
    }

    private void runJackson(FormatStatistics statistics) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024 * 1024);

        long totalTime = 0;

        PM instance = null;
        for (PM pvd : pvdList) {
            long start = System.nanoTime();

            MAPPER.writerFor(pvd.getClass()).writeValue(baos, pvd);
            baos.write('\n');

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.JwriteStat.addValue(time);

            instance = pvd;
        }

        statistics.JtotalWriteStat.addValue(totalTime);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        ObjectReader reader = MAPPER.readerFor(instance.getClass());

        totalTime = 0;

        Iterator<PM> iterator = pvdList.iterator();

        while (bais.available() > 0) {
            String tmp = IOUtils.readString(bais, '\n');
            ByteArrayInputStream tb = new ByteArrayInputStream(tmp.getBytes(UTF_8));

            long start = System.nanoTime();

            PM pvd = reader.readValue(tb);

            long end = System.nanoTime();
            long time = end - start;
            totalTime += time;

            statistics.JreadStat.addValue(time);

            if (!iterator.hasNext()) {
                throw new AssertionError("More read than written objects!");
            }
            PM next = iterator.next();
            if (!pvd.equals(next)) {
                throw new AssertionError("Read value not equal written");
            }
        }
        if (iterator.hasNext()) {
            throw new AssertionError("More written than read objects!");
        }

        statistics.JtotalReadStat.addValue(totalTime);
    }
}
