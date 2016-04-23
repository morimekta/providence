package net.morimekta.providence.converter.options;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.io.File;

import static net.morimekta.console.FormatString.except;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class StreamOptionHandler extends OptionHandler<ConvertStream> {
    public StreamOptionHandler(CmdLineParser parser, OptionDef option, Setter<ConvertStream> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        if (params.size() == 0) {
            throw except(owner, "");
        }

        Format format = Format.json;
        File file = null;

        String next = params.getParameter(0);
        if (next.startsWith("file:")) {
            file = new File(next.substring(5));
        }
        for (String part : next.split("[,]", 2)) {
            if (part.startsWith("file:")) {
                file = new File(part.substring(5));
            } else {
                try {
                    format = Format.valueOf(part);
                } catch (IllegalArgumentException iae) {
                    throw except(owner, "No such format " + part);
                }
            }
        }

        setter.addValue(new ConvertStream(format, file));

        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "args";
    }
}
