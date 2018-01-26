package net.morimekta.providence.config.impl;

import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;
import net.morimekta.util.Numeric;
import net.morimekta.util.Stringable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asBoolean;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asDouble;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asInteger;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asLong;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asString;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.readCanonicalPath;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.resolveFile;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Testing for the providence config utils.
 */
public class ProvidenceConfigUtilTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Declaration declaration;
    private Service service;

    @Before
    public void setUp() throws IOException {
        declaration = Declaration.withDeclConst(ConstType.builder()
                                                         .setName("Name")
                                                         .setType("i32")
                                                         .setValue("44")
                                                         .build());
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);

        copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", tmp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", tmp.getRoot());

        File cfg = copyResourceTo("/net/morimekta/providence/config/files/stage.cfg", tmp.getRoot());

        service = new ProvidenceConfig(registry).getConfig(cfg);
    }

    @Test
    public void testGetInMessage() throws ProvidenceConfigException {
        // Return the field value.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value"));
        assertEquals("Name", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals("44", ProvidenceConfigUtil.getInMessage(declaration, "decl_const.value", "66"));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, ProvidenceConfigUtil.getInMessage(declaration, "decl_const.documentation"));

        assertThat(ProvidenceConfigUtil.getInMessage(service, "name"), is("stage"));
        assertThat(ProvidenceConfigUtil.getInMessage(service, "admin"), is(nullValue()));
        ServicePort def = ServicePort.builder().build();
        assertThat(ProvidenceConfigUtil.getInMessage(service, "admin", def), is(sameInstance(def)));
    }

    @Test
    public void testGetInMessage_fail() {
        try {
            ProvidenceConfigUtil.getInMessage(service, "does_not_exist");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "does_not_exist.name");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Service has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "db.does_not_exist");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Message config.Database has no field named does_not_exist"));
        }

        try {
            ProvidenceConfigUtil.getInMessage(service, "name.db");
            fail("No exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Field 'name' is not of message type in config.Service"));
        }
    }

    @Test
    public void testAsBoolean() throws ProvidenceConfigException {
        assertThat(asBoolean("true"), is(true));
        assertThat(asBoolean(true), is(true));
        assertThat(asBoolean(1L), is(true));
        assertThat(asBoolean((byte) 0), is(false));
        assertThat(asBoolean(new StringBuilder("F")), is(false));
        try {
            asBoolean(new Object());
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to a boolean"));
        }
        try {
            asBoolean("foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse the string \"foo\" to boolean"));
        }
        try {
            asBoolean(111);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert number 111 to boolean"));
        }
        try {
            asBoolean(1.0);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert real value to boolean"));
        }
    }

    @Test
    public void testAsInteger() throws ProvidenceConfigException {
        assertThat(asInteger(2,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(2));
        assertThat(asInteger("1234",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(1234));
        assertThat(asInteger("0xff",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(0xff));
        assertThat(asInteger("0777",
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(511));
        assertThat(asInteger((Numeric) () -> 111,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(111));
        assertThat(asInteger(new StringBuilder("111"),
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(111));
        assertThat(asInteger(false,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(0));
        assertThat(asInteger(new Date(1234567890000L),
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(1234567890));
        assertThat(asInteger(12345.0,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(12345));

        try {
            asInteger(new StringBuilder("foo"), Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse string \"foo\" to an int"));
        }
        try {
            asInteger(1234567890123456789L, Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Long value outsize of bounds: 1234567890123456789 > 2147483647"));
        }
        try {
            asInteger(-1234, Byte.MIN_VALUE, Byte.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Integer value outsize of bounds: -1234 < -128"));
        }
        try {
            asInteger(12.345, Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Truncating integer decimals from 12.345"));
        }
        try {
            asInteger(new Object(), Byte.MIN_VALUE, Byte.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to an int"));
        }
    }

    @Test
    public void testAsLong() throws ProvidenceConfigException {
        assertThat(asLong(2.0f), is(2L));
        assertThat(asLong((byte) 2), is(2L));
        assertThat(asLong((Numeric) () -> 55), is(55L));
        assertThat(asLong(false), is(0L));
        assertThat(asLong(true), is(1L));
        assertThat(asLong("55"), is(55L));
        assertThat(asLong("0x55"), is(85L));
        assertThat(asLong(new Date(1234567890000L)), is(1234567890000L));

        try {
            asLong(2.2);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Truncating long decimals from 2.2"));
        }
        try {
            asLong("foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse string \"foo\" to a long"));
        }
        try {
            asLong(new Object());
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to a long"));
        }
    }

    @Test
    public void testAsDouble() throws ProvidenceConfigException {
        assertThat(asDouble(5.5f), is(5.5));
        assertThat(asDouble((byte) 5), is(5.0));
        assertThat(asDouble((Numeric) () -> 55), is(55.0));
        assertThat(asDouble("55"), is(55.0));
        assertThat(asDouble("55.5"), is(55.5));

        try {
            asDouble("foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to parse string \"foo\" to a double"));
        }
        try {
            asDouble(new Object());
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert Object to a double"));
        }
    }

    @Test
    public void testAsString() throws ProvidenceConfigException {
        assertThat(asString(55.5), is("55.5"));
        assertThat(asString(new StringBuilder("123345")), is("123345"));
        assertThat(asString((Stringable) () -> "foo"), is("foo"));
        assertThat(asString(new Date(1234567890000L)), is("2009-02-13T23:31:30Z"));
        assertThat(asString(new Object() {
            @Override
            public String toString() {
                return "bar";
            }
        }), is("bar"));
        assertThat(asString(null), is("null"));

        try {
            asString(Collections.EMPTY_LIST);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert EmptyList to a string"));
        }
    }

    @Test
    public void testResolveFile() throws IOException {
        File test = tmp.newFolder("test");
        File other = tmp.newFolder("other");

        File f1_1 = new File(test, "test.cfg");
        File f1_2 = new File(test, "same.cfg");
        File f2_1 = new File(other, "other.cfg");
        File f2_2 = tmp.newFile("third.cfg");

        writeContentTo("a", f1_1);
        writeContentTo("a", f1_2);
        writeContentTo("a", f2_1);

        assertEquals(f1_1.getCanonicalFile().toPath(), resolveFile(null, tmp.getRoot() + "/test/test.cfg").toAbsolutePath());
        assertEquals(f1_2.getCanonicalFile().toPath(), resolveFile(f1_1.toPath(), "same.cfg").toAbsolutePath());
        assertEquals(f2_1.getCanonicalFile().toPath(), resolveFile(f1_1.toPath(), "../other/other.cfg").toAbsolutePath());
        assertEquals(f2_2.getCanonicalFile().toPath(), resolveFile(f1_1.toPath(), "../third.cfg").toAbsolutePath());

        assertFileNotResolved(f1_1, "../", "../ is a directory, expected file");
        assertFileNotResolved(f1_1, "../fourth.cfg", "Included file ../fourth.cfg not found");
        assertFileNotResolved(f1_1, "fourth.cfg", "Included file fourth.cfg not found");
        assertFileNotResolved(f1_1, "/fourth.cfg", "Absolute path includes not allowed: /fourth.cfg");
        assertFileNotResolved(f1_1, "other/fourth.cfg", "Included file other/fourth.cfg not found");
        assertFileNotResolved(f1_1, "../other", "../other is a directory, expected file");
        assertFileNotResolved(f1_1, "other", "Included file other not found");
        assertFileNotResolved(f1_1, "../../../../../../../../other", "Parent of root does not exist!");

        assertFileNotResolved(null, "../", "../ is a directory, expected file");
        assertFileNotResolved(null, "../fourth.cfg", "File ../fourth.cfg not found");
        assertFileNotResolved(null, "fourth.cfg", "File fourth.cfg not found");
        assertFileNotResolved(null, "/fourth.cfg", "File /fourth.cfg not found");
        assertFileNotResolved(null, "other/fourth.cfg", "File other/fourth.cfg not found");
        assertFileNotResolved(null, "../other", "File ../other not found");
        assertFileNotResolved(null, "other", "File other not found");
    }

    private void assertFileNotResolved(File ref, String file, String message) {
        try {
            resolveFile(ref == null ? null : ref.toPath(), file);
            fail("no exception on unresolved file");
        } catch (IOException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testReadCanonicalPath() throws IOException {
        Path dot = new File(".").getAbsoluteFile()
                                .getCanonicalFile()
                                .toPath();
        Path root = tmp.getRoot()
                       .getCanonicalFile()
                       .getAbsoluteFile()
                       .toPath();

        assertThat(readCanonicalPath(Paths.get(".")), is(dot));

        Path target1 = Files.createDirectory(root.resolve("target1"));
        Path target2 = Files.createDirectory(root.resolve("target2"));

        Path link = root.resolve("link");
        replaceSymbolicLink(link, target1);
        assertThat(readCanonicalPath(link), is(target1));
        replaceSymbolicLink(link, target2);

        assertThat(readCanonicalPath(link), is(target2));

        Path link2 = root.resolve("link2");
        replaceSymbolicLink(link2, Paths.get("target1"));
        assertThat(readCanonicalPath(link2), is(target1));

        Path link3 = target1.resolve("link3");
        Path target3 = Paths.get("../target2");
        replaceSymbolicLink(link3, target3);
        assertThat(readCanonicalPath(link3), is(target2));
    }

    /**
     * Similar to {@link Files#createSymbolicLink(Path, Path, FileAttribute[])}, but
     * will replace the link if it already exists, and will try to write / replace
     * it as an atomic operation.
     *
     * @param link The path of the symbolic link. The parent directory of the
     *             link file must already exist, and must be writable.
     *             See {@link Files#createDirectories(Path, FileAttribute[])}
     * @param target The target path.
     * @throws IOException If unable to create symbolic link.
     */
    private static void replaceSymbolicLink(Path link, Path target) throws IOException {
        link = link.toAbsolutePath();
        if (Files.exists(link, NOFOLLOW_LINKS)) {
            // This operation will follow the link. And we want it to.
            if (Files.isDirectory(link)) {
                // TODO: Figure out how to atomically replace link to directory.
                // Java complains about target being a directory, as it seems to
                // resolve the old link and trying to move onto that target, and
                // it won't replace a directory with a link.
                // And that is despite the NOFOLLOW_LINKS option.
                Files.delete(link);
                Files.createSymbolicLink(link, target);
            } else {
                Path parent = readCanonicalPath(link.getParent());
                Path temp   = parent.resolve(String.format("..tmp.%x", new Random().nextLong()));
                try {
                    Files.createSymbolicLink(temp, target);
                    Files.move(temp, link, ATOMIC_MOVE, REPLACE_EXISTING, NOFOLLOW_LINKS);
                } finally {
                    Files.deleteIfExists(temp);
                }
            }
        } else {
            Files.createSymbolicLink(link, target);
        }
    }

    @Test
    public void testReadCanonicalPath_fail() throws IOException {
        Path root = tmp.getRoot()
                       .getCanonicalFile()
                       .getAbsoluteFile()
                       .toPath();

        try {
            readCanonicalPath(root.resolve("../../../../../../../.."));
            fail("no exception");
        } catch (IOException e) {
            assertThat(e.getMessage(), is("Parent of root does not exist!"));
        }

        try {
            readCanonicalPath(Paths.get("/.."));
            fail("no exception");
        } catch (IOException e) {
            assertThat(e.getMessage(), is("Parent of root does not exist!"));
        }
    }

}
