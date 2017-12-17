package net.morimekta.providence.generator;

import net.morimekta.providence.generator.util.FakeFileManager;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Created by morimekta on 23.04.17.
 */
public class GeneratorTest {
    @Test
    public void testGenerator() {
        FakeFileManager fileManager = new FakeFileManager(new File("/"));
        TestGenerator generator = new TestGenerator(fileManager);

        assertThat(generator.getFileManager(), is(sameInstance(fileManager)));
    }

    @Test
    public void testGeneratorException() {
        GeneratorException e = new GeneratorException("a");
        assertThat(e.getMessage(), is("a"));

        GeneratorException ex = new GeneratorException("b", e);
        assertThat(ex.getMessage(), is("b"));
        assertThat(ex.getCause(), is(sameInstance(e)));
    }

    private static class TestGenerator extends Generator {
        public TestGenerator(FileManager manager) {
            super(manager);
        }

        @Override
        public void generate(ProgramTypeRegistry document) throws GeneratorException {
            // ignore
        }
    }
}
