package net.morimekta.providence.tools.common.formats;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.util.Parser;

import java.io.File;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class ConvertStreamParser implements Parser<ConvertStream> {
    @Override
    public ConvertStream parse(String next) {
        Format format = Format.json;
        File file = null;
        boolean base64 = false;

        if (next.startsWith("file:")) {
            file = new File(next.substring(5));
        }
        for (String part : next.split("[,]", 2)) {
            if (part.startsWith("file:")) {
                file = new File(part.substring(5));
            } else if ("base64".equals(part)) {
                base64 = true;
            } else {
                try {
                    format = Format.valueOf(part);
                } catch (IllegalArgumentException iae) {
                    throw new ArgumentException("No such format '" + part + "'");
                }
            }
        }

        return new ConvertStream(format, file, base64);
    }
}
