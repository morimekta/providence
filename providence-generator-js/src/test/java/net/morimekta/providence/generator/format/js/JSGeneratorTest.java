package net.morimekta.providence.generator.format.js;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.js.utils.WrappedReader;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramRegistry;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Note: This test mainly just runs the same test generation that is also
 * done in /providence-testing. Testing of the output is done there, this
 * just assures no exception is thrown, and generates correct coverage.
 */
public class JSGeneratorTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private GeneratorOptions    generatorOptions;
    private FileManager         fileManager;
    private ProgramRegistry     programRegistry;
    private ArrayList<CProgram> programs;
    private TypeLoader          typeLoader;
    private File                out;

    @Before
    public void setUp() throws IOException {
        out = tmp.newFolder("out");

        generatorOptions = new GeneratorOptions();
        generatorOptions.program_version = "0.1-SNAPSHOT";
        generatorOptions.generator_program_name = "providence-generator-java/test";

        fileManager = new FileManager(out);
        typeLoader = new TypeLoader(ImmutableList.of());
        programRegistry = typeLoader.getProgramRegistry();
        programs = new ArrayList<>();
    }

    @Test
    public void testGenerate_defaults() throws GeneratorException, IOException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        JSOptions options = new JSOptions();
        generateAndLoadSources(options, engine);
        assertWorkingJavascript(engine);
    }

    @Test
    public void testGenerate_closure() throws GeneratorException, IOException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        load(engine, "/js/closure.js");

        JSOptions options = new JSOptions();
        options.closure = true;
        generateAndLoadSources(options, engine);
        assertWorkingJavascript(engine);
    }

    @Test
    public void testGenerate_node_js() throws GeneratorException, IOException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        load(engine, "/js/node.js");

        JSOptions options = new JSOptions();
        options.node_js = true;
        generateSources(options, "/number.thrift", "/calculator.thrift", "/providence.thrift", "/service.thrift");
        loadModule(engine, "pvd.testing.number",     new File(out, "pvd/testing/number.js"));
        loadModule(engine, "pvd.testing.calculator", new File(out, "pvd/testing/calculator.js"));
        loadModule(engine, "pvd.testing.providence", new File(out, "pvd/testing/providence.js"));
        loadModule(engine, "pvd.testing.service",    new File(out, "pvd/testing/service.js"));

        engine.eval("var number     = node.registry['pvd.testing.number'];");
        engine.eval("var calculator = node.registry['pvd.testing.calculator'];");
        engine.eval("var providence = node.registry['pvd.testing.providence'];");
        engine.eval("var service    = node.registry['pvd.testing.service'];");

        assertWorkingJavascript(engine);
    }

    private void assertWorkingJavascript(ScriptEngine engine) throws ScriptException {
        // TODO: Test way more...
        engine.eval("var i = new number.Imaginary('{\"1\":0.123,\"2\":-12}');");
        assertThat(engine.eval("i.toJsonString(true);"), is("{\"v\":0.123,\"i\":-12}"));
        assertThat(engine.eval("i.toJsonString();"), is("{\"1\":0.123,\"2\":-12}"));
    }

    private void generateSources(JSOptions options, String... sources) throws IOException {
        File src = tmp.newFolder("src");
        for (String res : ImmutableList.copyOf(sources)) {
            File f = copyResourceTo(res, src).getAbsoluteFile()
                                             .getCanonicalFile();
            CProgram program = typeLoader.load(f)
                                         .getProgram();
            programRegistry.putProgram(f.getPath(), program);
            programs.add(program);
        }

        for (CProgram program : programs) {
            Generator generator = new JSGenerator(fileManager,
                                                  programRegistry.registryForPath(program.getProgramFilePath()),
                                                  generatorOptions,
                                                  options);
            generator.generate(program);
        }
    }

    private void generateAndLoadSources(JSOptions options, ScriptEngine engine) throws IOException, ScriptException {
        generateSources(options, "/number.thrift", "/calculator.thrift", "/providence.thrift", "/service.thrift");
        load(engine, new File(out, "pvd/testing/number.js"));
        load(engine, new File(out, "pvd/testing/calculator.js"));
        load(engine, new File(out, "pvd/testing/providence.js"));
        load(engine, new File(out, "pvd/testing/service.js"));

        engine.eval("var number     = pvd.testing.number;");
        engine.eval("var calculator = pvd.testing.calculator;");
        engine.eval("var providence = pvd.testing.providence;");
        engine.eval("var service    = pvd.testing.service;");
    }

    private void loadModule(ScriptEngine engine, String module, File file) throws ScriptException, IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file));
             WrappedReader wrapped = new WrappedReader("node.module('" + module + "', function(exports, require, module, __filename, __dirname) {\n",
                                                       reader,
                                                       "\n});")) {

            engine.eval(wrapped);
        }
    }

    private void load(ScriptEngine engine, String resource) throws IOException, ScriptException {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(resource))) {
            engine.eval(reader);
        }
    }

    private void load(ScriptEngine engine, File file) throws IOException, ScriptException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
            engine.eval(reader);
        }
    }
}
