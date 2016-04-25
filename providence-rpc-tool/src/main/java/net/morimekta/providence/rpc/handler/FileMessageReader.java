package net.morimekta.providence.rpc.handler;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.MessageReader;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A reader helper class for matching a serializer with an input stream.
 */
public class FileMessageReader implements MessageReader {
    private final File       file;
    private final Serializer serializer;

    public FileMessageReader(File file, Serializer serializer) {
        this.file = file;
        this.serializer = serializer;
    }

    @Override
    public <T extends PMessage<T>, TF extends PField> T read(PStructDescriptor<T, TF> descriptor)
            throws IOException, SerializerException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return serializer.deserialize(in, descriptor);
        }
    }

    @Override
    public <T extends PMessage<T>> PServiceCall<T> read(PService service) throws IOException, SerializerException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return serializer.deserialize(in, service);
        }
    }
}
