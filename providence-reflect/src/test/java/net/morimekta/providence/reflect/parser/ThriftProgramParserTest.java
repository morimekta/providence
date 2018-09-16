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
import net.morimekta.providence.serializer.pretty.TokenizerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeSet;

import static net.morimekta.providence.util.ProvidenceHelper.debugString;
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

        assertEquals(
                "{\n" +
                "  program_name = \"calculator\"\n" +
                "  includes = [\n" +
                "    \"number.thrift\"\n" +
                "  ]\n" +
                "  namespaces = {\n" +
                "    \"java\": \"net.morimekta.test.calculator\"\n" +
                "  }\n" +
                "  decl = [\n" +
                "    {\n" +
                "      decl_enum = {\n" +
                "        documentation = \"Block comment on type.\"\n" +
                "        name = \"Operator\"\n" +
                "        values = [\n" +
                "          {\n" +
                "            name = \"IDENTITY\"\n" +
                "            id = 1\n" +
                "            start_pos = {\n" +
                "              line_no = 10\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 10\n" +
                "              line_pos = 17\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            documentation = \"Block comment on enum.\"\n" +
                "            name = \"ADD\"\n" +
                "            id = 2\n" +
                "            start_pos = {\n" +
                "              line_no = 14\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 14\n" +
                "              line_pos = 8\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"SUBTRACT\"\n" +
                "            id = 3\n" +
                "            start_pos = {\n" +
                "              line_no = 15\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 15\n" +
                "              line_pos = 13\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"MULTIPLY\"\n" +
                "            id = 4\n" +
                "            start_pos = {\n" +
                "              line_no = 16\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 16\n" +
                "              line_pos = 13\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"DIVIDE\"\n" +
                "            id = 5\n" +
                "            start_pos = {\n" +
                "              line_no = 17\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 17\n" +
                "              line_pos = 11\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 8\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 18\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        variant = UNION\n" +
                "        name = \"Operand\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            type = \"Operation\"\n" +
                "            name = \"operation\"\n" +
                "            start_pos = {\n" +
                "              line_no = 24\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 24\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            documentation = \"Block comment\\n - with formatting.\\nOn field.\"\n" +
                "            id = 2\n" +
                "            type = \"double\"\n" +
                "            name = \"number\"\n" +
                "            start_pos = {\n" +
                "              line_no = 30\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 30\n" +
                "              line_pos = 21\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 3\n" +
                "            type = \"number.Imaginary\"\n" +
                "            name = \"imaginary\"\n" +
                "            start_pos = {\n" +
                "              line_no = 31\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 31\n" +
                "              line_pos = 34\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 21\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 32\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        name = \"Operation\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            type = \"Operator\"\n" +
                "            name = \"operator\"\n" +
                "            start_pos = {\n" +
                "              line_no = 35\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 35\n" +
                "              line_pos = 25\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 2\n" +
                "            type = \"list<Operand>\"\n" +
                "            name = \"operands\"\n" +
                "            start_pos = {\n" +
                "              line_no = 36\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 36\n" +
                "              line_pos = 30\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        annotations = {\n" +
                "          \"compact\": \"\"\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 34\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 37\n" +
                "          line_pos = 17\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        variant = EXCEPTION\n" +
                "        name = \"CalculateException\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            requirement = REQUIRED\n" +
                "            type = \"string\"\n" +
                "            name = \"message\"\n" +
                "            start_pos = {\n" +
                "              line_no = 40\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 40\n" +
                "              line_pos = 31\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 2\n" +
                "            type = \"Operation\"\n" +
                "            name = \"operation\"\n" +
                "            start_pos = {\n" +
                "              line_no = 41\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 41\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 39\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 42\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_service = {\n" +
                "        name = \"BaseCalculator\"\n" +
                "        methods = [\n" +
                "          {\n" +
                "            one_way = true\n" +
                "            name = \"iamalive\"\n" +
                "            params = []\n" +
                "            start_pos = {\n" +
                "              line_no = 46\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 46\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            return_type = \"i32\"\n" +
                "            name = \"numCalls\"\n" +
                "            params = [\n" +
                "              {\n" +
                "                id = 1\n" +
                "                requirement = REQUIRED\n" +
                "                type = \"i32\"\n" +
                "                name = \"minus\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 47\n" +
                "                  line_pos = 18\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 47\n" +
                "                  line_pos = 39\n" +
                "                }\n" +
                "              },\n" +
                "              {\n" +
                "                id = 2\n" +
                "                requirement = OPTIONAL\n" +
                "                type = \"i32\"\n" +
                "                name = \"inAdditionTo\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 47\n" +
                "                  line_pos = 41\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 47\n" +
                "                  line_pos = 69\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "            start_pos = {\n" +
                "              line_no = 47\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 47\n" +
                "              line_pos = 70\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        annotations = {\n" +
                "          \"deprecated\": \"Because reasons\"\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 44\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 48\n" +
                "          line_pos = 35\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_service = {\n" +
                "        documentation = \"Block comment on service\"\n" +
                "        name = \"Calculator\"\n" +
                "        extend = \"BaseCalculator\"\n" +
                "        methods = [\n" +
                "          {\n" +
                "            documentation = \"Block comment on method.\"\n" +
                "            return_type = \"Operand\"\n" +
                "            name = \"calculate\"\n" +
                "            params = [\n" +
                "              {\n" +
                "                id = 1\n" +
                "                type = \"Operation\"\n" +
                "                name = \"op\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 57\n" +
                "                  line_pos = 23\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 57\n" +
                "                  line_pos = 38\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "            exceptions = [\n" +
                "              {\n" +
                "                id = 1\n" +
                "                type = \"CalculateException\"\n" +
                "                name = \"ce\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 57\n" +
                "                  line_pos = 48\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 57\n" +
                "                  line_pos = 72\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "            start_pos = {\n" +
                "              line_no = 57\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 57\n" +
                "              line_pos = 73\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 53\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 58\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_const = {\n" +
                "        documentation = \"Block comment on constant.\"\n" +
                "        type = \"Operand\"\n" +
                "        name = \"PI\"\n" +
                "        value = \"{\\n  \\\"number\\\": 3.141592\\n}\"\n" +
                "        value_start_pos = {\n" +
                "          line_no = 63\n" +
                "          line_pos = 20\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 63\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 65\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_const = {\n" +
                "        type = \"set<Operator>\"\n" +
                "        name = \"kComplexOperands\"\n" +
                "        value = \"[\\n    Operator.MULTIPLY,\\n    Operator.DIVIDE\\n]\"\n" +
                "        value_start_pos = {\n" +
                "          line_no = 68\n" +
                "          line_pos = 40\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 68\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 71\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}", debugString(program));
    }

    @Test
    public void testParse_calculator_strict() throws IOException {
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        copyResourceTo("/parser/calculator/calculator_strict.thrift", tmp.getRoot());

        File calculator = new File(tmp.getRoot(), "calculator_strict.thrift");

        ThriftProgramParser parser = new ThriftProgramParser(true, true);
        ProgramType program = parser.parse(new FileInputStream(calculator),
                                           calculator,
                                           new TreeSet<>());

        assertEquals(
                "{\n" +
                "  program_name = \"calculator_strict\"\n" +
                "  includes = [\n" +
                "    \"number.thrift\"\n" +
                "  ]\n" +
                "  namespaces = {\n" +
                "    \"java\": \"net.morimekta.test.calculator\"\n" +
                "  }\n" +
                "  decl = [\n" +
                "    {\n" +
                "      decl_enum = {\n" +
                "        documentation = \"Block comment on type.\"\n" +
                "        name = \"Operator\"\n" +
                "        values = [\n" +
                "          {\n" +
                "            name = \"IDENTITY\"\n" +
                "            id = 1\n" +
                "            start_pos = {\n" +
                "              line_no = 10\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 10\n" +
                "              line_pos = 17\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            documentation = \"Block comment on enum.\"\n" +
                "            name = \"ADD\"\n" +
                "            id = 2\n" +
                "            start_pos = {\n" +
                "              line_no = 14\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 14\n" +
                "              line_pos = 12\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"SUBTRACT\"\n" +
                "            id = 3\n" +
                "            start_pos = {\n" +
                "              line_no = 15\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 15\n" +
                "              line_pos = 17\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"MULTIPLY\"\n" +
                "            id = 4\n" +
                "            start_pos = {\n" +
                "              line_no = 16\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 16\n" +
                "              line_pos = 17\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            name = \"DIVIDE\"\n" +
                "            id = 5\n" +
                "            start_pos = {\n" +
                "              line_no = 17\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 17\n" +
                "              line_pos = 15\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 8\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 18\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        variant = UNION\n" +
                "        name = \"Operand\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            type = \"Operation\"\n" +
                "            name = \"operation\"\n" +
                "            start_pos = {\n" +
                "              line_no = 24\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 24\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            documentation = \"Block comment\\n - with formatting.\\nOn field.\"\n" +
                "            id = 2\n" +
                "            type = \"double\"\n" +
                "            name = \"number\"\n" +
                "            start_pos = {\n" +
                "              line_no = 30\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 30\n" +
                "              line_pos = 21\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 3\n" +
                "            type = \"number.Imaginary\"\n" +
                "            name = \"imaginary\"\n" +
                "            start_pos = {\n" +
                "              line_no = 31\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 31\n" +
                "              line_pos = 34\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 21\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 32\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        name = \"Operation\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            type = \"Operator\"\n" +
                "            name = \"operator\"\n" +
                "            start_pos = {\n" +
                "              line_no = 35\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 35\n" +
                "              line_pos = 25\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 2\n" +
                "            type = \"list<Operand>\"\n" +
                "            name = \"operands\"\n" +
                "            start_pos = {\n" +
                "              line_no = 36\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 36\n" +
                "              line_pos = 30\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        annotations = {\n" +
                "          \"compact\": \"\"\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 34\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 37\n" +
                "          line_pos = 17\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_message = {\n" +
                "        variant = EXCEPTION\n" +
                "        name = \"CalculateException\"\n" +
                "        fields = [\n" +
                "          {\n" +
                "            id = 1\n" +
                "            requirement = REQUIRED\n" +
                "            type = \"string\"\n" +
                "            name = \"message\"\n" +
                "            start_pos = {\n" +
                "              line_no = 40\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 40\n" +
                "              line_pos = 31\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            id = 2\n" +
                "            type = \"Operation\"\n" +
                "            name = \"operation\"\n" +
                "            start_pos = {\n" +
                "              line_no = 41\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 41\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 39\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 42\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_service = {\n" +
                "        name = \"Calculator\"\n" +
                "        methods = [\n" +
                "          {\n" +
                "            documentation = \"Block comment on method.\"\n" +
                "            return_type = \"Operand\"\n" +
                "            name = \"calculate\"\n" +
                "            params = [\n" +
                "              {\n" +
                "                id = 1\n" +
                "                type = \"Operation\"\n" +
                "                name = \"op\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 48\n" +
                "                  line_pos = 23\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 48\n" +
                "                  line_pos = 38\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "            exceptions = [\n" +
                "              {\n" +
                "                id = 1\n" +
                "                type = \"CalculateException\"\n" +
                "                name = \"ce\"\n" +
                "                start_pos = {\n" +
                "                  line_no = 48\n" +
                "                  line_pos = 48\n" +
                "                }\n" +
                "                end_pos = {\n" +
                "                  line_no = 48\n" +
                "                  line_pos = 72\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "            start_pos = {\n" +
                "              line_no = 48\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 48\n" +
                "              line_pos = 73\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            one_way = true\n" +
                "            name = \"iamalive\"\n" +
                "            params = []\n" +
                "            start_pos = {\n" +
                "              line_no = 50\n" +
                "              line_pos = 5\n" +
                "            }\n" +
                "            end_pos = {\n" +
                "              line_no = 50\n" +
                "              line_pos = 27\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "        start_pos = {\n" +
                "          line_no = 44\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 51\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_const = {\n" +
                "        documentation = \"Block comment on constant.\"\n" +
                "        type = \"Operand\"\n" +
                "        name = \"PI\"\n" +
                "        value = \"{\\n  \\\"number\\\": 3.141592\\n}\"\n" +
                "        value_start_pos = {\n" +
                "          line_no = 56\n" +
                "          line_pos = 20\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 56\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 58\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      decl_const = {\n" +
                "        type = \"set<Operator>\"\n" +
                "        name = \"kComplexOperands\"\n" +
                "        value = \"[\\n    Operator.MULTIPLY,\\n    Operator.DIVIDE\\n]\"\n" +
                "        value_start_pos = {\n" +
                "          line_no = 61\n" +
                "          line_pos = 40\n" +
                "        }\n" +
                "        start_pos = {\n" +
                "          line_no = 61\n" +
                "          line_pos = 1\n" +
                "        }\n" +
                "        end_pos = {\n" +
                "          line_no = 64\n" +
                "          line_pos = 2\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}", debugString(program));
    }

    @Test
    public void testParse_number() throws IOException {
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());

        File number = new File(tmp.getRoot(), "number.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(number),
                                          number,
                                          new TreeSet<>());

        assertThat(debugString(program),
                   is(is(
                           "{\n" +
                           "  program_name = \"number\"\n" +
                           "  namespaces = {\n" +
                           "    \"java\": \"net.morimekta.test.number\"\n" +
                           "  }\n" +
                           "  decl = [\n" +
                           "    {\n" +
                           "      decl_typedef = {\n" +
                           "        type = \"double\"\n" +
                           "        name = \"real\"\n" +
                           "        start_pos = {\n" +
                           "          line_no = 4\n" +
                           "          line_pos = 1\n" +
                           "        }\n" +
                           "        end_pos = {\n" +
                           "          line_no = 4\n" +
                           "          line_pos = 20\n" +
                           "        }\n" +
                           "      }\n" +
                           "    },\n" +
                           "    {\n" +
                           "      decl_message = {\n" +
                           "        name = \"Imaginary\"\n" +
                           "        fields = [\n" +
                           "          {\n" +
                           "            id = 1\n" +
                           "            requirement = REQUIRED\n" +
                           "            type = \"real\"\n" +
                           "            name = \"v\"\n" +
                           "            start_pos = {\n" +
                           "              line_no = 7\n" +
                           "              line_pos = 5\n" +
                           "            }\n" +
                           "            end_pos = {\n" +
                           "              line_no = 7\n" +
                           "              line_pos = 23\n" +
                           "            }\n" +
                           "          },\n" +
                           "          {\n" +
                           "            id = 2\n" +
                           "            type = \"double\"\n" +
                           "            name = \"i\"\n" +
                           "            default_value = \"0.0\"\n" +
                           "            start_pos = {\n" +
                           "              line_no = 8\n" +
                           "              line_pos = 5\n" +
                           "            }\n" +
                           "            end_pos = {\n" +
                           "              line_no = 8\n" +
                           "              line_pos = 22\n" +
                           "            }\n" +
                           "          }\n" +
                           "        ]\n" +
                           "        annotations = {\n" +
                           "          \"compact\": \"true\"\n" +
                           "        }\n" +
                           "        start_pos = {\n" +
                           "          line_no = 6\n" +
                           "          line_pos = 1\n" +
                           "        }\n" +
                           "        end_pos = {\n" +
                           "          line_no = 9\n" +
                           "          line_pos = 21\n" +
                           "        }\n" +
                           "      }\n" +
                           "    },\n" +
                           "    {\n" +
                           "      decl_typedef = {\n" +
                           "        type = \"Imaginary\"\n" +
                           "        name = \"I\"\n" +
                           "        start_pos = {\n" +
                           "          line_no = 11\n" +
                           "          line_pos = 1\n" +
                           "        }\n" +
                           "        end_pos = {\n" +
                           "          line_no = 11\n" +
                           "          line_pos = 20\n" +
                           "        }\n" +
                           "      }\n" +
                           "    },\n" +
                           "    {\n" +
                           "      decl_const = {\n" +
                           "        type = \"Imaginary\"\n" +
                           "        name = \"kSqrtMinusOne\"\n" +
                           "        value = \"{\\n  \\\"v\\\": 0.0,\\n  \\\"i\\\": -1.0\\n}\"\n" +
                           "        value_start_pos = {\n" +
                           "          line_no = 13\n" +
                           "          line_pos = 33\n" +
                           "        }\n" +
                           "        start_pos = {\n" +
                           "          line_no = 13\n" +
                           "          line_pos = 1\n" +
                           "        }\n" +
                           "        end_pos = {\n" +
                           "          line_no = 16\n" +
                           "          line_pos = 2\n" +
                           "        }\n" +
                           "      }\n" +
                           "    }\n" +
                           "  ]\n" +
                           "}")));
    }

    @Test
    public void testParser_annotations() throws IOException {
        copyResourceTo("/parser/tests/annotations.thrift", tmp.getRoot());

        File annotations = new File(tmp.getRoot(), "annotations.thrift");

        ThriftProgramParser parser  = new ThriftProgramParser();
        ProgramType         program = parser.parse(new FileInputStream(annotations), annotations, new TreeSet<>());

        assertThat(debugString(program),
                   is("{\n" +
                      "  program_name = \"annotations\"\n" +
                      "  namespaces = {\n" +
                      "    \"java\": \"net.morimekta.test.annotations\"\n" +
                      "  }\n" +
                      "  decl = [\n" +
                      "    {\n" +
                      "      decl_enum = {\n" +
                      "        name = \"E\"\n" +
                      "        values = [\n" +
                      "          {\n" +
                      "            name = \"VAL\"\n" +
                      "            id = 0\n" +
                      "            annotations = {\n" +
                      "              \"anno\": \"str\"\n" +
                      "              \"anno.other\": \"other\"\n" +
                      "            }\n" +
                      "            start_pos = {\n" +
                      "              line_no = 4\n" +
                      "              line_pos = 5\n" +
                      "            }\n" +
                      "            end_pos = {\n" +
                      "              line_no = 4\n" +
                      "              line_pos = 45\n" +
                      "            }\n" +
                      "          }\n" +
                      "        ]\n" +
                      "        annotations = {\n" +
                      "          \"e.anno\": \"E\"\n" +
                      "        }\n" +
                      "        start_pos = {\n" +
                      "          line_no = 3\n" +
                      "          line_pos = 1\n" +
                      "        }\n" +
                      "        end_pos = {\n" +
                      "          line_no = 5\n" +
                      "          line_pos = 17\n" +
                      "        }\n" +
                      "      }\n" +
                      "    },\n" +
                      "    {\n" +
                      "      decl_message = {\n" +
                      "        variant = EXCEPTION\n" +
                      "        name = \"S\"\n" +
                      "        fields = [\n" +
                      "          {\n" +
                      "            id = 1\n" +
                      "            type = \"bool\"\n" +
                      "            name = \"val\"\n" +
                      "            annotations = {\n" +
                      "              \"anno\": \"str\"\n" +
                      "            }\n" +
                      "            start_pos = {\n" +
                      "              line_no = 8\n" +
                      "              line_pos = 5\n" +
                      "            }\n" +
                      "            end_pos = {\n" +
                      "              line_no = 8\n" +
                      "              line_pos = 31\n" +
                      "            }\n" +
                      "          }\n" +
                      "        ]\n" +
                      "        annotations = {\n" +
                      "          \"other\": \"\"\n" +
                      "        }\n" +
                      "        start_pos = {\n" +
                      "          line_no = 7\n" +
                      "          line_pos = 1\n" +
                      "        }\n" +
                      "        end_pos = {\n" +
                      "          line_no = 9\n" +
                      "          line_pos = 10\n" +
                      "        }\n" +
                      "      }\n" +
                      "    },\n" +
                      "    {\n" +
                      "      decl_service = {\n" +
                      "        name = \"Srv\"\n" +
                      "        methods = [\n" +
                      "          {\n" +
                      "            name = \"method\"\n" +
                      "            params = [\n" +
                      "              {\n" +
                      "                id = 1\n" +
                      "                type = \"i32\"\n" +
                      "                name = \"param\"\n" +
                      "                annotations = {\n" +
                      "                  \"abba\": \"7\"\n" +
                      "                }\n" +
                      "                start_pos = {\n" +
                      "                  line_no = 12\n" +
                      "                  line_pos = 17\n" +
                      "                }\n" +
                      "                end_pos = {\n" +
                      "                  line_no = 12\n" +
                      "                  line_pos = 42\n" +
                      "                }\n" +
                      "              }\n" +
                      "            ]\n" +
                      "            annotations = {\n" +
                      "              \"anno\": \"anno\"\n" +
                      "            }\n" +
                      "            start_pos = {\n" +
                      "              line_no = 12\n" +
                      "              line_pos = 5\n" +
                      "            }\n" +
                      "            end_pos = {\n" +
                      "              line_no = 12\n" +
                      "              line_pos = 59\n" +
                      "            }\n" +
                      "          },\n" +
                      "          {\n" +
                      "            name = \"method2\"\n" +
                      "            params = [\n" +
                      "              {\n" +
                      "                id = 1\n" +
                      "                type = \"i32\"\n" +
                      "                name = \"param\"\n" +
                      "                annotations = {\n" +
                      "                  \"abba\": \"7\"\n" +
                      "                }\n" +
                      "                start_pos = {\n" +
                      "                  line_no = 13\n" +
                      "                  line_pos = 18\n" +
                      "                }\n" +
                      "                end_pos = {\n" +
                      "                  line_no = 13\n" +
                      "                  line_pos = 43\n" +
                      "                }\n" +
                      "              }\n" +
                      "            ]\n" +
                      "            exceptions = [\n" +
                      "              {\n" +
                      "                id = 1\n" +
                      "                type = \"S\"\n" +
                      "                name = \"e\"\n" +
                      "                annotations = {\n" +
                      "                  \"ex\": \"667\"\n" +
                      "                }\n" +
                      "                start_pos = {\n" +
                      "                  line_no = 14\n" +
                      "                  line_pos = 15\n" +
                      "                }\n" +
                      "                end_pos = {\n" +
                      "                  line_no = 14\n" +
                      "                  line_pos = 34\n" +
                      "                }\n" +
                      "              }\n" +
                      "            ]\n" +
                      "            annotations = {\n" +
                      "              \"anno\": \"anno\"\n" +
                      "            }\n" +
                      "            start_pos = {\n" +
                      "              line_no = 13\n" +
                      "              line_pos = 5\n" +
                      "            }\n" +
                      "            end_pos = {\n" +
                      "              line_no = 15\n" +
                      "              line_pos = 22\n" +
                      "            }\n" +
                      "          }\n" +
                      "        ]\n" +
                      "        annotations = {\n" +
                      "          \"bin\": \"bin\"\n" +
                      "          \"src\": \"src\"\n" +
                      "        }\n" +
                      "        start_pos = {\n" +
                      "          line_no = 11\n" +
                      "          line_pos = 1\n" +
                      "        }\n" +
                      "        end_pos = {\n" +
                      "          line_no = 16\n" +
                      "          line_pos = 29\n" +
                      "        }\n" +
                      "      }\n" +
                      "    }\n" +
                      "  ]\n" +
                      "}"));
    }

    @Test
    public void testAutoId() throws IOException {
        copyResourceTo("/parser/tests/autoid.thrift", tmp.getRoot());

        File autoid = new File(tmp.getRoot(), "autoid.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(autoid),
                                           autoid,
                                           new TreeSet<>());

        assertEquals("{\n" +
                     "  program_name = \"autoid\"\n" +
                     "  namespaces = {\n" +
                     "    \"java\": \"net.morimekta.test.autoid\"\n" +
                     "  }\n" +
                     "  decl = [\n" +
                     "    {\n" +
                     "      decl_message = {\n" +
                     "        variant = EXCEPTION\n" +
                     "        name = \"AutoId\"\n" +
                     "        fields = [\n" +
                     "          {\n" +
                     "            id = -1\n" +
                     "            type = \"string\"\n" +
                     "            name = \"message\"\n" +
                     "            start_pos = {\n" +
                     "              line_no = 4\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 4\n" +
                     "              line_pos = 19\n" +
                     "            }\n" +
                     "          },\n" +
                     "          {\n" +
                     "            id = -2\n" +
                     "            type = \"i32\"\n" +
                     "            name = \"second\"\n" +
                     "            start_pos = {\n" +
                     "              line_no = 5\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 5\n" +
                     "              line_pos = 15\n" +
                     "            }\n" +
                     "          }\n" +
                     "        ]\n" +
                     "        start_pos = {\n" +
                     "          line_no = 3\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "        end_pos = {\n" +
                     "          line_no = 6\n" +
                     "          line_pos = 2\n" +
                     "        }\n" +
                     "      }\n" +
                     "    },\n" +
                     "    {\n" +
                     "      decl_service = {\n" +
                     "        name = \"AutoParam\"\n" +
                     "        methods = [\n" +
                     "          {\n" +
                     "            return_type = \"i32\"\n" +
                     "            name = \"method\"\n" +
                     "            params = [\n" +
                     "              {\n" +
                     "                id = -1\n" +
                     "                type = \"i32\"\n" +
                     "                name = \"a\"\n" +
                     "                start_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 16\n" +
                     "                }\n" +
                     "                end_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 21\n" +
                     "                }\n" +
                     "              },\n" +
                     "              {\n" +
                     "                id = -2\n" +
                     "                type = \"i32\"\n" +
                     "                name = \"b\"\n" +
                     "                start_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 23\n" +
                     "                }\n" +
                     "                end_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 28\n" +
                     "                }\n" +
                     "              }\n" +
                     "            ]\n" +
                     "            exceptions = [\n" +
                     "              {\n" +
                     "                id = -1\n" +
                     "                type = \"AutoId\"\n" +
                     "                name = \"auto1\"\n" +
                     "                start_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 38\n" +
                     "                }\n" +
                     "                end_pos = {\n" +
                     "                  line_no = 9\n" +
                     "                  line_pos = 50\n" +
                     "                }\n" +
                     "              }\n" +
                     "            ]\n" +
                     "            start_pos = {\n" +
                     "              line_no = 9\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 9\n" +
                     "              line_pos = 51\n" +
                     "            }\n" +
                     "          }\n" +
                     "        ]\n" +
                     "        start_pos = {\n" +
                     "          line_no = 8\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "        end_pos = {\n" +
                     "          line_no = 10\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "      }\n" +
                     "    }\n" +
                     "  ]\n" +
                     "}", debugString(program));
    }

    @Test
    public void testAutoValue() throws IOException {
        copyResourceTo("/parser/tests/autovalue.thrift", tmp.getRoot());

        File autovalue = new File(tmp.getRoot(), "autovalue.thrift");

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(autovalue), autovalue, new TreeSet<>());

        assertEquals("{\n" +
                     "  program_name = \"autovalue\"\n" +
                     "  namespaces = {\n" +
                     "    \"java\": \"net.morimekta.test.autoid\"\n" +
                     "  }\n" +
                     "  decl = [\n" +
                     "    {\n" +
                     "      decl_enum = {\n" +
                     "        name = \"AutoValue\"\n" +
                     "        values = [\n" +
                     "          {\n" +
                     "            name = \"FIRST\"\n" +
                     "            id = 0\n" +
                     "            start_pos = {\n" +
                     "              line_no = 4\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 4\n" +
                     "              line_pos = 10\n" +
                     "            }\n" +
                     "          },\n" +
                     "          {\n" +
                     "            name = \"SECOND\"\n" +
                     "            id = 1\n" +
                     "            start_pos = {\n" +
                     "              line_no = 5\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 5\n" +
                     "              line_pos = 11\n" +
                     "            }\n" +
                     "          },\n" +
                     "          {\n" +
                     "            name = \"THIRD\"\n" +
                     "            id = 2\n" +
                     "            start_pos = {\n" +
                     "              line_no = 6\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 6\n" +
                     "              line_pos = 10\n" +
                     "            }\n" +
                     "          }\n" +
                     "        ]\n" +
                     "        start_pos = {\n" +
                     "          line_no = 3\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "        end_pos = {\n" +
                     "          line_no = 7\n" +
                     "          line_pos = 2\n" +
                     "        }\n" +
                     "      }\n" +
                     "    },\n" +
                     "    {\n" +
                     "      decl_enum = {\n" +
                     "        name = \"PartialAutoValue\"\n" +
                     "        values = [\n" +
                     "          {\n" +
                     "            name = \"FIRST\"\n" +
                     "            id = 5\n" +
                     "            start_pos = {\n" +
                     "              line_no = 10\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 10\n" +
                     "              line_pos = 14\n" +
                     "            }\n" +
                     "          },\n" +
                     "          {\n" +
                     "            name = \"SECOND\"\n" +
                     "            id = 6\n" +
                     "            start_pos = {\n" +
                     "              line_no = 11\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 11\n" +
                     "              line_pos = 11\n" +
                     "            }\n" +
                     "          },\n" +
                     "          {\n" +
                     "            name = \"THIRD\"\n" +
                     "            id = 7\n" +
                     "            start_pos = {\n" +
                     "              line_no = 12\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 12\n" +
                     "              line_pos = 10\n" +
                     "            }\n" +
                     "          }\n" +
                     "        ]\n" +
                     "        start_pos = {\n" +
                     "          line_no = 9\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "        end_pos = {\n" +
                     "          line_no = 13\n" +
                     "          line_pos = 2\n" +
                     "        }\n" +
                     "      }\n" +
                     "    }\n" +
                     "  ]\n" +
                     "}", debugString(program));
    }


    @Test
    public void testParseExceptions() {
        copyResourceTo("/failure/conflicting_field_name.thrift", tmp.getRoot());
        copyResourceTo("/failure/reserved_field_name.thrift", tmp.getRoot());
        copyResourceTo("/failure/duplicate_field_id.thrift", tmp.getRoot());
        copyResourceTo("/failure/duplicate_field_name.thrift", tmp.getRoot());
        copyResourceTo("/failure/invalid_namespace.thrift", tmp.getRoot());
        copyResourceTo("/failure/invalid_include.thrift", tmp.getRoot());
        copyResourceTo("/failure/valid_reference.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_include.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_program.thrift", tmp.getRoot());
        copyResourceTo("/failure/unknown_type.thrift", tmp.getRoot());
        copyResourceTo("/failure/valid_reference.thrift", tmp.getRoot());

        assertBadThrift("Error in conflicting_field_name.thrift on line 5, pos 10: Field separatedName has field with conflicting name in T\n" +
                        "  2: i32 separatedName;\n" +
                        "---------^^^^^^^^^^^^^",
                        "conflicting_field_name.thrift");
        assertBadThrift("Error in reserved_field_name.thrift on line 5, pos 10: Field with reserved name: string\n" +
                        "  2: i32 string;\n" +
                        "---------^^^^^^",
                        "reserved_field_name.thrift");
        assertBadThrift("Error in duplicate_field_id.thrift on line 6, pos 3: Field id 1 already exists in T\n" +
                        "  1: i32 second;\n" +
                        "--^",
                        "duplicate_field_id.thrift");
        assertBadThrift("Error in duplicate_field_name.thrift on line 5, pos 10: Field first already exists in T\n" +
                        "  2: i32 first;\n" +
                        "---------^^^^^",
                        "duplicate_field_name.thrift");
        assertBadThrift("Error in invalid_namespace.thrift on line 1, pos 16: Identifier with double '.'\n" +
                        "namespace java org.apache..test.failure\n" +
                        "---------------^^^^^^^^^^^^",
                        "invalid_namespace.thrift");
        // assertBadThrift("Unknown Type 'i128'",
        //                 "/failure/unknown_type.thrift");
        assertBadThrift("Error in invalid_include.thrift on line 8, pos 1: Unexpected token 'include', expected type declaration\n" +
                        "include \"valid_reference.thrift\"\n" +
                        "^^^^^^^",
                        "invalid_include.thrift");
        assertBadThrift("Error in unknown_program.thrift on line 4, pos 6: Unknown program 'valid_reference' for type valid_reference.Message\n" +
                        "  1: valid_reference.Message message;\n" +
                        "-----^^^^^^^^^^^^^^^^^^^^^^^",
                        "unknown_program.thrift");
        assertBadThrift("Error in unknown_include.thrift on line 3, pos 9: Included file not found no_such_file.thrift\n" +
                        "include \"no_such_file.thrift\"\n" +
                        "--------^^^^^^^^^^^^^^^^^^^^^",
                        "unknown_include.thrift");
    }

    @Test
    public void testParseStrictExceptions() {
        copyResourceTo("/parser/calculator/calculator.thrift", tmp.getRoot());
        copyResourceTo("/parser/calculator/number.thrift", tmp.getRoot());
        copyResourceTo("/failure/reserved_field_name.thrift", tmp.getRoot());

        assertBadStrictThrift("Error in calculator.thrift on line 14, pos 8: Missing enum value in strict declaration\n" +
                              "    ADD,\n" +
                              "-------^",
                              "calculator.thrift");
        assertBadStrictThrift("Error in reserved_field_name.thrift on line 4, pos 10: Field with reserved name: global\n" +
                              "  1: i32 global;\n" +
                              "---------^^^^^^",
                              "reserved_field_name.thrift");
    }

    @Test
    public void testRegression() throws IOException {
        File regressed = copyResourceTo("/idl/backend-libraries/common_fileserver.thrift", tmp.getRoot());

        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramType program = parser.parse(new FileInputStream(regressed), regressed, new TreeSet<>());

        assertEquals("{\n" +
                     "  program_name = \"common_fileserver\"\n" +
                     "  decl = [\n" +
                     "    {\n" +
                     "      decl_message = {\n" +
                     "        name = \"FileServerConfig\"\n" +
                     "        fields = [\n" +
                     "          {\n" +
                     "            documentation = \"Configuration for the /download… URL.\\nSee https://wiki.trd.zedge.net/operations:cdn:cloudflare\"\n" +
                     "            id = 3\n" +
                     "            requirement = OPTIONAL\n" +
                     "            type = \"i32\"\n" +
                     "            name = \"download_url\"\n" +
                     "            start_pos = {\n" +
                     "              line_no = 6\n" +
                     "              line_pos = 5\n" +
                     "            }\n" +
                     "            end_pos = {\n" +
                     "              line_no = 6\n" +
                     "              line_pos = 33\n" +
                     "            }\n" +
                     "          }\n" +
                     "        ]\n" +
                     "        start_pos = {\n" +
                     "          line_no = 1\n" +
                     "          line_pos = 1\n" +
                     "        }\n" +
                     "        end_pos = {\n" +
                     "          line_no = 7\n" +
                     "          line_pos = 2\n" +
                     "        }\n" +
                     "      }\n" +
                     "    }\n" +
                     "  ]\n" +
                     "}", debugString(program));
    }

    private void assertBadThrift(String message, String fileName) {
        try {
            ThriftProgramParser parser = new ThriftProgramParser();
            File file = new File(tmp.getRoot(), fileName);
            parser.parse(new FileInputStream(file), file, new TreeSet<>());
            fail("No exception on bad thrift: " + fileName);
        } catch (TokenizerException e) {
            assertThat(e.asString().replaceAll("\\r", ""), is(message));
        } catch (IOException e) {
            assertThat(e.getMessage(), is(message));
        }
    }

    private void assertBadStrictThrift(String message, String fileName) {
        try {
            ThriftProgramParser parser = new ThriftProgramParser(true, true, false);
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
