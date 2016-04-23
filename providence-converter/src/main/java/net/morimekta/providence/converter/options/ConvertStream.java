package net.morimekta.providence.converter.options;

import java.io.File;

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
}
