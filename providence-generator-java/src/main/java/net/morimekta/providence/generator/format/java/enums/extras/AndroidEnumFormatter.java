package net.morimekta.providence.generator.format.java.enums.extras;

import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class AndroidEnumFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;

    public AndroidEnumFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }
}
