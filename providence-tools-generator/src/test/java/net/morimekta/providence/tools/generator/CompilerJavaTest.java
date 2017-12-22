package net.morimekta.providence.tools.generator;

import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by morimekta on 4/26/16.
 */
public class CompilerJavaTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    private int           exitCode;
    private GeneratorMain generator;
    private File          testFile;
    private File          refFile;
    private File          include;
    private File          output;

    @Before
    public void setUp() throws IOException {
        include = temp.newFolder("include");
        output = temp.newFolder("output");

        refFile = copyResourceTo("/thrift/ref.thrift", include);
        testFile = copyResourceTo("/thrift/test.thrift", temp.getRoot());

        File generator = temp.newFolder("generator");

        copyResourceTo("/generator/java.jar", generator);
        copyResourceTo("/generator/js.jar", generator);

        exitCode = 0;
        this.generator = new GeneratorMain(new GeneratorOptions(console.tty()) {
            @Override
            public File currentJarDirectory() {
                return temp.getRoot();
            }}) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @Test
    public void testCompile() throws IOException {
        generator.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(""));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testCompile_missingInclude() throws IOException {
        generator.run(
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Error in test.thrift on line 3, pos 9: Included file not found ref.thrift\n" +
                "include \"ref.thrift\"\n" +
                "--------^^^^^^^^^^^^\n"));
        assertThat(exitCode, is(1));
    }
}
