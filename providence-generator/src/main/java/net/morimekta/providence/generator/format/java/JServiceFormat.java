package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;

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
    public void format(IndentedPrintWriter writer, CService cs) throws GeneratorException, IOException {
        JService service = new JService(cs, helper);

        if (cs.getComment() != null) {
            JUtils.appendBlockComment(writer, cs.getComment());
        }


        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", service.className())
              .begin();

        appendIface(writer, service);

        appendStructs(writer, service);

        // private constructor should defeat instantiation.
        writer.formatln("private %s() {}", service.className());

        writer.end()
              .appendln('}');
    }

    private void appendStructs(IndentedPrintWriter writer, JService service) throws GeneratorException, IOException {
        for (JServiceMethod method : service.methods()) {
            JMessage<?> request = new JMessage<>(method.getMethod().getRequestType(), helper);
            writer.formatln("// type --> %s", request.descriptor().getName());

            messageFormat.format(writer, method.getMethod().getRequestType(), service.getService());

            if (method.getMethod().getResponseType() != null) {
                JMessage<?> response = new JMessage<>(method.getMethod().getResponseType(), helper);
                writer.formatln("// type <-- %s", response.descriptor().getName());

                messageFormat.format(writer, method.getMethod().getResponseType(), service.getService());
            }
        }
    }

    private void appendIface(IndentedPrintWriter writer, JService service) throws GeneratorException {
        writer.appendln("public interface IFace {")
              .begin();

        boolean firstMethod = true;
        for (JServiceMethod method : service.methods()) {
            if (firstMethod) {
                firstMethod = false;
            } else {
                writer.newline();
            }

            if (method.getMethod().getComment() != null) {
                JUtils.appendBlockComment(writer, method.getMethod().getComment());
            }

            // Field ID 0 is the return type.
            JField ret = method.getResponse();
            if (ret != null) {
                writer.appendln(ret.valueType());
            } else {
                writer.appendln("void");
            }

            // Use the un-changed method name.
            // TODO: change to use a camel-cased name.
            writer.format(" %s(", method.name());

            boolean first = true;
            for (JField param : method.params()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(", ");
                }
                writer.format("%s %s", param.valueType(), param.param());
            }

            writer.format(")");

            first = true;
            for (JField ex : method.exceptions()) {
                if (first) {
                    first = false;
                    writer.appendln("        throws ");
                } else {
                    writer.append(",");
                    writer.appendln("               ");
                }

                writer.append(ex.instanceType());
            }

            writer.format(";");
        }

        writer.end()
              .appendln('}')
              .newline();
    }
}
