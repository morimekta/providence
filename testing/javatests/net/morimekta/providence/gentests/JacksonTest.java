package net.morimekta.providence.gentests;

import net.morimekta.test.providence.CompactFields;
import net.morimekta.test.providence.DefaultValues;
import net.morimekta.test.providence.Value;
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

import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        mapper.writeValue(out, primitives);

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8);

        assertEquals("{" +
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
                     "}", serialized);
    }

    @Test
    public void testSerialize_collection() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        mapper.writeValue(out, Collections.singletonList(primitives));

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8);

        assertEquals("[{" +
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
                     "}]", serialized);
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

        assertThat(out, messageEq(primitives));
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
                         "\"enumValue\":\"FIRST\"," +
                         "\"compactValue\":{\"name\":\"Test\",\"id\":4}" +
                         "}]";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        ArrayList<DefaultValues> out = mapper.readValue(in,
                                                        mapper.getTypeFactory()
                                                              .constructCollectionType(ArrayList.class,
                                                                                       DefaultValues.class));

        assertThat(out.get(0), messageEq(primitives));
    }
}
