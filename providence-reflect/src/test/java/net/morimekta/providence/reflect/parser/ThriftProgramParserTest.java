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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeSet;

import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class ThriftProgramParserTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testParse_calculator() throws IOException, ParseException {
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        copyResourceTo("/parser/calculator/calculator.thrift", tmp.getRoot());

        File calculator = new File(tmp.getRoot(), "calculator.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(calculator),
                                           calculator,
                                           new TreeSet<>());

        assertThat(debugString(program),
                   is(equalToLines(
                           "program_name = \"calculator\"\n" +
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
                           "          params = [\n" +
                           "          ]\n" +
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
                           "]")));
    }

    @Test
    public void testParse_calculator_strict() throws IOException, ParseException {
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        copyResourceTo("/parser/calculator/calculator_strict.thrift", tmp.getRoot());

        File calculator = new File(tmp.getRoot(), "calculator_strict.thrift");

        ThriftProgramParser parser = new ThriftProgramParser(true, true);
        ProgramType program = parser.parse(new FileInputStream(calculator),
                                           calculator,
                                           new TreeSet<>());

        assertEquals("program_name = \"calculator_strict\"\n" +
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
                     "          params = [\n" +
                     "          ]\n" +
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
                     "]", debugString(program));
    }

    @Test
    public void testParse_number() throws IOException, ParseException {
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());

        File number = new File(tmp.getRoot(), "number.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(number),
                                          number,
                                          new TreeSet<>());

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
                     "]", debugString(program));
    }

    @Test
    public void testParser_annotations() throws IOException, ParseException {
        copyResourceTo("/parser/tests/annotations.thrift", tmp.getRoot());

        File annotations = new File(tmp.getRoot(), "annotations.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(annotations), annotations, new TreeSet<>());

        assertThat(debugString(program),
                   equalToLines("program_name = \"annotations\"\n" +
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
                                "]"));
    }

    @Test
    public void testAutoId() throws IOException, ParseException {
        copyResourceTo("/parser/tests/autoid.thrift", tmp.getRoot());

        File autoid = new File(tmp.getRoot(), "autoid.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(autoid),
                                           autoid,
                                           new TreeSet<>());

        assertEquals("program_name = \"autoid\"\n" +
                     "namespaces = {\n" +
                     "  \"java\": \"net.morimekta.test.autoid\"\n" +
                     "}\n" +
                     "decl = [\n" +
                     "  {\n" +
                     "    decl_struct = {\n" +
                     "      variant = EXCEPTION\n" +
                     "      name = \"AutoId\"\n" +
                     "      fields = [\n" +
                     "        {\n" +
                     "          key = -1\n" +
                     "          type = \"string\"\n" +
                     "          name = \"message\"\n" +
                     "        },\n" +
                     "        {\n" +
                     "          key = -2\n" +
                     "          type = \"i32\"\n" +
                     "          name = \"second\"\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_service = {\n" +
                     "      name = \"AutoParam\"\n" +
                     "      methods = [\n" +
                     "        {\n" +
                     "          return_type = \"i32\"\n" +
                     "          name = \"method\"\n" +
                     "          params = [\n" +
                     "            {\n" +
                     "              key = -1\n" +
                     "              type = \"i32\"\n" +
                     "              name = \"a\"\n" +
                     "            },\n" +
                     "            {\n" +
                     "              key = -2\n" +
                     "              type = \"i32\"\n" +
                     "              name = \"b\"\n" +
                     "            }\n" +
                     "          ]\n" +
                     "          exceptions = [\n" +
                     "            {\n" +
                     "              key = -1\n" +
                     "              type = \"AutoId\"\n" +
                     "              name = \"auto1\"\n" +
                     "            }\n" +
                     "          ]\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  }\n" +
                     "]", debugString(program));
    }

    @Test
    public void testAutoValue() throws IOException, ParseException {
        copyResourceTo("/parser/tests/autovalue.thrift", tmp.getRoot());

        File autovalue = new File(tmp.getRoot(), "autovalue.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(autovalue), autovalue, new TreeSet<>());

        assertEquals("program_name = \"autovalue\"\n" +
                     "namespaces = {\n" +
                     "  \"java\": \"net.morimekta.test.autoid\"\n" +
                     "}\n" + "decl = [\n" +
                     "  {\n" +
                     "    decl_enum = {\n" +
                     "      name = \"AutoValue\"\n" +
                     "      values = [\n" +
                     "        {\n" +
                     "          name = \"FIRST\"\n" +
                     "          value = 0\n" +
                     "        },\n" + "        {\n" +
                     "          name = \"SECOND\"\n" +
                     "          value = 1\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"THIRD\"\n" +
                     "          value = 2\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    decl_enum = {\n" +
                     "      name = \"PartialAutoValue\"\n" +
                     "      values = [\n" +
                     "        {\n" +
                     "          name = \"FIRST\"\n" +
                     "          value = 5\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"SECOND\"\n" +
                     "          value = 6\n" +
                     "        },\n" +
                     "        {\n" +
                     "          name = \"THIRD\"\n" +
                     "          value = 7\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  }\n" +
                     "]", debugString(program));
    }


    @Test
    public void testParseExceptions() {
        copyResourceTo("/failure/conflicting_field_name.thrift", tmp.getRoot());
        copyResourceTo("/failure/duplicate_field_id.thrift", tmp.getRoot());
        copyResourceTo("/failure/duplicate_field_name.thrift", tmp.getRoot());
        copyResourceTo("/failure/invalid_namespace.thrift", tmp.getRoot());
        copyResourceTo("/failure/invalid_include.thrift", tmp.getRoot());
        copyResourceTo("/failure/valid_reference.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_include.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_program.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_type.thrift", tmp.getRoot());
        copyResourceTo("/failure/valid_reference.thrift", tmp.getRoot());

        assertBadThrfit("Parse error on line 5, pos 9: Field separatedName has field with conflicting name in T\n" +
                        "  2: i32 separatedName;\n" +
                        "---------^",
                        "conflicting_field_name.thrift");
        assertBadThrfit("Parse error on line 6, pos 2: Field id 1 already exists in struct T\n" +
                        "  1: i32 second;\n" +
                        "--^",
                        "duplicate_field_id.thrift");
        assertBadThrfit("Parse error on line 5, pos 9: Field first already exists in struct T\n" +
                        "  2: i32 first;\n" +
                        "---------^",
                        "duplicate_field_name.thrift");
        assertBadThrfit("Parse error on line 1, pos 15: Identifier with double '..' at line 1 pos 15\n" +
                        "namespace java org.apache..test.failure\n" +
                        "---------------^",
                        "invalid_namespace.thrift");
        // assertBadThrift("Unknown Type 'i128'",
        //                 "/failure/unknown_type.thrift");
        assertBadThrfit("Parse error on line 8, pos 0: Unexpected token 'include', expected type declaration\n" +
                        "include \"valid_reference.thrift\"\n" +
                        "^",
                        "invalid_include.thrift");
        assertBadThrfit("Parse error on line 4, pos 5: Unknown program valid_reference for type valid_reference.Message\n" +
                        "  1: valid_reference.Message message;\n" +
                        "-----^",
                        "unknown_program.thrift");
        assertBadThrfit("Parse error on line 3, pos 8: Included file not found no_such_file.thrift\n" +
                        "include \"no_such_file.thrift\"\n" +
                        "--------^",
                        "unknown_include.thrift");
    }

    @Test
    public void testParseStrictExceptions() {
        copyResourceTo("/parser/calculator/calculator.thrift", tmp.getRoot());
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());

        assertBadStrictThrfit("Parse error on line 14, pos 7: Missing enum value in strict declaration\n" +
                              "    ADD,\n" +
                              "-------^",
                              "calculator.thrift");
    }

    private void assertBadThrfit(String message, String fileName) {
        try {
            ThriftProgramParser parser = new ThriftProgramParser();
            File file = new File(tmp.getRoot(), fileName);
            parser.parse(new FileInputStream(file), file, new TreeSet<>());
            fail("No exception on bad thrift: " + fileName);
        } catch (ParseException e) {
            assertEquals(message, e.asString().replaceAll("\\r", ""));
        } catch (IOException e) {
            assertEquals(message, e.getMessage());
        }
    }

    private void assertBadStrictThrfit(String message, String fileName) {
        try {
            ThriftProgramParser parser = new ThriftProgramParser(true, true);
            File file = new File(tmp.getRoot(), fileName);
            parser.parse(new FileInputStream(file), file, new TreeSet<>());
            fail("No exception on bad thrift: " + fileName);
        } catch (ParseException e) {
            assertEquals(message, e.asString().replaceAll("\\r", ""));
        } catch (IOException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
