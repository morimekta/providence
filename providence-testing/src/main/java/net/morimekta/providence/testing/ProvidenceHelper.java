package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.Serializer;
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
    public static <T extends PMessage<T>, TF extends PField> T
    fromJsonResource(String path, PStructDescriptor<T, TF> descriptor)
            throws SerializerException, IOException {
        return fromResource(path, descriptor, new JsonSerializer(true));
    }

    public static <T extends PMessage<T>, F extends PField> ArrayList<T>
    arrayListFromJsonResource(String path, PStructDescriptor<T, F> descriptor)
            throws SerializerException, IOException {
        return arrayListFromResource(path, descriptor, new JsonSerializer(true));
    }

    public static <T extends PMessage<T>, TF extends PField> T
    fromResource(String path, PStructDescriptor<T, TF> descriptor, Serializer serializer)
            throws SerializerException, IOException {
        InputStream in = ProvidenceHelper.class.getResourceAsStream(path);
        if (in == null) {
            Assert.fail("Resource " + path + " does not exist.");
        }
        return serializer.deserialize(new BufferedInputStream(in), descriptor);
    }

    public static <T extends PMessage<T>, F extends PField> ArrayList<T>
    arrayListFromResource(String path, PStructDescriptor<T, F> descriptor, Serializer serializer)
            throws SerializerException, IOException {
        return (ArrayList<T>) MessageStreams.resource(path, serializer, descriptor)
                                            .collect(Collectors.toList());
    }

    private ProvidenceHelper() {}
}
