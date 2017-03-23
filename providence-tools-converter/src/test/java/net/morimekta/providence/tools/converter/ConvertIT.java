package net.morimekta.providence.tools.converter;

import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.testing.ResourceUtils;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private IntegrationExecutor convert;
    private File rc;

    @Before
    public void setUp() throws IOException {
        rc = ResourceUtils.copyResourceTo("/pvdrc", temp.getRoot());

        File thriftFile = temp.newFile("cont.thrift");
        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/cont.thrift"), file);
        file.flush();
        file.close();

        convert = new IntegrationExecutor("providence-tools-converter", "providence-tools-converter.jar");
        // 2 second deadline.
        convert.setDeadlineMs(2000L);
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    @Test
    public void testStream_BinaryToJson() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/binary.data")) {
            IOUtils.copy(in, tmp);
        }
        convert.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        int exitCode = convert.run(
                "--rc", rc.getAbsolutePath(),
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

        try {
            int exitCode = convert.run(
                    "--rc", rc.getAbsolutePath(),
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

        } catch (IOException e) {
            System.err.println(convert.getError());
        }
    }
}
