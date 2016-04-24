package net.morimekta.providence.compiler;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.Collections;
import java.util.LinkedList;

import static net.morimekta.console.FormatString.except;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class GeneratorOptionHandler extends OptionHandler<GeneratorOptions> {
    public GeneratorOptionHandler(CmdLineParser parser, OptionDef option, Setter<GeneratorOptions> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        if (params.size() == 0) {
            throw except(owner, "");
        }

        GeneratorSpec generator = null;
        LinkedList<String> options = new LinkedList<>();

        String[] gen = params.getParameter(0).split("[:]", 2);
        if (gen.length > 2) {
            throw except(owner, "");
        }

        for (GeneratorSpec spec : GeneratorSpec.values()) {
            if (gen[0].equalsIgnoreCase(spec.name())) {
                generator = spec;
                break;
            }
        }
        if (generator == null) {
            throw except(owner, "Unknown language " + gen[0]);
        }

        if (gen.length > 1) {
            Collections.addAll(options, gen[1].split("[,]"));
        }

        setter.addValue(new GeneratorOptions(generator, options));
        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "generator";
    }
}
