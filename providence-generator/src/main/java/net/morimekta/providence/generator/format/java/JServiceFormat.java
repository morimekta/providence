package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * Created by morimekta on 4/24/16.
 */
public class JServiceFormat {
    private final JHelper helper;
    private final JOptions options;
    private final JMessageFormat messageFormat;

    public JServiceFormat(JHelper helper, JOptions options, JMessageFormat messageFormat) {
        this.helper = helper;
        this.options = options;
        this.messageFormat = messageFormat;
    }

    @SuppressWarnings("unused")
    public void format(IndentedPrintWriter writer, CService service) throws GeneratorException {
        if (service.getComment() != null) {
            JUtils.appendBlockComment(writer, service.getComment());
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", JUtils.getClassName(service))
              .begin();

        appendIface(writer, service);

        appendStructs(writer, service);

        // private constructor should defeat instantiation.
        writer.formatln("private %s() {}", JUtils.getClassName(service));

        writer.end()
              .appendln('}');
    }

    private void appendStructs(IndentedPrintWriter writer, CService service) {

    }

    private void appendIface(IndentedPrintWriter writer, CService service) throws GeneratorException {
        writer.appendln("public interface IFace {")
              .begin();

        boolean firstMethod = true;
        for (CServiceMethod method : service.getMethods()) {
            if (firstMethod) {
                firstMethod = false;
            } else {
                writer.newline();
            }

            if (method.getComment() != null) {
                JUtils.appendBlockComment(writer, method.getComment());
            }

            // Field ID 0 is the return type.
            if (method.getResponseType() != null) {
                PField ret = method.getResponseType()
                                   .getField(0);
                if (ret != null) {
                    writer.appendln(helper.getValueType(ret.getDescriptor()));
                } else {
                    writer.appendln("void");
                }
            } else {
                writer.appendln("void");
            }

            // Use the un-changed method name.
            // TODO: change to use a camel-cased name.
            writer.format(" %s(", method.getName());

            boolean first = true;
            for (CField param : method.getRequestType().getFields()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(", ");
                }
                JField field = new JField(param, helper, param.getKey());

                writer.format("%s %s", field.valueType(), field.param());
            }

            writer.format(")");

            if (method.getResponseType() != null) {
                CUnionDescriptor resp = method.getResponseType();
                first = true;
                for (CField ex : resp.getFields()) {
                    if (ex.getKey() != 0) {
                        if (first) {
                            first = false;
                            writer.appendln("    throws ");
                        } else {
                            writer.append(", ");
                        }

                        JField field = new JField(ex, helper, ex.getKey());

                        writer.append(field.instanceType());
                    }
                }
            }

            writer.format(";");
        }

        writer.end()
              .appendln('}')
              .newline();
    }
}
