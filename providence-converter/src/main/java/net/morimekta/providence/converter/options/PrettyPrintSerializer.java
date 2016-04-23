package net.morimekta.providence.converter.options;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.util.PPrettyPrinter;
import net.morimekta.util.io.CountingOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple serializer wrapper for pretty printer.
 */
public class PrettyPrintSerializer extends PSerializer {
    private final PPrettyPrinter prettyPrinter = new PPrettyPrinter();

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream output, T message) throws IOException, PSerializeException {
        CountingOutputStream out = new CountingOutputStream(output);
        out.write(prettyPrinter.format(message).getBytes(StandardCharsets.UTF_8));
        out.write('\n');
        out.flush();
        return out.getByteCount();
    }

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream output, PServiceCall<T> call) throws IOException, PSerializeException {
        throw new IOException("Only able to print messages.");
    }

    @Override
    public <T extends PMessage<T>, TF extends PField> T deserialize(InputStream input, PStructDescriptor<T, TF> descriptor) throws IOException, PSerializeException {
        throw new IOException("Pretty printer not allowed as input type.");
    }

    @Override
    public <T extends PMessage<T>> PServiceCall<T> deserialize(InputStream input, PService service) throws IOException, PSerializeException {
        throw new IOException("Pretty printer not allowed as input type.");
    }

    @Override
    public boolean binaryProtocol() {
        return false;
    }

    @Override
    public String mimeType() {
        return "text/plain";
    }
}
