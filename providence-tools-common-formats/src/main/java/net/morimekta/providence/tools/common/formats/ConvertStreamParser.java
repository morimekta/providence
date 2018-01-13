package net.morimekta.providence.tools.common.formats;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.util.Parser;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class ConvertStreamParser implements Parser<ConvertStream> {
    private final Format defaultFormat;

    public ConvertStreamParser() {
        this(Format.json);
    }

    public ConvertStreamParser(Format defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    @Override
    public ConvertStream parse(@Nonnull String next) {
        Format format = defaultFormat;
        File file = null;
        boolean base64 = false;

        for (;;) {
            if (next.startsWith("file:")) {
                file = new File(next.substring(5));
                break;
            }
            String[] parts = next.split("[,]", 2);
            if ("base64".equals(parts[0])) {
                base64 = true;
            } else {
                try {
                    format = Format.valueOf(parts[0]);
                } catch (IllegalArgumentException iae) {
                    throw new ArgumentException("No such format '" + parts[0] + "'");
                }
            }
            if (parts.length == 1) {
                break;
            } else {
                next = parts[1];
            }
        }

        return new ConvertStream(format, file, base64);
    }
}
