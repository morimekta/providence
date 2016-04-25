package net.morimekta.providence.rpc.handler;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.MessageWriter;
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

    public FileMessageWriter(File file, Serializer serializer) {
        this.file = file;
        this.serializer = serializer;
    }

    @Override
    public <T extends PMessage<T>> int write(T message) throws IOException, SerializerException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        int ret = serializer.serialize(out, message);
        out.flush();
        out.close();
        return ret;
    }

    @Override
    public <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        int ret = serializer.serialize(out, call);
        out.flush();
        out.close();
        return ret;
    }
}
