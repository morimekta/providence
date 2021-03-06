package net.morimekta.providence.generator.format.json;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.testing.ResourceUtils;
import net.morimekta.util.io.IOUtils;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 6/12/16.
 */
public class JsonGeneratorTest {
    @Rule
    public  TemporaryFolder tmp = new TemporaryFolder();

    private File          out;
    private TypeLoader    typeLoader;
    private JsonGenerator generator;

    @Before
    public void setUp() throws IOException {
        out = tmp.newFolder("out");

        File inc = tmp.newFolder("includes");
        FileManager fileManager = new FileManager(out);
        ProgramParser parser = new ThriftProgramParser();

        typeLoader = new TypeLoader(ImmutableList.of(inc), parser);
        generator = new JsonGenerator(fileManager);
    }

    @Test
    public void testGenerate() throws GeneratorException, IOException {
        ResourceUtils.copyResourceTo("/net/morimekta/providence/generator/format/json/test.thrift", tmp.getRoot());
        ResourceUtils.copyResourceTo("/net/morimekta/providence/generator/format/json/included.thrift", tmp.getRoot());
        File file = new File(tmp.getRoot(), "test.thrift");

        generator.generate(typeLoader.load(file));

        File test = new File(out, "test.json");
        assertTrue(test.exists());

        assertEquals("{\n" +
                     "    \"program_name\": \"test\",\n" +
                     "    \"includes\": [\n" +
                     "        \"included.json\"\n" +
                     "    ],\n" +
                     "    \"namespaces\": {\n" +
                     "        \"java\": \"net.morimekta.test.json.test\"\n" +
                     "    },\n" +
                     "    \"decl\": [\n" +
                     "        {\n" +
                     "            \"decl_message\": {\n" +
                     "                \"name\": \"Test\",\n" +
                     "                \"fields\": [\n" +
                     "                    {\n" +
                     "                        \"id\": 1,\n" +
                     "                        \"requirement\": \"REQUIRED\",\n" +
                     "                        \"type\": \"i32\",\n" +
                     "                        \"name\": \"test\",\n" +
                     "                        \"start_pos\": {\n" +
                     "                            \"line_no\": 6,\n" +
                     "                            \"line_pos\": 5\n" +
                     "                        },\n" +
                     "                        \"end_pos\": {\n" +
                     "                            \"line_no\": 6,\n" +
                     "                            \"line_pos\": 25\n" +
                     "                        }\n" +
                     "                    },\n" +
                     "                    {\n" +
                     "                        \"id\": 15,\n" +
                     "                        \"requirement\": \"OPTIONAL\",\n" +
                     "                        \"type\": \"i32\",\n" +
                     "                        \"name\": \"another\",\n" +
                     "                        \"start_pos\": {\n" +
                     "                            \"line_no\": 7,\n" +
                     "                            \"line_pos\": 5\n" +
                     "                        },\n" +
                     "                        \"end_pos\": {\n" +
                     "                            \"line_no\": 7,\n" +
                     "                            \"line_pos\": 29\n" +
                     "                        }\n" +
                     "                    },\n" +
                     "                    {\n" +
                     "                        \"id\": 2,\n" +
                     "                        \"type\": \"included.Included\",\n" +
                     "                        \"name\": \"included\",\n" +
                     "                        \"start_pos\": {\n" +
                     "                            \"line_no\": 8,\n" +
                     "                            \"line_pos\": 5\n" +
                     "                        },\n" +
                     "                        \"end_pos\": {\n" +
                     "                            \"line_no\": 8,\n" +
                     "                            \"line_pos\": 34\n" +
                     "                        }\n" +
                     "                    }\n" +
                     "                ],\n" +
                     "                \"start_pos\": {\n" +
                     "                    \"line_no\": 5,\n" +
                     "                    \"line_pos\": 1\n" +
                     "                },\n" +
                     "                \"end_pos\": {\n" +
                     "                    \"line_no\": 9,\n" +
                     "                    \"line_pos\": 2\n" +
                     "                }\n" +
                     "            }\n" +
                     "        }\n" +
                     "    ]\n" +
                     "}\n",
                     IOUtils.readString(new FileInputStream(test)));
    }

}
