package net.morimekta.providence.tools.config;

import net.morimekta.providence.tools.common.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigHelpTest extends ConfigTestBase {
    private String version;

    @Before
    public void setUp() throws IOException {
        super.setUp();

        version = Utils.getVersionString();
    }

    @Test
    public void testHelp() {
        sut.run("--help", "--verbose");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                "Providence Config Tool - " + version + "\n" +
                "Usage: pvdcfg [-hVvS] [--rc FILE] [-I dir] [help | resolve | validate] [...]\n" +
                "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --rc FILE          : Providence RC to use (default: ~/.pvdrc)\n" +
                " --strict (-S)      : Parse config strictly (default: false)\n" +
                " --include (-I) dir : Read config definitions from these directories.\n" +
                " cmd                : Config action.\n" +
                "\n" +
                "Available Commands:\n" +
                "\n" +
                " help     : Show help for sub-commands.\n" +
                " resolve  : Resolve the resulting config.\n" +
                " validate : Validate the files, print error if not valid.\n")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testHelpCmd() {
        sut.run("help");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                "Providence Config Tool - " + version + "\n" +
                "Usage: pvdcfg [-hVvS] [--rc FILE] [-I dir] [help | resolve | validate] [...]\n" +
                "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --rc FILE          : Providence RC to use (default: ~/.pvdrc)\n" +
                " --strict (-S)      : Parse config strictly (default: false)\n" +
                " --include (-I) dir : Read config definitions from these directories.\n" +
                " cmd                : Config action.\n" +
                "\n" +
                "Available Commands:\n" +
                "\n" +
                " help     : Show help for sub-commands.\n" +
                " resolve  : Resolve the resulting config.\n" +
                " validate : Validate the files, print error if not valid.\n")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testHelpPrint() {
        sut.run("help", "resolve");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(equalToLines(
                "Providence Config Tool - " + version + "\n" +
                "Usage: pvdcfg [...] resolve [-f fmt] file...\n" +
                "\n" +
                " --format (-f) fmt : the output format (default: pretty)\n" +
                " file              : Config files to resolve\n")));
        assertThat(exitCode, is(0));
    }
}
