package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PClient;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseServiceFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JService;
import net.morimekta.providence.generator.format.java.utils.JServiceMethod;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;

public class JavaServiceFormatter implements BaseServiceFormatter {
    private final JHelper              helper;
    private final JavaMessageFormatter messageFormat;
    private final IndentedPrintWriter  writer;

    JavaServiceFormatter(IndentedPrintWriter writer, JHelper helper, JavaMessageFormatter messageFormat) {
        this.writer = writer;
        this.helper = helper;
        this.messageFormat = messageFormat;
    }

    @Override
    public void appendServiceClass(CService cs) throws GeneratorException, IOException {
        JService service = new JService(cs, helper);

        if (cs.getDocumentation() != null) {
            new BlockCommentBuilder(writer)
                    .comment(cs.getDocumentation())
                    .finish();
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", service.className())
              .begin();

        appendIface(writer, service);

        appendClient(writer, service);

        appendProcessor(writer, service);

        appendDescriptor(writer, service);

        appendStructs(writer, service);

        // private constructor should defeat instantiation.
        writer.formatln("private %s() {}", service.className());

        writer.end()
              .appendln('}');
    }

    private void appendClient(IndentedPrintWriter writer, JService service) throws GeneratorException {
        writer.appendln("public static class Client")
              .formatln("        extends %s", PClient.class.getName())
              .formatln("        implements Iface {")
              .begin();

        writer.formatln("private final %s handler;", PServiceCallHandler.class.getName())
              .newline();

        writer.formatln("public Client(%s handler) {", PServiceCallHandler.class.getName())
              .appendln("    this.handler = handler;")
              .appendln('}')
              .newline();

        boolean firstMethod = true;
        for (JServiceMethod method : service.methods()) {
            if (firstMethod) {
                firstMethod = false;
            } else {
                writer.newline();
            }

            writer.appendln("@Override");

            // Field ID 0 is the return type.
            JField ret = method.getResponse();

            writer.appendln("public ");
            if (ret != null) {
                writer.append(ret.valueType());
            } else {
                writer.append("void");
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

            writer.format(" {")
                  .end()
                  .begin();

            writer.formatln("%s._Builder rq = %s.builder();", method.getRequestClass(), method.getRequestClass());

            for (JField param : method.params()) {
                writer.formatln("rq.%s(%s);", param.setter(), param.param());
            }

            String type = method.getMethod().isOneway()
                          ? PServiceCallType.ONEWAY.name()
                          : PServiceCallType.CALL.name();
            writer.newline()
                  .formatln("%s call = new %s<>(\"%s\", %s.%s, getNextSequenceId(), rq.build());",
                            PServiceCall.class.getName(),
                            PServiceCall.class.getName(),
                            method.name(),
                            PServiceCallType.class.getName(),
                            type)
                  .appendln();

            if (method.getResponseClass() != null) {
                writer.format("%s resp = ", PServiceCall.class.getName());
            }

            writer.format("handler.handleCall(call, %s.kDescriptor);", service.className());

            if (method.getResponseClass() != null) {
                writer.formatln("%s msg = (%s) resp.getMessage();",
                                method.getResponseClass(), method.getResponseClass());

                writer.newline()
                      .formatln("if (resp.getType() == %s.%s) {", PServiceCallType.class.getName(), PServiceCallType.EXCEPTION.name())
                      .formatln("    throw (%s) resp.getMessage();",
                                PApplicationException.class.getName())
                      .appendln('}');

                writer.appendln("if (msg.unionField() != null) {")
                      .begin()
                      .appendln("switch (msg.unionField()) {")
                      .begin();
                if (method.exceptions().length > 0) {
                    for (JField ex : method.exceptions()) {
                        writer.formatln("case %s:", ex.fieldEnum())
                              .formatln("    throw msg.%s();", ex.getter());
                    }
                }
                if (method.getResponse() != null) {
                    writer.formatln("case %s:", method.getResponse().fieldEnum());
                    if (method.getResponse().isVoid()) {
                        writer.formatln("    return;");
                    } else {
                        writer.formatln("    return msg.%s();", method.getResponse().getter());
                    }
                }

                writer.end()
                      .appendln("}")
                      .end()
                      .appendln("}")
                      .newline();

                // In case there is no return value, and no exception,
                // the union field is not set. This *should* cause an error.
                writer.formatln("throw new %s(\"Result field for %s.%s() not set\",",
                                PApplicationException.class.getName(),
                                service.getService().getQualifiedName(null),
                                method.name())
                      .formatln("          %s %s.%s);",
                                PApplicationException.class.getName().replaceAll(".", " "),
                                PApplicationExceptionType.class.getName(),
                                PApplicationExceptionType.MISSING_RESULT.name());
            }

            writer.end()
                  .appendln('}');
        }

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendProcessor(IndentedPrintWriter writer, JService service) throws GeneratorException {
        writer.formatln("public static class Processor implements %s {", PProcessor.class.getName())
              .begin()
              .appendln("private final Iface impl;");

        writer.formatln("public Processor(Iface impl) {")
              .appendln("    this.impl = impl;")
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s getDescriptor() {", PService.class.getName())
              .appendln("    return kDescriptor;")
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public <Request extends %s<Request, RequestField>,", PMessage.class.getName())
              .formatln("        Response extends %s<Response, ResponseField>,", PMessage.class.getName())
              .formatln("        RequestField extends %s,", PField.class.getName())
              .formatln("        ResponseField extends %s>", PField.class.getName())
              .formatln("%s<Response, ResponseField> handleCall(", PServiceCall.class.getName())
              .formatln("        %s<Request, RequestField> call,", PServiceCall.class.getName())
              .formatln("        %s service)", PService.class.getName())
              .formatln("        throws %s,", IOException.class.getName())
              .formatln("               %s {", SerializerException.class.getName())
              .begin();

        writer.appendln("switch(call.getMethod()) {")
              .begin();

        for (JServiceMethod method : service.methods()) {
            writer.formatln("case \"%s\": {", method.name())
                  .begin();
            if (method.getResponseClass() != null) {
                writer.formatln("%s._Builder rsp = %s.builder();", method.getResponseClass(), method.getResponseClass());
            }

            if (method.exceptions().length > 0) {
                writer.appendln("try {")
                      .begin();
            }

            writer.formatln("%s req = (%s) call.getMessage();",
                            method.getRequestClass(),
                            method.getRequestClass());

            String indent = "      " + Strings.times(" ", method.methodName().length());
            if (method.getResponse() != null && !method.getResponse().isVoid()) {
                writer.formatln("%s result =", method.getResponse().valueType());
                writer.appendln("        ");
                indent += "        ";
            } else {
                writer.appendln();
            }

            writer.format("impl.%s(", method.methodName())
                  .begin(indent);

            boolean first = true;
            for (JField param : method.params()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',')
                          .appendln();
                }
                writer.format("req.%s()", param.getter());
            }
            writer.end()
                  .append(");");

            if (method.getResponse() != null) {
                if (method.getResponse().isVoid()) {
                    writer.formatln("rsp.%s();", method.getResponse().setter());
                } else {
                    writer.formatln("rsp.%s(result);", method.getResponse().setter());
                }
            }

            if (method.exceptions().length > 0) {
                writer.end();
                for (JField ex : method.exceptions()) {
                    writer.formatln("} catch (%s e) {", ex.instanceType())
                          .begin()
                          .formatln("rsp.%s(e);", ex.setter())
                          .end();
                }
                writer.appendln('}');
            }

            if (method.getResponseClass() != null) {
                String spaces = PServiceCall.class.getName().replaceAll("[\\S]", " ");
                writer.formatln("%s reply =", PServiceCall.class.getName())
                      .formatln("        new %s<>(call.getMethod(),",
                                PServiceCall.class.getName())
                      .formatln("            %s   %s.%s,",
                                spaces,
                                PServiceCallType.class.getName(),
                                PServiceCallType.REPLY.name())
                      .formatln("            %s   call.getSequence(),",
                                spaces)
                      .formatln("            %s   rsp.build());",
                                spaces)
                      .appendln("return reply;");
            } else {
                // No reply, but it's fine.
                writer.appendln("return null;");
            }

            writer.end()
                  .appendln('}');
        }

        writer.appendln("default: {")
              .begin()
              .formatln("%s ex =", PApplicationException.class.getName())
              .formatln("        new %s(", PApplicationException.class.getName())
              .formatln("                \"Unknown method \\\"\" + call.getMethod() + \"\\\" on %s.\",",
                        service.getService().getQualifiedName(null))
              .formatln("                %s.%s);",
                        PApplicationExceptionType.class.getName(),
                        PApplicationExceptionType.UNKNOWN_METHOD.getName());

        String spaces = PServiceCall.class.getName().replaceAll("[\\S]", " ");
        writer.formatln("%s reply =", PServiceCall.class.getName())
              .formatln("        new %s(call.getMethod(),",
                        PServiceCall.class.getName())
              .formatln("            %s %s.%s,",
                        spaces,
                        PServiceCallType.class.getName(),
                        PServiceCallType.EXCEPTION.name())
              .formatln("            %s call.getSequence(),",
                        spaces)
              .formatln("            %s ex);",
                        spaces)
              .appendln("return reply;");

        writer.end()
              .appendln('}')
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(IndentedPrintWriter writer, JService service) throws GeneratorException {
        writer.formatln("public enum Method implements %s {", PServiceMethod.class.getName())
              .begin();

        for (JServiceMethod method : service.methods()) {
            String responseDesc = method.getResponseClass() == null
                                  ? "null"
                                  : method.getResponseClass() + ".kDescriptor";

            writer.formatln("%s(\"%s\", %b, %s.kDescriptor, %s),",
                            method.constant(),
                            method.name(),
                            method.getMethod().isOneway(),
                            method.getRequestClass(),
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
                       new JService(other, helper).className() + ".provider()";
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

        writer.formatln("private static class _Provider implements %s {",
                        PServiceProvider.class.getName())
              .begin()
              .appendln("@Override")
              .formatln("public %s getService() {", PService.class.getName())
              .appendln("    return kDescriptor;")
              .appendln("}")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final %s kDescriptor = new _Descriptor();",
                        PService.class.getName())
              .newline();

        writer.formatln("public static %s provider() {",
                        PServiceProvider.class.getName())
              .appendln("    return new _Provider();")
              .appendln('}')
              .newline();
    }

    private void appendStructs(IndentedPrintWriter writer, JService service) throws GeneratorException, IOException {
        for (JServiceMethod method : service.methods()) {
            JMessage<?> request = new JMessage<>(method.getMethod().getRequestType(), helper);
            writer.formatln("// type --> %s", request.descriptor().getName());

            messageFormat.appendMessageClass(method.getMethod().getRequestType());

            if (method.getMethod().getResponseType() != null) {
                JMessage<?> response = new JMessage<>(method.getMethod().getResponseType(), helper);
                writer.formatln("// type <-- %s", response.descriptor().getName());

                messageFormat.appendMessageClass(method.getMethod().getResponseType());
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
        for (JServiceMethod method : service.declaredMethods()) {
            if (firstMethod) {
                firstMethod = false;
            } else {
                writer.newline();
            }

            if (method.getMethod().getDocumentation() != null) {
                new BlockCommentBuilder(writer)
                        .comment(method.getMethod().getDocumentation())
                        .finish();
            }

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
