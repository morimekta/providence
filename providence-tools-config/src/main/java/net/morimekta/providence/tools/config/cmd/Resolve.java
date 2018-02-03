package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.tools.common.formats.Format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolve the resulting config.
 */
public class Resolve extends CommandBase {
    private Serializer serializer = new PrettySerializer().config();
    private List<File> files      = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void execute(ProvidenceConfig config) throws IOException {
        if (files.isEmpty()) {
            throw new IllegalArgumentException("No config files to resolve");
        }

        PMessage message = null;
        for (File configFile : files) {
            if (message == null) {
                message = config.getConfig(configFile);
            } else {
                message = (PMessage) config.getConfig(configFile, message);
            }
        }

        serializer.serialize(System.out, message);
        System.out.println();
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " [...] resolve",
                                                   parent.getVersion(),
                                                   "Resolve config files to final config message.");
        parser.add(new Option("--format", "f", "fmt", "the output format", this::setSerializer, "pretty"));
        parser.add(new Argument("file", "Config files to resolve", Parser.file(this::addFile), null, null, true, true, false));
        return parser;
    }

    private void addFile(File file) {
        this.files.add(file);
    }

    private void setSerializer(String name) {
        Format format = Format.forName(name);
        serializer = format.createSerializer(false);
    }
}
