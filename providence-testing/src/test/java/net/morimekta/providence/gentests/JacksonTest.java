package net.morimekta.providence.gentests;

import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.testing.generator.GeneratorWatcher;
import net.morimekta.providence.testing.generator.SimpleGeneratorBase;
import net.morimekta.providence.testing.generator.SimpleGeneratorContext;
import net.morimekta.test.jackson.CompactFields;
import net.morimekta.test.jackson.Containers;
import net.morimekta.test.jackson.OptionalFields;
import net.morimekta.test.jackson.UnionFields;
import net.morimekta.test.jackson.Value;
import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Jackson serialization and seserialization testing.
 */
public class JacksonTest {
    @Rule
    public GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> generator =
            GeneratorWatcher.create();

    private OptionalFields primitives;

    @Before
    public void setUp() {
        primitives = new OptionalFields(true,
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
                     "\"compactValue\":[\"Test\",4]" +
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
                     "\"compactValue\":[\"Test\",4]" +
                     "}]", serialize(Collections.singletonList(primitives)));
    }

    @Test
    public void testSerialize_union() throws IOException {
        assertEquals("{\"booleanValue\":true}", serialize(UnionFields.withBooleanValue(true)));
        assertEquals("{\"binaryValue\":\"AAECAwQFBgcICQA\"}", serialize(UnionFields.withBinaryValue(Binary.fromBase64("AAECAwQFBgcICQA"))));
        assertEquals("{\"compactValue\":[\"test\",4]}",
                     serialize(UnionFields.withCompactValue(new CompactFields("test", 4, null))));
    }

    @Test
    public void testDeserialize_primitives() throws IOException {
        OptionalFields primitives = new OptionalFields(true,
                                                       (byte) 64,
                                                       (short) 12345,
                                                       1234567890,
                                                       1234567890123456789L,
                                                       1234567890.12345,
                                                       "Ûñı©óð€",
                                                       Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0}),
                                                       Value.FIRST,
                                                       new CompactFields("Test", 4, null));

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

        OptionalFields out = mapper.readValue(in, OptionalFields.class);

        assertEquals(primitives, out);
    }

    @Test
    public void testDeserialize_collection() throws IOException {
        String message = "[{" +
                         "\"1\":true," +
                         "\"2\":64," +
                         "\"3\":12345," +
                         "\"4\":1234567890," +
                         "\"5\":1234567890123456789," +
                         "\"6\":1.23456789012345E9," +
                         "\"7\":\"Ûñı©óð€\"," +
                         "\"8\":\"AAECAwQFBgcICQA=\"," +
                         "\"9\":1," +
                         "\"10\":[\"Test\",4]" +
                         "}]";

        ObjectMapper mapper = new ObjectMapper();

        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        ArrayList<OptionalFields> out = mapper.readValue(in,
                                                         mapper.getTypeFactory()
                                                               .constructCollectionType(ArrayList.class, OptionalFields.class));

        assertEquals(primitives.toString()
                               .replaceAll(",", ",\n"),
                     out.get(0)
                        .toString()
                        .replaceAll(",", ",\n"));
    }

    @Test
    public void testJsonSerializerCompat() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSerializer compact = new JsonSerializer();
        JsonSerializer pretty = new JsonSerializer().pretty();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < 100; ++i) {
            Containers containers = generator.generate(Containers.kDescriptor);

            out.reset();
            compact.serialize(out, containers);
            Containers res = mapper.readValue(out.toByteArray(), Containers.class);
            assertThat(res, is(equalToMessage(containers)));

            out.reset();
            pretty.serialize(out, containers);
            res = mapper.readValue(out.toByteArray(), Containers.class);
            assertThat(res, is(equalToMessage(containers)));
        }
    }

    private String serialize(Object value) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, value);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }


    @Test
    public void testSerializable() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        OptionalFields original = generator.generate(OptionalFields.kDescriptor);

        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);

        OptionalFields actual = (OptionalFields) in.readObject();

        assertThat(actual, is(equalToMessage(original)));
    }
}
