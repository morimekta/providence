/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.providence.serializer;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.Containers;
import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.test.providence.core.RequiredFields;
import net.morimekta.test.providence.core.Value;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.util.Binary;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class JsonSerializerTest {
    private JsonSerializer compact = new JsonSerializer(true);
    private JsonSerializer named   = new JsonSerializer(true).named();
    private JsonSerializer pretty  = new JsonSerializer(true).pretty();
    private JsonSerializer lenient = new JsonSerializer(false);

    @Test
    public void testProperties() {
        assertThat(compact.binaryProtocol(), is(false));
        assertThat(pretty.mimeType(), is(JsonSerializer.MIME_TYPE));
    }

    @Test
    public void testSerializer_CompactFields() throws IOException {
        assertSerializer(named,
                         CompactFields.builder()
                                      .setName("my_category")
                                      .setId(44)
                                      .build(),
                         "[\"my_category\",44]");
        assertSerializer(compact,
                         CompactFields.builder()
                                      .setName("my_category")
                                      .setId(44)
                                      .setLabel("My Category")
                                      .build(),
                         "[\"my_category\",44,\"My Category\"]");
    }

    @Test
    public void testSerializer_OptionalFields() throws IOException {
        RequiredFields struct =
                RequiredFields.builder()
                              .setBooleanValue(true)
                              .setByteValue((byte) 123)
                              .setShortValue((short) 12345)
                              .setIntegerValue(1234567890)
                              .setLongValue(1234567890123456789L)
                              .setBinaryValue(Binary.fromHexString("AABBCCDD"))
                              .setStringValue("\"nee'ds\033\u2021esc")
                              .setDoubleValue(12345.12345)
                              .setEnumValue(Value.EIGHTEENTH)
                              .setCompactValue(CompactFields.builder()
                                                            .setName("my_category")
                                                            .setId(44)
                                                            .build())
                              .build();

        assertSerializer(named,
                         struct,
                         "{\"booleanValue\":true," +
                         "\"byteValue\":123," +
                         "\"shortValue\":12345," +
                         "\"integerValue\":1234567890," +
                         "\"longValue\":1234567890123456789," +
                         "\"doubleValue\":12345.12345," +
                         "\"stringValue\":\"\\\"nee'ds\\u001b\u2021esc\"," +
                         "\"binaryValue\":\"qrvM3Q\"," +
                         "\"enumValue\":\"EIGHTEENTH\"," +
                         "\"compactValue\":[\"my_category\",44]}");
        assertSerializer(compact,
                         struct,
                         "{\"1\":true," +
                         "\"2\":123," +
                         "\"3\":12345," +
                         "\"4\":1234567890," +
                         "\"5\":1234567890123456789," +
                         "\"6\":12345.12345," +
                         "\"7\":\"\\\"nee'ds\\u001b\u2021esc\"," +
                         "\"8\":\"qrvM3Q\"," +
                         "\"9\":4181," +
                         "\"10\":[\"my_category\",44]}");
        assertSerializer(pretty,
                         struct,
                         "{\n" +
                         "    \"booleanValue\": true,\n" +
                         "    \"byteValue\": 123,\n" +
                         "    \"shortValue\": 12345,\n" +
                         "    \"integerValue\": 1234567890,\n" +
                         "    \"longValue\": 1234567890123456789,\n" +
                         "    \"doubleValue\": 12345.12345,\n" +
                         "    \"stringValue\": \"\\\"nee'ds\\u001b\u2021esc\",\n" +
                         "    \"binaryValue\": \"qrvM3Q\",\n" +
                         "    \"enumValue\": \"EIGHTEENTH\",\n" +
                         "    \"compactValue\": [\n" +
                         "        \"my_category\",\n" +
                         "        44\n" +
                         "    ]\n" +
                         "}");
        assertSerializer(lenient,
                         struct.mutate()
                               .clearBinaryValue()
                               .clearStringValue()
                               .clearCompactValue()
                               .build(),
                         "{\"1\":true,\"2\":123,\"3\":12345,\"4\":1234567890," +
                         "\"5\":1234567890123456789,\"6\":12345.12345,\"9\":4181}");
    }

    private <M extends PMessage<M,F>, F extends PField>
    void assertSerializer(JsonSerializer serializer,
                          M obj,
                          String json) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = serializer.serialize(out, obj);

        assertThat(new String(out.toByteArray(), UTF_8), is(json));
        assertThat(len, is(out.toByteArray().length));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        M res = serializer.deserialize(in, obj.descriptor());

        assertThat(res, is(equalTo(obj)));
        assertThat(in.read(), is(-1));
    }

    @Test
    public void testSerializer_services() throws IOException {
        assertSerializer(compact,
                         Calculator.kDescriptor,
                         "[\"iamalive\",4,44,{}]");
        assertSerializer(compact,
                         Calculator.kDescriptor,
                         "[\"ping\",2,44,{\"0\":true}]");
    }

    private <M extends PMessage<M,F>, F extends PField>
    void assertSerializer(JsonSerializer serializer,
                          PService service,
                          String json) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(UTF_8));
        PServiceCall<M,F> call = serializer.deserialize(in, service);

        assertThat(in.read(), is(-1));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = serializer.serialize(out, call);

        assertThat(new String(out.toByteArray(), UTF_8), is(json));
        assertThat(len, is(out.toByteArray().length));

        in = new ByteArrayInputStream(out.toByteArray());
        PServiceCall<M,F> res = serializer.deserialize(in, service);

        assertThat(res, is(equalTo(call)));
        assertThat(in.read(), is(-1));
    }

    @Test
    public void testDeserialize_simpleFails() {
        PMessageDescriptor<?,?> opt = OptionalFields.kDescriptor;
        PMessageDescriptor<?,?> cnt = Containers.kDescriptor;
        PService calc = Calculator.kDescriptor;

        assertFail(opt, compact, "",
                   "Empty json body");
        assertFail(opt, compact, "[false, 123, 54321]",
                   "OptionalFields is not compatible for compact struct notation.");
        assertFail(cnt, compact, "{\"optionalFields\":[false,123,54321]}",
                   "OptionalFields is not compatible for compact struct notation.");
        assertFail(opt, compact, "null",
                   "Null value as body.");
        assertFail(opt, compact, "\"not a message\"",
                   "expected message start, found: '\"not a message\"'");

        assertFail(opt, compact, "{\"booleanValue\":\"str\"}",
                   "No boolean value for token: '\"str\"'");
        assertFail(opt, compact, "{\"booleanValue\":{}}",
                   "No boolean value for token: '{'");
        assertFail(opt, compact, "{\"byteValue\":\"str\"}",
                   "Not a valid byte value: '\"str\"'");
        assertFail(opt, compact, "{\"shortValue\":\"str\"}",
                   "Not a valid short value: '\"str\"'");
        assertFail(opt, compact, "{\"integerValue\":\"str\"}",
                   "Not a valid int value: '\"str\"'");
        assertFail(opt, compact, "{\"longValue\":\"str\"}",
                   "Not a valid long value: '\"str\"'");
        assertFail(opt, compact, "{\"doubleValue\":\"str\"}",
                   "Not a valid double value: '\"str\"'");
        assertFail(opt, compact, "{\"stringValue\":55}",
                   "Not a valid string value: '55'");
        assertFail(opt, compact, "{\"binaryValue\":55}",
                   "Not a valid binary value: 55");
        assertFail(opt, compact, "{\"binaryValue\":\"g./ar,bl'e\"}",
                   "Unable to parse Base64 data: \"g./ar,bl'e\"");

        assertFail(calc, lenient, "{}",
                   "Expected service call start (one of ['[']): but found '{'");
        assertFail(calc, lenient, "[123]",
                   "Expected method name (string literal): but found '123'");
        assertFail(calc, lenient, "[\"iamalive\", 77]",
                   "Service call type 77 is not valid");
        assertFail(calc, lenient, "[\"iamalive\", 1, -55]",
                   "Expected entry sep (one of [',']): but found ']'");
        assertFail(calc, lenient, "[\"iamalive\", 2, -55, {\"0\": 6}]",
                   "No response type for calculator.Calculator.iamalive()");
        assertFail(calc, lenient, "[\"iamalive\", \"boo\", -55, {\"0\": 6}]",
                   "Service call type \"boo\" is not valid");
        assertFail(calc, lenient, "[\"iamalive\", false, -55, {\"0\": 6}]",
                   "Invalid service call type token false");
        assertFail(calc, compact, "[\"calculate\", \"reply\", 55, {}]",
                   "No union field set in calculator.calculate___response");
        assertFail(calc, compact, "[\"ping\", \"reply\", 55, {\"0\": 3}]",
                   "Not a void token value: '3'");

        assertFail(opt, compact, "{\"binaryValue\",\"AAss\"}",
                   "Expected field KV sep (one of [':']): but found ','");
        assertFail(opt, compact, "{\"enumValue\":false}",
                   "false is not a enum value type");
        assertFail(Containers.kDescriptor, compact, "{\"enumMap\":[]}",
                   "Invalid start of map '['");
        assertFail(Containers.kDescriptor, compact, "{\"enumSet\":{}}",
                   "Invalid start of set '{'");
        assertFail(Containers.kDescriptor, compact, "{\"enumList\":{}}",
                   "Invalid start of list '{'");

        assertFail(Containers.kDescriptor, compact, "{\"booleanMap\":{\"fleece\":false}}",
                   "Invalid boolean value: \"fleece\"");
        assertFail(Containers.kDescriptor, compact, "{\"byteMap\":{\"5boo\":55}}",
                   "Unable to parse numeric value 5boo");
        assertFail(Containers.kDescriptor, compact, "{\"shortMap\":{\"5boo\":55}}",
                   "Unable to parse numeric value 5boo");
        assertFail(Containers.kDescriptor, compact, "{\"integerMap\":{\"5boo\":55}}",
                   "Unable to parse numeric value 5boo");
        assertFail(Containers.kDescriptor, compact, "{\"longMap\":{\"5boo\":4}}",
                   "Unable to parse numeric value 5boo");
        assertFail(Containers.kDescriptor, compact, "{\"doubleMap\":{\"5.5boo\":4.4}}",
                   "Unable to parse double from key \"5.5boo\"");
        assertFail(Containers.kDescriptor, compact, "{\"doubleMap\":{\"5.5 boo\":4.4}}",
                   "Garbage after double: \"5.5 boo\"");
        assertFail(Containers.kDescriptor, compact, "{\"doubleMap\":{\"boo 2\":4.4}}",
                   "Unable to parse double from key \"boo 2\"");

        assertFail(Containers.kDescriptor, compact, "{\"binaryMap\":{\"\\_(^.^)_/\":\"\"}}",
                   "Unable to parse Base64 data");
        assertFail(Containers.kDescriptor, compact, "{\"enumMap\":{\"1\":\"BOO\"}}",
                   "\"BOO\" is not a known enum value for providence.Value");
        assertFail(Containers.kDescriptor, compact, "{\"enumMap\":{\"BOO\":\"1\"}}",
                   "\"BOO\" is not a known enum value for providence.Value");
        assertFail(Containers.kDescriptor, compact, "{\"messageKeyMap\":{\"{\\\"1\\\":55}\":\"str\"}}",
                   "Error parsing message key: Not a valid string value: '55'");

    }

    private <M extends PMessage<M, F>, F extends PField>
    void assertFail(PMessageDescriptor<M,F> descriptor,
                    JsonSerializer serializer,
                    String json,
                    String exception) {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(UTF_8));
        try {
            serializer.deserialize(bais, descriptor);
            fail("no exception");
        } catch (IOException e) {
            if (!e.getMessage().equals(exception)) {
                e.printStackTrace();
            }
            assertThat(e.getMessage(), is(exception));
        }
    }

    private void assertFail(PService service,
                            JsonSerializer serializer,
                            String json,
                            String exception) {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(UTF_8));
        try {
            serializer.deserialize(bais, service);
            fail("no exception");
        } catch (IOException e) {
            if (!e.getMessage().equals(exception)) {
                e.printStackTrace();
            }
            assertThat(e.getMessage(), is(exception));
        }
    }
}
