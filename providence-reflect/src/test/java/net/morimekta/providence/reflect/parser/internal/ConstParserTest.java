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
import com.google.common.collect.ImmutableSet;
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
        assertThat(parse("test", "binary", "\"AAAA\""),
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
        assertThat(parse("test", "providence.Value", "5"),
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

        try {
            parse("providence", "list<i8>", "nope");
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 8, pos 4: Expected list start, found nope\n" +
                    " = nope\n" +
                    "---^^^^"));
        }
    }

    @Test
    public void testSets() throws IOException {
        try {
            parse("providence", "set<i64>",
                  "{}");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 8, pos 2: Expected list start, found {\n" +
                    " {}\n" +
                    "-^"));
        }

        assertThat(parse("providence", "set<bool>",
                         "[ 0, true ]"),
                   is(ImmutableSet.of(false, true)));
        assertThat(parse("providence", "set<i8>",
                         "[ 2, Value.FIFTH ]"),
                   is(ImmutableSet.of((byte) 2, (byte) 8)));
        assertThat(parse("providence", "set<i16>",
                         "[ 2, Value.FIFTH ]"),
                   is(ImmutableSet.of((short) 2, (short) 8)));
        assertThat(parse("providence", "set<i32>",
                         "[ 2, Value.FIFTH ]"),
                   is(ImmutableSet.of(2, 8)));
        assertThat(parse("providence", "set<i64>",
                         "[ 2,\n" +
                         " // or \n" +
                         " Value.FIFTH ]"),
                   is(ImmutableSet.of( 2L, 8L)));
        assertThat(parse("providence", "set<double>",
                         "[ /** nooo */ 2.2 ]"),
                   is(ImmutableSet.of(2.2)));
    }

    @Test
    public void testMaps() throws IOException {
        try {
            parse("providence", "map<i64,i64>",
                  "[]");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 12, pos 2: Expected map start, found [\n" +
                    " []\n" +
                    "-^"));
        }

        assertThat(parse("providence", "map<bool,bool>",
                         "{" +
                         "  0: 1," +
                         "  true: false}"),
                   is(ImmutableMap.of(false, true, true, false)));
        assertThat(parse("providence", "map<i8,i8>",
                         "{" +
                         "  1: 2," +
                         "  \"124\": 125," +
                         "  Value.SECOND: Value.THIRD," +
                         "}"),
                   is(ImmutableMap.of((byte)1, (byte)2,
                                      (byte)2, (byte)3,
                                      (byte)124, (byte)125)));
        assertThat(parse("providence", "map<i16,i16>",
                         "{" +
                         "  1: 2," +
                         "  \"124\": 125," +
                         "  Value.SECOND: Value.THIRD," +
                         "}"),
                   is(ImmutableMap.of((short)1, (short)2,
                                      (short)2, (short)3,
                                      (short)124, (short)125)));
        assertThat(parse("providence", "map<i32,i32>",
                         "{" +
                         "  1: 2," +
                         "  \"124\": 125," +
                         "  Value.SECOND: Value.THIRD," +
                         "}"),
                   is(ImmutableMap.of(1, 2,
                                      2, 3,
                                      124, 125)));
        assertThat(parse("providence", "map<i64,i64>",
                         "{" +
                         "  1: 2," +
                         "  // 1: 2,\n" +
                         "  \"124\": 125," +
                         "  /* 1: 2, */" +
                         "  Value.SECOND: Value.THIRD," +
                         "}"),
                   is(ImmutableMap.of(1L, 2L,
                                      2L, 3L,
                                      124L, 125L)));
        try {
            parse("providence", "map<i64,i64>",
                  "{ Foo.SECOND: Value.THIRD }");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 12, pos 29: No type named Foo.\n" +
                    "....................... = { Foo.SECOND: Value.THIRD }\n" +
                    "----------------------------^^^^^^^^^^"));
        }

        try {
            parse("providence", "map<i64,i64>",
                  "{ OptionalFields.SECOND: Value.THIRD }");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 12, pos 40: OptionalFields is not an enum.\n" +
                    ".................................. = { OptionalFields.SECOND: Value.THIRD }\n" +
                    "---------------------------------------^^^^^^^^^^^^^^^^^^^^^"));
        }

        assertThat(parse("providence", "map<double,double>",
                         "{" +
                         "  1.1: 2.2e+4," +
                         "}"),
                   is(ImmutableMap.of(1.1, 22000.0)));
        try {
            parse("providence", "map<double,double>",
                  "{" +
                  "  \"boo\": 2.2e+4," +
                  "}");
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 18, pos 21: Unable to parse double value\n" +
                    ".............. = {  \"boo\": 2.2e+4,}\n" +
                    "--------------------^^^^^"));
        }

        assertThat(parse("providence", "map<string,string>",
                         "{" +
                         "  \"a\": \"b\"," +
                         "}"),
                   is(ImmutableMap.of("a", "b")));

        assertThat(parse("providence", "map<binary,binary>",
                         "{" +
                         "  \"AAg\": \"AAg\"," +
                         "}"),
                   is(ImmutableMap.of(Binary.fromBase64("AAg="),
                                      Binary.fromBase64("AAg="))));
    }

    @Test
    public void testMapListSet() throws IOException {
        assertThat(parse("providence", "map<Value,list<i32>>",
                         "{\n" +
                         "  Value.FIRST: [1, 2, 3, 4]," +
                         "  2: [4, 3, 2, 1]" +
                         "}"),
                   is(ImmutableMap.of(
                           Value.FIRST, ImmutableList.of(1, 2, 3, 4),
                           Value.SECOND, ImmutableList.of(4, 3, 2, 1)
                   )));
        assertThat(parse("providence", "map<bool,set<i32>>",
                         "{\n" +
                         "  true: [1, 2, 3, 4]," +
                         "  false: [5, 6, 7, 8]" +
                         "}"),
                   is(ImmutableMap.of(
                           Boolean.TRUE, ImmutableSet.of(1, 2, 3, 4),
                           Boolean.FALSE, ImmutableSet.of(5, 6, 7, 8)
                   )));

        assertThat(parse("providence", "map<i32,i32>",
                         "{\n" +
                         "}"),
                   is(ImmutableMap.of()));
        assertThat(parse("providence", "list<i32>",
                         "[\n" +
                         "]"),
                   is(ImmutableList.of()));
        assertThat(parse("providence", "set<i32>",
                         "[\n" +
                         "]"),
                   is(ImmutableSet.of()));
    }

    @Test
    public void testMessages() throws IOException {
        assertThat(parse("providence", "OptionalFields",
                         "{\n" +
                         "}\n"),
                   is(OptionalFields.builder()
                                    .build()));
        assertThat(parse("providence", "OptionalFields",
                         "\n    null\n"),
                   is(nullValue()));
        assertFailure("providence", "OptionalFields", "[]",
                      "Error on line 14, pos 2: Not a valid message start.\n" +
                      " []\n" +
                      "-^");
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
        assertFailure("providence", "double", "\"44\"",
                      "Error on line 6, pos 4: \"44\" is not a valid double value.\n" +
                      " = \"44\"\n" +
                      "---^^^^");
        assertFailure("providence", "binary", "0x44",
                      "Error on line 6, pos 4: Not a valid binary value.\n" +
                      " = 0x44\n" +
                      "---^^^^");
        assertFailure("providence", "OptionalFields", "{ \"foo\": \"bar\" }",
                      "Error on line 14, pos 18: No such field in providence.OptionalFields: foo\n" +
                      "............ = { \"foo\": \"bar\" }\n" +
                      "-----------------^^^^^");
        assertFailure("providence", "bool", "\"foo\"",
                      "Error on line 4, pos 5: Not boolean value: \"foo\"\n" +
                      ". = \"foo\"\n" +
                      "----^^^^^");
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
