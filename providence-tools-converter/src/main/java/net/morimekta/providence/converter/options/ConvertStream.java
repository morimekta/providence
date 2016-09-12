package net.morimekta.providence.converter.options;

import java.io.File;
import java.nio.file.Path;

/**
 * Convert params for input or output of providence data.
 */
public class ConvertStream {
    // expected format.
    public final Format format;
    // If file is set: read / write file, otherwise use std in / out.
    public final File file;

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
            Path pwd = new File(System.getenv("PWD")).getAbsoluteFile().toPath();
            String abs = file.getAbsolutePath();
            String rel = pwd.relativize(file.getAbsoluteFile()
                                            .toPath())
                            .toString();
            if (abs.length() < rel.length() || rel.startsWith("../../")) {
                builder.append(abs);
            } else {
                builder.append(rel);
            }
        } else if (!hasFormat) {
            builder.append("none");
        }
        return builder.toString();
    }
}
