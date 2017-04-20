package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.config.ProvidenceConfig;

import java.io.IOException;

/**
 *
 */
public interface Command {
    void execute(ProvidenceConfig config) throws IOException;

    ArgumentParser parser(ArgumentParser parent);
}
