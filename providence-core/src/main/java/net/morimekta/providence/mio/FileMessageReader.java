package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A reader helper class for matching a serializer with an input stream.
 */
public class FileMessageReader implements MessageReader {
    private final File       file;
    private final Serializer serializer;

    private InputStream in;

    public FileMessageReader(File file, Serializer serializer) {
        this.file = file;
        this.serializer = serializer;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message read(PStructDescriptor<Message, Field> descriptor)
            throws IOException, SerializerException {
        return serializer.deserialize(getInputStream(), descriptor);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> read(PService service) throws IOException, SerializerException {
        return serializer.deserialize(getInputStream(), service);
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            try {
                in.close();
            } finally {
                in = null;
            }
        }
    }

    private InputStream getInputStream() throws FileNotFoundException {
        if (in == null) {
            in = new BufferedInputStream(new FileInputStream(file));
        }
        return in;
    }
}
