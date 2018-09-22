package net.morimekta.providence.client;

import com.google.api.client.http.HttpContent;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.Binary;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * HTTP content wrapper for providence messages.
 *
 * @since 1.8.0
 */
public class ProvidenceHttpContent implements HttpContent {
    private final String type;
    private final Binary binary;

    @SuppressWarnings("unchecked")
    public ProvidenceHttpContent(@Nonnull PMessage message,
                                 @Nonnull Serializer serializer) throws IOException {
        this.type = serializer.mediaType();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(baos, message);
        this.binary = Binary.wrap(baos.toByteArray());
    }

    @Override
    public long getLength() {
        return binary.length();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean retrySupported() {
        return true;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        binary.write(out);
    }
}
