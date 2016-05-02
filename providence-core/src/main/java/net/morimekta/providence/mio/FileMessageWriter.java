package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageStreams;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A writer helper class for matching a serializer with an output stream.
 */
public class FileMessageWriter implements MessageWriter, Closeable {
    private final File       file;
    private final Serializer serializer;
    private final boolean    append;

    private OutputStream out;

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
        int ret = serializer.serialize(getOutputStream(), message);
        if (!serializer.binaryProtocol()) {
            out.write(MessageStreams.READABLE_ENTRY_SEP);
        }
        return ret;
    }

    @Override
    public <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException {
        int ret = serializer.serialize(getOutputStream(), call);
        if (!serializer.binaryProtocol()) {
            out.write(MessageStreams.READABLE_ENTRY_SEP);
        }
        return ret;
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } finally {
                out = null;
            }
        }
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        if (out == null) {
            out = new BufferedOutputStream(new FileOutputStream(file, append));
        }
        return out;
    }
}
