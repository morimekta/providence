/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.generator.format.js.formatter;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.generator.format.js.utils.ClosureDocBuilder;
import net.morimekta.providence.generator.format.js.utils.ClosureUtils;
import net.morimekta.providence.generator.format.js.utils.JSUtils;
import net.morimekta.providence.generator.format.js.utils.TSUtils;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static net.morimekta.providence.generator.format.js.utils.JSUtils.getClassName;

/**
 * Formatter for a single '.js' file. Supports inclusion variants
 * for simple (no libraries), Google closure and node.js. Otherwise
 * the generated code should be totally platform independent and not
 * requiring much of a browse to be compatible.
 */
public class TSDefinitionFormatter extends BaseFormatter {
    private final ProgramTypeRegistry registry;
    private final String              programContext;

    public TSDefinitionFormatter(JSOptions options,
                                 ProgramTypeRegistry registry) {
        super(options);
        this.registry = registry;
        this.programContext = registry.getProgram().getProgramName();
    }

    @Override
    public String getFileName(CProgram program) {
        return super.getFileName(program).replaceAll("\\.js$", ".d.ts");
    }

    public void format(IndentedPrintWriter writer, CProgram program) {
        formatHeader(writer, program);

        for (PDeclaredDescriptor descriptor : program.getDeclaredTypes()) {
            if (descriptor instanceof PEnumDescriptor) {
                formatEnum(writer, (CEnumDescriptor) descriptor);
            } else if (descriptor instanceof PMessageDescriptor) {
                formatMessage(writer, (CMessageDescriptor) descriptor);
            } else {
                throw new IllegalArgumentException("Impossible");
            }
        }
        for (CConst constant : program.getConstants()) {
            formatConstant(writer, program, constant);
        }
        if (!options.es51) {
            // es5.1 does not support Promises, so no services either.
            for (CService service : program.getServices()) {
                formatService(writer, service);
            }
        }
    }

    private void formatHeader(IndentedPrintWriter writer, CProgram program) {
        if (program.getDocumentation() != null) {
            new ClosureDocBuilder(writer).comment(program.getDocumentation())
                                         .finish();
        }

        if (JSUtils.hasService(program)) {
            writer.formatln("import * as _ from '%s';", "../morimekta-providence/service")
                  .newline();
        }
        if (program.getIncludedPrograms()
                   .size() > 0) {
            for (String include : program.getIncludedPrograms()) {
                CProgram included = registry.getProgramForName(include);
                writer.formatln("import * as _%s from '%s';",
                                included.getProgramName(),
                                getNodePackageInclude(included, program));
            }
            writer.newline();
        }
    }

    String getClassReference(CService service) {
        if (programContext.equals(service.getProgramName())) {
            return Strings.camelCase("", service.getName());
        }

        return super.getClassReference(service);
    }

    String getClassReference(PDeclaredDescriptor declaredDescriptor) {
        return TSUtils.getTypeReference(programContext, declaredDescriptor);
    }

    private void formatEnum(IndentedPrintWriter writer, CEnumDescriptor descriptor) {
        maybeComment(writer, descriptor, comment -> comment.enum_("number"));

        writer.formatln("export declare enum %s {", getClassName(descriptor));
        writer.begin();

        boolean first = true;
        for (CEnumValue value : descriptor.getValues()) {
            if (first) {
                first = false;
            } else {
                writer.append(",");
            }

            maybeComment(writer, value, comment -> comment.const_("number"));

            writer.formatln("%s = %s", JSUtils.enumConst(value), value.asInteger());
        }

        writer.end();
        writer.appendln("}");
        writer.newline();

        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        comment.comment("Get the value of the enum, given value or name");
        comment.newline()
               .param_("id", "number|string", "Identification for enum value")
               .param_("opt_keepNumeric", "boolean=", "Optional arg to keep numeric values even if invalid.")
               .return_(ClosureUtils.getTypeString(descriptor, options) + "?",
                        "The enum value if valid.");
        comment.finish();
        writer.formatln("namespace %s {", getClassName(descriptor))
              .begin();

        writer.formatln("export function valueOf(id:any, opt_keepNumeric?:boolean):number;")
              .newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("Get the string name of the enum value.");
        comment.newline()
               .param_("value", descriptor.getProgramName() + "." + getClassName(descriptor),
                       "The enum value")
               .return_("string?", "The enum name.");
        comment.finish();

        writer.appendln("export function nameOf(value:any, opt_keepNumeric?:boolean):string;")
              .newline();

        writer.end()
              .appendln("}");
    }

    private void formatMessage(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        writer.formatln("export declare class %s {", getClassReference(descriptor))
              .begin();

        if (JSUtils.isUnion(descriptor)) {
            writer.appendln("private _field: string;");
            writer.appendln("private _value: any;");
        } else {
            // CA: declare fields.
            for (CField field : descriptor.getFields()) {
                writer.formatln("private _%s: %s;",
                                field.getName(),
                                TSUtils.getTypeString(programContext, field.getDescriptor(), options));
            }
        }
        writer.newline();

        // A: constructor
        formatMessageConstructor(writer, descriptor);
        // B: getters, setters
        for (CField field : descriptor.getFields()) {
            formatMessageFieldMethods(writer, descriptor, field);
        }
        // C: toJson, toJsonString
        formatMessageMethods(writer, descriptor);

        if (options.type_script) {
            writer.end()
                  .appendln("}");
        }
    }

    private void formatMessageConstructor(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        maybeComment(writer, descriptor, comment -> {
            if (JSUtils.jsonCompactible(descriptor)) {
                comment.param_("opt_json",
                               "Object|Array|string=",
                               "Optional json object, array or serialized string.");
            } else {
                comment.param_("opt_json",
                               "Object|string=",
                               "Optional json object or serialized string.");
            }
            comment.constructor_();
        });

        writer.formatln("constructor(opt_json?:any);")
              .newline();
    }

    private void formatMessageFieldMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor, CField field) {
        maybeComment(writer, field, comment -> comment.return_(ClosureUtils.getFieldType(field, options), "The field value"));
        writer.formatln("%s():%s;",
                        Strings.camelCase("get", field.getName()),
                        TSUtils.getTypeString(programContext, field.getDescriptor(), options))
              .newline();

        maybeComment(writer,
                     field,
                     comment -> comment.param_("value", ClosureUtils.getFieldType(field, options), "The new field value"));

        writer.formatln("%s(value?:%s):void;",
                        Strings.camelCase("set", field.getName()),
                        TSUtils.getTypeString(programContext, field.getDescriptor(), options))
              .newline();
    }

    private void formatMessageMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        if (JSUtils.isUnion(descriptor)) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.comment("Get the current set field on the union.");
            comment.newline()
                   .return_("string?", "The set field or null if none.");
            comment.finish();

            writer.formatln("unionField(): string;")
                  .newline();
        } else if (JSUtils.jsonCompactible(descriptor)) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.comment("Check if the instance can be serialized as compact");
            comment.newline()
                   .return_("boolean", "If compact");
            comment.finish();

            writer.formatln("compact(): boolean {")
                  .newline();
        }

        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON compatible object representation of the message.");
        comment.newline()
               .param_("opt_named", "boolean=", "Optional use named json.");
        if (JSUtils.jsonCompactible(descriptor)) {
            comment.return_("Object|Array", "Json representation.");
        } else {
            comment.return_("Object", "Json representation.");
        }
        comment.finish();

        writer.formatln("toJson(opt_named?:boolean): any {")
              .newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON string representation of the message.");
        comment.newline()
               .param_("opt_named", "boolean=", "Optional use named json.")
               .return_("string", "The stringified json.");
        comment.finish();

        writer.formatln("toJsonString(opt_named?:boolean):string;")
              .newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("String representation of the message.");
        comment.newline()
               .return_("string", "Message as string.");
        comment.finish();
        writer.formatln("toString():string;")
              .newline();
    }

    private void maybeComment(@Nonnull IndentedPrintWriter writer,
                              @Nonnull CAnnotatedDescriptor descriptor,
                              @Nonnull Consumer<ClosureDocBuilder> closure) {
        if (descriptor.getDocumentation() != null || options.closure) {
            ClosureDocBuilder builder = new ClosureDocBuilder(writer);
            if (descriptor.getDocumentation() != null) {
                builder.comment(descriptor.getDocumentation());
            }
            if (descriptor.getDocumentation() != null && options.closure) {
                builder.newline();
            }
            if (options.closure) {
                closure.accept(builder);

                if (descriptor.hasAnnotation(ThriftAnnotation.DEPRECATED)) {
                    builder.deprecated_(descriptor.getAnnotationValue(ThriftAnnotation.DEPRECATED));
                }
            }
            builder.finish();
        }
    }

    private void formatConstant(IndentedPrintWriter writer, CProgram program, CConst constant) {
        maybeComment(writer, constant, comment -> comment.const_(ClosureUtils.getTypeString(constant.getDescriptor(), options)));
        writer.formatln("export declare const %s: %s;",
                        constant.getName(),
                        TSUtils.getTypeString(programContext, constant.getDescriptor(), options));
    }

    private void formatService(IndentedPrintWriter writer, CService service) {
        // A: Service Interface
        formatServiceInterface(writer, service);

        // B: Client
        formatServiceClient(writer, service);
    }

    private void formatServiceInterface(IndentedPrintWriter writer, CService service) {
        String serviceName = Strings.camelCase("", service.getName());
        maybeComment(writer, service, comment -> {
            comment.interface_();
            if (service.getExtendsService() != null) {
                String ext = getClassReference(service.getExtendsService());
                comment.extends_(ext);
            }
        });

        if (service.getExtendsService() != null) {
            String ext = getClassReference(service.getExtendsService());
            writer.formatln("export interface %s extends %s {", serviceName, ext);
        } else {
            writer.formatln("export interface %s {", serviceName);
        }
        writer.begin()
              .newline();

        for (CServiceMethod method : service.getMethods()) {
            if (method.getDocumentation() != null) {
                new ClosureDocBuilder(writer).comment(method.getDocumentation())
                                             .finish();
            }
            writer.formatln("%s(", method.getName());

            boolean first = true;
            for (CField param : method.getRequestType()
                                      .getFields()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(",");
                }
                writer.format("%s:%s",
                              JSUtils.getParamName(param),
                              TSUtils.getTypeString(programContext, param.getDescriptor(), options));
            }
            CUnionDescriptor responseType = method.getResponseType();
            if (responseType == null) {
                writer.append("):void;");
            } else {
                CField response = responseType.fieldForId(0);
                if (response.getType() == PType.VOID) {
                    writer.append("):Promise;");
                } else {
                    writer.format("):Promise<%s>;",
                                  TSUtils.getTypeString(programContext, response.getDescriptor(), options));
                }
            }
            writer.newline();
        }

        writer.end()
              .appendln("}")
              .newline();
    }

    private void formatServiceClient(IndentedPrintWriter writer, CService service) {
        String serviceName = Strings.camelCase("", service.getName());

        // A: Interface.
        writer.formatln("namespace %s {", serviceName)
              .begin()
              .newline();

        writer.formatln("declare class Client extends %s {", serviceName)
              .begin()
              .appendln("private _seq_id: number;")
              .appendln("private _named: boolean;")
              .appendln("private _endpoint: string;")
              .appendln("private _headers: {[key:string]:any};")
              .newline();

        writer.formatln("constructor(endpoint:string, opt_headers?:{[key:string]:any});")
              .newline();

        for (CServiceMethod method : service.getMethodsIncludingExtended()) {
            if (method.getDocumentation() != null) {
                new ClosureDocBuilder(writer).comment(method.getDocumentation())
                                             .finish();
            }
            writer.formatln("%s(", method.getName());

            boolean first = true;
            for (CField param : method.getRequestType()
                                      .getFields()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(",");
                }
                writer.format("%s:%s",
                              JSUtils.getParamName(param),
                              TSUtils.getTypeString(programContext, param.getDescriptor(), options));
            }
            CUnionDescriptor responseType = method.getResponseType();
            if (responseType == null) {
                writer.append("):void;");
            } else {
                CField response = responseType.fieldForId(0);
                if (response.getType() == PType.VOID) {
                    writer.append("):Promise;");
                } else {
                    writer.format("):Promise<%s>;",
                                  TSUtils.getTypeString(programContext, response.getDescriptor(), options));
                }
            }
            writer.newline();
        }

        writer.end()
              .appendln("}")
              .end()
              .appendln("}")
              .newline();
    }
}
