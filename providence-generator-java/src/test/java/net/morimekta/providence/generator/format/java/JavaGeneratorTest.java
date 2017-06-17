package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
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
import java.util.LinkedList;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;

/**
 * Note: This test mainly just runs the same test generation that is also
 * done in /providence-testing. Testing of the output is done there, this
 * just assures no exception is thrown, and generates correct coverage.
 */
public class JavaGeneratorTest {
    @Rule
    public  TemporaryFolder tmp = new TemporaryFolder();

    private GeneratorOptions     generatorOptions;
    private FileManager          fileManager;
    private ProgramRegistry      programRegistry;
    private LinkedList<CProgram> programs;
    private TypeLoader           typeLoader;

    @Before
    public void setUp() throws IOException, ParseException {
        File out = tmp.newFolder("out");

        generatorOptions = new GeneratorOptions();
        generatorOptions.program_version = "0.1-SNAPSHOT";
        generatorOptions.generator_program_name = "providence-generator-java/test";

        fileManager = new FileManager(out);
        ProgramParser parser = new ThriftProgramParser();
        programRegistry = new ProgramRegistry();
        typeLoader = new TypeLoader(ImmutableList.of(), parser);
        programs = new LinkedList<>();
    }

    private void defaultSources() throws IOException, ParseException {
        File src = tmp.newFolder("src");
        for (String res : ImmutableList.of(
                "/providence/number.thrift",
                "/providence/calculator.thrift",
                "/providence/providence.thrift",
                "/providence/service.thrift")) {
            CProgram program = typeLoader.load(copyResourceTo(res, src));
            programRegistry.putDocument(res, program);
            programs.add(program);
        }
    }

    @Test
    public void testGenerate_defaults() throws GeneratorException, IOException {
        defaultSources();

        JavaOptions options = new JavaOptions();
        Generator generator = new JavaGenerator(fileManager, programRegistry, generatorOptions, options);
        for (CProgram program : programs) {
            generator.generate(program);
        }
    }

    @Test
    public void testGenerate_android() throws GeneratorException, IOException {
        defaultSources();

        JavaOptions options = new JavaOptions();
        options.android = true;
        Generator generator = new JavaGenerator(fileManager, programRegistry, generatorOptions, options);
        for (CProgram program : programs) {
            generator.generate(program);
        }
    }

    @Test
    public void testGenerate_jackson() throws GeneratorException, IOException {
        defaultSources();

        JavaOptions options = new JavaOptions();
        options.jackson = true;
        Generator generator = new JavaGenerator(fileManager, programRegistry, generatorOptions, options);
        for (CProgram program : programs) {
            generator.generate(program);
        }
    }

    @Test
    public void testHazelcast() throws IOException {
        File src = tmp.newFolder("hz");
        for (String res : ImmutableList.of(
                "/hazelcast/hazelcast.thrift")) {
            CProgram program = typeLoader.load(copyResourceTo(res, src));
            programRegistry.putDocument(res, program);
            programs.add(program);
        }

        JavaOptions options = new JavaOptions();
        options.hazelcast_portable = true;
        Generator generator = new JavaGenerator(fileManager, programRegistry, generatorOptions, options);
        for (CProgram program : programs) {
            generator.generate(program);
        }
    }
}
