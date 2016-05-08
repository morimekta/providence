package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JOptions;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.Collection;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JConstantsFormat {
    private static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JOptions options;
    private final JHelper  helper;

    JConstantsFormat(JHelper helper, JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, CDocument document) throws GeneratorException {
        ValueBuilder value = new ValueBuilder(writer, options, helper);

        writer.format("package %s;", helper.getJavaPackage(document))
              .newline();

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

            String name = c.getName();
            JField constant = new JField(c, helper, 1);
            switch (c.getType()) {
                case MESSAGE: {
                    String instance = helper.getValueType(c.getDescriptor());
                    writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                          .appendln("static {")
                          .begin()
                          .formatln("%s = %s.builder()", name, instance)
                          .begin(DBL_INDENT);

                    PMessage<?> message = (PMessage<?>) c.getDefaultValue();
                    int i = 0;
                    for (PField f : message.descriptor()
                                           .getFields()) {
                        CField cField = (CField) f;
                        JField field = new JField(cField, helper, i++);
                        if (message.has(f.getKey())) {
                            writer.formatln(".%s(", field.setter());
                            value.appendTypedValue(message.get(f.getKey()), f.getDescriptor());
                            writer.append(")");
                        }
                    }

                    writer.formatln(".build();", name)
                          .end()
                          .end()
                          .appendln('}');
                    break;
                }
                case LIST:
                case SET: {
                    PContainer<?> lDesc = (PContainer<?>) c.getDescriptor();
                    PDescriptor itemDesc = lDesc.itemDescriptor();

                    writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                          .appendln("static {")
                          .begin()
                          .formatln("%s = new %s<%s>()",
                                    name,
                                    constant.builderInstanceType(),
                                    helper.getFieldType(itemDesc))
                          .begin(DBL_INDENT);

                    @SuppressWarnings("unchecked")
                    Collection<Object> items = (Collection<Object>) c.getDefaultValue();
                    for (Object item : items) {
                        writer.appendln(".add(")
                              .begin();

                        value.appendTypedValue(item, itemDesc);

                        writer.end()
                              .append(")");
                    }

                    writer.formatln(".build();", name);
                    writer.end()
                          .end()
                          .appendln('}');
                    break;
                }
                case MAP: {
                    JField field = new JField(c, helper, 1);

                    PMap<?,?> mDesc = (PMap<?,?>) c.getDescriptor();
                    PDescriptor itemDesc = mDesc.itemDescriptor();
                    PDescriptor keyDesc = mDesc.keyDescriptor();

                    writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                          .appendln("static {")
                          .begin()
                          .formatln("%s = new %s<>()", name, field.builderInstanceType())
                          .begin(DBL_INDENT);

                    @SuppressWarnings("unchecked")
                    Map<Object, Object> items = (Map<Object, Object>) c.getDefaultValue();
                    for (Map.Entry<Object, Object> item : items.entrySet()) {
                        writer.appendln(".put(")
                              .begin();

                        value.appendTypedValue(item.getKey(), keyDesc);

                        writer.append(", ");

                        value.appendTypedValue(item.getValue(), itemDesc);

                        writer.end()
                              .append(")");
                    }

                    writer.formatln(".build();", name);
                    writer.end()
                          .end()
                          .appendln('}');
                    break;
                }
                default:
                    writer.formatln("public static final %s %s = ", helper.getValueType(c.getDescriptor()), c.getName())
                          .begin(DBL_INDENT);
                    value.appendTypedValue(c.getDefaultValue(), c.getDescriptor());
                    writer.append(';')
                          .end();
            }
        }

        writer.end()
              .appendln('}');
    }
}
