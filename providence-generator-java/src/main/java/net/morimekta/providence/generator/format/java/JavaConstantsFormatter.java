package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseConstantsFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JavaConstantsFormatter implements BaseConstantsFormatter {
    private static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final IndentedPrintWriter writer;

    public JavaConstantsFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendConstantsClass(CProgram document) throws GeneratorException {
        ValueBuilder value = new ValueBuilder(writer, helper);

        if (document.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(document.getComment())
                    .finish();
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", helper.getConstantsClassName(document))
              .begin()
              .formatln("private %s() {}", helper.getConstantsClassName(document));

        for (CField c : document.getConstants()) {
            writer.newline();

            try {
                String name = c.getName();

                writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                      .appendln("static {")
                      .begin()
                      .formatln("%s = ", name)
                      .begin();

                value.appendTypedValue(c.getDefaultValue(), c.getDescriptor());

                writer.append(';')
                      .end()
                      .appendln('}')
                      .end();

            } catch (Exception e) {
                throw new GeneratorException("Unable to generate constant " + document.getProgramName() + "." + c.getName(),
                                             e);
            }
        }

        writer.end()
              .appendln('}');
    }
}
