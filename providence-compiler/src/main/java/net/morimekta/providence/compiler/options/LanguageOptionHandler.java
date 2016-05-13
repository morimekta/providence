package net.morimekta.providence.compiler.options;

import net.morimekta.providence.generator.Language;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Options handler for format. Avoids listing all the formats in one go.
 */
public class LanguageOptionHandler extends OptionHandler<Language> {
    public LanguageOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Language> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        String s = params.getParameter(0)
                         .replaceAll("-", "_");
        Language value = null;
        for (Language o : Language.values()) {
            if (o.name()
                 .equalsIgnoreCase(s)) {
                value = o;
                break;
            }
        }
        if (value == null) {
            if (option.isArgument()) {
                throw new CmdLineException(owner, Messages.ILLEGAL_OPERAND, option.toString(), s);
            } else {
                throw new CmdLineException(owner, Messages.ILLEGAL_OPERAND, params.getParameter(-1), s);
            }
        }
        setter.addValue(value);
        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "lang";
    }
}
