package net.morimekta.providence.tools.compiler;

import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.testing.ResourceUtils;
import net.morimekta.util.io.IOUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 4/26/16.
 */
public class CompilerIT {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private IntegrationExecutor compiler;
    private File                testFile;
    private File                refFile;
    private File                include;
    private File                output;

    @Before
    public void setUp() throws IOException {
        include = temp.newFolder("include");
        output = temp.newFolder("output");

        ResourceUtils.copyResourceTo("/compiler/ref.thrift", include);
        ResourceUtils.copyResourceTo("/compiler/test.thrift", temp.getRoot());

        refFile = new File(include, "ref.thrift");
        testFile = new File(temp.getRoot(), "test.thrift");

        compiler = new IntegrationExecutor("providence-tools-compiler", "providence-tools-compiler.jar");
    }

    @Test
    public void testCompile() throws IOException {
        int exitCode = compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("", compiler.getError());
        assertEquals(0, exitCode);

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
        int exitCode = compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                refFile.getAbsolutePath(),
                testFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("", compiler.getError());
        assertEquals(0, exitCode);

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
        File hz = temp.newFile("hz.thrift");
        FileOutputStream fos = new FileOutputStream(hz);
        IOUtils.copy(getClass().getResourceAsStream("/compiler/hz.thrift"), fos);
        fos.flush();
        fos.close();

        int exitCode = compiler.run(
                "--out", output.getAbsolutePath(),
                "-g", "java:hazelcast_portable",
                hz.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("", compiler.getError());
        assertEquals(0, exitCode);

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
        int exitCode = compiler.run(
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("Parse error on line 3, pos 8: Included file not found ref.thrift\n" +
                     "include \"ref.thrift\"\n" +
                     "--------^\n", compiler.getError());
        assertEquals(1, exitCode);
    }

    @Test
    public void testCompile_badReference() throws IOException {
        FileOutputStream file = new FileOutputStream(testFile);
        IOUtils.copy(getClass().getResourceAsStream("/compiler/test_2.thrift"), file);
        file.flush();
        file.close();

        int exitCode = compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                testFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("No such type \"Request2\" in package \"ref\"\n", compiler.getError());
        assertEquals(1, exitCode);
    }
}
