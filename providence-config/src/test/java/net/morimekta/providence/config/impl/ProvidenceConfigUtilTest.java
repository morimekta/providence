package net.morimekta.providence.config.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.ServicePort;
import net.morimekta.test.providence.config.Value;
import net.morimekta.util.Binary;
import net.morimekta.util.Numeric;
import net.morimekta.util.Stringable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asBoolean;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asCollection;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asDouble;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asInteger;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asLong;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asMap;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asString;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asType;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.canonicalFileLocation;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.consumeValue;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.readCanonicalPath;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.resolveFile;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

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
    public void testAsType() throws ProvidenceConfigException {
        // null value.
        assertThat(asType(PPrimitive.STRING, null), is(nullValue()));

        // ENUM
        assertThat(asType(Value.kDescriptor, Value.SECOND), is(Value.SECOND));
        assertThat(asType(Value.kDescriptor, 2), is(Value.SECOND));
        assertThat(asType(Value.kDescriptor, "SECOND"), is(Value.SECOND));
        assertThat(asType(Value.kDescriptor, (Numeric) () -> 2), is(Value.SECOND));

        try {
            asType(Value.kDescriptor, Value.kDescriptor);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(),
                       is("Unable to cast _Descriptor to enum config.Value"));
        }
        try {
            asType(Value.kDescriptor, PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to cast PApplicationExceptionType to enum config.Value"));
        }

        // MESSAGE
        Service service = Service.builder().build();
        Database db = Database.builder().build();
        assertThat(asType(Service.kDescriptor, service), is(service));

        try {
            asType(Service.kDescriptor, db);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(),
                       is("Message type mismatch: config.Database is not compatible with config.Service"));
        }

        try {
            asType(Service.kDescriptor, "foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(),
                       is("String is not compatible with message config.Service"));
        }

        // BINARY
        assertThat(asType(PPrimitive.BINARY, Binary.fromHexString("abcd")),
                   is(Binary.fromHexString("abcd")));
        try {
            asType(PPrimitive.BINARY, 123);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(),
                       is("Integer is not compatible with binary"));
        }

        // LIST
        assertThat(asType(PList.provider(PPrimitive.STRING.provider()).descriptor(),
                          ImmutableList.of(1, 2)),
                   is(ImmutableList.of("1", "2")));

        // SET
        assertThat(new ArrayList((Collection) asType(PSet.sortedProvider(PPrimitive.STRING.provider()).descriptor(),
                                                     ImmutableList.of(3, 4, 2, 1))),
                   is(ImmutableList.of("1", "2", "3", "4")));
        // MAP
        Map<String,String> map = (Map) asType(PMap.sortedProvider(PPrimitive.STRING.provider(),
                                                                  PPrimitive.STRING.provider()).descriptor(),
                                              ImmutableMap.of(1, 2, 3, 4));
        assertThat(map, is(instanceOf(ImmutableSortedMap.class)));
        assertThat(map, is(ImmutableMap.of("1", "2", "3", "4")));

        // General Failure
        try {
            asType(PPrimitive.VOID, "true");
            fail("no exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                       is("Unhandled field type: void"));
        }
    }

    @Test
    public void testAsBoolean() throws ProvidenceConfigException {
        assertThat(asBoolean(true), is(true));
        assertThat(asBoolean(1L), is(true));
        assertThat(asBoolean((byte) 0), is(false));
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
            assertThat(e.getMessage(), is("Unable to convert String to a boolean"));
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
        assertThat(asInteger((Numeric) () -> 111,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(111));
        assertThat(asInteger(false,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(0));
        assertThat(asInteger(12345.0,
                             Integer.MIN_VALUE, Integer.MAX_VALUE), is(12345));

        try {
            asInteger("foo", Integer.MIN_VALUE, Integer.MAX_VALUE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert String to an int"));
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
            assertThat(e.getMessage(), is("Unable to convert String to a long"));
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

        try {
            asDouble("foo");
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert String to a double"));
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
    public void testAsCollection() throws ProvidenceConfigException {
        assertThat(asCollection(new LinkedList<>(), PPrimitive.STRING),
                   is(instanceOf(ArrayList.class)));
        assertThat(asCollection(ImmutableSet.of(1, 2, 3), PPrimitive.STRING),
                   is(containsInAnyOrder("1", "2", "3")));

        try {
            asCollection("foo", PPrimitive.BOOL);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Unable to convert String to a collection"));
        }
    }

    @Test
    public void testAsMap() throws ProvidenceConfigException {
        assertThat(asMap(new TreeMap<>(), PPrimitive.STRING, PPrimitive.I32),
                   is(instanceOf(TreeMap.class)));
        assertThat(asMap(new HashMap<>(), PPrimitive.STRING, PPrimitive.I32),
                   is(instanceOf(LinkedHashMap.class)));
        assertThat(asMap(ImmutableSortedMap.of(1, (short) 2), PPrimitive.STRING, PPrimitive.I32),
                   is(instanceOf(TreeMap.class)));

        assertThat(asMap(ImmutableMap.of(1, 2.0), PPrimitive.STRING, PPrimitive.I32),
                   is(ImmutableMap.of("1", 2)));

        try {
            asMap(ImmutableSet.of(1, 2), PPrimitive.BOOL, PPrimitive.DOUBLE);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(),
                       is("Unable to convert RegularImmutableSet to a collection"));
        }
    }

    @Test
    public void testConsumeValue() throws IOException {
        validateConsume("undefined __after__", null);
        validateConsume("not.a.ref\n__after__", null);

        ProvidenceConfigContext context = mock(ProvidenceConfigContext.class);
        when(context.getReference(eq("ref"), any(Token.class), any(Tokenizer.class)))
                .thenReturn(true);
        validateConsume("ref __after__", context);
        reset(context);

        validateConsume("{} __after__", null);
        validateConsume("unknown {} __after__", null);

        validateConsume("{ FIRST: SECOND } __after__", null);
        validateConsume("{ foo = \"bar\" } __after__", null);
        validateConsume("{ foo { bar = 12 } } __after__", null);
        validateConsume("{ foo = 12 bar = 13 } __after__", null);
        validateConsume("{ foo & bar = 12 ; other = bar } __after__", null);

        validateConsume("[] __after__", null);
        validateConsume("[ \"foo\", ] __after__", null);
        validateConsume("[ \"foo\" ] __after__", null);

        validateConsume("hex(abcdef) __after__", null);
        validateConsume("b64(AbDfm_) __after__", null);
    }

    @Test
    public void testConsumeValue_fails() throws IOException {
        try {
            validateConsume("{ first.Value: OOPS }", null);
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Invalid map key: first.Value"));
        }

        try {
            validateConsume("{ 12 = OOPS }", null);
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Invalid field name: 12"));
        }

        try {
            validateConsume("{ foo = bar ; other : 12 }", null);
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Unknown field value sep: :"));
        }

        try {
            validateConsume("[ 12 13 ]", null);
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.getMessage(),
                       is("Expected list separator or end (one of [',', ']']): but found '13'"));
        }

        try {
            validateConsume("[ 12; 13 ]", null);
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.getMessage(),
                       is("Expected list separator or end (one of [',', ']']): but found ';'"));
        }
   }

    private void validateConsume(String content,
                                 ProvidenceConfigContext context) throws IOException {
        if (context == null) {
            context = new ProvidenceConfigContext();
        }
        Tokenizer tokenizer = tokenizer(content);
        consumeValue(context, tokenizer, tokenizer.expect("consumed value"));
        Token next = tokenizer.expect("after consuming");
        assertThat(next, is(notNullValue()));
        assertThat(next.asString(), is("__after__"));
    }

    private Tokenizer tokenizer(String content) {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8));
        return new Tokenizer(in);
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
    public void testCanonicalFileLocation() throws IOException {
        try {
            canonicalFileLocation(Paths.get("/"));
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Trying to read root directory"));
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

    @Test
    public void testConstructor() throws
                                  NoSuchMethodException,
                                  IllegalAccessException,
                                  InvocationTargetException,
                                  InstantiationException {
        Constructor<ProvidenceConfigUtil> constructor = ProvidenceConfigUtil.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible(), is(false));
        try {
            constructor.setAccessible(true);
            assertThat(constructor.newInstance(), is(instanceOf(ProvidenceConfigUtil.class)));
        } finally {
            constructor.setAccessible(false);
        }
    }
}
