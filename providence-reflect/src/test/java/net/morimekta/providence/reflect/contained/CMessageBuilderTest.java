package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramConverter;
import net.morimekta.providence.reflect.util.ProgramRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CMessageBuilderTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProgramRegistry registry;

    @Before
    public void setUp() throws IOException {
        File file = tmp.newFile("test.thrift").getCanonicalFile().getAbsoluteFile();
        registry = new ProgramRegistry();
        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramConverter converter = new ProgramConverter(registry);
        ProgramType program = parser.parse(getClass().getResourceAsStream("/parser/tests/test.thrift"),
                                           file, ImmutableList.of());
        registry.putDocument(file.getPath(), converter.convert(file.getPath(), program));
    }

    @Test
    public void testOptionals() {
        CStruct.Builder ba = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();
        CStruct.Builder bb = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();

        ba.set(1, false);
        ba.set(2, (byte) 55);
        ba.set(42, ImmutableList.of());  // does not exists.
        ba.set(5, null);

        bb.set(2, (byte) 42);
        bb.set(5, 42L);

        assertThat(ba.isSet(1), is(true));
        assertThat(ba.isSet(2), is(true));
        assertThat(ba.isSet(5), is(false));

        CStruct a = ba.build();

        assertThat(a.has(1), is(true));
        assertThat(a.has(2), is(true));
        assertThat(a.has(5), is(false));

        assertThat(a.get(1), is(false));
        assertThat(a.get(2), is((byte) 55));
        assertThat(a.get(5), is(0L));

        ba.merge(bb.build());

        assertThat(ba.isSet(1), is(true));
        assertThat(ba.isSet(2), is(true));
        assertThat(ba.isSet(5), is(true));

        CMessage b = ba.build();

        assertThat(b.has(1), is(true));
        assertThat(b.has(2), is(true));
        assertThat(b.has(5), is(true));

        assertThat(b.get(1), is(false));
        assertThat(b.get(2), is((byte) 42));
        assertThat(b.get(5), is(42L));

    }

    @Test
    public void testContainers() {
        CStruct.Builder of = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();
        of.set(1, true);
        of.set(2, (byte) 42);

        CStruct.Builder ba = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.Containers").builder();
        CStruct.Builder bb = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.Containers").builder();

        ba.set(4, ImmutableList.of(1, 2, 3, 4));
        ba.set(14, ImmutableSet.of(4, 3, 2, 1));
        ba.set(24, ImmutableMap.of(1, 4,
                                   2, 3,
                                   3, 2,
                                   4, 1));
        ba.set(53, of.build());

        of.clear(2);
        of.set(4, 42);
        bb.set(4, ImmutableList.of(3, 4, 5, 6));
        bb.set(14, ImmutableSet.of(3, 4, 5, 6));
        bb.set(24, ImmutableMap.of(3, 5,
                                   4, 6,
                                   5, 7,
                                   6, 8));
        bb.set(5, ImmutableList.of(5L, 6L, 7L, 8L));
        bb.set(15, ImmutableSet.of(5L, 6L, 7L, 8L));
        bb.set(25, ImmutableMap.of(5L, 4L,
                                   6L, 3L,
                                   7L, 2L,
                                   8L, 1L));
        bb.set(53, of.build());

        bb.mutator(52).set(1, true);
        CStruct.Builder df = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.DefaultFields")
                                                                         .builder();
        df.set(1, true);
        df.set(2, (byte) 42);
        bb.set(52, df.build());
        bb.mutator(52).set(5, 42L);
        bb.mutator(52).set(5, 52L);

        ba.merge(bb.build());

        CStruct a = ba.build();

        assertThat(debugString(a), is(equalToLines(
                "integerList = [3, 4, 5, 6]\n" +
                "longList = [5, 6, 7, 8]\n" +
                "integerSet = [4, 3, 2, 1, 5, 6]\n" +
                "longSet = [5, 6, 7, 8]\n" +
                "integerMap = {\n" +
                "  1: 4\n" +
                "  2: 3\n" +
                "  3: 5\n" +
                "  4: 6\n" +
                "  5: 7\n" +
                "  6: 8\n" +
                "}\n" +
                "longMap = {\n" +
                "  5: 4\n" +
                "  6: 3\n" +
                "  7: 2\n" +
                "  8: 1\n" +
                "}\n" +
                "defaultFields = {\n" +
                "  booleanValue = true\n" +
                "  byteValue = 42\n" +
                "  shortValue = 0\n" +
                "  integerValue = 0\n" +
                "  longValue = 52\n" +
                "  doubleValue = 0\n" +
                "  stringValue = \"\"\n" +
                "  binaryValue = b64()\n" +
                "}\n" +
                "optionalFields = {\n" +
                "  booleanValue = true\n" +
                "  byteValue = 42\n" +
                "  integerValue = 42\n" +
                "}")));
    }

}
