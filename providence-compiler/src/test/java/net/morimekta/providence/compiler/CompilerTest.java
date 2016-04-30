package net.morimekta.providence.compiler;

import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
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

import static org.junit.Assert.assertEquals;

/**
 * Created by morimekta on 4/26/16.
 */
public class CompilerTest {
    private static InputStream defaultIn;
    private static PrintStream defaultOut;
    private static PrintStream defaultErr;

    @Rule
    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private int      exitCode;
    private Compiler compiler;
    private File     thriftFile;
    private String   version;

    @BeforeClass
    public static void setUpIO() {
        defaultIn = System.in;
        defaultOut = System.out;
        defaultErr = System.err;
    }

    @Before
    public void setUp() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/build.properties"));
        version = properties.getProperty("build.version");

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

        compiler = new Compiler() {
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
    }

    @Test
    public void testHelp() {
        compiler.run("--help");

        assertEquals("Providence compiler - v" + version + "\n" +
                     "Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                     "\n" +
                     "Example code to run:\n" +
                     "$ pvdc -I thrift/ --out target/ --gen java:android thrift/the-one.thrift\n" +
                     "\n" +
                     " file                       : Files to compile.\n" +
                     " --gen (-g) generator       : Generate files for this language spec.\n" +
                     " --help (-h, -?) [language] : Show this help or about language. (default: help())\n" +
                     " --include (-I) dir         : Allow includes of files in directory.\n" +
                     " --out (-o) dir             : Output directory. (default: .)\n" +
                     "\n" +
                     "Available generators:\n" +
                     " - java       : Main java (1.7+) code generator.\n" +
                     " - thrift     : Re-generate thrift files with the same spec.\n" +
                     " - json       : Create JSON specification files.\n",
                     outContent.toString());
        assertEquals("", errContent.toString());
        assertEquals(0, exitCode);
    }

    @Test
    public void testHelp_java() {
        compiler.run("--help", "java");

        assertEquals("Providence compiler - v" + version + "\n" +
                     "Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...\n" +
                     "\n" +
                     "java : Main java (1.7+) code generator.\n" +
                     "Available options\n" +
                     "\n" +
                     " - android : Add android parcelable interface to model classes.\n" +
                     " - jackson : Add jackson 2 annotations to model classes.\n",
                     outContent.toString());
        assertEquals("", errContent.toString());
        assertEquals(0, exitCode);
    }
}
