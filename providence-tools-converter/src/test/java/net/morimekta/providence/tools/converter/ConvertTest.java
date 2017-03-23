package net.morimekta.providence.tools.converter;

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

import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the providence converter (pvd) command.
 */
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
    private STTY tty;

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

        convert = new Convert(tty) {
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

        assertEquals("", errContent.toString());
        assertThat(outContent.toString(), is(equalToLines(
                "Providence Converter - " + version + "\n" +
                "Usage: pvd [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] type\n" +
                "\n" +
                "Example code to run:\n" +
                "$ cat call.json | pvd -I thrift/ -S cal.Calculator\n" +
                "$ pvd -i binary,file:my.data -o json_protocol -I thrift/ cal.Calculator\n" +
                "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --rc FILE          : Providence RC to use (default: ~/.pvdrc)\n" +
                " --include (-I) dir : Include from directories. (default: ${PWD})\n" +
                " --in (-i) spec     : Input specification (default: binary)\n" +
                " --out (-o) spec    : Output specification (default: pretty)\n" +
                " --strict (-S)      : Read incoming messages strictly.\n" +
                " type               : Qualified identifier name from definitions to use for parsing source file.\n" +
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
        assertEquals(0, exitCode);
    }
}
