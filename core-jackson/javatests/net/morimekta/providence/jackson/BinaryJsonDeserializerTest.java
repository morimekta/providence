package net.morimekta.providence.jackson;

import net.morimekta.util.Base64;
import net.morimekta.util.Binary;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen
 * @since 07.01.16.
 */
public class BinaryJsonDeserializerTest {
    @Test
    public void testDeserialize() throws IOException {
        BinaryJsonDeserializer deserializer = new BinaryJsonDeserializer();

        byte[] data = new byte[]{12, 34, 56, 78, 91, 23, 45, 67, 78, 90};
        String encoded = "[\"" + Base64.encode(data) + "\"]";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(encoded);

        parser.nextToken();  // start of array.
        parser.nextToken();  // start of literal.

        Assert.assertEquals(Binary.wrap(data), deserializer.deserialize(parser, null));
    }
}
