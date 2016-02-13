package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JConstantsFormat {
    private static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JOptions options;
    private final JHelper  helper;

    public JConstantsFormat(JHelper helper, JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, CDocument document) throws GeneratorException {
        JValueFormat value = new JValueFormat(writer, options, helper);

        appendHeader(writer, value, document);

        if (document.getComment() != null) {
            JUtils.appendBlockComment(writer, document.getComment());
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", helper.getConstantsClassName(document))
              .begin()
              .formatln("private %s() {}", helper.getConstantsClassName(document))
              .newline();

        for (PField<?> c : document.getConstants()) {
            switch (c.getType()) {
                case MESSAGE:
                    String name = c.getName();
                    String instance = helper.getValueType(c.getDescriptor());
                    writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                          .appendln("static {")
                          .begin()
                          .formatln("%s._Builder builder = %s.builder();", instance, instance);

                    PMessage<?> message = (PMessage<?>) c.getDefaultValue();
                    int i = 0;
                    for (PField<?> f : message.descriptor()
                                              .getFields()) {
                        JField field = new JField(f, helper, i++);
                        if (message.has(f.getKey())) {
                            writer.formatln("builder.%s(", field.setter());
                            value.appendTypedValue(message.get(f.getKey()), f.getDescriptor());
                            writer.append(");");
                        }
                    }

                    writer.formatln("%s = builder.build();", name)
                          .end()
                          .appendln('}');
                    break;
                case LIST:
                case SET:
                    name = c.getName();
                    instance = helper.getInstanceClassName(c.getDescriptor());

                    PContainer<?, ?> lDesc = (PContainer<?, ?>) c.getDescriptor();
                    PDescriptor<?> itemDesc = lDesc.itemDescriptor();

                    writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                          .appendln("static {")
                          .begin()
                          .formatln("%s builder = new %s<>();", instance, instance);

                    @SuppressWarnings("unchecked")
                    Collection<Object> items = (Collection<Object>) c.getDefaultValue();
                    for (Object item : items) {
                        writer.appendln("builder.add(")
                              .begin(DBL_INDENT);

                        value.appendTypedValue(item, itemDesc);

                        writer.end()
                              .append(");");
                    }

                    if (c.getType() == PType.LIST) {
                        writer.formatln("%s = Collections.unmodifiableList(builder);", name);
                    } else {
                        writer.formatln("%s = Collections.unmodifiableSet(builder);", name);
                    }
                    writer.end()
                          .appendln('}');
                    break;
                case MAP:
                    break;
                default:
                    writer.formatln("public static final %s %s = ", helper.getValueType(c.getDescriptor()), c.getName())
                          .begin(DBL_INDENT);
                    value.appendTypedValue(c.getDefaultValue(), c.getDescriptor());
                    writer.append(';')
                          .end();
            }

            writer.newline();
        }

        writer.end()
              .appendln('}');
    }

    private void appendHeader(IndentedPrintWriter writer, JValueFormat value, CDocument document)
            throws GeneratorException {
        JHeader header = new JHeader(helper.getJavaPackage(document));
        for (PField field : document.getConstants()) {
            value.addTypeImports(header, field.getDescriptor());
        }

        header.format(writer);
    }
}
