package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.config.ProvidenceConfig;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Print an overview over the available params for the config.
 */
public class Params implements Command {
    private File file = null;

    @Override
    public void execute(ProvidenceConfig config) throws IOException {
        List<ProvidenceConfig.Param> paramList = config.params(file);
        paramList.sort(Comparator.comparing(a -> a.name));
        for (ProvidenceConfig.Param param : paramList) {
            System.out.println(param.toString());
        }
    }

    private void setFile(File file) {
        this.file = file;
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " [...] params", parent.getVersion(), "");
        parser.add(new Argument("file", "Config file to read params for, with includes", Parser.file(this::setFile), null, null, false, true, false));
        return parser;
    }
}
