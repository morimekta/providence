package net.morimekta.providence.it.serialization;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.testing.util.MessageGenerator;

import com.google.common.collect.ImmutableList;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Data generator for running tests for a given object type.
 */
public class ITGenerator<PM extends PMessage<PM, PF>, PF extends PField,
                         TM extends TBase<TM, TF>, TF extends TFieldIdEnum> {
    private final PMessageDescriptor<PM, PF> descriptor;
    private final Supplier<TM>               supplier;

    private final ImmutableList.Builder<PM> pvdListBuilder;
    private final ImmutableList.Builder<TM> thrListBuilder;
    private final MessageGenerator          generator;

    private static final Locale[]   LOCALES = new Locale[]{
            Locale.ENGLISH,
            Locale.FRENCH,
            Locale.GERMAN,
            Locale.ITALIAN,
            Locale.SIMPLIFIED_CHINESE,
            new Locale("es"),
            new Locale("pl"),
            new Locale("sv"),
    };
    private static final Serializer BINARY  = new BinarySerializer(true);

    public ITGenerator(PMessageDescriptor<PM, PF> descriptor,
                       Supplier<TM> supplier,
                       MessageGenerator generator) {
        this.descriptor = descriptor;
        this.supplier = supplier;
        this.thrListBuilder = ImmutableList.builder();
        this.pvdListBuilder = ImmutableList.builder();
        this.generator = generator;
    }

    public void generate(final int n) throws IOException, TException {
        for (int i = 0; i < n; ++i) {
            Locale locale = LOCALES[new Random().nextInt(LOCALES.length)];
            generator.setLocale(locale);

            PM providence = generator.generate(descriptor);
            TM thrift = convert(providence);

            pvdListBuilder.add(providence);
            thrListBuilder.add(thrift);
        }
    }

    public void load(File file) throws IOException {
        MessageStreams.file(file, BINARY, descriptor).forEach(providence -> {
            try {
                TM thrift = convert(providence);

                pvdListBuilder.add(providence);
                thrListBuilder.add(thrift);
            } catch (TException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private TM convert(PM message) throws IOException, TException {
        TM tBase = supplier.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BINARY.serialize(baos, message);

        TIOStreamTransport transport = new TIOStreamTransport(new ByteArrayInputStream(baos.toByteArray()));
        TProtocol protocol = new TBinaryProtocol(transport);
        tBase.read(protocol);

        return tBase;
    }

    public List<PM> getProvidence() {
        return pvdListBuilder.build();
    }

    public List<TM> getThrift() {
        return thrListBuilder.build();
    }
}
