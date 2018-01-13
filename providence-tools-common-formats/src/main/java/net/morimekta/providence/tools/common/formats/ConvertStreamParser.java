package net.morimekta.providence.tools.common.formats;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.util.Parser;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * arg4j options handler for stream specification (file / url, format).
 */
public class ConvertStreamParser implements Parser<ConvertStream> {
    private final ConvertStream defaultStream;

    public ConvertStreamParser(@Nonnull ConvertStream defaultStream) {
        this.defaultStream = defaultStream;
    }

    @Override
    public ConvertStream parse(@Nonnull String next) {
        Format format = defaultStream.format;
        File file = defaultStream.file;
        boolean base64 = defaultStream.base64;
        boolean base64mime = defaultStream.base64mime;

        for (;;) {
            if (next.startsWith("file:")) {
                file = new File(next.substring(5));
                break;
            }
            String[] parts = next.split("[,]", 2);
            if ("base64".equals(parts[0])) {
                base64 = true;
                base64mime = false;
            } else if ("base64mime".equals(parts[0])) {
                base64mime = true;
                base64 = false;
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

        return new ConvertStream(format, file, base64, base64mime);
    }
}
