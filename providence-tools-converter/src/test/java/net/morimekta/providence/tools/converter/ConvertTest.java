package net.morimekta.providence.tools.converter;

import net.morimekta.providence.tools.common.Utils;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

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
            .withTerminalSize(40, 100);

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
                "Usage: pvd [-hVvSL] [--rc FILE] [-I dir] [-i spec] [-o spec] type\n" +
                "\n" +
                "Example code to run:\n" +
                "$ cat call.json | pvd -I thrift/ -S cal.Calculator\n" +
                "$ pvd -i binary,file:my.data -o json_protocol -I thrift/ cal.Operation\n" +
                "\n" +
                "Note that when handling service calls, only 1 call can be converted.\n" +
                "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --rc FILE          : Providence RC to use (default: ~/.pvdrc)\n" +
                " --include (-I) dir : Include from directories. (default: ${PWD})\n" +
                " --in (-i) spec     : Input specification (default: json)\n" +
                " --out (-o) spec    : Output specification (default: pretty_json)\n" +
                " --strict (-S)      : Read incoming messages strictly.\n" +
                " --list-types (-L)  : List the parsed types based on the input files\n" +
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
                " - pretty_compact       : Compact format similar to toString of messages.\n" +
                " - config               : As a complete config (see providence-config)\n" +
                " - json_protocol        : TJsonProtocol\n" +
                " - binary_protocol      : TBinaryProtocol\n" +
                " - compact_protocol     : TCompactProtocol\n" +
                " - tuple_protocol       : TTupleProtocol\n")));
        assertEquals(0, exitCode);
    }

    @Test
    public void testListTypes() throws IOException {
        convert.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "--list-types",
                "cont.Containers");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                temp.getRoot().getCanonicalPath() + "/cont.thrift:\n" +
                "  struct    cont.CompactFields\n" +
                "  struct    cont.OptionalFields\n" +
                "  struct    cont.RequiredFields\n" +
                "  struct    cont.DefaultFields\n" +
                "  union     cont.UnionFields\n" +
                "  exception cont.ExceptionFields\n" +
                "  struct    cont.DefaultValues\n" +
                "  struct    cont.Containers\n" +
                "  service   cont.Conting\n")));
        assertEquals(0, exitCode);
    }

    @Test
    public void testServiceCall() {
        console.setInput(getResourceAsBytes("/call.json"));
        convert.run(
                "--verbose",
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "json",
                "-o", "pretty",
                "cont.Conting");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                "42: reply cont(\n" +
                "      success = {\n" +
                "        booleanList = [false, true]\n" +
                "        defaultValues = {\n" +
                "          byteValue = 27\n" +
                "          shortValue = 32275\n" +
                "          compactValue = {\n" +
                "            name = \"тuQȳʶ\"\n" +
                "            id = 798\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    )\n" +
                "\n")));
        assertEquals(0, exitCode);
    }

    @Test
    public void testStream_BinaryToJson() {
        console.setInput(getResourceAsBytes("/binary.data"));

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary",
                "-o", "pretty_json",
                "cont.Containers");

        String tmp = getResourceAsString("/pretty.json");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(tmp)));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testStream_JsonToBinary_empty() {
        console.setInput("{\n" +
                         "}\n");

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "json",
                "-o", "binary",
                "cont.Containers");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("\0"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testStream_BinaryToJson_empty() {
        console.setInput('\0');

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary",
                "-o", "json",
                "cont.Containers");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("{}\n"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testStream_JsonToBinary() {
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
    public void testStream_BinaryToBinary_base64() {
        byte[] data = getResourceAsBytes("/binary.data");
        console.setInput(Base64.getMimeEncoder()
                               .encodeToString(data),
                         "\n");

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary,base64mime",
                "-o", "binary,base64",
                "cont.Containers");

        assertThat(console.error(),
                   is(equalToLines("")));
        assertThat(console.output(),
                   is(equalToLines(Base64.getEncoder()
                                         .withoutPadding()
                                         .encodeToString(data))));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testStream_BinaryToBinary_base64mime() {
        byte[] data = getResourceAsBytes("/binary.data");
        console.setInput(Base64.getEncoder()
                               .withoutPadding()
                               .encodeToString(data));

        convert.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-i", "binary,base64",
                "-o", "binary,base64mime",
                "--verbose",
                "cont.Containers");

        assertThat(console.error(),
                   is(equalToLines("")));
        assertThat(console.output(),
                   is(Base64.getMimeEncoder()
                            .encodeToString(data) + System.lineSeparator()));
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

        String tmp = getResourceAsString("/pretty.json");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(tmp)));
        assertThat(exitCode, is(0));
    }
}
