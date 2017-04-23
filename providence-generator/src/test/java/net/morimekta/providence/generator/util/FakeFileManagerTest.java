package net.morimekta.providence.generator.util;

import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FakeFileManagerTest {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testFakeFileManager() throws IOException {
        FakeFileManager fake = new FakeFileManager(tmp.getRoot());

        OutputStream out = fake.create("tmp/boo", "foo.bar");

        out.write(("Test\n" +
                   "More\n").getBytes(UTF_8));

        fake.finalize(out);

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("\n" +
                                        "### --> tmp/boo/foo.bar\n" +
                                        "Test\n" +
                                        "More\n" +
                                        "### <-- END\n"));
    }
}
