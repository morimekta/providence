package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A writer helper class for matching a serializer with an output stream.
 */
public class FileMessageWriter implements MessageWriter {
    private final File       file;
    private final Serializer serializer;
    private final boolean    append;

    public FileMessageWriter(File file, Serializer serializer) {
        this(file, serializer, false);
    }

    public FileMessageWriter(File file, Serializer serializer, boolean append) {
        this.file = file;
        this.serializer = serializer;
        this.append = append;
    }

    @Override
    public <T extends PMessage<T>> int write(T message) throws IOException, SerializerException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {
            return serializer.serialize(out, message);
        }
    }

    @Override
    public <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {
            return serializer.serialize(out, call);
        }
    }
}
