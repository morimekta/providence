package net.morimekta.providence.generator.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.util.io.IOUtils.readString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by morimekta on 23.04.17.
 */
public class FileManagerTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testFileManager() throws IOException {
        FileManager fm = new FileManager(tmp.getRoot());

        OutputStream out = fm.create("tmp/boo", "foo.bar");

        out.write(("Test\n" +
                   "More\n").getBytes(UTF_8));

        fm.finalize(out);

        File file = new File(tmp.getRoot(), "tmp/boo/foo.bar");
        try (FileInputStream in = new FileInputStream(file)) {
            assertThat(readString(in),
                       is("Test\n" +
                          "More\n"));
        }
    }
}
