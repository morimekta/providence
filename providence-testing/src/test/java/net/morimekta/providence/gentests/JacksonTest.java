package net.morimekta.providence.gentests;

import net.morimekta.test.jackson.CompactFields;
import net.morimekta.test.jackson.DefaultValues;
import net.morimekta.test.jackson.UnionFields;
import net.morimekta.test.jackson.Value;
import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Jackson serialization and seserialization testing.
 */
public class JacksonTest {
    private DefaultValues primitives;

    @Before
    public void setUp() {
        primitives = new DefaultValues(true,
                                       (byte) 64,
                                       (short) 12345,
                                       1234567890,
                                       1234567890123456789L,
                                       1234567890.12345,
                                       "Ûñı©óð€",
                                       Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0}),
                                       Value.FIRST,
                                       new CompactFields("Test", 4, null));
    }

    @Test
    public void testSerialize_primitives() throws IOException {
        assertEquals("{" +
                     "\"booleanValue\":true," +
                     "\"byteValue\":64," +
                     "\"shortValue\":12345," +
                     "\"integerValue\":1234567890," +
                     "\"longValue\":1234567890123456789," +
                     "\"doubleValue\":1.23456789012345E9," +
                     "\"stringValue\":\"Ûñı©óð€\"," +
                     "\"binaryValue\":\"AAECAwQFBgcICQA\"," +
                     "\"enumValue\":1," +
                     "\"compactValue\":{\"name\":\"Test\",\"id\":4}" +
                     "}", serialize(primitives));
    }

    @Test
    public void testSerialize_collection() throws IOException {
        assertEquals("[{" +
                     "\"booleanValue\":true," +
                     "\"byteValue\":64," +
                     "\"shortValue\":12345," +
                     "\"integerValue\":1234567890," +
                     "\"longValue\":1234567890123456789," +
                     "\"doubleValue\":1.23456789012345E9," +
                     "\"stringValue\":\"Ûñı©óð€\"," +
                     "\"binaryValue\":\"AAECAwQFBgcICQA\"," +
                     "\"enumValue\":1," +
                     "\"compactValue\":{\"name\":\"Test\",\"id\":4}" +
                     "}]", serialize(Collections.singletonList(primitives)));
    }

    @Test
    public void testSerialize_union() throws IOException {
        assertEquals("{\"booleanValue\":true}", serialize(UnionFields.withBooleanValue(true)));
        assertEquals("{\"binaryValue\":\"AAECAwQFBgcICQA\"}", serialize(UnionFields.withBinaryValue(Binary.fromBase64("AAECAwQFBgcICQA"))));
        assertEquals("{\"compactValue\":{\"name\":\"test\",\"id\":4}}", serialize(UnionFields.withCompactValue(new CompactFields("test", 4, null))));
    }

    @Test
    public void testDeserialize_primitives() throws IOException {
        String message = "{" +
                         "\"booleanValue\":true," +
                         "\"byteValue\":64," +
                         "\"shortValue\":12345," +
                         "\"integerValue\":1234567890," +
                         "\"longValue\":1234567890123456789," +
                         "\"doubleValue\":1.23456789012345E9," +
                         "\"stringValue\":\"Ûñı©óð€\"," +
                         "\"binaryValue\":\"AAECAwQFBgcICQA\"," +
                         "\"enumValue\":\"FIRST\"," +
                         "\"compactValue\":{\"name\":\"Test\",\"id\":4}" +
                         "}";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        DefaultValues out = mapper.readValue(in, DefaultValues.class);

        assertEquals(primitives.toString().replaceAll(",", ",\n"),
                     out.toString().replaceAll(",", ",\n"));
    }

    @Test
    public void testDeserialize_collection() throws IOException {
        String message = "[{" +
                         "\"booleanValue\":true," +
                         "\"byteValue\":64," +
                         "\"shortValue\":12345," +
                         "\"integerValue\":1234567890," +
                         "\"longValue\":1234567890123456789," +
                         "\"doubleValue\":1.23456789012345E9," +
                         "\"stringValue\":\"Ûñı©óð€\"," +
                         "\"binaryValue\":\"AAECAwQFBgcICQA=\"," +
                         "\"enumValue\":1," +
                         "\"compactValue\":[\"Test\",4]" +
                         "}]";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        ArrayList<DefaultValues> out = mapper.readValue(in,
                                                        mapper.getTypeFactory()
                                                              .constructCollectionType(ArrayList.class,
                                                                                       DefaultValues.class));

        assertEquals(primitives.toString().replaceAll(",", ",\n"),
                     out.get(0).toString().replaceAll(",", ",\n"));
    }

    private String serialize(Object value) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, value);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
