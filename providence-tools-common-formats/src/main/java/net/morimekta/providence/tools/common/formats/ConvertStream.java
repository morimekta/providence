package net.morimekta.providence.tools.common.formats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Convert params for input or output of providence data.
 */
public class ConvertStream {
    // expected format.
    public final Format format;
    // If file is set: read / write file, otherwise use std in / out.
    public final File file;
    // If the content is (or should be) base 64 encoded.
    public final boolean base64;
    // If the content is (or should be) base 64 encoded line wrapped and padded.
    public final boolean base64mime;

    private static final String PARENT_PARENT = ".." + File.separator + ".." + File.separator;

    public ConvertStream(Format format) {
        this(format, null, false, false);
    }

    public ConvertStream(Format format,
                         File file,
                         boolean base64,
                         boolean base64mime) {
        this.format = format;
        this.file = file;
        this.base64 = base64;
        this.base64mime = base64mime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), format, base64, base64mime, file);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !getClass().equals(o.getClass())) return false;
        ConvertStream other = (ConvertStream) o;
        return base64 == other.base64 &&
               base64mime == other.base64mime &&
               Objects.equals(format, other.format) &&
               Objects.equals(file, other.file);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean hasFormat = false;
        if (format != null) {
            hasFormat = true;
            builder.append(format.name());
        }
        if (base64) {
            if (hasFormat) {
                builder.append(',');
            }
            builder.append("base64");
            hasFormat = true;
        }
        if (base64mime) {
            if (hasFormat) {
                builder.append(',');
            }
            builder.append("base64mime");
            hasFormat = true;
        }
        if (file != null) {
            if (hasFormat) {
                builder.append(',');
            }
            builder.append("file:");
            Path pwd = new File(System.getenv("PWD")).toPath();
            try {
                pwd = pwd.toFile().getCanonicalFile().toPath();
            } catch (IOException e) {
                // ignore.
            }
            String abs = file.getAbsolutePath();
            String rel = pwd.relativize(file.getAbsoluteFile()
                                            .toPath())
                            .toString();
            if (abs.length() < rel.length() || rel.startsWith(PARENT_PARENT)) {
                builder.append(abs);
            } else {
                builder.append(rel);
            }
            hasFormat = true;
        }

        if (!hasFormat) {
            builder.append("none");
        }
        return builder.toString();
    }
}
