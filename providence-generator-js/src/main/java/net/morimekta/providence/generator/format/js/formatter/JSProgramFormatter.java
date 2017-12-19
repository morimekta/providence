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

import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.generator.format.js.utils.ClosureDocBuilder;
import net.morimekta.providence.generator.format.js.utils.ClosureUtils;
import net.morimekta.providence.generator.format.js.utils.JSConstFormatter;
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
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.morimekta.providence.generator.format.js.utils.JSUtils.getClassName;

/**
 * Formatter for a single '.js' file. Supports inclusion variants
 * for simple (no libraries), Google closure and node.js. Otherwise
 * the generated code should be totally platform independent and not
 * requiring much of a browse to be compatible.
 */
public class JSProgramFormatter extends BaseFormatter {
    private final ProgramTypeRegistry registry;
    private final AtomicInteger       tmp;
    private final String              programContext;

    public JSProgramFormatter(JSOptions options,
                              ProgramTypeRegistry registry) {
        super(options);
        this.registry = registry;
        this.programContext = registry.getProgram().getProgramName();
        this.tmp = new AtomicInteger();
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

        formatFooter(writer);
    }

    private String tmpVar(String name) {
        return name + "_" + tmp.incrementAndGet();
    }

    private void formatHeader(IndentedPrintWriter writer, CProgram program) {
        String namespace = JSUtils.getPackage(program);
        if (program.getDocumentation() != null) {
            new ClosureDocBuilder(writer)
                    .comment(program.getDocumentation())
                    .finish();
        }

        if (options.type_script) {
            if (JSUtils.hasService(program)) {
                // TODO: Relative???
                writer.formatln("import * as _ from '%s';", "morimekta-providence/service").newline();
            }
            if (program.getIncludedPrograms().size() > 0) {
                for (String include : program.getIncludedPrograms()) {
                    CProgram included = registry.getProgramForName(include);
                    writer.formatln("import * as _%s from '%s';", included.getProgramName(), getNodePackageInclude(included, program));
                }
                writer.newline();
            }
        } else if (options.node_js) {
            if (JSUtils.hasService(program)) {
                // TODO: Relative???
                writer.formatln("var _ = require('%s');", "morimekta-providence/service").newline();
            }

            for (String include : program.getIncludedPrograms()) {
                CProgram included = registry.getProgramForName(include);
                writer.formatln("var _%s = require('%s');", included.getProgramName(), getNodePackageInclude(included, program));
            }

            writer.formatln("var _%s = module.exports = exports = {};", program.getProgramName())
                  .newline();
        } else {
            if (options.closure) {
                writer.formatln("goog.provide('%s');", namespace);

                if (JSUtils.hasService(program)) {
                    writer.formatln("goog.require('%s');", "morimekta.providence.service");
                }

                for (String include : program.getIncludedPrograms()) {
                    CProgram included = registry.getProgramForName(include);
                    String includedNs = JSUtils.getPackage(included);
                    writer.formatln("goog.require('%s');", includedNs);
                }
            } else {
                // build global namespaces manually...
                String[] array = namespace.split("[.]");
                StringBuilder parent = new StringBuilder();

                boolean first = true;
                for (String name : array) {
                    if (first) {
                        writer.formatln("var %s = %s || {};", name, name);
                        first = false;
                    } else {
                        writer.formatln("%s%s = %s%s || {};", parent.toString(), name, parent.toString(), name);
                    }
                    parent.append(name)
                          .append(".");
                }

                // includes are assumed to be handled by loading all the files in correct order...
                // It's a hack, but works.
            }

            // Start enclosure.
            writer.newline()
                  .appendln("(function(){")
                  .appendln("'use strict';")
                  .newline();

            for (String include : program.getIncludedPrograms()) {
                CProgram included = registry.getProgramForName(include);
                String includedNs = JSUtils.getPackage(included);
                if (!includedNs.equals(include)) {
                    writer.formatln("var _%s = %s;", included.getProgramName(), includedNs);
                }
            }

            if (JSUtils.hasService(program)) {
                writer.formatln("var _ = %s;", "morimekta.providence.service");
            }

            writer.formatln("var _%s = %s;", program.getProgramName(), namespace);
            writer.newline();
        }
    }

    private String getClassReference(CService service) {
        if (options.type_script && programContext.equals(service.getProgramName())) {
            return Strings.camelCase("", service.getName());
        }
        return "_" + service.getProgramName() + "." + Strings.camelCase("", service.getName());
    }

    private String getClassReference(CMessageDescriptor descriptor) {
        return getClassReference((PDeclaredDescriptor) descriptor);
    }

    private String getClassReference(PDeclaredDescriptor declaredDescriptor) {
        if (options.type_script) {
            return TSUtils.getTypeReference(programContext, declaredDescriptor);
        }
        return JSUtils.getClassReference(declaredDescriptor);
    }

    private void formatEnum(IndentedPrintWriter writer, CEnumDescriptor descriptor) {
        maybeComment(writer, descriptor, comment -> comment.enum_("number"));

        if (options.type_script) {
            writer.formatln("export enum %s {", getClassName(descriptor));
        } else {
            writer.formatln("%s = {", getClassReference(descriptor));
        }
        writer.begin();

        boolean first = true;
        for (CEnumValue value : descriptor.getValues()) {
            if (first) {
                first = false;
            } else {
                writer.append(",");
            }

            maybeComment(writer, value, comment -> comment.const_("number"));

            if (options.type_script) {
                writer.formatln("%s = %s", JSUtils.enumConst(value), value.asInteger());
            } else {
                writer.formatln("%s: %d", JSUtils.enumConst(value), value.asInteger());
            }
        }

        writer.end();
        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();

        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        comment.comment("Get the value of the enum, given value or name");
        if (options.closure) {
            comment.newline()
                   .param_("id", "number|string", "Identification for enum value")
                   .param_("opt_keepNumeric", "boolean=", "Optional arg to keep numeric values even if invalid.")
                   .return_(ClosureUtils.getTypeString(descriptor, options) + "?",
                            "The enum value if valid.");
        }
        comment.finish();
        if (options.type_script) {
            writer.formatln("namespace %s {", getClassName(descriptor))
                  .begin();

            writer.formatln("export function valueOf(id:any, opt_keepNumeric?:boolean):number {");
        } else {
            writer.formatln("%s.valueOf = function(id, opt_keepNumeric) {", getClassReference(descriptor));
        }
        writer.begin()
              .appendln("switch(id) {")
              .begin();

        for (CEnumValue value : descriptor.getValues()) {
            writer.formatln("case %d:", value.asInteger())
                  .formatln("case '%d':", value.asInteger())
                  .formatln("case '%s':", value.asString())
                  .formatln("    return %s.%s;",
                            getClassReference(descriptor),
                            JSUtils.enumConst(value));
        }

        writer.appendln("default:")
              .appendln("    if (opt_keepNumeric && 'number' === typeof(id)) {")
              .appendln("        return id;")
              .appendln("    }")
              .appendln("    return null;");

        writer.end()
              .appendln("}")
              .end();
        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("Get the string name of the enum value.");
        if (options.closure) {
            comment.newline()
                   .param_("value", descriptor.getProgramName() + "." + getClassName(descriptor),
                           "The enum value")
                   .return_("string?", "The enum name.");
        }
        comment.finish();

        if (options.type_script) {
            writer.appendln("export function nameOf(value:any, opt_keepNumeric?:boolean):string {");
        } else {
            writer.formatln("%s.nameOf = function(value, opt_keepNumeric) {", getClassReference(descriptor));
        }
        writer.begin()
              .appendln("switch(value) {")
              .begin();

        for (CEnumValue value : descriptor.getValues()) {
            writer.formatln("case %d:", value.asInteger())
                  .formatln("    return '%s';", value.getName());
        }

        writer.appendln("default:");
        if (options.type_script) {
            // Typescript enforces string return type here...
            writer.appendln("    if (!!opt_keepNumeric) return String(value);");
        } else {
            writer.appendln("    if (!!opt_keepNumeric) return value;");
        }
        writer.appendln("    return null;");

        writer.end()
              .appendln("}")
              .end();
        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();

        if (options.type_script) {
            writer.end()
                  .appendln("}");
        }
    }

    private void formatMessage(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        if (options.type_script) {
            writer.formatln("export class %s {", getClassReference(descriptor))
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
        }

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

        if (options.type_script) {
            writer.formatln("constructor(opt_json?:any) {");
        } else {
            writer.formatln("%s = function(opt_json) {", getClassReference(descriptor));
        }
        writer.begin();

        if (JSUtils.isUnion(descriptor)) {
            if (options.closure) {
                ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                comment.type_("boolean")
                       .private_()
                       .finish();
            }
            writer.formatln("this._field = null;");
            if (options.closure) {
                ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                comment.type_("*")
                       .private_()
                       .finish();
            }
            writer.formatln("this._value = null;");
        } else {
            for (CField field : descriptor.getFields()) {
                if (options.closure) {
                    ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                    comment.type_(ClosureUtils.getFieldType(field, options))
                           .private_()
                           .finish();
                }

                if (!JSUtils.alwaysPresent(field)) {
                    writer.formatln("this._%s = null;", field.getName());
                } else {
                    writer.formatln("this._%s = ", field.getName());

                    new JSConstFormatter(writer, options, programContext).format(JSUtils.defaultValue(field));

                    writer.append(";");
                }
            }
        }
        writer.newline()
              .appendln("if ('string' === typeof(opt_json)) {")
              .appendln("    opt_json = JSON.parse(opt_json);")
              .appendln("}");

        if (JSUtils.isUnion(descriptor)) {

            // union parsing.

            writer.appendln("if ('object' === typeof(opt_json)) {")
                  .begin();

            writer.appendln("for (var key in opt_json) {")
                  .begin()
                  .appendln("if (opt_json.hasOwnProperty(key)) {")
                  .begin()
                  .appendln("switch (key) {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                writer.formatln("case '%d':", field.getId())
                      .formatln("case '%s':", field.getName())
                      .begin();

                formatValueFromJson(writer,
                                    field.getDescriptor(),
                                    "this._value",
                                    "opt_json[key]");

                writer.formatln("this._field = '%s';", field.getName())
                      .formatln("break;")
                      .end();
            }

            // ignore default.
            writer.appendln("default:")
                  .appendln("    break;");

            writer.end()
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .end()
                  .appendln("} else if ('undefined' !== typeof(opt_json)) {")
                  .appendln("    throw 'Bad input: ' + typeof(opt_json);")
                  .appendln("}");

        } else {
            if (JSUtils.jsonCompactible(descriptor)) {
                writer.appendln("if (Array.isArray(opt_json)) {")
                      .begin();

                ArrayList<CField> fields = new ArrayList<>();
                Collections.addAll(fields, descriptor.getFields());
                fields.sort(Comparator.comparingInt(CField::getId).reversed());

                // compact / array parsing.
                // set fields in a switch in reverse order.
                // switch (opt_json.length) {
                //    case 4: this._v4 = ...(opt_json[3]);
                //    case 3: this._v3 = ...(opt_json[2]);
                //    case 2: this._v2 = ...(opt_json[1]);
                //    case 1: this._v1 = ...(opt_json[0]);
                // }

                writer.appendln("switch(opt_json.length) {")
                      .begin();

                boolean isRequired = false;

                for (CField field : fields) {
                    if (!isRequired) {
                        writer.formatln("case %d:", field.getId());
                    }
                    if (JSUtils.alwaysPresent(field)) {
                        isRequired = true;
                    }

                    writer.begin();
                    formatValueFromJson(writer, field.getDescriptor(),
                                        "this._" + field.getName(),
                                        "opt_json[" + (field.getId() - 1) + "]");
                    writer.end();
                }

                writer.appendln("    break;")
                      .appendln("default:")
                      .appendln("    throw 'Wrong number of compact fields: ' + opt_json.length;")
                      .end()
                      .appendln("}");

                writer.end()
                      .appendln("} else if ('object' === typeof(opt_json)) {")
                      .begin();
            } else {
                writer.appendln("if ('object' === typeof(opt_json)) {")
                      .begin();
            }

            // standard object parsing.
            writer.appendln("for (var key in opt_json) {")
                  .begin()
                  .appendln("if (opt_json.hasOwnProperty(key)) {")
                  .begin()
                  .appendln("switch (key) {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                writer.formatln("case '%d':", field.getId())
                      .formatln("case '%s':", field.getName())
                      .begin();

                formatValueFromJson(writer, field.getDescriptor(), "this._" + field.getName(), "opt_json[key]");

                writer.appendln("break;")
                      .end();
            }

            // ignore default.
            writer.appendln("default:")
                  .appendln("    break;");

            writer.end()
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .end()
                  .appendln("}");

            writer.end()
                  .appendln("} else if ('undefined' !== typeof(opt_json)){")
                  .appendln("    throw 'Bad json input type: ' + typeof(opt_json);")
                  .appendln("}");
        }

        writer.end();
        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();
    }

    private void formatMessageFieldMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor, CField field) {
        maybeComment(writer, field, comment -> comment.return_(ClosureUtils.getFieldType(field, options), "The field value"));
        if (options.type_script) {
            writer.formatln("%s():%s {",
                            Strings.camelCase("get", field.getName()),
                            TSUtils.getTypeString(programContext, field.getDescriptor(), options));
        } else {
            writer.formatln("%s.prototype.%s = function() {",
                            getClassReference(descriptor),
                            Strings.camelCase("get", field.getName()));
        }
        if (JSUtils.isUnion(descriptor)) {
            writer.formatln("    return '%s' == this._field ? this._value : null;", field.getName());
        } else if (JSUtils.defaultValue(field) != null && !JSUtils.alwaysPresent(field)) {
            writer.begin()
                  .formatln("if (this._%s === null) {", field.getName())
                  .begin()
                  .formatln("return ");

            new JSConstFormatter(writer, options, programContext).format(JSUtils.defaultValue(field));

            writer.append(";")
                  .end()
                  .appendln("} else {")
                  .formatln("    return this._%s;", field.getName())
                  .appendln("}")
                  .end();
        } else {
            writer.formatln("    return this._%s;", field.getName());
        }

        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();

        maybeComment(writer,
                     field,
                     comment -> comment.param_("value", ClosureUtils.getFieldType(field, options), "The new field value"));

        if (options.type_script) {
            writer.formatln("%s(value?:%s):void {",
                            Strings.camelCase("set", field.getName()),
                            TSUtils.getTypeString(programContext, field.getDescriptor(), options));
        } else {
            writer.formatln("%s.prototype.%s = function(value) {",
                            getClassReference(descriptor),
                            Strings.camelCase("set", field.getName()));
        }
        writer.begin()
              // If value is neither null nor undefined, it should be OK.
              .appendln("if (value !== null && value !== undefined) {")
              .begin();

        if (JSUtils.isUnion(descriptor)) {
            if (options.type_script) {
                writer.appendln("this._value = value;");
            } else {
                switch (field.getType()) {
                    // Skip value coercing for these field types.
                    case MESSAGE:
                    case ENUM:
                    case LIST:
                    case SET:
                    case MAP:
                        writer.appendln("this._value = value;");
                        break;
                    default:
                        formatValueFromJson(writer, field.getDescriptor(), "this._value", "value");
                        break;
                }
            }
            writer.formatln("this._field = '%s';", field.getName());
        } else {
            if (options.type_script) {
                writer.formatln("this._%s = value;", field.getName());
            } else {
                switch (field.getType()) {
                    // Skip value coercing for these field types.
                    case MESSAGE:
                    case ENUM:
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("this._%s = value;", field.getName());
                        break;
                    default:
                        formatValueFromJson(writer, field.getDescriptor(), "this._" + field.getName(), "value");
                        break;
                }
            }
        }

        if (JSUtils.isUnion(descriptor)) {

            writer.end()
                  .formatln("} else if (this._field === '%s') {", field.getName())
                  .appendln("    this._field = null;")
                  .appendln("    this._value = null;")
                  .appendln("}")
                  .end();

        } else {
            writer.end()
                  .appendln("} else {")
                  .begin();

            if (JSUtils.alwaysPresent(field)) {
                writer.formatln("this._%s = ", field.getName());
                new JSConstFormatter(writer, options, programContext).format(JSUtils.defaultValue(field));
                writer.append(";");
            } else {
                writer.formatln("this._%s = null;", field.getName());
            }

            writer.end()
                  .appendln("}")
                  .end();
        }

        if (options.type_script) {
            writer.appendln("}");
        } else {
            writer.appendln("};");
        }
        writer.newline();

    }

    private void formatMessageMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        if (JSUtils.isUnion(descriptor)) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.comment("Get the current set field on the union.");
            if (options.closure) {
                comment.newline()
                       .return_("string?", "The set field or null if none.");
            }
            comment.finish();

            if (options.type_script) {
                writer.formatln("unionField(): string {");
            } else {
                writer.formatln("%s.prototype.unionField = function() {", getClassReference(descriptor));
            }

            writer.appendln("    return this._field;");

            if (options.type_script) {
                writer.formatln("}");
            } else {
                writer.formatln("};");
            }
            writer.newline();
        } else if (JSUtils.jsonCompactible(descriptor)) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.comment("Check if the instance can be serialized as compact");
            if (options.closure) {
                comment.newline()
                       .return_("boolean", "If compact");
            }
            comment.finish();

            if (options.type_script) {
                writer.formatln("compact(): boolean {");
            } else {
                writer.formatln("%s.prototype.compact = function() {", getClassReference(descriptor));
            }
            writer.begin()
                  .appendln("var missing = false;");

            for (CField field : descriptor.getFields()) {
                if (!JSUtils.alwaysPresent(field)) {
                    writer.formatln("if (this._%s === null) {", field.getName())
                          .appendln("    missing = true;")
                          .appendln("} else if (missing) {")
                          .appendln("    return false;")
                          .appendln("}");
                }
            }

            writer.appendln("return true;")
                  .end();
            if (options.type_script) {
                writer.formatln("}");
            } else {
                writer.formatln("};");
            }
            writer.newline();
        }

        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON compatible object representation of the message.");
        if (options.closure) {
            comment.newline()
                   .param_("opt_named", "boolean=", "Optional use named json.");
            if (JSUtils.jsonCompactible(descriptor)) {
                comment.return_("Object|Array", "Json representation.");
            } else {
                comment.return_("Object", "Json representation.");
            }
        }
        comment.finish();

        if (options.type_script) {
            writer.formatln("toJson(opt_named?:boolean): any {");
        } else {
            writer.formatln("%s.prototype.toJson = function(opt_named) {", getClassReference(descriptor));
        }
        writer.begin();

        if (JSUtils.isUnion(descriptor)) {
            if (options.type_script) {
                writer.appendln("var ret: {[key:string]:any} = {}");
            } else {
                writer.appendln("var ret = {}");
            }
            writer.appendln("switch (this._field) {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                writer.formatln("case '%s':", field.getName())
                      .begin();

                String coerced = coerceJsonFromValue(field.getDescriptor(), "this._value", null, false, "opt_named");
                if (coerced == null) {
                    String tmp = tmpVar("v");
                    writer.formatln("var %s: any;", tmp);
                    coerced = tmp;
                    formatJsonFromValue(writer, field.getDescriptor(), tmp, "this._value", null, "opt_named");
                }

                writer.formatln("ret[opt_named ? '%s' : '%s'] = %s;",
                                field.getName(), field.getId(), coerced)
                      .appendln("break;")
                      .end();
            }

            writer.appendln("default:")
                  .appendln("    break;")
                  .end()
                  .appendln('}')
                  .appendln("return ret;");
        } else {
            if (JSUtils.jsonCompactible(descriptor)) {
                // Check for and maybe make compact.
                writer.appendln("if (this.compact()) {")
                      .begin();
                if (options.type_script) {
                    writer.appendln("var arr: any[] = [];");
                } else {
                    writer.appendln("var arr = [];");
                }
                ArrayList<CField> fields = new ArrayList<>();
                Collections.addAll(fields, descriptor.getFields());
                fields.sort(Comparator.comparing(CField::getId));

                for (CField field : fields) {
                    if (!JSUtils.alwaysPresent(field)) {
                        writer.formatln("if (this._%s === null) {", field.getName())
                              .appendln("    return arr;")
                              .appendln("}");
                    }

                    formatJsonFromValue(writer, field.getDescriptor(),
                                        "arr[arr.length]",
                                        "this._" + field.getName(),
                                        null,
                                        "opt_named");
                }

                writer.appendln("return arr;")
                      .end()
                      .appendln("}");
            }

            if (options.type_script) {
                writer.appendln("var obj : { [key:string]: any } = {};");
            } else {
                writer.appendln("var obj = {};");
            }
            writer.appendln("if (opt_named) {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                if (!JSUtils.alwaysPresent(field)) {
                    writer.formatln("if (this._%s !== null) {", field.getName())
                          .begin();
                }

                formatJsonFromValue(writer,
                                    field.getDescriptor(),
                                    "obj['" + field.getName() + "']",
                                    "this._" + field.getName(),
                                    true,
                                    "opt_named");

                if (!JSUtils.alwaysPresent(field)) {
                    writer.end()
                          .appendln("}");
                }
            }

            writer.end()
                  .appendln("} else {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                if (!JSUtils.alwaysPresent(field)) {
                    writer.formatln("if (this._%s !== null) {", field.getName())
                          .begin();
                }

                formatJsonFromValue(writer,
                                    field.getDescriptor(),
                                    "obj['" + field.getId() + "']",
                                    "this._" + field.getName(),
                                    false,
                                    "opt_named");

                if (!JSUtils.alwaysPresent(field)) {
                    writer.end()
                          .appendln("}");
                }
            }

            writer.end()
                  .appendln("}")  // end named check
                  .appendln("return obj;");
        }

        if (options.type_script) {
            writer.end()
                  .appendln("}");
        } else {
            writer.end()
                  .appendln("};");
        }
        writer.newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON string representation of the message.");
        if (options.closure) {
            comment.newline()
                   .param_("opt_named", "boolean=", "Optional use named json.")
                   .return_("string", "The stringified json.");
        }
        comment.finish();

        if (options.type_script) {
            writer.formatln("toJsonString(opt_named?:boolean):string {")
                  .appendln("    return JSON.stringify(this.toJson(opt_named));")
                  .appendln("}")
                  .newline();
        } else {
            writer.formatln("%s.prototype.toJsonString = function(opt_named) {", getClassReference(descriptor))
                  .appendln("    return JSON.stringify(this.toJson(opt_named));")
                  .appendln("};")
                  .newline();
        }

        comment = new ClosureDocBuilder(writer);
        comment.comment("String representation of the message.");
        if (options.closure) {
            comment.newline()
                   .return_("string", "Message as string.");
        }
        comment.finish();
        if (options.type_script) {
            writer.formatln("toString():string {")
                  .formatln("    return '%s' + JSON.stringify(this.toJson(true));", getClassName((PMessageDescriptor) descriptor))
                  .appendln("}")
                  .newline();
        } else {
            writer.formatln("%s.prototype.toString = function() {", getClassReference(descriptor))
                  .formatln("    return '%s' + JSON.stringify(this.toJson(true));", getClassName((PMessageDescriptor) descriptor))
                  .appendln("};")
                  .newline();
        }
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

    private void formatValueFromJson(@Nonnull IndentedPrintWriter writer,
                                     @Nonnull PDescriptor descriptor,
                                     @Nonnull String target,
                                     @Nonnull String source) {
        String coerced = coerceValueFromJson(descriptor, source, false);
        if (coerced != null) {
            if (!target.equals(coerced)) {
                writer.formatln("%s = %s;", target, coerced);
            }
            return;
        }

        if (descriptor.getType() == PType.SET ||
            descriptor.getType() == PType.LIST) {
            PContainer container = (PContainer) descriptor;
            if (options.type_script) {
                writer.formatln("%s = %s.map(function(i:any) {", target, source)
                      .begin();
            } else {
                writer.formatln("%s = %s.map(function(i) {", target, source)
                      .begin();
            }

            coerced = coerceValueFromJson(container.itemDescriptor(), "i", false);
            if (coerced != null) {
                writer.formatln("return %s;", coerced);
            } else {
                formatValueFromJson(writer, container.itemDescriptor(), "i", "i");
                writer.formatln("return i;");
            }

            writer.end()
                  .appendln("});");
        } else if (descriptor.getType() == PType.MAP) {
            PMap map = (PMap) descriptor;
            String tmpMap = tmpVar("m");
            String tmpKey = tmpVar("k");
            String tmpItem = String.format("%s[%s]", source, tmpKey);

            if (options.useMaps()) {
                writer.formatln("var %s = new Map();", tmpMap);
            } else if (options.type_script) {
                writer.formatln("var %s: {[key:string]:any} = {};", tmpMap);
            } else {
                writer.formatln("var %s = {};", tmpMap);
            }

            writer.formatln("for (var %s in %s) {", tmpKey, source)
                  .begin()
                  .formatln("if (%s.hasOwnProperty(%s)) {", source, tmpKey)
                  .begin();

            String coerceKey = coerceValueFromJson(map.keyDescriptor(), tmpKey, true);
            String coerceValue = coerceValueFromJson(map.itemDescriptor(), tmpItem, false);

            if (coerceValue == null) {
                tmpItem = tmpVar("v");
                if (options.type_script) {
                    writer.formatln("var %s: any = %s[%s];", tmpItem, source, tmpKey);
                } else {
                    writer.formatln("var %s = %s[%s];", tmpItem, source, tmpKey);
                }
                formatValueFromJson(writer, map.itemDescriptor(), tmpItem, tmpItem);
                coerceValue = tmpItem;
            }

            if (options.useMaps()) {
                writer.formatln("%s.set(%s, %s);", tmpMap, coerceKey, coerceValue);
            } else {
                writer.formatln("%s[%s] = %s;", tmpMap, coerceKey, coerceValue);
            }
            writer.end()
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .formatln("%s = %s;", target, tmpMap);
        } else {
            throw new GeneratorException("Unhandled type: " + descriptor.getType());
        }
    }

    private void formatJsonFromValue(@Nonnull IndentedPrintWriter writer,
                                     @Nonnull PDescriptor descriptor,
                                     @Nonnull String target,
                                     @Nonnull String source,
                                     @Nullable Boolean named,
                                     @Nonnull String optNamed) {
        String coerced = coerceJsonFromValue(descriptor, source, named, false, optNamed);
        if (coerced != null) {
            if (!target.equals(coerced)) {
                writer.formatln("%s = %s;", target, coerced);
            }
            return;
        }

        // If it contains a map of any kins, simple coercion does not work.
        if (descriptor.getType() == PType.LIST ||
            descriptor.getType() == PType.SET) {
            PContainer pc = (PContainer) descriptor;
            writer.formatln("%s = %s.map(function(i) {", target, source)
                  .begin();

            coerced = coerceJsonFromValue(pc.itemDescriptor(), "i", named, false, optNamed);
            if (coerced != null) {
                writer.formatln("return %s;", coerced);
            } else {
                String tmp = tmpVar("i");
                if (options.type_script) {
                    writer.formatln("var %s: any;");
                } else {
                    writer.formatln("var %s;", tmp);
                }
                formatJsonFromValue(writer, pc.itemDescriptor(), tmp, "i", named, optNamed);
            }
            writer.end()
                  .appendln("});");
        } else if (descriptor.getType() == PType.MAP) {
            PMap map = (PMap) descriptor;

            String tmpKey = tmpVar("k");
            String tmpValue = tmpVar("v");
            String tmpMap = tmpVar("m");
            // ...
            if (options.type_script) {
                writer.formatln("var %s: {[key:string]:any} = {};", tmpMap);
            } else {
                writer.formatln("var %s = {};", tmpMap);
            }
            if (options.useMaps()) {
                if (options.type_script) {
                    writer.formatln("%s.forEach(function(%s:any,%s:any) {",
                                    source, tmpValue, tmpKey)
                          .begin();
                } else {
                    writer.formatln("%s.forEach(function(%s,%s) {",
                                    source, tmpValue, tmpKey)
                          .begin();
                }
            } else {
                writer.formatln("for (var %s in %s) {", tmpKey, source);
                writer.begin()
                      .formatln("if (%s.hasOwnProperty(%s)) {", source, tmpKey)
                      .begin();
                if (options.type_script) {
                    writer.formatln("var %s: any = %s[%s]", tmpValue, source, tmpKey);
                } else {
                    writer.formatln("var %s = %s[%s]", tmpValue, source, tmpKey);
                }
            }

            String coerceKey = coerceJsonFromValue(map.keyDescriptor(), tmpKey, named, true, optNamed);
            String coerceValue = coerceJsonFromValue(map.itemDescriptor(), tmpValue, named, false, optNamed);
            if (coerceValue == null) {
                formatJsonFromValue(writer, map.itemDescriptor(), tmpValue, tmpValue, named, optNamed);
                coerceValue = tmpValue;
            }

            writer.formatln("%s[%s] = %s;", tmpMap, coerceKey, coerceValue);

            if (options.useMaps()) {
                writer.end()
                      .appendln("});");
            } else {
                writer.end()
                      .appendln("}")
                      .end()
                      .appendln("}");
            }
            writer.formatln("%s = %s;", target, tmpMap);
        } else {
            throw new GeneratorException("Unhandled type: " + descriptor.getType());
        }
    }

    private String coerceJsonFromValue(@Nonnull PDescriptor descriptor,
                                       @Nonnull String source,
                                       @Nullable Boolean named,
                                       boolean mapKey,
                                       String optNamed) {
        switch (descriptor.getType()) {
            case VOID:
            case BOOL:
            case BYTE:
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                if (mapKey) {
                    return "String(" + source + ")";
                }
            case STRING:
            case BINARY:
                return source;
            case ENUM:
                if (mapKey) {
                    if (named == Boolean.TRUE) {
                        return "String(" + getClassReference((PEnumDescriptor) descriptor) + ".nameOf(" + source +
                               ", true))";
                    } else if (named == null) {
                        return "String(" + optNamed + " ? " + getClassReference((PEnumDescriptor) descriptor) + ".nameOf(" +
                               source + ", true) : " + source + ")";
                    }
                } else if (named == Boolean.TRUE) {
                    return getClassReference((PEnumDescriptor) descriptor) + ".nameOf(" + source + ", true)";
                } else if (named == null) {
                    return optNamed + " ? " + getClassReference((PEnumDescriptor) descriptor) + ".nameOf(" + source +
                           ", true) : " + source;
                }
                return source;
            case MESSAGE:
                if (mapKey) {
                    if (named == Boolean.TRUE) {
                        return "new " + getClassReference((PMessageDescriptor) descriptor) + "(" + source + ").toJsonString(true)";
                    } else if (named == null) {
                        return String.format(optNamed + " ? new %s(%s).toJsonString(true) : %s",
                                             getClassReference((PMessageDescriptor) descriptor),
                                             source, source);
                    }
                    return source;
                }
                return source + ".toJson(" + optNamed + ")";
            case LIST:
            case SET: {
                PContainer container = (PContainer) descriptor;
                switch (container.itemDescriptor().getType()) {
                    case VOID:
                    case BOOL:
                    case BYTE:
                    case I16:
                    case I32:
                    case I64:
                    case DOUBLE:
                    case STRING:
                    case BINARY:
                        return source;
                    case ENUM:
                        if (named == Boolean.FALSE) {
                            // the enum is in fact a number.
                            return source;
                        }
                        break;
                    case MESSAGE:
                        break;
                    case LIST:
                    case SET:
                        // If non-coerced value, no change is needed, the whole list can be coerce-pretended.
                        if ("i".equals(coerceJsonFromValue(container.itemDescriptor(), "i", named, false, optNamed))) {
                            return source;
                        }
                        break;
                    case MAP:
                        break;
                    default:
                        throw new GeneratorException("Unhandled list item type for coercion: " + container.itemDescriptor().getType());
                }
                break;
            }
            case MAP:
                // maps are too complex to be simply coerced.
            default:
                break;
        }

        return null;  // no simple coercion available.
    }

    private String coerceValueFromJson(PDescriptor descriptor,
                                       String source,
                                       boolean mapKey) {
        switch (descriptor.getType()) {
            case VOID:
                // is never key.
                return "true";
            case BOOL:
                if (mapKey) {
                    if (options.type_script) {
                        return String.format("('string' === typeof(%s) ? 'true' === %s : !!%s) ? 1 : 0",
                                             source, source, source);
                    }
                    return String.format("'string' === typeof(%s) ? 'true' === %s : !!%s",
                                             source, source, source);
                }
                return "!!" + source;
            case BYTE:
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                return "Number(" + source + ")";
            case STRING:
            case BINARY:
                return "String(" + source + ")";
            case ENUM:
                return getClassReference((PEnumDescriptor) descriptor) + ".valueOf(" + source + ", true)";
            case MESSAGE:
                if (mapKey) {
                    return "new " + getClassReference((PMessageDescriptor) descriptor) + "(" + source + ").toJsonString()";
                }

                return "new " + getClassReference((PMessageDescriptor) descriptor) + "(" + source + ")";
            case LIST:
            case SET: {
                PContainer container = (PContainer) descriptor;
                switch (container.itemDescriptor().getType()) {
                    case VOID:
                    case BOOL:
                    case BYTE:
                    case I16:
                    case I32:
                    case I64:
                    case DOUBLE:
                    case STRING:
                    case BINARY:
                        return source;
                    default:
                        // Non-trivial list.
                        break;
                }
                break;
            }
            case MAP:
                // All maps are non-trivial.
                break;
            default:
                throw new GeneratorException("Unhandled coerced type: " + descriptor.getType());
        }

        return null;  // no simple coercion available.
    }


    private void formatConstant(IndentedPrintWriter writer, CProgram program, CConst constant) {
        maybeComment(writer, constant, comment -> comment.const_(ClosureUtils.getTypeString(constant.getDescriptor(), options)));
        if (options.type_script) {
            writer.formatln("export const %s = ", constant.getName());
        } else {
            writer.formatln("_%s.%s = ", program.getProgramName(), constant.getName());
        }
        new JSConstFormatter(writer, options, programContext).format(constant.getDefaultValue());
        writer.append(";")
              .newline();
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

        if (options.type_script) {
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
                    new ClosureDocBuilder(writer)
                            .comment(method.getDocumentation())
                            .finish();
                }
                writer.formatln("%s(", method.getName());

                boolean first = true;
                for (CField param : method.getRequestType().getFields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(",");
                    }
                    writer.format("%s:%s", JSUtils.getParamName(param), TSUtils.getTypeString(programContext, param.getDescriptor(), options));
                }
                CUnionDescriptor responseType = method.getResponseType();
                if (responseType == null) {
                    writer.append("):void;");
                } else {
                    CField response = responseType.fieldForId(0);
                    if (response.getType() == PType.VOID) {
                        writer.append("):Promise;");
                    } else {
                        writer.format("):Promise<%s>;", TSUtils.getTypeString(programContext, response.getDescriptor(), options));
                    }
                }
                writer.newline();
            }

            writer.end()
                  .appendln("}")
                  .newline();
        } else if (options.closure){
            if (service.getExtendsService() != null) {
                String ext = getClassReference(service.getExtendsService());
                writer.formatln("%s = function() {", getClassReference(service));
                writer.formatln("    %s.base(this);", getClassReference(service));
                writer.appendln("}");
                writer.formatln("goog.inherits(%s, %s);", getClassReference(service), ext)
                      .newline();
            } else {
                writer.formatln("%s = function() {};", getClassReference(service))
                      .newline();
            }

            for (CServiceMethod method : service.getMethods()) {
                ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                if (method.getDocumentation() != null) {
                    comment.comment(method.getDocumentation())
                           .newline();
                }

                for (CField param : method.getRequestType()
                                          .getFields()) {
                    comment.param_(JSUtils.getParamName(param), ClosureUtils.getFieldType(param, options), param.getDocumentation());
                }
                CUnionDescriptor responseType = method.getResponseType();
                if (responseType != null) {
                    CField response = responseType.fieldForId(0);
                    if (response.getType() != PType.VOID) {
                        comment.return_("Promise<" + ClosureUtils.getFieldType(response, options) + ">", null);
                    } else {
                        comment.return_("Promise", null);
                    }
                }
                if (method.hasAnnotation(ThriftAnnotation.DEPRECATED)) {
                    comment.deprecated_(method.getAnnotationValue(ThriftAnnotation.DEPRECATED));
                }
                comment.finish();

                writer.formatln("%s.prototype.%s = function(", getClassReference(service), method.getName());

                boolean first = true;
                for (CField field : method.getRequestType().getFields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(",");
                    }
                    writer.format("%s", field.getName());
                }
                writer.append(") {};")
                      .newline();
            }
        } else {
            // no interface in plain JS, we just need the block.
            writer.formatln("%s = {};", getClassReference(service))
                  .newline();
        }
    }

    private void formatServiceClient(IndentedPrintWriter writer, CService service) {
        String serviceName = Strings.camelCase("", service.getName());

        // A: Interface.
        if (options.type_script) {
            writer.formatln("namespace %s {", serviceName)
                  .begin()
                  .newline();

            writer.formatln("class Client extends %s {", serviceName)
                  .begin()
                  .appendln("private _seq_id: number;")
                  .appendln("private _named: boolean;")
                  .appendln("private _endpoint: string;")
                  .appendln("private _headers: {[key:string]:any};")
                  .newline();

            writer.formatln("constructor(endpoint:string, opt_headers?:{[key:string]:any}) {")
                  .begin();

            formatServiceConstructor(writer);

            writer.end()
                  .appendln("}");

            writer.newline();

            for (CServiceMethod method : service.getMethodsIncludingExtended()) {
                if (method.getDocumentation() != null) {
                    new ClosureDocBuilder(writer)
                            .comment(method.getDocumentation())
                            .finish();
                }
                writer.formatln("%s(", method.getName());

                boolean first = true;
                for (CField param : method.getRequestType().getFields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(",");
                    }
                    writer.format("%s:%s", JSUtils.getParamName(param), TSUtils.getTypeString(programContext, param.getDescriptor(), options));
                }
                CUnionDescriptor responseType = method.getResponseType();
                if (responseType == null) {
                    writer.append("):void {");
                } else {
                    CField response = responseType.fieldForId(0);
                    if (response.getType() == PType.VOID) {
                        writer.append("):Promise {");
                    } else {
                        writer.format("):Promise<%s> {", TSUtils.getTypeString(programContext, response.getDescriptor(), options));
                    }
                }
                writer.begin();

                formatServiceMethod(writer, method);

                writer.end()
                      .appendln("}")
                      .newline();
            }

            writer.end()
                  .appendln("}")
                  .end()
                  .appendln("}")
                  .newline();
        } else {
            writer.formatln("%s.Client = function(endpoint, opt_headers) {", getClassReference(service))
                  .begin();
            if (options.closure) {
                writer.formatln("%s.Client.base(this);", getClassReference(service));
            }

            formatServiceConstructor(writer);

            writer.end()
                  .appendln("};");
            if (options.closure) {
                writer.formatln("goog.inherits(%s.Client, %s);", getClassReference(service), getClassReference(service));
            }
            writer.newline();

            for (CServiceMethod method : service.getMethodsIncludingExtended()) {
                if (method.getDocumentation() != null || options.closure) {
                    ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                    if (method.getDocumentation() != null) {
                        comment.comment(method.getDocumentation());
                    }
                    if (method.getDocumentation() != null && options.closure) {
                        comment.newline();
                    }
                    if (options.closure) {
                        for (CField field : method.getRequestType()
                                                  .getFields()) {
                            comment.param_(field.getName(), ClosureUtils.getFieldType(field, options), field.getDocumentation());
                        }
                        CUnionDescriptor responseType = method.getResponseType();
                        if (responseType != null) {
                            CField response = responseType.fieldForId(0);
                            if (response.getType() != PType.VOID) {
                                comment.return_("Promise<" + ClosureUtils.getFieldType(response, options) + ">", null);
                            } else {
                                comment.return_("Promise", null);
                            }
                        }
                        if (method.hasAnnotation(ThriftAnnotation.DEPRECATED)) {
                            comment.deprecated_(method.getAnnotationValue(ThriftAnnotation.DEPRECATED));
                        }
                    }
                    comment.finish();
                }
                writer.formatln("%s.Client.prototype.%s = function(", getClassReference(service), method.getName());

                boolean first = true;
                for (CField param : method.getRequestType().getFields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(",");
                    }
                    writer.format("%s", JSUtils.getParamName(param));
                }
                writer.append(") {")
                      .begin();

                // method impl...
                formatServiceMethod(writer, method);

                writer.end()
                      .appendln("};")
                      .newline();
            }
        }
    }

    private void formatServiceConstructor(IndentedPrintWriter writer) {
        if (options.closure) {
            new ClosureDocBuilder(writer).type_("number")
                                         .private_()
                                         .finish();
        }
        writer.appendln("this._seq_id = 0;");

        if (options.closure) {
            new ClosureDocBuilder(writer).type_("boolean")
                                         .private_()
                                         .finish();
        }
        writer.appendln("this._named = false;");

        if (options.closure) {
            new ClosureDocBuilder(writer).type_("string")
                                         .private_()
                                         .finish();
        }
        writer.appendln("this._endpoint = endpoint;");

        if (options.closure) {
            new ClosureDocBuilder(writer).type_("Object")
                                         .private_()
                                         .finish();
        }
        writer.appendln("this._headers = opt_headers || {};");
    }

    private void formatServiceMethod(IndentedPrintWriter writer, CServiceMethod method) {
        if (options.type_script) {
            writer.appendln("var message: {[key:string]:any} = {};");
        } else {
            writer.appendln("var message = {};");
        }
        for (CField param : method.getRequestType().getFields()) {
            writer.formatln("if (%s !== null && %s !== undefined) {", JSUtils.getParamName(param), JSUtils.getParamName(param))
                  .begin();
            String target = String.format("message[this._named ? '%s' : '%s']", param.getName(), param.getId());
            formatJsonFromValue(writer, param.getDescriptor(), target, JSUtils.getParamName(param), null, "this._named");
            if (JSUtils.alwaysPresent(param)) {
                writer.end()
                      .appendln("} else {")
                      .begin()
                      .formatln("message[this._named ? '%s' : '%s'] = ", param.getName(), param.getId());

                new JSConstFormatter(writer, options, programContext).format(JSUtils.defaultValue(param));

                writer.append(";");
            }
            writer.end()
                  .appendln("}");
        }

        writer.appendln("var seq_id = ++this._seq_id;");
        writer.formatln("var call = ['%s', (this._named ? '%s' : %d), seq_id, message];",
                        method.getName(),
                        method.isOneway() ? PServiceCallType.ONEWAY.getName() : PServiceCallType.CALL.getName(),
                        method.isOneway() ? PServiceCallType.ONEWAY.asInteger() : PServiceCallType.CALL.asInteger());

        // TODO: This should be replaced with a "build XHR" method or function.
        writer.appendln("var xhr = new XMLHttpRequest();");
        writer.appendln("xhr.open('POST', this._endpoint, true);")
              .newline();
        writer.formatln("xhr.setRequestHeader('Content-Type', this._named ? '%s' : '%s');",
                        JsonSerializer.JSON_MEDIA_TYPE, JsonSerializer.MEDIA_TYPE);
        writer.appendln("for (var header in this._headers) {")
              .appendln("    xhr.setRequestHeader(header, this._headers[header]);")
              .appendln("}")
              .newline();

        if (method.getResponseType() != null) {
            writer.appendln("return new Promise(function(onSuccess, onFailure) {")
                  .begin()
                  .appendln("xhr.onreadystatechange = function() {")
                  .begin()
                  .appendln("if (xhr.readyState == XMLHttpRequest.DONE) {")
                  .begin()
                  .appendln("if (xhr.status == 200) {")
                  .begin()
                  .appendln("var response = JSON.parse(xhr.responseText);")
                  .appendln("if (Array.isArray(response) && response.length == 4) {")
                  .begin();
            // Just assume method, call type and sequence no are good.

            writer.formatln("if (response[1] === '%s' || response[1] === %d) {",
                            PServiceCallType.EXCEPTION.asString(),
                            PServiceCallType.EXCEPTION.asInteger())
                  .appendln("    onFailure(new _.PApplicationException(response[3]))")
                  .appendln("}");

            writer.appendln("try {")
                  .begin();

            writer.appendln("for (var k in response[3]) {")
                  .begin()
                  .appendln("if (response[3].hasOwnProperty(k)) {")
                  .begin()
                  .appendln("switch (k) {")
                  .begin();

            for (CField field : method.getResponseType().getFields()) {
                writer.formatln("case '%s':", field.getId())
                      .formatln("case '%s':", field.getName())
                      .begin();

                if (field.getId() == 0) {
                    // success
                    if (field.getType() == PType.VOID) {
                        writer.appendln("onSuccess();");
                    } else {
                        String coerce = coerceValueFromJson(field.getDescriptor(), "response[3][k]", false);
                        if (coerce == null) {
                            String tmp = tmpVar("suc");
                            if (options.type_script) {
                                writer.formatln("var %s: %s;", tmp, TSUtils.getTypeString(programContext, field.getDescriptor(), options));
                            } else {
                                writer.formatln("var %s;", tmp);
                            }
                            formatValueFromJson(writer, field.getDescriptor(), tmp, "response[3][k]");
                            coerce = tmp;
                        }
                        writer.formatln("onSuccess(%s);", coerce);
                    }
                } else {
                    // exception, always coerced.
                    String coerce = coerceValueFromJson(field.getDescriptor(), "response[3][k]", false);
                    writer.formatln("onFailure(%s);", coerce);
                }

                writer.appendln("return;")
                      .end();
            }

            writer.end()
                  .appendln("}")  // switch
                  .end()
                  .appendln("}")  // hasOwnProperty
                  .end()
                  .appendln("}")  // for k in response
                  .end()
                  .appendln("} catch (ex) {")  // try
                  .appendln("    onFailure(new _.PApplicationException({")
                  .appendln("        'message': String(ex),")
                  .appendln("        'id': _.PApplicationExceptionType.INTERNAL_ERROR")
                  .appendln("    }));")
                  .appendln("    return;")
                  .appendln("}")  // catch
                  .newline()
                  .appendln("onFailure(new _.PApplicationException({")
                  .appendln("    'message': 'Unknown response field: ' + JSON.stringify(response[3]),")
                  .appendln("    'id': _.PApplicationExceptionType.PROTOCOL_ERROR")
                  .appendln("}));")
                  .appendln("return;")
                  .end()
                  .appendln("}"); // isArray length 4

            writer.appendln("onFailure(new _.PApplicationException({")
                  .appendln("    'message': 'Unknown response: ' + JSON.stringify(response),")
                  .appendln("    'id': _.PApplicationExceptionType.PROTOCOL_ERROR")
                  .appendln("}));")
                  .appendln("return;")
                  .end()
                  .appendln("}");  // status

            writer.appendln("onFailure(new _.PApplicationException({")
                  .appendln("    'message': 'HTTP ' + xhr.status + ' ' + xhr.statusText,")
                  .appendln("    'id': _.PApplicationExceptionType.PROTOCOL_ERROR")
                  .appendln("}));");

            writer.end()
                  .appendln("}")  // ready
                  .end()
                  .appendln("};")  // onreadystatechange
                  .newline();
        }  // has response

        writer.appendln("xhr.send(JSON.stringify(call));");

        if (method.getResponseType() != null) {
            writer.end()
                  .appendln("});");  // new Promise
        }  // has response
    }

    private void formatFooter(IndentedPrintWriter writer) {
        if (!options.node_js && !options.type_script) {
            // node modules and typescript modules already handle the enclosure.
            writer.appendln("})();");
        }
    }
}
