package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.serializer.SerializerException;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Print an overview over the available params for the config.
 */
public class Params implements Command {
    protected File file = null;

    @Override
    public void execute(ProvidenceConfig config) throws SerializerException {
        try {
            List<ProvidenceConfig.Param> paramList = config.params(file);
            paramList.sort((a, b) -> a.name.compareTo(b.name));
            for (ProvidenceConfig.Param param : paramList) {
                System.out.println(param.toString());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setFile(File file) {
        this.file = file;
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " params", parent.getVersion(), "");
        parser.add(new Argument("file", "Config file to read params for, with includes", Parser.file(this::setFile), null, null, false, true, false));
        return parser;
    }
}
