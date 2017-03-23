package net.morimekta.providence.tools.rpc;

import net.morimekta.console.util.STTY;
import net.morimekta.console.util.TerminalSize;
import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the flag values for .
 */
public class RPCFlagsTest {
    private static InputStream defaultIn;
    private static PrintStream defaultOut;
    private static PrintStream defaultErr;

    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private int exitCode;
    private RPC rpc;
    private File thriftFile;
    private String version;
    private STTY tty;

    public String endpoint() {
        return "http://localhost:8080/test";
    }

    @BeforeClass
    public static void setUpIO() {
        defaultIn = System.in;
        defaultOut = System.out;
        defaultErr = System.err;
    }

    @Before
    public void setUp() throws IOException {
        tty = mock(STTY.class);
        when(tty.getTerminalSize()).thenReturn(new TerminalSize(40, 100));
        when(tty.isInteractive()).thenReturn(true);

        version = Utils.getVersionString();

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

        rpc = new RPC(tty) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @After
    public void tearDown() {
        System.setErr(defaultErr);
        System.setOut(defaultOut);
        System.setIn(defaultIn);

        temp.delete();
    }

    @Test
    public void testHelp() {
        rpc.run("--help");

        assertEquals(0, exitCode);
        assertEquals("", errContent.toString());
        assertThat(outContent.toString(), is(equalToLines(
                "Providence RPC Tool - " + version + "\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URL\n" +
                "\n" +
                "Example code to run:\n" +
                "$ cat call.json | pvdrpc -I thrift/ -s cal.Calculator http://localhost:8080/service\n" +
                "$ pvdrpc -i binary,file:my.data -f json_protocol -I thrift/ -s cal.Calculator http://localhost:8080/service\n" +
                "\n" +
                " --help (-h, -?)           : This help listing.\n" +
                " --verbose (-V)            : Show verbose output and error messages.\n" +
                " --version (-v)            : Show program version.\n" +
                " --rc FILE                 : Providence RC to use (default: ~/.pvdrc)\n" +
                " --include (-I) dir        : Allow includes of files in directory\n" +
                " --in (-i) spec            : Input specification (default: json)\n" +
                " --out (-o) spec           : Output Specification (default: pretty_json)\n" +
                " --service (-s) srv        : Qualified identifier name from definitions to use for parsing source\n" +
                "                             file.\n" +
                " --format (-f) fmt         : Request RPC format (default: binary)\n" +
                " --connect_timeout (-C) ms : Connection timeout in milliseconds. 0 means infinite. (default: 10000)\n" +
                " --read_timeout (-R) ms    : Request timeout in milliseconds. 0 means infinite. (default: 10000)\n" +
                " --header (-H) hdr         : Header to set on the request, K/V separated by ':'.\n" +
                " --strict (-S)             : Read incoming messages strictly.\n" +
                " URL                       : The endpoint URI\n" +
                "\n" +
                "Available formats are:\n" +
                " - json                 : Readable JSON with numeric field IDs and enums.\n" +
                " - named_json           : Compact JSON with names fields and enums.\n" +
                " - pretty_json          : Prettified named json output (multiline).\n" +
                " - binary               : Binary serialization.\n" +
                " - unversioned_binary   : Binary serialization without version spec (deprecated).\n" +
                " - fast_binary          : Fast binary protocol based on proto format.\n" +
                " - pretty               : Debug format that allows comments with an easy to read syntax.\n" +
                " - json_protocol        : TJsonProtocol\n" +
                " - binary_protocol      : TBinaryProtocol\n" +
                " - compact_protocol     : TCompactProtocol\n" +
                " - tuple_protocol       : TTupleProtocol\n")));
    }

    @Test
    public void testMissingFlags_noService() {
        rpc.run(endpoint());

        assertEquals(1, exitCode);
        assertEquals("", outContent.toString());
        assertThat(errContent.toString(), is(equalToLines(
                "Option --service is required\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URL\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }


    @Test
    public void testMissingFlags_noURL() {
        rpc.run("-s", "test.MyTest");

        assertEquals(1, exitCode);
        assertEquals("", outContent.toString());
        assertThat(errContent.toString(), is(equalToLines(
                "Argument \"URL\" is required\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URL\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testFlag_Includes_NoSuchDirectory() throws IOException {
        File dir = temp.newFolder();
        dir.delete();

        rpc.run("-I", dir.getCanonicalFile().getAbsolutePath(), "-s", "test.MyService", endpoint());

        assertEquals(1, exitCode);
        assertEquals("", outContent.toString());
        assertThat(errContent.toString(), is(equalToLines(
                "No such directory " + dir.getCanonicalFile().getAbsolutePath() + "\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URL\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }
}
