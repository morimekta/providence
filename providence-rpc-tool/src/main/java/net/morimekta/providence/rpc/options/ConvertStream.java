package net.morimekta.providence.rpc.options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Convert params for input or output of providence data.
 */
public class ConvertStream {
    // expected format.
    public final Format format;
    // If file is set: read / write file, otherwise use std in / out.
    public final File   file;

    public ConvertStream(Format format, File file) {
        this.format = format;
        this.file = file;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean hasFormat = false;
        if (format != null) {
            hasFormat = true;
            builder.append(format.name());
        }
        if (file != null) {
            if (hasFormat) {
                builder.append(',');
            }
            Path pwd = Paths.get(".")
                            .toFile()
                            .getAbsoluteFile()
                            .toPath();
            builder.append(file.toPath()
                               .relativize(pwd)
                               .toString());
        } else if (!hasFormat) {
            builder.append("none");
        }
        return builder.toString();
    }
}
