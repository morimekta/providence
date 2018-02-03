package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.config.ProvidenceConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolve an overview over the available params for the config.
 */
public class Validate extends CommandBase {
    private List<File> files = new ArrayList<>();

    @Override
    public void execute(ProvidenceConfig config) throws IOException {
        for (File file : files) {
            config.getConfig(file);
        }
    }

    private void addFile(File file) {
        this.files.add(file);
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " [...] validate",
                                                   parent.getVersion(),
                                                   "Verify content of config files, also checking includes");
        parser.add(new Argument("file", "Config files to validate", Parser.file(this::addFile), null, null, true, true, false));
        return parser;
    }
}
