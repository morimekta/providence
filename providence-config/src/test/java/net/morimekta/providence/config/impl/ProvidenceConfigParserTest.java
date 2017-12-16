package net.morimekta.providence.config.impl;

import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.RefMerge;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.Value;
import net.morimekta.util.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import static net.morimekta.providence.config.impl.ProvidenceConfigParser.resolveFile;
import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class ProvidenceConfigParserTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private SimpleTypeRegistry registry;
    private ProvidenceConfigParser parser;

    @Before
    public void setUp() {
        registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
        registry.registerRecursively(Value.kDescriptor);
        registry.registerRecursively(RefMerge.kDescriptor);

        parser = new ProvidenceConfigParser(registry, false);
    }

    @Test
    public void testParseWithUnknownInclude() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/files/unknown.cfg", temp.getRoot());
        File file = copyResourceTo("/net/morimekta/providence/config/files/unknown_include.cfg", temp.getRoot());

        Pair<Database, Set<String>> cfg = parser.parseConfig(file, null);

        // all the unknowns are skipped.
        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "}",
                     debugString(cfg.first));

        file = copyResourceTo("/net/morimekta/providence/config/files/unknown_field.cfg", temp.getRoot());
        cfg = parser.parseConfig(file, null);
        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "}",
                     debugString(cfg.first));

        file = copyResourceTo("/net/morimekta/providence/config/files/unknown_enum_value.cfg", temp.getRoot());
        cfg = parser.parseConfig(file, null);
        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "}",
                     debugString(cfg.first));
    }

    @Test
    public void testParseWithUnknown_strict() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/files/unknown.cfg", temp.getRoot());
        File file = copyResourceTo("/net/morimekta/providence/config/files/unknown_include.cfg", temp.getRoot());

        ProvidenceConfigParser config = new ProvidenceConfigParser(registry, true);
        try {
            config.parseConfig(file, null);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertEquals("Unknown declared type: unknown.OtherConfig", e.getMessage());
        }

        file = copyResourceTo("/net/morimekta/providence/config/files/unknown_field.cfg", temp.getRoot());
        try {
            config.parseConfig(file, null);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertEquals("No such field unknown_field in config.Database", e.getMessage());
        }

        file = copyResourceTo("/net/morimekta/providence/config/files/unknown_enum_value.cfg", temp.getRoot());
        try {
            config.parseConfig(file, null);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertEquals("No such enum value LAST for config.Value.", e.getMessage());
        }
    }

    @Test
    public void testCircularIncludes() throws IOException {
        File a = temp.newFile("a.cfg");
        File b = temp.newFile("b.cfg");
        File c = temp.newFile("c.cfg");

        writeContentTo("include \"b.cfg\" as a\n" +
                       "config.Database {}\n", a);
        writeContentTo("include \"c.cfg\" as a\n" +
                       "config.Database {}\n", b);
        writeContentTo("include \"a.cfg\" as a\n" +
                       "config.Database {}\n", c);

        ProvidenceConfigParser config = new ProvidenceConfigParser(registry, false);

        try {
            config.parseConfig(a, null);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Circular includes detected: a.cfg -> b.cfg -> c.cfg -> a.cfg", e.getMessage());
        }
    }

    @Test
    public void testIncludeNoSuchFile() throws IOException {
        File a = temp.newFile("a.cfg");
        writeContentTo("include \"b.cfg\" as a\n" +
                       "config.Database {}\n", a);

        ProvidenceConfigParser config = new ProvidenceConfigParser(registry, false);

        try {
            config.parseConfig(a, null);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Included file \"b.cfg\" not found.", e.getMessage());
        }
    }

    @Test
    public void testInternalReference() throws IOException {
        File a = writeContentTo(
                "config.RefMerge {\n" +
                "  ref1 & first = {\n" +
                "    bool_value & boo = false\n" +
                "    msg_value & db {\n" +
                "      driver = \"Driver\"\n" +
                "    }\n" +
                "  }\n" +
                "  ref1_1 = first {\n" +
                "    i16_value = 12345\n" +
                "    msg_value & db2 = db {\n" +
                "      uri = \"someuri\"\n" +
                "    }\n" +
                "  }\n" +
                "  ref2 {\n" +
                "    bool_value = boo" +
                "    msg_value = db2\n" +
                "  }\n" +
                "}\n", temp.newFile("a.cfg"));

        try {
            ProvidenceConfigParser config = new ProvidenceConfigParser(registry, false);
            RefMerge merged = config.parseConfig(a, (RefMerge) null).first;

            assertThat(debugString(merged), is(
                    "{\n" +
                    "  ref1 = {\n" +
                    "    bool_value = false\n" +
                    "    msg_value = {\n" +
                    "      driver = \"Driver\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "  ref1_1 = {\n" +
                    "    bool_value = false\n" +
                    "    i16_value = 12345\n" +
                    "    msg_value = {\n" +
                    "      uri = \"someuri\"\n" +
                    "      driver = \"Driver\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "  ref2 = {\n" +
                    "    bool_value = false\n" +
                    "    msg_value = {\n" +
                    "      uri = \"someuri\"\n" +
                    "      driver = \"Driver\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"));
        } catch (ProvidenceConfigException e) {
            System.err.println(e.asString());
            throw e;
        }
    }

    @Test
    public void testInternalReferenceFails() throws IOException {
        writeContentTo("config.Database {}\n", temp.newFile("db.cfg"));

        assertReferenceFails("include \"db.cfg\" as db\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & db {\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign include alias 'db' to reference.");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {}\n" +
                             "  ref1_1 & first {}\n" +
                             "}\n",
                             "Trying to reassign reference 'first', original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & def {\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to assign reference id 'def', which is reserved.");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value & first {}\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value = first\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reference 'first' while it's being defined, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value = second\n" +
                             "  }\n" +
                             "}\n",
                             "No such reference 'second'");

        // --- with unknown / consumed values
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    unk & first = {\n" +
                             "    }\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    unk = {\n" +
                             "      val & first = \"str\"\n" +
                             "    }\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
    }

    private void assertReferenceFails(String cfg, String message) throws IOException {
        try {
            File a = writeContentTo(cfg, temp.newFile());
            ProvidenceConfigParser config = new ProvidenceConfigParser(registry, false);
            config.parseConfig(a, null);
            fail("No exception on fail: " + message);
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is(message));
        }
    }

    @Test
    public void testResolveFile() throws IOException {
        File test = temp.newFolder("test");
        File other = temp.newFolder("other");

        File f1_1 = new File(test, "test.cfg");
        File f1_2 = new File(test, "same.cfg");
        File f2_1 = new File(other, "other.cfg");
        File f2_2 = temp.newFile("third.cfg");

        writeContentTo("a", f1_1);
        writeContentTo("a", f1_2);
        writeContentTo("a", f2_1);

        assertEquals(f1_1.getCanonicalPath(), resolveFile(null, temp.getRoot() + "/test/test.cfg").getAbsolutePath());
        assertEquals(f1_2.getCanonicalPath(), resolveFile(f1_1, "same.cfg").getAbsolutePath());
        assertEquals(f2_1.getCanonicalPath(), resolveFile(f1_1, "../other/other.cfg").getAbsolutePath());
        assertEquals(f2_2.getCanonicalPath(), resolveFile(f1_1, "../third.cfg").getAbsolutePath());

        assertFileNotResolved(f1_1, "../", "../ is a directory, expected file");
        assertFileNotResolved(f1_1, "../fourth.cfg", "Included file ../fourth.cfg not found");
        assertFileNotResolved(f1_1, "fourth.cfg", "Included file fourth.cfg not found");
        assertFileNotResolved(f1_1, "/fourth.cfg", "Absolute path includes not allowed: /fourth.cfg");
        assertFileNotResolved(f1_1, "other/fourth.cfg", "Included file other/fourth.cfg not found");
        assertFileNotResolved(f1_1, "../other", "../other is a directory, expected file");
        assertFileNotResolved(f1_1, "other", "Included file other not found");

        assertFileNotResolved(null, "../", "../ is a directory, expected file");
        assertFileNotResolved(null, "../fourth.cfg", "File ../fourth.cfg not found");
        assertFileNotResolved(null, "fourth.cfg", "File fourth.cfg not found");
        assertFileNotResolved(null, "/fourth.cfg", "File /fourth.cfg not found");
        assertFileNotResolved(null, "other/fourth.cfg", "File other/fourth.cfg not found");
        assertFileNotResolved(null, "../other", "File ../other not found");
        assertFileNotResolved(null, "other", "File other not found");
    }

    private void assertFileNotResolved(File ref, String file, String message) throws IOException {
        try {
            resolveFile(ref, file);
            fail("no exception on unresolved file");
        } catch (FileNotFoundException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testParseFailure() throws IOException {
        writeContentTo("config.Database {}", temp.newFile("a.cfg"));
        writeContentTo("config.Database {}", temp.newFile("b.cfg"));

        assertParseFailure("Error: No message in config: test.cfg",
                           "");
        assertParseFailure("Error in test.cfg on line 1, pos 11: Invalid termination of number: '1f'\n" +
                           "def { n = 1f }\n" +
                           "----------^^",
                           "def { n = 1f }");
        assertParseFailure("Error in test.cfg on line 3, pos 1: Unexpected token 'def', expected end of file.\n" +
                           "def { y = \"baa\"}\n" +
                           "^^^",
                           "include \"a.cfg\" as a\n" +
                           "config.Database { driver = \"baa\"}\n" +
                           "def { y = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 1: Expected the token 'as', but got 'config.Database'\n" +
                           "config.Database { driver = \"baa\"}\n" +
                           "^^^^^^^^^^^^^^^",
                           "include \"a.cfg\"\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 17: Expected token 'as' after included file \"a.cfg\".\n" +
                           "include \"a.cfg\" ass db\n" +
                           "----------------^^^",
                           "include \"a.cfg\" ass db\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 1: Expected Include alias, but got 'config.Database'\n" +
                           "config.Database { driver = \"baa\"}\n" +
                           "^^^^^^^^^^^^^^^",
                           "include \"a.cfg\" as\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 20: Alias \"def\" is a reserved word.\n" +
                           "include \"a.cfg\" as def\n" +
                           "-------------------^^^",
                           "include \"a.cfg\" as def\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 20: Alias \"a\" is already used.\n" +
                           "include \"a.cfg\" as a\n" +
                           "-------------------^",
                           "include \"a.cfg\" as a\n" +
                           "include \"a.cfg\" as a\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 11: Unexpected newline in string literal\n" +
                           "def { s = \"\n" +
                           "----------^",
                           "def { s = \"\n\"}");
        assertParseFailure("Error in test.cfg on line 1, pos 11: Unescaped non-printable char in literal: '\\t'\n" +
                           "def { s = \"\t\"}\n" +
                           "----------^^",
                           "def { s = \"\t\"}");
        assertParseFailure("Error in test.cfg on line 1, pos 11: Unexpected end of stream in string literal\n" +
                           "def { s = \"a\n" +
                           "----------^^",
                           "def { s = \"a");
        assertParseFailure("Error in test.cfg on line 1, pos 7: Token '1' is not valid reference name.\n" +
                           "def { 1 = \"boo\" }\n" +
                           "------^",
                           "def { 1 = \"boo\" }");
        assertParseFailure("Error in test.cfg on line 1, pos 1: Unexpected token '44'. Expected include, defines or message type\n" +
                           "44\n" +
                           "^^",
                           "44");
        assertParseFailure("Error in test.cfg on line 1, pos 1: Unexpected token 'boo'. Expected include, defines or message type\n" +
                           "boo {\n" +
                           "^^^",
                           "boo {\n" +
                           "}\n");

        // Parsing that only fails in strict mode.
        assertParseFailure("Error in test.cfg on line 1, pos 11: Unknown enum identifier: boo.En\n" +
                           "def { s = boo.En.VAL }\n" +
                           "----------^^^^^^^^^^",
                           "def { s = boo.En.VAL }", true);
    }


    private void assertParseFailure(String message,
                                    String pretty) throws IOException {
        assertParseFailure(message, pretty, false);
    }

    private void assertParseFailure(String message,
                                    String pretty,
                                    boolean strict) throws IOException {
        File a = temp.newFile("test.cfg");
        writeContentTo(pretty, a);

        ProvidenceConfigParser config = new ProvidenceConfigParser(registry, strict);

        try {
            config.parseConfig(a, null);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            String actual = e.asString().replaceAll("\\r", "");
            if (!actual.equals(message)) {
                e.printStackTrace();
            }
            assertThat(actual, is(message));
        }
        a.delete();
    }
}
