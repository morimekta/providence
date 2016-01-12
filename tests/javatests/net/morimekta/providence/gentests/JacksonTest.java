package net.morimekta.providence.gentests;

import net.morimekta.providence.Binary;
import net.morimekta.test.primitives.Primitives;
import net.morimekta.test.primitives.Value;

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
    private Primitives primitives;

    @Before
    public void setUp() {
        primitives = new Primitives(true,
                                    (byte) 64,
                                    (short) 12345,
                                    1234567890,
                                    1234567890123456789L,
                                    1234567890.12345,
                                    "Ûñı©óð€",
                                    Binary.wrap(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 }),
                                    Value.FIRST);
    }

    @Test
    public void testSerialize_primitives() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        mapper.writeValue(out, primitives);

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8);

        assertEquals(
                "{" +
                "\"bl\":true," +
                "\"bt\":64," +
                "\"sh\":12345," +
                "\"i\":1234567890," +
                "\"l\":1234567890123456789," +
                "\"d\":1.23456789012345E9," +
                "\"s\":\"Ûñı©óð€\"," +
                "\"bn\":\"AAECAwQFBgcICQA=\"," +
                "\"v\":\"FIRST\"" +
                "}", serialized);
    }

    @Test
    public void testSerialize_collection() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        mapper.writeValue(out, Collections.singletonList(primitives));

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8);

        assertEquals(
                "[{" +
                "\"bl\":true," +
                "\"bt\":64," +
                "\"sh\":12345," +
                "\"i\":1234567890," +
                "\"l\":1234567890123456789," +
                "\"d\":1.23456789012345E9," +
                "\"s\":\"Ûñı©óð€\"," +
                "\"bn\":\"AAECAwQFBgcICQA=\"," +
                "\"v\":\"FIRST\"" +
                "}]", serialized);
    }

    @Test
    public void testDeserialize_primitives() throws IOException {
        String message = "{" +
                "\"bl\":true," +
                "\"bt\":64," +
                "\"sh\":12345," +
                "\"i\":1234567890," +
                "\"l\":1234567890123456789," +
                "\"d\":1.23456789012345E9," +
                "\"s\":\"Ûñı©óð€\"," +
                "\"bn\":\"AAECAwQFBgcICQA=\"," +
                "\"v\":\"FIRST\"" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        Primitives out = mapper.readValue(in, Primitives.class);

        assertEquals(primitives, out);
    }
    @Test
    public void testDeserialize_collection() throws IOException {
        String message = "[{" +
                         "\"bl\":true," +
                         "\"bt\":64," +
                         "\"sh\":12345," +
                         "\"i\":1234567890," +
                         "\"l\":1234567890123456789," +
                         "\"d\":1.23456789012345E9," +
                         "\"s\":\"Ûñı©óð€\"," +
                         "\"bn\":\"AAECAwQFBgcICQA=\"," +
                         "\"v\":\"FIRST\"" +
                         "}]";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        ArrayList<Primitives> out = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Primitives.class));

        assertEquals(primitives, out.get(0));

    }
}
