package net.morimekta.providence.tools.compiler;

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

public class CompilerFlagsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher()
            .withTerminalSize(40, 100);

    private int      exitCode;
    private Compiler sut;
    private File     thriftFile;
    private String   version;

    @Before
    public void setUp() throws IOException {
        version = Utils.getVersionString();

        File include = temp.newFolder("include").getCanonicalFile();

        copyResourceTo("/compiler/ref.thrift", include);
        thriftFile = copyResourceTo("/compiler/test.thrift", temp.getRoot());

        File generator = temp.newFolder("generator");

        copyResourceTo("/generator/java.jar", generator);
        copyResourceTo("/generator/js.jar", generator);

        exitCode = 0;
        sut = new Compiler(new CompilerOptions(console.tty()) {
            @Override
            public File currentJarDirectory() {
                return temp.getRoot();
            }}) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @Test
    public void testHelp() {
        sut.run("--help");

        assertThat(console.error(), is(""));
        assertThat(console.output(),
                   is(equalToLines("Providence compiler - " + version + "\n" +
                                   "Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" + "\n" +
                                   "Example code to run:\n" +
                                   "$ pvdc -I thrift/ --out target/ --gen java:android thrift/the-one.thrift\n" + "\n" +
                                   " --gen (-g) generator       : Generate files for this language spec.\n" +
                                   " --help (-h, -?) [language] : Show this help or about language.\n" +
                                   " --verbose (-V)             : Show verbose output and error messages.\n" +
                                   " --version (-v)             : Show program version.\n" +
                                   " --rc FILE                  : Providence RC to use (default: ~/.pvdrc)\n" +
                                   " --include (-I) dir         : Allow includes of files in directory\n" +
                                   " --out (-o) dir             : Output directory (default: ${PWD})\n" +
                                   " --require-field-id         : Require all fields to have a defined ID\n" +
                                   " --require-enum-value       : Require all enum values to have a defined ID\n" +
                                   " --add-generator FILE       : Add extra generator .jar file\n" +
                                   " file                       : Files to compile.\n" +
                                   "\n" +
                                   "Available generators:\n" +
                                   " - java       : Generates java (1.8+) classes.\n" +
                                   " - js         : Generates JavaScript (es5.1 or es6).\n" +
                                   " - json       : Print out the IDL as json files.\n" +
                                   "")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testHelp_java() {
        sut.run("--help", "java");

        assertThat(console.error(), is(""));
        assertThat(console.output(),
                   is(equalToLines("Providence compiler - " + version + "\n" +
                                   "Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" + "\n" +
                                   "java : Generates java (1.8+) classes.\n" +
                                   "\n" +
                                   "Available options\n" +
                                   "\n" +
                                   " - android             : Add android parcelable interface to model classes.\n" +
                                   " - jackson             : Add jackson 2 annotations to model classes.\n" +
                                   " - no_rw_binary        : Skip adding the binary RW methods to generated code. [Default on]\n" +
                                   " - hazelcast_portable  : Add hazelcast portable to annotated model classes, and add portable\n" +
                                   "                         factories.\n" +
                                   " - no_generated_annotation_version : Remove providence version from the <code>@Generated</code>\n" +
                                   "                         annotation for each generated class. [Default on]\n" +
                                   " - public_constructors : Generate public constructors for all structs and exceptions. Have no\n" +
                                   "                         effect on unions.\n")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testHelp_js() {
        sut.run("--help", "js");

        assertThat(console.error(), is(""));
        assertThat(console.output(),
                   is(equalToLines("Providence compiler - " + version + "\n" +
                                   "Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                                   "\n" +
                                   "js : Generates JavaScript (es5.1 or es6).\n" +
                                   "\n" +
                                   "Available options\n" +
                                   "\n" +
                                   " - es51                : Generate for ECMA Script 5.1 (no maps, promises).\n" +
                                   " - ts                  : Generate definition files for typescript.\n" +
                                   " - closure             : Generate google closure dependencies (goog.require and goog.provide).\n" +
                                   " - node_js             : Generate node.js module wrapper.\n" +
                                   " - pvd                 : Provide core providence models for services if needed.\n")));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testIncludeNotExist() {
        sut.run("-I",
                     temp.getRoot().getAbsolutePath() + "/does_not_exist",
                "-g", "json",
                thriftFile.getAbsolutePath());

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is(equalToLines("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                     "No such directory " + temp.getRoot().getAbsolutePath() + "/does_not_exist\n" +
                     "\n" +
                     "Run $ pvdc --help # for available options.\n")));
        assertThat(exitCode, is(1));
    }

    @Test
    public void testIncludeNotADirectory() throws IOException {
        sut.run("-I",
                thriftFile.getCanonicalPath(),
                "-g", "json",
                thriftFile.getCanonicalPath());

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is(equalToLines("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                     "" + temp.getRoot().getCanonicalPath() + File.separator + "test.thrift is not a directory\n" +
                     "\n" +
                     "Run $ pvdc --help # for available options.\n")));
        assertThat(exitCode, is(1));
    }

    @Test
    public void testOutputDirNotADirectory() {
        sut.run("--out",
                thriftFile.getAbsolutePath(),
                "-g", "json",
                thriftFile.getAbsolutePath());

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   is(equalToLines("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                     thriftFile.getAbsolutePath() + " exists and is not a directory\n" +
                     "\n" +
                     "Run $ pvdc --help # for available options.\n")));
        assertThat(exitCode, is(1));
    }
}
