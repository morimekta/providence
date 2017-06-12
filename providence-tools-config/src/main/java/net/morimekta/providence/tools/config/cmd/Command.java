package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.tools.config.ConfigOptions;

import java.io.IOException;

/**
 *
 */
public interface Command {
    void execute(ConfigOptions options) throws IOException;

    ArgumentParser parser(ArgumentParser parent);
}
