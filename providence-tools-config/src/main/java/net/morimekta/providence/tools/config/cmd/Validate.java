package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.config.ProvidenceConfig;

import java.io.File;
import java.io.IOException;

/**
 * Print an overview over the available params for the config.
 */
public class Validate implements Command {
    protected File file = null;

    @Override
    public void execute(ProvidenceConfig config) throws IOException {
        config.load(file);
    }

    private void setFile(File file) {
        this.file = file;
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " [...] validate", parent.getVersion(), "");
        parser.add(new Argument("file", "Config file to validate", Parser.file(this::setFile), null, null, false, true, false));
        return parser;
    }
}
