package net.morimekta.providence.tools.rpc;

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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for the flag values for .
 */
public class RPCFlagsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher()
            .withTerminalSize(40, 100);

    private int exitCode;
    private RPC rpc;
    private String version;

    public String endpoint() {
        return "http://localhost:8080/test";
    }

    @Before
    public void setUp() throws IOException {
        version = Utils.getVersionString();
        copyResourceTo("/test.thrift", temp.getRoot());

        exitCode = 0;
        rpc = new RPC(console.tty()) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @Test
    public void testHelp() {
        rpc.run("--help");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                "Providence RPC Tool - " + version + "\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
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
                " URI                       : The endpoint URI\n" +
                "\n" +
                "Available formats are:\n" +
                " - json                 : Readable JSON with numeric field IDs and enums.\n" +
                " - named_json           : Compact JSON with names fields and enums.\n" +
                " - pretty_json          : Prettified named json output (multiline).\n" +
                " - binary               : Binary serialization.\n" +
                " - unversioned_binary   : Binary serialization without version spec (deprecated).\n" +
                " - fast_binary          : Fast binary protocol based on proto format.\n" +
                " - pretty               : Debug format that allows comments with an easy to read syntax.\n" +
                " - config               : As a complete config (see providence-config)\n" +
                " - json_protocol        : TJsonProtocol\n" +
                " - binary_protocol      : TBinaryProtocol\n" +
                " - compact_protocol     : TCompactProtocol\n" +
                " - tuple_protocol       : TTupleProtocol\n")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testMissingFlags_noService() {
        rpc.run(endpoint());

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "Option --service is required\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testMissingFlags_noURL() {
        rpc.run("-s", "test.MyTest");

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "Argument URI is required\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testMissingFlags_invalidURL() {
        rpc.run("-s", "test.MyTest", "not-a-url");

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "No option found for not-a-url\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testMissingFlags_missingSchemaURL() {
        rpc.run("-s", "test.MyTest", "://boo/");

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "Expected scheme name at index 0: ://boo/\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testMissingFlags_missingHostnameURL() {
        rpc.run("-s", "test.MyTest", "http:///");

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "Missing authority in URI: 'http:///'\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }

    @Test
    public void testFlag_Includes_NoSuchDirectory() throws IOException {
        File dir = temp.newFolder();
        dir.delete();

        rpc.run("-I", dir.getCanonicalFile().getAbsolutePath(), "-s", "test.MyService", endpoint());

        assertThat(exitCode, is(1));
        assertThat(console.output(), is(""));
        assertThat(console.error(), is(equalToLines(
                "No such directory " + dir.getCanonicalFile().getAbsolutePath() + "\n" +
                "Usage: pvdrpc [-hVvS] [--rc FILE] [-I dir] [-i spec] [-o spec] -s srv [-f fmt] [-C ms] [-R ms] [-H hdr] URI\n" +
                "\n" +
                "Run $ pvdrpc --help # for available options.\n")));
    }
}
