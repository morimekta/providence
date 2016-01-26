package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;

import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Convenience methods for reading providence messages from resources.
 */
public class ProvidenceHelper {
    public static <T extends PMessage<T>> T fromJsonResource(
            String path, PDescriptor<T> descriptor)
            throws PSerializeException, IOException {
        return fromResource(path, descriptor, new PJsonSerializer(true));
    }

    public static <T extends PMessage<T>> ArrayList<T> arrayListFromJsonResource(
            String path, PDescriptor<T> descriptor)
            throws PSerializeException, IOException {
        return arrayListFromResource(path, descriptor, new PJsonSerializer(true));
    }

    public static <T extends PMessage<T>> T fromResource(
            String path, PDescriptor<T> descriptor, PSerializer serializer)
            throws PSerializeException, IOException {
        InputStream in = ProvidenceHelper.class.getResourceAsStream(path);
        if (in == null) {
            Assert.fail("Resource " + path + " does not exist.");
        }
        return serializer.deserialize(new BufferedInputStream(in), descriptor);
    }

    public static <T extends PMessage<T>> ArrayList<T> arrayListFromResource(
            String path, PDescriptor<T> descriptor, PSerializer serializer)
            throws PSerializeException, IOException {
        InputStream in = ProvidenceHelper.class.getResourceAsStream(path);
        if (in == null) {
            Assert.fail("Resource " + path + " does not exist.");
        }
        in = new BufferedInputStream(in);
        ArrayList<T> out = new ArrayList<>();
        while (true) {
            T item = serializer.deserialize(in, descriptor);
            if (item == null) break;
            out.add(item);
            if (in.read() < 0) break;
        }
        return out;
    }

    private ProvidenceHelper() {}
}
