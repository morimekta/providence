package net.morimekta.providence.testing.util;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.testing.util.ResourceUtils.getResourceAsByteBuffer;
import static net.morimekta.providence.testing.util.ResourceUtils.getResourceAsString;
import static net.morimekta.util.io.IOUtils.readString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class ResourceUtilsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testCopyResourceTo() throws IOException {
        ResourceUtils.copyResourceTo("/test.txt", temp.getRoot());

        File test = new File(temp.getRoot(), "test.txt");

        assertTrue(test.exists());
        assertTrue(test.isFile());
        assertEquals("Test!\n", readString(new FileInputStream(test)).replaceAll("\\r", ""));
    }

    @Test
    public void testCopyResrouceTo_invalidParams() throws IOException {
        try {
            ResourceUtils.copyResourceTo("/does-not-exist.txt", temp.getRoot());
            fail("No exception on no such resource");
        } catch (AssertionError e) {
            assertEquals("Trying to read non-existing resource /does-not-exist.txt",
                         e.getMessage());
        }

        File tmp = temp.newFile();
        tmp.createNewFile();
        try {
            ResourceUtils.copyResourceTo("/test.txt", tmp);
            fail("No exception on target is file");
        } catch (AssertionError e) {
            assertEquals("Trying to copy resource /test.txt to file: " + tmp.getAbsolutePath() +
                         ", directory required",
                         e.getMessage());
        }

        try {
            temp.delete();
            ResourceUtils.copyResourceTo("/test.txt", temp.getRoot());
            fail("No exception on missing target dir");
        } catch (AssertionError e) {
            assertEquals("Trying to copy resource /test.txt to non-existing directory: " +
                         temp.getRoot().getAbsolutePath(), e.getMessage());
        }
    }

    @Test
    public void testWriteContentTo() throws IOException {
        ResourceUtils.writeContentTo("/test.txt", temp.newFile("test2.txt"));

        File test = new File(temp.getRoot(), "test2.txt");

        assertTrue(test.exists());
        assertTrue(test.isFile());
        assertEquals("/test.txt", readString(new FileInputStream(test)));
    }

    @Test
    public void testWriteContentTo_fails() throws IOException {
        File file = new File(temp.getRoot(), "a/b/c");
        try {
            ResourceUtils.writeContentTo("/test.txt", file);
            fail("No exception on unable to write to file.");
        } catch (UncheckedIOException e) {
            assertThat(e.getMessage(), startsWith("java.io.FileNotFoundException: " + file.getAbsolutePath() + " ("));
        }
    }

    @Test
    public void testGetReourceAsString() {
        assertEquals("Test!\n", getResourceAsString("/test.txt"));

        try {
            getResourceAsString("/test-2.txt"); // Does not exist.
            fail("Passing with no resource.");
        } catch (AssertionError error) {
            assertEquals("Trying to read non-existing resource /test-2.txt", error.getMessage());
        }
    }

    @Test
    public void testGetResourceAsByteBuffer() {
        ByteBuffer bb = getResourceAsByteBuffer("/test.txt");
        assertEquals("Test!\n", new String(bb.array(), UTF_8).replaceAll("\\r", ""));

        try {
            getResourceAsByteBuffer("/test-2.txt"); // Does not exist.
            fail("Passing with no resource.");
        } catch (AssertionError error) {
            assertEquals("Trying to read non-existing resource /test-2.txt", error.getMessage());
        }
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = ResourceUtils.class.getDeclaredConstructor();
        assertFalse(constructor.isAccessible());
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
    }
}
