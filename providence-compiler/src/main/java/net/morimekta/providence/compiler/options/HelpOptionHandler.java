package net.morimekta.providence.compiler.options;

import net.morimekta.providence.generator.Language;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import static net.morimekta.console.FormatString.except;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class HelpOptionHandler extends OptionHandler<HelpOptions> {
    public HelpOptionHandler(CmdLineParser parser, OptionDef option, Setter<HelpOptions> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        if (params.size() > 1) {
            throw except(owner, "Only one help spec allowed");
        }

        Language language = null;

        if (params.size() > 0) {
            if (params.size() > 1) {
                throw except(owner, "Only one language can be showed help about.");
            }

            String lang = params.getParameter(0);
            for (Language spec : Language.values()) {
                if (lang.equalsIgnoreCase(spec.name())) {
                    language = spec;
                    break;
                }
            }
            if (language == null) {
                throw except(owner, "Unknown language " + lang);
            }
        }

        setter.addValue(new HelpOptions(language));
        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "[language]";
    }
}
