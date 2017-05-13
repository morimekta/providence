package net.morimekta.providence.tools.compiler;

import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 4/26/16.
 */
public class CompilerJavaTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    private int      exitCode;
    private Compiler compiler;
    private File     testFile;
    private File     refFile;
    private File     include;
    private File     output;

    @Before
    public void setUp() throws IOException {
        include = temp.newFolder("include");
        output = temp.newFolder("output");

        refFile = copyResourceTo("/compiler/ref.thrift", include);
        testFile = copyResourceTo("/compiler/test.thrift", temp.getRoot());

        exitCode = 0;
        compiler = new Compiler(console.tty()) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @Test
    public void testCompile() throws IOException {
        compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(""));
        assertThat(exitCode, is(0));

        // It generated the file in test.thrift.
        File service = new File(output, "net/morimekta/test/compiler/MyService.java");

        assertTrue(service.exists());
        assertTrue(service.isFile());

        // And not the one in ref.thrift
        File failure = new File(output, "net/morimekta/test/compiler_ref/Failure.java");

        assertFalse(failure.exists());
    }

    @Test
    public void testCompile_2() throws IOException {
        compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                refFile.getAbsolutePath(),
                testFile.getAbsolutePath());

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(""));
        assertThat(exitCode, is(0));

        // It generated the file in test.thrift.
        File service = new File(output, "net/morimekta/test/compiler/MyService.java");

        assertTrue(service.exists());
        assertTrue(service.isFile());

        // And not the one in ref.thrift
        File failure = new File(output, "net/morimekta/test/compiler_ref/Failure.java");

        assertTrue(failure.exists());
    }

    @Test
    public void testCompile_hazelcast() throws IOException {
        File hz = copyResourceTo("/compiler/hz.thrift", temp.getRoot());

        compiler.run(
                "--out", output.getAbsolutePath(),
                "-g", "java:hazelcast_portable",
                hz.getAbsolutePath());

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(""));
        assertThat(exitCode, is(0));

        // It generated the file in test.thrift.
        File service = new File(output, "net/morimekta/test/compiler/hz/OptionalListFields.java");
        assertTrue(service.exists());
        assertTrue(service.isFile());

        // It generated the file in test.thrift.
        File factory = new File(output, "net/morimekta/test/compiler/hz/Hz_Factory.java");
        assertTrue(factory.exists());
        assertTrue(factory.isFile());
    }

    @Test
    public void testCompile_missingInclude() throws IOException {
        compiler.run(
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Error on line 3, pos 9: Included file not found ref.thrift\n" +
                "include \"ref.thrift\"\n" +
                "--------^^^^^^^^^^^^\n"));
        assertThat(exitCode, is(1));
    }

    @Test
    public void testCompile_badReference() throws IOException {
        File test2 = copyResourceTo("/compiler/test_2.thrift", temp.getRoot());

        compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                test2.getAbsolutePath());

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "No such type \"Request2\" in package \"ref\"\n"));
        assertThat(exitCode, is(1));
    }
}
