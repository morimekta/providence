package net.morimekta.providence.it.serialization;

import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.testing.ResourceUtils;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Speed test running through:
 * - Read a file for each factory / serialization format.
 * - Write the same file back to a temp file.
 */
public class SerializationIT {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher().dumpOnFailure();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private IntegrationExecutor it;
    private File                containers;

    @Before
    public void setUp() throws IOException {
        it = new IntegrationExecutor("it-serialization", "it-serialization.jar");
        it.setDeadlineMs(10 * 60 * 1000);
        containers = ResourceUtils.copyResourceTo("/containers.bin", temp.getRoot())
                .getCanonicalFile();
    }

    @Test
    public void testSerializationSpeed_consistentData() throws IOException {
        assertThat(it.run("--no_progress",
                          "--runs", "10",
                          containers.getAbsolutePath()), is(0));

        System.err.println(console.output());
    }
}
