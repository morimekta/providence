package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramRegistry;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 6/12/16.
 */
public class JavaGeneratorTest {
    @Rule
    public  TemporaryFolder tmp = new TemporaryFolder();

    private File            out;
    private TypeLoader      typeLoader;
    private JavaGenerator   generator;

    @Before
    public void setUp() throws IOException {
        out = tmp.newFolder("out");

        JavaOptions options = new JavaOptions();
        File inc = tmp.newFolder("includes");
        FileManager fileManager = new FileManager(out);
        ProgramParser parser = new ThriftProgramParser();
        ProgramRegistry programRegistry = new ProgramRegistry();

        typeLoader = new TypeLoader(ImmutableList.of(inc), parser);
        generator = new JavaGenerator(fileManager, programRegistry, options);
    }

    @Test
    public void testGenerate() throws GeneratorException, IOException, ParseException {
        copyResourceTo("/net/morimekta/providence/generator/format/java/test.thrift", tmp.getRoot());
        File file = new File(tmp.getRoot(), "test.thrift");

        generator.generate(typeLoader.load(file));

        File test = new File(out, "net/morimekta/test/java/Test.java");
        assertTrue(test.exists());
    }
}
