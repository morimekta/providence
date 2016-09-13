package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.serializer.SerializerException;

/**
 *
 */
public interface Command {
    void execute(ProvidenceConfig config) throws SerializerException;

    ArgumentParser parser(ArgumentParser parent);
}
