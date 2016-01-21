package net.morimekta.providence.jackson;

import net.morimekta.providence.Binary;
import net.morimekta.providence.util.PBase64Utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 07.01.16.
 */
public class BinaryJsonSerializerTest {
    @Test
    public void testSerialize() throws IOException {
        BinaryJsonSerializer deserializer = new BinaryJsonSerializer();

        byte[] data = new byte[]{12, 34, 56, 78, 91, 23, 45, 67, 78, 90};
        String encoded = "\"" + PBase64Utils.encode(data) + "\"";

        JsonFactory factory = new JsonFactory();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator parser = factory.createGenerator(out);

        Binary binary = Binary.wrap(data);

        deserializer.serialize(binary, parser, null);

        parser.flush();

        assertEquals(encoded, out.toString());
    }
}
