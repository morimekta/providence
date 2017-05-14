/*
 * Copyright 2016 Providence Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

import javax.annotation.Generated;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class JavaServiceFormatter implements BaseServiceFormatter {
    private final JHelper              helper;
    private final JavaMessageFormatter messageFormat;
    private final IndentedPrintWriter  writer;
    private final JavaOptions          options;
    private final String               version;

    JavaServiceFormatter(IndentedPrintWriter writer,
                         JHelper helper,
                         JavaMessageFormatter messageFormat,
                         JavaOptions options) {
        this.writer = writer;
        this.helper = helper;
        this.messageFormat = messageFormat;
        this.options = options;

        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/java_generator_version.properties"));

            this.version = properties.getProperty("java_generator_version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    @Override
    public void appendServiceClass(CService cs) throws GeneratorException, IOException {
        JService service = new JService(cs, helper);

        if (cs.getDocumentation() != null) {
            new BlockCommentBuilder(writer)
                    .comment(cs.getDocumentation())
                    .finish();
        }

        String inherits = "";
        if (service.getService().getExtendsService() != null) {
            CService other = service.getService().getExtendsService();
            inherits = "extends " + helper.getJavaPackage(other) + "." +
                       new JService(other, helper).className() + " ";
        }

        writer.appendln("@SuppressWarnings(\"unused\")");
        if (options.generated_annotation) {
            writer.formatln("@%s(\"providence java generator %s\")", Generated.class.getName(), version);
        }

        writer.formatln("public class %s %s{", service.className(), inherits)
              .begin();

        appendIface(writer, service);

        appendClient(writer, service);

        appendProcessor(writer, service);

        appendDescriptor(writer, service);

        appendStructs(writer, service);

        // protected constructor should defeat instantiation, but can inherit
        // from parent to be able to get access to inner protected classes.
        writer.formatln("protected %s() {}", service.className());

        writer.end()
              .appendln('}');
    }

    private void appendClient(IndentedPrintWriter writer, JService service) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (service.getService()
                   .getDocumentation() != null) {
            comment.comment(service.getService().getDocumentation())
                   .newline();
        }
        comment.comment("Client implementation for " + service.getService().getQualifiedName())
               .finish();

        writer.appendln("public static class Client")
              .formatln("        extends %s", PClient.class.getName())
              .formatln("        implements Iface {")
              .begin();

        writer.formatln("private final %s handler;", PServiceCallHandler.class.getName())
              .newline();

        new BlockCommentBuilder(writer)
                .comment("Create " + service.getService().getQualifiedName() + " service client.")
                .newline()
                .param_("handler", "The client handler.")
                .finish();

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

            writer.formatln("%s._Builder rq = %s.builder();",
                            service.getRequestClassRef(method),
                            service.getRequestClassRef(method));

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
                writer.newline()
                      .formatln("if (resp.getType() == %s.%s) {", PServiceCallType.class.getName(), PServiceCallType.EXCEPTION.name())
                      .formatln("    throw (%s) resp.getMessage();",
                                PApplicationException.class.getName())
                      .appendln('}')
                      .newline()
                      .formatln("%s msg = (%s) resp.getMessage();",
                                service.getResponseClassRef(method),
                                service.getResponseClassRef(method));

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
                                service.getService().getQualifiedName(),
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
                writer.formatln("%s._Builder rsp = %s.builder();",
                                service.getResponseClassRef(method),
                                service.getResponseClassRef(method));
            }
            String methodThrows = service.methodsThrows(method);

            if (methodThrows != null || method.exceptions().length > 0) {
                writer.appendln("try {")
                      .begin();
            }

            writer.formatln("%s req = (%s) call.getMessage();",
                            service.getRequestClassRef(method),
                            service.getRequestClassRef(method));

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

            if (methodThrows != null || method.exceptions().length > 0) {
                writer.end();
                for (JField ex : method.exceptions()) {
                    writer.formatln("} catch (%s e) {", ex.instanceType())
                          .formatln("    rsp.%s(e);", ex.setter());
                }
                if (methodThrows != null) {
                    writer.formatln("} catch (%s e) {", methodThrows)
                          .formatln("    throw new %s(e.getMessage(), e);", IOException.class.getName());
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
                        service.getService().getQualifiedName())
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
                                  : service.getResponseClassRef(method) + ".kDescriptor";

            writer.formatln("%s(\"%s\", %b, %s.kDescriptor, %s),",
                            method.constant(),
                            method.name(),
                            method.getMethod().isOneway(),
                            service.getRequestClassRef(method),
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
            CService other = service.getService().getExtendsService();
            inherits = helper.getJavaPackage(other) + "." +
                       new JService(other, helper).className() + ".provider()";
        }

        writer.formatln("private static class _Descriptor extends %s {",
                        PService.class.getName())
              .begin()
              .appendln("private _Descriptor() {")
              .formatln("    super(\"%s\", \"%s\", %s, Method.values());",
                        service.getService().getProgramName(),
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

    @SuppressWarnings("unchecked")
    private void appendStructs(IndentedPrintWriter writer, JService service) throws GeneratorException, IOException {
        for (JServiceMethod method : service.declaredMethods()) {
            JMessage request = new JMessage<>(method.getMethod().getRequestType(), helper);
            writer.formatln("// type --> %s", request.descriptor().getName());

            messageFormat.appendMessageClass(method.getMethod().getRequestType());

            if (method.getMethod().getResponseType() != null) {
                JMessage response = new JMessage(method.getMethod().getResponseType(), helper);
                writer.formatln("// type <-- %s", response.descriptor().getName());

                messageFormat.appendMessageClass(method.getMethod().getResponseType());
            }
        }
    }

    private void appendIface(IndentedPrintWriter writer, JService service) throws GeneratorException {
        String inherits = "";
        if (service.getService().getExtendsService() != null) {
            CService other = service.getService().getExtendsService();
            inherits = "extends " + helper.getJavaPackage(other) + "." +
                       new JService(other, helper).className() + ".Iface ";
        }

        if (service.getService().getDocumentation() != null) {
            new BlockCommentBuilder(writer)
                    .comment(service.getService().getDocumentation())
                    .finish();
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
            String methodThrows = service.methodsThrows(method);

            BlockCommentBuilder comment = new BlockCommentBuilder(writer);

            if (method.getMethod().getDocumentation() != null) {
                comment.comment(method.getMethod().getDocumentation())
                       .newline();
            }

            for (JField param : method.params()) {
                if (param.comment() != null) {
                    comment.param_(param.param(), param.comment());
                } else {
                    comment.param_(param.param(), "The " + param.name() + " value.");
                }
            }

            if (method.getResponse() != null &&
                    !method.getResponse().isVoid()) {
                comment.return_("The " + method.name() + " result.");
            }

            if (methodThrows != null) {
                comment.throws_(methodThrows, "On any declared exception.");
            } else {
                for (JField param : method.exceptions()) {
                    if (param.comment() != null) {
                        comment.throws_(param.fieldType(), param.comment());
                    } else {
                        comment.throws_(param.fieldType(), "The " + param.name() + " exception.");
                    }
                }
            }
            comment.throws_(IOException.class, "On providence or non-declared exceptions.")
                   .finish();

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
                  .format(")");
            writer.formatln("        throws %s", IOException.class.getName())
                  .begin("               ");
            if (methodThrows != null) {
                writer.append(",");
                writer.formatln("%s", methodThrows);
            } else {
                for (JField ex : method.exceptions()) {
                    writer.append(",");
                    writer.appendln(ex.instanceType());
                }
            }
            writer.format(";")
                  .end();
        }

        writer.end()
              .appendln('}')
              .newline();
    }
}
