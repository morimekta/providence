package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.streams.MessageStreams;

import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Convenience methods for reading providence messages from resources.
 */
public class ProvidenceHelper {
    public static <T extends PMessage<T>> T fromJsonResource(String path, PDescriptor<T> descriptor)
            throws PSerializeException, IOException {
        return fromResource(path, descriptor, new PJsonSerializer(true));
    }

    public static <T extends PMessage<T>, F extends PField> ArrayList<T> arrayListFromJsonResource(String path,
                                                                                                   PStructDescriptor<T, F> descriptor)
            throws PSerializeException, IOException {
        return arrayListFromResource(path, descriptor, new PJsonSerializer(true));
    }

    public static <T extends PMessage<T>> T fromResource(String path, PDescriptor<T> descriptor, PSerializer serializer)
            throws PSerializeException, IOException {
        InputStream in = ProvidenceHelper.class.getResourceAsStream(path);
        if (in == null) {
            Assert.fail("Resource " + path + " does not exist.");
        }
        return serializer.deserialize(new BufferedInputStream(in), descriptor);
    }

    public static <T extends PMessage<T>, F extends PField> ArrayList<T> arrayListFromResource(String path,
                                                                                               PStructDescriptor<T, F> descriptor,
                                                                                               PSerializer serializer)
            throws PSerializeException, IOException {
        return (ArrayList<T>) MessageStreams.resource(path, serializer, descriptor)
                                            .collect(Collectors.toList());
    }

    private ProvidenceHelper() {}
}
