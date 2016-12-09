package net.morimekta.providence.tools.compiler;

import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
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
    private TemporaryFolder temp;

    private IntegrationExecutor compiler;
    private File     thriftFile;
    private File     refFile;
    private File     include;
    private File     output;

    @Before
    public void setUp() throws IOException {
        temp = new TemporaryFolder();
        temp.create();

        include = temp.newFolder("include");
        output = temp.newFolder("output");

        refFile = new File(include, "ref.thrift");
        thriftFile = temp.newFile("test.thrift");

        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/compiler/test.thrift"), file);
        file.flush();
        file.close();

        file = new FileOutputStream(refFile);
        IOUtils.copy(getClass().getResourceAsStream("/compiler/ref.thrift"), file);
        file.flush();
        file.close();

        compiler = new IntegrationExecutor("providence-tools-compiler", "providence-tools-compiler.jar");
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    @Test
    public void testCompile() throws IOException {
        int exitCode = compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                thriftFile.getAbsolutePath());

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
                "--out", output.getAbsolutePath(),
                "-g", "java",
                refFile.getAbsolutePath(),
                thriftFile.getAbsolutePath());

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
    public void testCompile_missingInclude() throws IOException {
        int exitCode = compiler.run(
                "--out", output.getAbsolutePath(),
                "-g", "java",
                thriftFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("No such package \"ref\" exists for type \"Response\"\n", compiler.getError());
        assertEquals(1, exitCode);
    }

    @Test
    public void testCompile_badReference() throws IOException {
        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/compiler/test_2.thrift"), file);
        file.flush();
        file.close();

        int exitCode = compiler.run(
                "-I", include.getAbsolutePath(),
                "--out", output.getAbsolutePath(),
                "-g", "java",
                thriftFile.getAbsolutePath());

        assertEquals("", compiler.getOutput());
        assertEquals("No such type \"Request2\" in package \"ref\"\n", compiler.getError());
        assertEquals(1, exitCode);
    }
}
