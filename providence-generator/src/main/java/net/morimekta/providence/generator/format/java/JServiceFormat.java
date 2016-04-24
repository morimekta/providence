package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PProcessor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.serializer.MessageReader;
import net.morimekta.providence.serializer.MessageWriter;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;

import static net.morimekta.providence.generator.format.java.JUtils.camelCase;

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

        appendProcessor(writer, service);

        appendDescriptor(writer, service);

        appendStructs(writer, service);

        // private constructor should defeat instantiation.
        writer.formatln("private %s() {}", service.className());

        writer.end()
              .appendln('}');
    }

    private void appendProcessor(IndentedPrintWriter writer, JService service) {
        writer.formatln("public static class Processor implements %s {", PProcessor.class.getName())
              .begin()
              .appendln("private final Iface impl;");

        writer.formatln("public Processor(Iface impl) {")
              .appendln("    this.impl = impl;")
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public boolean process(%s reader, %s writer) {",
                        MessageReader.class.getName(),
                        MessageWriter.class.getName())
              .begin()
              .appendln("return true;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(IndentedPrintWriter writer, JService service) throws GeneratorException {
        writer.formatln("public enum Method implements %s {", PServiceMethod.class.getName())
              .begin();

        for (JServiceMethod method : service.methods()) {
            String requestDesc = camelCase("", method.getMethod().getRequestType().getName());
            String responseDesc = method.getMethod().getResponseType() == null
                                  ? "null"
                                  : camelCase("", method.getMethod().getResponseType().getName()) + ".kDescriptor";

            writer.formatln("%s(\"%s\", %b, %s.kDescriptor, %s),",
                            method.constant(),
                            method.name(),
                            method.getMethod().isOneway(),
                            requestDesc,
                            responseDesc);
        }

        writer.appendln(';')
              .newline();

        writer.appendln("private final String name;")
              .appendln("private final boolean oneway;")
              .formatln("private final %s request;", PStructDescriptor.class.getName())
              .formatln("private final %s response;", PUnionDescriptor.class.getName())
              .newline();

        writer.formatln("private Method(String name, boolean oneway, %s request, %s response) {",
                        PStructDescriptor.class.getName(), PUnionDescriptor.class.getName())
              .appendln("    this.name = name;")
              .appendln("    this.oneway = oneway;")
              .appendln("    this.request = request;")
              .appendln("    this.response = response;")
              .appendln('}')
              .newline();

        writer.appendln("public String getName() {")
              .appendln("    return name;")
              .appendln('}')
              .newline()
              .appendln("public boolean isOneway() {")
              .appendln("    return oneway;")
              .appendln('}')
              .newline()
              .formatln("public %s getRequestType() {",
                        PStructDescriptor.class.getName())
              .formatln("    return request;")
              .appendln('}')
              .newline()
              .formatln("public %s getResponseType() {",
                        PUnionDescriptor.class.getName())
              .formatln("    return response;")
              .appendln('}')
              .newline();

        writer.appendln("public static Method forName(String name) {")
              .begin()
              .appendln("switch (name) {")
              .begin();

        for (JServiceMethod method : service.methods()) {
            writer.formatln("case \"%s\": return %s;",
                            method.name(), method.constant());
        }

        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();

        String inherits = "null";
        if (service.getService().getExtendsService() != null) {
            CService other = (CService) service.getService().getExtendsService();
            inherits = helper.getJavaPackage(other) + "." +
                       new JService(other, helper).className() + ".kDescriptor";
        }

        writer.formatln("private static class _Descriptor extends %s {",
                        PService.class.getName())
              .begin()
              .appendln("private _Descriptor() {")
              .formatln("    super(\"%s\", \"%s\", %s, Method.values());",
                        service.getService().getPackageName(),
                        service.getService().getName(),
                        inherits)
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public Method getMethod(String name) {")
              .appendln("    return Method.forName(name);")
              .appendln("}")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final %s kDescriptor = new _Descriptor();",
                        PService.class.getName())
              .newline();
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
        String inherits = "";
        if (service.getService().getExtendsService() != null) {
            CService other = (CService) service.getService().getExtendsService();
            inherits = "extends " + helper.getJavaPackage(other) + "." +
                       new JService(other, helper).className() + ".Iface ";
        }

        writer.formatln("public interface Iface %s{", inherits)
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

            writer.format(" %s(", method.methodName())
                  .begin("        ");

            boolean first = true;
            for (JField param : method.params()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(",");
                }
                writer.formatln("%s %s", param.valueType(), param.param());
            }

            writer.end()
                  .format(")")
                  .formatln("        throws %s", IOException.class.getName())
                  .begin(   "               ");

            for (JField ex : method.exceptions()) {
                writer.append(",");
                writer.appendln(ex.instanceType());
            }

            writer.format(";")
                  .end();
        }

        writer.end()
              .appendln('}')
              .newline();
    }
}
