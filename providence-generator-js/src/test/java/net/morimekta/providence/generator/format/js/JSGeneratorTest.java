package net.morimekta.providence.generator.format.js;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.js.utils.WrappedReader;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.testing.ProvidenceMatchers;
import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
import net.morimekta.test.providence.testing.CompactFields;
import net.morimekta.test.providence.testing.Containers;
import net.morimekta.test.providence.testing.OptionalFields;
import net.morimekta.test.providence.testing.RequiredFields;
import net.morimekta.test.providence.testing.number.Imaginary;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JSGeneratorTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public SimpleGeneratorWatcher generator = SimpleGeneratorWatcher.create();

    private GeneratorOptions    generatorOptions;
    private FileManager         fileManager;
    private ProgramRegistry     programRegistry;
    private ArrayList<CProgram> programs;
    private TypeLoader          typeLoader;
    private File                out;
    private ScriptEngine        engine;
    private JSOptions           options;

    @Before
    public void setUp() throws IOException {
        // tmp.create();
        out = tmp.newFolder("out");

        generatorOptions = new GeneratorOptions();
        generatorOptions.program_version = "0.1-SNAPSHOT";
        generatorOptions.generator_program_name = "providence-generator-js/test";

        fileManager = new FileManager(out);
        typeLoader = new TypeLoader(ImmutableList.of());
        programRegistry = typeLoader.getProgramRegistry();
        programs = new ArrayList<>();

        generator.setRandom(new Random() {
            @Override
            public long nextLong() {
                return super.nextInt();
            }
        });

        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        options = new JSOptions();
    }

    @Test
    public void testGenerate_es6() throws GeneratorException, IOException, ScriptException {
        load("/js/es6-shim.js");
        generateAndLoadSources();
        assertWorkingJavascript();
    }

    @Test
    public void testGenerate_es51() throws GeneratorException, IOException, ScriptException {
        options.es51 = true;
        generateAndLoadSources();
        assertWorkingJavascript();
    }

    @Test
    public void testGenerate_closure() throws GeneratorException, IOException, ScriptException {
        load("/js/es6-shim.js");
        load("/js/closure.js");

        options.closure = true;
        generateAndLoadSources();
        assertWorkingJavascript();
    }

    @Test
    public void testGenerate_node_js() throws GeneratorException, IOException, ScriptException {
        load("/js/es6-shim.js");
        load("/js/node.js");

        options.node_js = true;
        generateSources("/number.thrift", "/calculator.thrift", "/providence.thrift", "/service.thrift");
        loadModule("pvd.testing.number",     new File(out, "pvd/testing/number.js"));
        loadModule("pvd.testing.calculator", new File(out, "pvd/testing/calculator.js"));
        loadModule("pvd.testing.providence", new File(out, "pvd/testing/providence.js"));
        loadModule("pvd.testing.service",    new File(out, "pvd/testing/service.js"));

        engine.eval("var number     = node.registry['pvd.testing.number'];");
        engine.eval("var calculator = node.registry['pvd.testing.calculator'];");
        engine.eval("var providence = node.registry['pvd.testing.providence'];");
        engine.eval("var service    = node.registry['pvd.testing.service'];");

        assertWorkingJavascript();
    }

    private void assertWorkingJavascript() throws ScriptException, IOException {
        assertRoundTrip(Imaginary.kDescriptor);
        assertRoundTrip(CompactFields.kDescriptor);
        assertRoundTrip(OptionalFields.kDescriptor);
        assertRoundTrip(RequiredFields.kDescriptor);
        assertRoundTrip(Containers.kDescriptor);
        // TODO: There seems to be a named vs non-named bug when serializing... Unknown where.
    }

    private <M extends PMessage<M,F>, F extends PField> void assertRoundTrip(PMessageDescriptor<M,F> descriptor)
            throws IOException, ScriptException {
        M expected = generator.generate(descriptor);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JsonSerializer().named().serialize(out, expected);

        String inputJson = new String(out.toByteArray(), StandardCharsets.UTF_8);
        Object o = engine.eval("new " + descriptor.getQualifiedName() + "('" +
                               inputJson + "').toJsonString(true);");
        String outputJson = String.valueOf(o);
        M actual = new JsonSerializer().deserialize(new ByteArrayInputStream(outputJson.getBytes(StandardCharsets.UTF_8)), descriptor);

        // System.err.println("in:  " + inputJson);
        // System.err.println("out: " + outputJson);

        assertThat(actual, is(ProvidenceMatchers.equalToMessage(expected)));
    }

    private void generateSources(String... sources) throws IOException {
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

    private void generateAndLoadSources() throws IOException, ScriptException {
        generateSources("/number.thrift", "/calculator.thrift", "/providence.thrift", "/service.thrift");
        load(new File(out, "pvd/testing/number.js"));
        load(new File(out, "pvd/testing/calculator.js"));
        load(new File(out, "pvd/testing/providence.js"));
        load(new File(out, "pvd/testing/service.js"));

        engine.eval("var number     = pvd.testing.number;");
        engine.eval("var calculator = pvd.testing.calculator;");
        engine.eval("var providence = pvd.testing.providence;");
        engine.eval("var service    = pvd.testing.service;");
    }

    private void loadModule(String module, File file) throws ScriptException, IOException {
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)));
             WrappedReader wrapped = new WrappedReader("node.module('" + module + "', function(exports, require, module, __filename, __dirname) {\n",
                                                       reader,
                                                       "\n});")) {

            engine.eval(wrapped);
        }
    }

    private void load(String resource) throws IOException, ScriptException {
        try (Reader reader = new InputStreamReader(new BufferedInputStream(getClass().getResourceAsStream(resource)))) {
            engine.eval(reader);
        }
    }

    private void load(File file) throws IOException, ScriptException {
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)))) {
            engine.eval(reader);
        }
    }
}
