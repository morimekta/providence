package net.morimekta.providence.reflect.parser.internal;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.providence.util.WritableTypeRegistry;
import net.morimekta.test.providence.reflect.CompactFields;
import net.morimekta.test.providence.reflect.Containers;
import net.morimekta.test.providence.reflect.OptionalFields;
import net.morimekta.test.providence.reflect.Value;
import net.morimekta.test.providence.reflect.calculator.Operation;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConstParserTest {
    private WritableTypeRegistry registry;

    @Before
    public void setUp() {
        registry = new SimpleTypeRegistry();
        registry.registerRecursively(Operation.kDescriptor);
        registry.registerRecursively(Containers.kDescriptor);
    }

    @Test
    public void testPrimitives() throws IOException {
        assertThat(parse("test", "bool", "false"),
                   is(false));
        assertThat(parse("test", "bool", "0"),
                   is(false));
        assertThat(parse("test", "bool", "3"),
                   is(true));
        assertThat(parse("test", "byte", "44"),
                   is((byte) 44));
        assertThat(parse("test", "byte", "providence.Value.SIXTH"),
                   is((byte) 13));
        assertThat(parse("test", "i8", "44"),
                   is((byte) 44));
        assertThat(parse("test", "i16", "44"),
                   is((short) 44));
        assertThat(parse("providence", "i16", "providence.Value.SIXTH"),
                   is((short) 13));
        assertThat(parse("test", "i32", "44"),
                   is(44));
        assertThat(parse("providence", "i32", "Value.SIXTH"),
                   is(13));
        assertThat(parse("test", "i64", "44"),
                   is(44L));
        assertThat(parse("providence", "i64", "providence.Value.SIXTH"),
                   is(13L));
        assertThat(parse("test", "double", "44.42"),
                   is(44.42));
        assertThat(parse("test", "string", "\"value\""),
                   is("value"));
        assertThat(parse("test", "string", "\'value\'"),
                   is("value"));
        assertThat(parse("test", "string", "null"),
                   is(nullValue()));
        assertThat(parse("test", "binary", "'AAAA'"),
                   is(Binary.fromBase64("AAAA")));
        assertThat(parse("test", "binary", "null"),
                   is(nullValue()));
    }

    @Test
    public void testEnum() throws IOException {
        assertThat(parse("test", "providence.Value", "null"),
                   is(nullValue()));
        assertThat(parse("providence", "providence.Value", "Value.FOURTH"),
                   is(Value.FOURTH));
        assertThat(parse("providence", "Value", "providence.Value.FOURTH"),
                   is(Value.FOURTH));
        assertThat(parse("test", "providence.Value", "Value.FOURTH"),
                   is(Value.FOURTH));
        assertThat(parse("test", "providence.Value", "providence.Value.FOURTH"),
                   is(Value.FOURTH));
    }

    @Test
    public void testMessage() throws IOException {
        assertThat(parse("providence", "OptionalFields",
                         "{\n" +
                         "  'byteValue': 44\n" +
                         "  \"compactValue\": {\n" +
                         "    \"name\": 'name',\n" +
                         "    'id': 42\n" +
                         "  }\n" +
                         "}"),
                   is(OptionalFields.builder()
                                    .setByteValue((byte) 44)
                                    .setCompactValue(new CompactFields("name",
                                                                       42,
                                                                       null))
                                    .build()));
        assertThat(parse("providence", "OptionalFields",
                         "{\n" +
                         "  # shell comment\n" +
                         "  'byteValue': 44\n" +
                         "  /*\n" +
                         "   * Java block comment.\n" +
                         "   */\n" +
                         "  \"compactValue\": {\n" +
                         "    // java line comment\n" +
                         "    \"name\": 'name',\n" +
                         "    'id': 42\n" +
                         "  }\n" +
                         "}"),
                   is(OptionalFields.builder()
                                    .setByteValue((byte) 44)
                                    .setCompactValue(new CompactFields("name",
                                                                       42,
                                                                       null))
                                    .build()));
        assertThat(parse("providence", "OptionalFields", "{}"),
                   is(OptionalFields.builder().build()));
    }

    @Test
    public void testContainers_lists() throws IOException {
        assertThat(parse("providence", "Containers",
                         "{\n" +
                         "  # shell comment\n" +
                         "  'booleanList': [\n" +
                         "    # shell me\n" +
                         "    1,\n" +
                         "    // java me\n" +
                         "    false,\n" +
                         "    /*\n" +
                         "     * block me\n" +
                         "     */\n" +
                         "    true, 0\n" +
                         "    # shell anywhere\n" +
                         "  ];\n" +
                         "\n" +
                         "  /*\n" +
                         "   * Java block comment.\n" +
                         "   */\n" +
                         "  \"doubleList\": [0, 0.1, 4.2e-4]\n" +
                         "  'stringList': [\n" +
                         "    # nothing here\n" +
                         "  ]\n" +
                         "  # truly anywhere\n" +
                         "}"),
                   is(Containers.builder()
                                .setBooleanList(ImmutableList.of(true, false, true, false))
                                .setDoubleList(ImmutableList.of(0.0, 0.1, 4.2e-4))
                                .setStringList(ImmutableList.of())
                                .build()));
    }

    @Test
    public void testFailures() throws IOException {
        assertFailure("test", "string", "44",
                      "Error on line 6, pos 2: Not a valid string value.\n" +
                      " 44\n" +
                      "-^^");
        assertFailure("providence", "i32", "\"44\"",
                      "Error on line 3, pos 4: \"44\" is not a valid i32 value.\n" +
                      " = \"44\"\n" +
                      "---^^^^");
        assertFailure("providence", "Value", "13",
                      "Error on line 5, pos 2: No such providence.Value enum value.\n" +
                      " 13\n" +
                      "-^^");
    }

    private Object parse(String context, String type, String text) throws IOException {
        ConstParser parser = new ConstParser(registry, context, type.length(), text.length());
        PDescriptor descriptor = registry.getProvider(type, context, ImmutableMap.of()).descriptor();
        ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes(UTF_8));
        return parser.parse(bais, descriptor);
    }

    private void assertFailure(String context, String type, String text, String message) throws IOException {
        try {
            parse(context, type, text);
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.asString(), is(message));
        }
    }
}
