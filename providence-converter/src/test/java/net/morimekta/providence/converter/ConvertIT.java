package net.morimekta.providence.converter;

import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Test the providence converter (pvd) command.
 */
public class ConvertIT {
    public TemporaryFolder temp;

    private IntegrationExecutor convert;

    @Before
    public void setUp() throws IOException {
        temp = new TemporaryFolder();
        temp.create();

        File thriftFile = temp.newFile("cont.thrift");
        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/cont.thrift"), file);
        file.flush();
        file.close();

        convert = new IntegrationExecutor("providence-converter", "providence-converter.jar");
        // 1 second deadline.
        convert.setDeadlineMs(1000L);
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    @Test
    @Ignore("See issue #9 in morimekta/utils: Output is too large for 1 pipe buffer, and process locks on IO.")
    public void testStream_BinaryToJson() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/binary.data")) {
            IOUtils.copy(in, tmp);
        }
        convert.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        int exitCode = convert.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary",
                "-o", "pretty_json",
                "cont.Containers");

        tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/pretty.json")) {
            IOUtils.copy(in, tmp);
        }

        assertEquals("", convert.getError());
        assertEquals(tmp.toString(), convert.getOutput());
        assertEquals(0, exitCode);
    }

    @Test
    public void testStream_JsonToBinary() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/pretty.json")) {
            IOUtils.copy(in, tmp);
        }
        convert.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        int exitCode = convert.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "pretty_json",
                "-o", "binary",
                "cont.Containers");

        tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/binary.data")) {
            IOUtils.copy(in, tmp);
        }

        assertEquals("", convert.getError());
        assertEquals(tmp.toString(), convert.getOutput());
        assertEquals(0, exitCode);
    }
}
