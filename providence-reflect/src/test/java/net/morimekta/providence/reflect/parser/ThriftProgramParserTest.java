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

package net.morimekta.providence.reflect.parser;

import net.morimekta.providence.model.ProgramType;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class ThriftProgramParserTest {
    private ThriftProgramParser parser;

    @Before
    public void setUp() {
        this.parser = new ThriftProgramParser();
    }

    @Test
    public void testParse_calculator() throws IOException, ParseException {
        ProgramType calculator = parser.parse(getClass().getResourceAsStream("/parser/calculator/calculator.thrift"),
                                              "calculator.thrift");

        assertEquals("program_name = \"calculator\"\n" +
                     "includes = [\n" +
                     "  \"number.thrift\"\n" +
                     "]\n" +
                     "namespaces = {\n" +
                     "  \"java\": \"net.morimekta.test.calculator\"\n" +
                     "}\n" +
                     "decl = [\n" +
                     "  {\n" +
                     "    decl_enum = {\n" +
                     "      documentation = \"Block comment on type.\"\n" +
                     "      name = \"Operator\"\n" +
                     "      values = [\n" +
                     "        {\n" +
                     "          documentation = \"line comment on enum\"\n" +
                     "          name = \"IDENTITY\"\n" +
                     "          value = 1\n" +
                     "        },\n" +
                     "        {\n" +
                     "          documentation = \"Block comment on enum.\"\n" +
                     "          name = \"ADD\"\n" +
                     "          value = 2\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"SUBTRACT\"\n" +
                     "          value = 3\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"MULTIPLY\"\n" +
                     "          value = 4\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"DIVIDE\"\n" +
                     "          value = 5\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      documentation = \"Line comment on type.\"\n" +
                     "      variant = UNION\n" +
                     "      name = \"Operand\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          documentation = \"Double line\\ncomment on field.\"\n" +
                     "          key = 1\n" +
                     "          type = \"Operation\"\n" +
                     "          name = \"operation\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          documentation = \"Block comment\\n - with formatting.\\nOn field.\"\n" +
                     "          key = 2\n" +
                     "          type = \"double\"\n" +
                     "          name = \"number\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          key = 3\n" +
                     "          type = \"number.Imaginary\"\n" +
                     "          name = \"imaginary\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      name = \"Operation\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          key = 1\n" +
                     "          type = \"Operator\"\n" +
                     "          name = \"operator\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          key = 2\n" +
                     "          type = \"list<Operand>\"\n" +
                     "          name = \"operands\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "      annotations = {\n" +
                     "        \"compact\": \"\"\n" +
                     "      }\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      variant = EXCEPTION\n" +
                     "      name = \"CalculateException\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          key = 1\n" +
                     "          requirement = REQUIRED\n" +
                     "          type = \"string\"\n" +
                     "          name = \"message\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          key = 2\n" +
                     "          type = \"Operation\"\n" +
                     "          name = \"operation\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_service = {\n" +
                     "      name = \"Calculator\"\n" +
                     "      methods = [\n" +
                     "        {\n" +
                     "          documentation = \"Block comment on method.\"\n" +
                     "          one_way = false\n" +
                     "          return_type = \"Operand\"\n" +
                     "          name = \"calculate\"\n" +
                     "          params = [\n" +
                     "            {\n" +
                     "              key = 1\n" +
                     "              type = \"Operation\"\n" +
                     "              name = \"op\"\n" +
                     "            }\n" +
                     "          ]\n" +
                     "          exceptions = [\n" +
                     "            {\n" +
                     "              key = 1\n" +
                     "              type = \"CalculateException\"\n" +
                     "              name = \"ce\"\n" +
                     "            }\n" +
                     "          ]\n" +
                     "        },\n" +
                     "        {\n" +
                     "          documentation = \"line comment on method.\"\n" +
                     "          one_way = true\n" +
                     "          name = \"iamalive\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_const = {\n" +
                     "      documentation = \"Block comment on constant.\"\n" +
                     "      type = \"Operand\"\n" +
                     "      name = \"PI\"\n" +
                     "      value = \"{\\\"number\\\":3.141592}\"\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_const = {\n" +
                     "      documentation = \"Line comment on constant.\"\n" +
                     "      type = \"set<Operator>\"\n" +
                     "      name = \"kComplexOperands\"\n" +
                     "      value = \"[Operator.MULTIPLY,Operator.DIVIDE]\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "]", debugString(calculator));
    }

    @Test
    public void testParse_number() throws IOException, ParseException {
        ProgramType number = parser.parse(getClass().getResourceAsStream("/parser/calculator/number.thrift"),
                                          "number.thrift");

        assertEquals("program_name = \"number\"\n" +
                     "namespaces = {\n" +
                     "  \"java\": \"net.morimekta.test.number\"\n" +
                     "}\n" +
                     "decl = [\n" +
                     "  {\n" +
                     "    decl_typedef = {\n" +
                     "      type = \"double\"\n" +
                     "      name = \"real\"\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      name = \"Imaginary\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          key = 1\n" +
                     "          requirement = REQUIRED\n" +
                     "          type = \"real\"\n" +
                     "          name = \"v\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          key = 2\n" +
                     "          type = \"double\"\n" +
                     "          name = \"i\"\n" +
                     "          default_value = \"0.\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "      annotations = {\n" +
                     "        \"compact\": \"true\"\n" +
                     "      }\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_const = {\n" +
                     "      type = \"Imaginary\"\n" +
                     "      name = \"kSqrtMinusOne\"\n" +
                     "      value = \"{\\\"v\\\":0.,\\\"i\\\":-1.0}\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "]", debugString(number));
    }

    @Test
    public void testParser_annotations() throws IOException, ParseException {
        ProgramType annotations = parser.parse(getClass().getResourceAsStream("/parser/tests/annotations.thrift"),
                                               "annotations.thrift");

        assertEquals("program_name = \"annotations\"\n" +
                     "namespaces = {\n" +
                     "  \"java\": \"net.morimekta.test.annotations\"\n" +
                     "}\n" +
                     "decl = [\n" +
                     "  {\n" +
                     "    decl_enum = {\n" +
                     "      name = \"E\"\n" +
                     "      values = [\n" +
                     "        {\n" +
                     "          name = \"VAL\"\n" +
                     "          value = 0\n" +
                     "          annotations = {\n" +
                     "            \"anno\": \"str\"\n" +
                     "            \"anno.other\": \"other\"\n" +
                     "          }\n" +
                     "        }\n" +
                     "      ]\n" +
                     "      annotations = {\n" +
                     "        \"e.anno\": \"E\"\n" +
                     "      }\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      variant = EXCEPTION\n" +
                     "      name = \"S\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          key = 1\n" +
                     "          type = \"bool\"\n" +
                     "          name = \"val\"\n" +
                     "          annotations = {\n" +
                     "            \"anno\": \"str\"\n" +
                     "          }\n" +
                     "        }\n" +
                     "      ]\n" +
                     "      annotations = {\n" +
                     "        \"other\": \"\"\n" +
                     "      }\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_service = {\n" +
                     "      name = \"Srv\"\n" +
                     "      methods = [\n" +
                     "        {\n" +
                     "          one_way = false\n" +
                     "          name = \"method\"\n" +
                     "          params = [\n" +
                     "            {\n" +
                     "              key = 1\n" +
                     "              type = \"i32\"\n" +
                     "              name = \"param\"\n" +
                     "              annotations = {\n" +
                     "                \"abba\": \"7\"\n" +
                     "              }\n" +
                     "            }\n" +
                     "          ]\n" +
                     "          annotations = {\n" +
                     "            \"anno\": \"anno\"\n" +
                     "          }\n" +
                     "        },\n" +
                     "        {\n" +
                     "          one_way = false\n" +
                     "          name = \"method2\"\n" +
                     "          params = [\n" +
                     "            {\n" +
                     "              key = 1\n" +
                     "              type = \"i32\"\n" +
                     "              name = \"param\"\n" +
                     "              annotations = {\n" +
                     "                \"abba\": \"7\"\n" +
                     "              }\n" +
                     "            }\n" +
                     "          ]\n" +
                     "          exceptions = [\n" +
                     "            {\n" +
                     "              key = 1\n" +
                     "              type = \"S\"\n" +
                     "              name = \"e\"\n" +
                     "              annotations = {\n" +
                     "                \"ex\": \"667\"\n" +
                     "              }\n" +
                     "            }\n" +
                     "          ]\n" +
                     "          annotations = {\n" +
                     "            \"anno\": \"anno\"\n" +
                     "          }\n" +
                     "        }\n" +
                     "      ]\n" +
                     "      annotations = {\n" +
                     "        \"src\": \"src\"\n" +
                     "        \"bin\": \"bin\"\n" +
                     "      }\n" +
                     "    }\n" +
                     "  }\n" +
                     "]", debugString(annotations));
    }

    @Test
    public void testParseExceptions() {
        assertBadThrfit("Parse error on line 5, pos 9: Field separatedName has field with conflicting name in T\n" +
                        "  2: i32 separatedName;\n" +
                        "---------^",
                        "/failure/conflicting_field_name.thrift");
        assertBadThrfit("Parse error on line 6, pos 2: Field id 1 already exists in struct T\n" +
                        "  1: i32 second;\n" +
                        "--^",
                        "/failure/duplicate_field_id.thrift");
        assertBadThrfit("Parse error on line 5, pos 9: Field first already exists in struct T\n" +
                        "  2: i32 first;\n" +
                        "---------^",
                        "/failure/duplicate_field_name.thrift");
        assertBadThrfit("Parse error on line 1, pos 15: Identifier with double '..' at line 1 pos 15\n" +
                        "namespace java org.apache..test.failure\n" +
                        "---------------^",
                        "/failure/invalid_namespace.thrift");
        // assertBadThrift("Unknown Type 'i128'",
        //                 "/failure/unknown_type.thrift");
        assertBadThrfit("Parse error on line 8, pos 0: Unexpected token 'include', expected type declaration\n" +
                        "include \"valid_reference.thrift\"\n" +
                        "^",
                        "/failure/invalid_include.thrift");
    }

    private void assertBadThrfit(String message, String resource) {
        try {
            parser.parse(getClass().getResourceAsStream(resource), new File(resource).getName());
            fail("No exception on bad thrift: " + resource);
        } catch (ParseException e) {
            assertEquals(message, e.asString());
        } catch (IOException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
