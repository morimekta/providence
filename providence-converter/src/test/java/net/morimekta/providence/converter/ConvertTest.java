package net.morimekta.providence.converter;

import net.morimekta.console.util.TerminalSize;
import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test the providence converter (pvd) command.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TerminalSize.class)
public class ConvertTest {
    private static InputStream defaultIn;
    private static PrintStream defaultOut;
    private static PrintStream defaultErr;

    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private int     exitCode;
    private Convert convert;
    private File    thriftFile;
    private String  version;

    @BeforeClass
    public static void setUpIO() {
        defaultIn = System.in;
        defaultOut = System.out;
        defaultErr = System.err;
    }

    @Before
    public void setUp() throws IOException {
        mockStatic(TerminalSize.class);
        when(TerminalSize.get()).thenReturn(new TerminalSize(40, 100));
        when(TerminalSize.isInteractive()).thenReturn(true);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/build.properties"));
        version = properties.getProperty("build.version");

        temp = new TemporaryFolder();
        temp.create();
        thriftFile = temp.newFile("cont.thrift");

        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/cont.thrift"), file);
        file.flush();
        file.close();

        exitCode = 0;

        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        convert = new Convert() {
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
        convert.run("--help");

        assertEquals(
                "Providence Converter - v" + version + "\n" +
                "Usage: pvd [-i spec] [-o spec] [-I dir] [-S] type\n" +
                "\n" +
                "Example code to run:\n" +
                "$ cat call.json | pvd -I thrift/ -s cal.Calculator\n" +
                "$ pvd -i binary,file:my.data -f json_protocol -I thrift/ -s cal.Calculator\n" +
                "\n" +
                " --include (-I) dir : Include from directories. (default: ${PWD})\n" +
                " --in (-i) spec     : Input specification\n" +
                " --out (-o) spec    : Output specification\n" +
                " --strict (-S)      : Read incoming messages strictly.\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " type               : Qualified identifier name from definitions to use for parsing source file.\n" +
                "\n" +
                "Available formats are:\n" +
                " - json                 : Readable JSON with ID enums.\n" +
                " - named_json           : Compact JSON with all named entities.\n" +
                " - pretty_json          : Prettified named json output (multiline).\n" +
                " - binary               : Compact binary_protocol serialization.\n" +
                " - fast_binary          : Fast binary protocol based on proto format\n" +
                " - pretty               : Debug format that allows comments with an easy to read syntax.\n" +
                " - json_protocol        : TJsonProtocol\n" +
                " - binary_protocol      : TBinaryProtocol\n" +
                " - compact_protocol     : TCompactProtocol\n" +
                " - tuple_protocol       : TTupleProtocol\n",
                outContent.toString());
        assertEquals("", errContent.toString());
        assertEquals(0, exitCode);
    }
}
