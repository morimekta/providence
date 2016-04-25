package net.morimekta.providence.converter;

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
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Test the providence converter (pvd) command.
 */
public class ConvertTest {
    @Rule
    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private int     exitCode;
    private Convert rpc;
    private File    thriftFile;

    @Before
    public void setUp() throws IOException {
        temp = new TemporaryFolder();
        temp.create();
        thriftFile = temp.newFile("test.thrift");

        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/test.thrift"), file);
        file.flush();
        file.close();

        exitCode = 0;

        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        rpc = new Convert() {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @After
    public void tearDown() {
        System.setErr(null);
        System.setOut(null);
        System.setIn(null);
    }

    @Test
    public void testHelp() {
        rpc.run("--help");

        assertEquals(
                "pvd [-i spec] [-o spec] [-I dir] [-S] type\n" +
                "\n" +
                "Example code to run:\n" +
                "$ cat call.json | pvd -I thrift/ -s cal.Calculator\n" +
                "$ pvd -i binary,file:my.data -f json_protocol -I thrift/ -s cal.Calculator\n" +
                "\n" +
                " type               : Qualified identifier name from definitions to use for parsing source file.\n" +
                " --help (-h, -?)    : This help listing. (default: true)\n" +
                " --in (-i) spec     : Input specification (default: json)\n" +
                " --include (-I) dir : Include from directories. Defaults to CWD.\n" +
                " --out (-o) spec    : Output specification (default: pretty)\n" +
                " --strict (-S)      : Read incoming messages strictly. (default: false)\n" +
                "\n" +
                "Available formats are:\n" +
                " - json                 : Readable JSON with ID enums.\n" +
                " - named_json           : Compact JSON with all named entities.\n" +
                " - pretty_json          : Prettified named json output (multiline).\n" +
                " - binary               : Compact binary_protocol serialization.\n" +
                " - fast_binary          : Fast binary protocol based on proto format\n" +
                " - json_protocol        : TJsonProtocol\n" +
                " - binary_protocol      : TBinaryProtocol\n" +
                " - compact_protocol     : TCompactProtocol\n" +
                " - tuple_protocol       : TTupleProtocol\n" +
                " - simple_json_protocol : TSimpleJSONProtocol (output only)\n" +
                " - pretty               : Pretty-Printer (output only)\n",
                outContent.toString());
        assertEquals("", errContent.toString());
        assertEquals(0, exitCode);
    }

    @Test
    public void testStream_BinaryToJson() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/binary.data")) {
            IOUtils.copy(in, tmp);
        }
        System.setIn(new ByteArrayInputStream(tmp.toByteArray()));

        rpc.run("-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary",
                "-o", "pretty_json",
                "test.Containers");

        tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/pretty.json")) {
            IOUtils.copy(in, tmp);
        }

        assertEquals("", errContent.toString());
        assertEquals(tmp.toString(), outContent.toString());
        assertEquals(0, exitCode);
    }
}
