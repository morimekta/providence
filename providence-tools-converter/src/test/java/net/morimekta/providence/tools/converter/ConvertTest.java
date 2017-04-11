package net.morimekta.providence.tools.converter;

import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.getResourceAsBytes;
import static net.morimekta.testing.ResourceUtils.getResourceAsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test the providence converter (pvd) command.
 */
public class ConvertTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher()
            .setTerminalSize(40, 100);

    private int     exitCode;
    private Convert convert;
    private String  version;
    private File    rc;

    @Before
    public void setUp() throws IOException {
        version = Utils.getVersionString();

        copyResourceTo("/cont.thrift", temp.getRoot());
        rc = copyResourceTo("/pvdrc", temp.getRoot());

        exitCode = 0;
        convert = new Convert(console.tty()) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @Test
    public void testHelp() {
        convert.run("--help");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
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

    @Test
    public void testStream_BinaryToJson() throws IOException {
        console.setInput(getResourceAsBytes("/binary.data"));

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary",
                "-o", "pretty_json",
                "cont.Containers");

        String tmp = getResourceAsString("/pretty.json");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(tmp));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testStream_JsonToBinary() throws IOException {
        console.setInput(getResourceAsBytes("/pretty.json"));

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "pretty_json",
                "-o", "binary",
                "cont.Containers");

        String tmp = getResourceAsString("/binary.data");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(tmp));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testFileToPretty() throws IOException {
        String input = copyResourceTo("/binary.data", temp.getRoot())
                .getCanonicalFile().getAbsolutePath();

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary,file:" + input,
                "-o", "pretty_json",
                "cont.Containers");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(getResourceAsString("/pretty.json")));
    }
}
