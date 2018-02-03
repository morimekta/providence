package net.morimekta.providence.tools.config;

import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigTestBase {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher()
            .withTerminalSize(40, 100);

    File   configRoot;
    File   thriftRoot;
    File   rc;
    Config sut;
    int    exitCode;

    @Before
    public void setUp() throws IOException {
        rc = copyResourceTo("/pvdrc", temp.getRoot());
        configRoot = temp.newFolder("config");
        thriftRoot = temp.newFolder("providence");

        exitCode = 0;
        sut = new Config(console.tty()) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    void standardConfig() {
        copyResourceTo("/config.thrift", thriftRoot);

        String path = "/net/morimekta/providence/tools/config/";
        copyResourceTo(path + "base_service.cfg", configRoot);
        copyResourceTo(path + "prod.cfg", configRoot);
        copyResourceTo(path + "prod_service.cfg", configRoot);
        copyResourceTo(path + "prod_db.cfg", configRoot);
        copyResourceTo(path + "stage.cfg", configRoot);
        copyResourceTo(path + "stage_db.cfg", configRoot);
    }
}
