package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class BaseEnumFormatter {
    private final IndentedPrintWriter       writer;
    private final List<EnumMemberFormatter> formatters;

    public BaseEnumFormatter(IndentedPrintWriter writer, List<EnumMemberFormatter> formatters) {
        this.writer = writer;
        this.formatters = ImmutableList.copyOf(formatters);
    }

    public void appendEnumClass(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        if (type.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(type.getComment())
                    .finish();
        }
        formatters.forEach(f -> f.appendClassAnnotations(type));

        writer.formatln("public enum %s", simpleClass)
              .begin();

        Set<String> impl = new LinkedHashSet<>();
        formatters.forEach(f -> impl.addAll(f.getExtraImplements(type)));
        if (impl.size() > 0) {
            writer.formatln("    implements ")
                  .begin(   "               ");
            boolean first = true;
            for (String i : impl) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',').appendln();
                }
                writer.append(i);
            }
            writer.end();
        }

        writer.append(" {");

        appendEnumValues(type);
        appendEnumFields(type);
        appendEnumConstructor(type);

        formatters.forEach(f -> f.appendMethods(type));
        formatters.forEach(f -> f.appendExtraProperties(type));

        writer.end()
              .appendln('}');
    }

    private void appendEnumValues(CEnumDescriptor type) {
        for (CEnumValue v : type.getValues()) {
            if (v.getComment() != null) {
                new BlockCommentBuilder(writer)
                        .comment(v.getComment())
                        .finish();
            }
            if (JAnnotation.isDeprecated(v)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
            writer.formatln("%s(%d, \"%s\"),",
                            JUtils.enumConst(v),
                            v.getValue(),
                            v.getName());
        }
        writer.appendln(';')
              .newline();
    }

    private void appendEnumFields(CEnumDescriptor type) {
        writer.appendln("private final int mValue;")
              .appendln("private final String mName;")
              .newline();
    }

    private void appendEnumConstructor(CEnumDescriptor type) {
        writer.formatln("%s(int value, String name) {", JUtils.getClassName(type))
              .begin()
              .appendln("mValue = value;")
              .appendln("mName = name;")
              .end()
              .appendln("}")
              .newline();
    }
}
