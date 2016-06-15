package net.morimekta.providence.generator.format.json;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.reflect.util.TypeRegistry;
import net.morimekta.util.io.IOUtils;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 6/12/16.
 */
public class JsonGeneratorTest {
    @Rule
    public  TemporaryFolder tmp;
    private FileManager     fileManager;
    private File            out;
    private TypeLoader      typeLoader;
    private File            inc;
    private Parser          parser;
    private File file;
    private TypeRegistry typeRegistry;

    @Before
    public void setUp() throws IOException {
        tmp = new TemporaryFolder();
        tmp.create();

        file = tmp.newFile("test.thrift");

        try (FileOutputStream fos = new FileOutputStream(file, false);
             InputStream in = getClass().getResourceAsStream("/net/morimekta/providence/generator/format/json/test.thrift")) {
            IOUtils.copy(in, fos);
        }

        out = tmp.newFolder("out");
        inc = tmp.newFolder("includes");

        fileManager = new FileManager(out);
        parser = new ThriftParser();
        typeLoader = new TypeLoader(ImmutableList.of(inc), parser);
        typeRegistry = new TypeRegistry();
    }

    @Test
    public void testGenerate() throws GeneratorException, IOException, ParseException {
        JsonGenerator generator = new JsonGenerator(fileManager, typeLoader);

        generator.generate(typeLoader.load(file));

        File test = new File(out, "test.json");
        assertTrue(test.exists());

        assertEquals("{\n" +
                     "    \"package\": \"test\",\n" +
                     "    \"namespaces\": {\n" +
                     "        \"java\": \"net.morimekta.test.json\"\n" +
                     "    },\n" +
                     "    \"decl\": [\n" +
                     "        {\n" +
                     "            \"decl_struct\": {\n" +
                     "                \"name\": \"Test\",\n" +
                     "                \"fields\": [\n" +
                     "                    {\n" +
                     "                        \"key\": 65535,\n" +
                     "                        \"requirement\": \"REQUIRED\",\n" +
                     "                        \"type\": \"i32\",\n" +
                     "                        \"name\": \"test\"\n" +
                     "                    },\n" +
                     "                    {\n" +
                     "                        \"key\": 65534,\n" +
                     "                        \"requirement\": \"OPTIONAL\",\n" +
                     "                        \"type\": \"i32\",\n" +
                     "                        \"name\": \"another\"\n" +
                     "                    }\n" +
                     "                ]\n" +
                     "            }\n" +
                     "        }\n" +
                     "    ]\n" +
                     "}\n",
                     IOUtils.readString(new FileInputStream(test)));
    }

}
