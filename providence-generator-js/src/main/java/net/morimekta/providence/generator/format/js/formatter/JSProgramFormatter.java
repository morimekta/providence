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

import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.generator.format.js.utils.ClosureDocBuilder;
import net.morimekta.providence.generator.format.js.utils.JSConstFormatter;
import net.morimekta.providence.generator.format.js.utils.JSUtils;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Formatter for a single '.js' file. Supports inclusion variants
 * for simple (no libraries), Google closure and node.js. Otherwise
 * the generated code should be totally platform independent and not
 * requiring much of a browse to be compatible.
 */
public class JSProgramFormatter extends ProgramFormatter {
    private final AtomicInteger tmp;

    public JSProgramFormatter(JSOptions options,
                              ProgramTypeRegistry registry) {
        super(options, registry);

        if (options.node_js && options.closure) {
            throw new IllegalArgumentException("Both node.js and google closure used!");
        }

        this.tmp = new AtomicInteger();
    }

    private String tmpVar(String name) {
        return name + "_" + tmp.incrementAndGet();
    }

    public String getFileName(CProgram program) {
        return program.getProgramName() + ".js";
    }

    protected void formatHeader(IndentedPrintWriter writer, CProgram program) {
        String namespace = JSUtils.getPackage(program);
        if (program.getDocumentation() != null) {
            new ClosureDocBuilder(writer)
                    .comment(program.getDocumentation())
                    .finish();
        }
        writer.appendln("'use strict';");

        if (options.node_js) {
            Path relativeTo = Paths.get(File.separator + JSUtils.getPackageClassPath(program));
            for (String include : program.getIncludedPrograms()) {
                CProgram included = registry.getProgramForName(include);

                Path includedPath = Paths.get(File.separator + JSUtils.getPackageClassPath(included), included.getProgramName());
                String relative = relativeTo.relativize(includedPath).toString();
                if (!relative.startsWith(".")) {
                    relative = "./" + relative;
                }

                writer.formatln("var %s = require('%s');", included.getProgramName(), relative);
            }

            writer.formatln("var %s = module.exports = exports = {};", program.getProgramName())
                  .newline();
        } else {
            if (options.closure) {
                writer.formatln("goog.provide('%s');", namespace);

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
                  .newline();

            boolean inc = false;
            for (String include : program.getIncludedPrograms()) {
                CProgram included = registry.getProgramForName(include);
                String includedNs = JSUtils.getPackage(included);
                if (!includedNs.equals(include)) {
                    writer.formatln("var %s = %s;", included.getProgramName(), includedNs);
                    inc = true;
                }
            }

            if (!namespace.equals(program.getProgramName())) {
                writer.formatln("var %s = %s;", program.getProgramName(), namespace);
                inc = true;
            }

            if (inc) {
                writer.newline();
            }
        }
    }

    protected void formatEnum(IndentedPrintWriter writer, CEnumDescriptor descriptor) {
        if (options.closure) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            if (descriptor.getDocumentation() != null) {
                comment.comment(descriptor.getDocumentation())
                       .newline();
            }
            comment.enum_("number")
                   .finish();
        }
        writer.formatln("%s.%s = {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .begin();

        boolean first = true;
        for (CEnumValue value : descriptor.getValues()) {
            if (first) {
                first = false;
            } else {
                writer.append(",");
            }

            if (options.closure) {
                ClosureDocBuilder comment = new ClosureDocBuilder(writer);
                if (value.getDocumentation() != null) {
                    comment.comment(value.getDocumentation())
                           .newline();
                }
                comment.const_("number")
                       .finish();
            }
            writer.formatln("%s: %d", JSUtils.enumConst(value), value.asInteger());
        }

        writer.end()
              .formatln("};")
              .newline();

        if (options.closure) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.param_("id", "number|string", "Identification for enum value")
                   .param_("opt_keepNumeric", "boolean=", "Optional arg to keep numeric values even if invalid.")
                   .return_(descriptor.getProgramName() + "." + JSUtils.getClassName(descriptor) + "?",
                            "The enum value if valid.")
                   .finish();
        }
        writer.formatln("%s.%s.valueOf = function(id, opt_keepNumeric) {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .begin()
              .appendln("switch(id) {")
              .begin();

        for (CEnumValue value : descriptor.getValues()) {
            writer.formatln("case %d:", value.asInteger())
                  .formatln("case '%d':", value.asInteger())
                  .formatln("case '%s':", value.asString())
                  .formatln("    return %s.%s.%s;",
                            descriptor.getProgramName(),
                            JSUtils.getClassName(descriptor),
                            JSUtils.enumConst(value));
        }

        writer.appendln("default:")
              .appendln("    if (!!opt_keepNumeric && 'number' == typeof(id)) {")
              .appendln("        return id;")
              .appendln("    }")
              .appendln("    return null;");

        writer.end()
              .appendln("}")
              .end()
              .appendln("};")
              .newline();

        if (options.closure) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.param_("value", descriptor.getProgramName() + "." + JSUtils.getClassName(descriptor),
                           "The enum value")
                   .return_("string?",
                            "The enum name.")
                   .finish();
        }
        writer.formatln("%s.%s.nameOf = function(value) {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .begin()
              .appendln("switch(value) {")
              .begin();

        for (CEnumValue value : descriptor.getValues()) {
            writer.formatln("case %d:", value.asInteger())
                  .formatln("    return '%s';", value.getName());
        }

        writer.appendln("default:")
              .appendln("    return null;");

        writer.end()
              .appendln("}")
              .end()
              .appendln("};")
              .newline();
    }

    protected void formatMessage(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        // A: constructor
        formatMessageConstructor(writer, descriptor);
        // B: getters, setters
        for (CField field : descriptor.getFields()) {
            formatMessageFieldMethods(writer, descriptor, field);
        }
        // C: toJson, toJsonString
        formatMessageMethods(writer, descriptor);
    }

    private void formatMessageConstructor(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        if (descriptor.getDocumentation() != null) {
            comment.comment(descriptor.getDocumentation())
                   .newline();
        }
        if (JSUtils.jsonCompactible(descriptor)) {
            comment.param_("opt_json", "Object|Array|string=", "Optional json object, array or serialized string.");
        } else {
            comment.param_("opt_json", "Object|string=", "Optional json object or serialized string.");
        }
        comment.constructor_()
               .finish();

        writer.formatln("%s.%s = function(opt_json) {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .begin();

        for (CField field : descriptor.getFields()) {
            comment = new ClosureDocBuilder(writer);
            comment.type_(JSUtils.getFieldType(field))
                   .private_()
                   .finish();

            if (!JSUtils.alwaysPresent(field)) {
                writer.formatln("this._%s = null;", field.getName());
            } else {
                writer.formatln("this._%s = ", field.getName());

                new JSConstFormatter(writer, options).format(JSUtils.defaultValue(field));

                writer.append(";");
            }
        }

        writer.appendln("if ('string' == typeof(opt_json)) {")
              .appendln("    opt_json = JSON.parse(opt_json);")
              .appendln("}");

        if (((PMessageDescriptor) descriptor).getVariant() == PMessageVariant.UNION) {

            // union parsing.

            writer.appendln("if ('object' == typeof(opt_json)) {")
                  .begin()
                  .appendln("var _set = false;");

            writer.appendln("for (var key in opt_json) {")
                  .begin()
                  .appendln("if (opt_json.hasOwnProperty(key)) {")
                  .begin()
                  .appendln("if (_set) throw 'Multiple union fields.';")
                  .newline()
                  .appendln("switch (key) {")
                  .begin();

            for (CField field : descriptor.getFields()) {
                writer.formatln("case '%d':", field.getId())
                      .formatln("case '%s':", field.getName())
                      .begin();

                formatValueFromJson(writer,
                                    field.getDescriptor(),
                                    "this._" + field.getName(),
                                    "opt_json[key]");

                writer.appendln("_set = true;")
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

        writer.end()
              .appendln("};")
              .newline();
    }

    private void formatMessageFieldMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor, CField field) {
        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        if (field.getDocumentation() != null) {
            comment.comment(field.getDocumentation())
                   .newline();
        }
        comment.return_(JSUtils.getFieldType(field), "The field value")
               .finish();

        writer.formatln("%s.%s.prototype.%s = function() {",
                        descriptor.getProgramName(), JSUtils.getClassName(descriptor),
                        Strings.camelCase("get", field.getName()))
              .formatln("    return this._%s;", field.getName())
              .appendln("};")
              .newline();

        comment = new ClosureDocBuilder(writer);
        comment.param_("value", JSUtils.getFieldType(field), "The new field value")
               .finish();

        writer.formatln("%s.%s.prototype.%s = function(value) {",
                        descriptor.getProgramName(), JSUtils.getClassName(descriptor), Strings.camelCase("set", field.getName()))
              .begin()
              // If value is neither null nor undefined, it should be OK.
              .appendln("if (value !== null && value !== undefined) {")
              .begin();

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

        writer.end()
              .appendln("} else {")
              .begin();

        if (JSUtils.alwaysPresent(field)) {
            writer.formatln("this._%s = ", field.getName());
            new JSConstFormatter(writer, options).format(JSUtils.defaultValue(field));
            writer.append(";");
        } else {
            writer.formatln("this._%s = null;", field.getName());
        }

        writer.end()
              .appendln("}")
              .end()
              .appendln("};")
              .newline();

    }

    private void formatMessageMethods(IndentedPrintWriter writer, CMessageDescriptor descriptor) {
        if (JSUtils.jsonCompactible(descriptor)) {
            ClosureDocBuilder comment = new ClosureDocBuilder(writer);
            comment.return_("boolean", "If the instance can be serialized as compact")
                   .finish();

            writer.formatln("%s.%s.prototype.compact = function() {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
                  .begin()
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
                  .end()
                  .formatln("};");
        }

        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON compatible object representation of the message.")
               .newline()
               .param_("opt_named", "boolean=", "Optional use named json.")
               .finish();

        writer.formatln("%s.%s.prototype.toJson = function(opt_named) {",
                        descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .begin();

        // Check for and maybe make compact.
        if (JSUtils.jsonCompactible(descriptor)) {
            writer.appendln("if (this.compact()) {")
                  .begin()
                  .appendln("var obj = [];");
            ArrayList<CField> fields = new ArrayList<>();
            Collections.addAll(fields, descriptor.getFields());
            fields.sort(Comparator.comparing(CField::getId));


            for (CField field : fields) {
                if (!JSUtils.alwaysPresent(field)) {
                    writer.formatln("if (this._%s === null) {", field.getName())
                          .appendln("    return obj;")
                          .appendln("}");
                }

                formatJsonFromValue(writer, field.getDescriptor(),
                                    "obj[obj.length]",
                                    "this._" + field.getName(),
                                    null);
            }

            writer.appendln("return obj;")
                  .end()
                  .appendln("}");
        }

        writer.appendln("var obj = {};")
              .appendln("if (!!opt_named) {")
              .begin();

        for (CField field : descriptor.getFields()) {
            if (!JSUtils.alwaysPresent(field)) {
                writer.formatln("if (this._%s !== null) {", field.getName())
                      .begin();
            }

            formatJsonFromValue(writer, field.getDescriptor(),
                                "obj['" + field.getName() + "']",
                                "this._" + field.getName(),
                                true);

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

            formatJsonFromValue(writer, field.getDescriptor(),
                                "obj['" + field.getId() + "']",
                                "this._" + field.getName(),
                                false);

            if (!JSUtils.alwaysPresent(field)) {
                writer.end()
                      .appendln("}");
            }
        }

        writer.end()
              .appendln("}")  // end named check
              .appendln("return obj;")
              .end()
              .appendln("};")
              .newline();

        comment = new ClosureDocBuilder(writer);
        comment.comment("Make a JSON string representation of the message.")
               .newline()
               .param_("opt_named", "boolean=", "Optional use named json.")
               .finish();
        writer.formatln("%s.%s.prototype.toJsonString = function(opt_named) {", descriptor.getProgramName(), JSUtils.getClassName(descriptor))
              .appendln("    return JSON.stringify(this.toJson(opt_named));")
              .appendln("};")
              .newline();
    }

    private void formatValueFromJson(IndentedPrintWriter writer,
                                     PDescriptor descriptor,
                                     String target,
                                     String source) {
        switch (descriptor.getType()) {
            case VOID:
                writer.formatln("%s = true;", target);
                break;
            case BOOL:
                writer.formatln("%s = ('string' == typeof(%s) ? 'true' == %s : !!%s);", target, source, source, source);
                break;
            case BYTE:
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                writer.formatln("%s = Number(%s);", target, source);
                break;
            case STRING:
            case BINARY:
                writer.formatln("%s = String(%s);", target, source);
                break;
            case ENUM: {
                PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                writer.formatln("%s = %s.%s.valueOf(%s, true);",
                                target,
                                descriptor.getProgramName(),
                                JSUtils.getClassName(ed),
                                source);
                break;
            }
            case MESSAGE: {
                PMessageDescriptor md = (PMessageDescriptor) descriptor;
                writer.formatln("%s = new %s.%s(%s);",
                                target,
                                md.getProgramName(),
                                JSUtils.getClassName(md),
                                source);
                break;
            }
            case LIST:
            case SET: {
                PContainer list = (PContainer) descriptor;
                String tmpIter = tmpVar("i");
                String tmpItem = tmpVar("v");
                String tmpArray = tmpVar("a");
                writer.formatln("var %s = [];", tmpArray)
                      .formatln("for (var %s = 0; %s < %s.length; %s++) {", tmpIter, tmpIter, source, tmpIter)
                      .begin()
                      .formatln("var %s = %s[%s];", tmpItem, source, tmpIter);

                // hard value coercion.
                formatValueFromJson(writer, list.itemDescriptor(), tmpItem, tmpItem);

                writer.formatln("%s.push(%s);", tmpArray, tmpItem)
                      .end()
                      .appendln("}")
                      .formatln("%s = %s;", target, tmpArray);
                break;
            }
            case MAP: {
                PMap map = (PMap) descriptor;
                String tmpMap = tmpVar("m");
                String tmpKey = tmpVar("k");
                String tmpItem = tmpVar("v");

                if (options.es6) {
                    writer.formatln("var %s = new Map();", tmpMap);
                } else {
                    writer.formatln("var %s = {};", tmpMap);
                }

                writer.formatln("for (var %s in %s) {", tmpKey, source)
                      .begin()
                      .formatln("if (%s.hasOwnProperty(%s)) {", source, tmpKey)
                      .begin()
                      .formatln("var %s = %s[%s];", tmpItem, source, tmpKey);

                if (map.keyDescriptor().getType() == PType.MESSAGE) {
                    PMessageDescriptor md = (PMessageDescriptor) map.keyDescriptor();
                    // reformat as id (not named).
                    writer.formatln("%s = new %s.%s(%s).toJsonString(opt_named);",
                                    tmpKey, md.getProgramName(), JSUtils.getClassName(md), tmpKey);
                } else {
                    formatValueFromJson(writer, map.keyDescriptor(), tmpKey, tmpKey);
                }
                formatValueFromJson(writer, map.itemDescriptor(), tmpItem, tmpItem);

                if (options.es6) {
                    writer.formatln("%s.set(%s, %s);", tmpMap, tmpKey, tmpItem);
                } else {
                    writer.formatln("%s[%s] = %s;", tmpMap, tmpKey, tmpItem);
                }
                writer.end()
                      .appendln("}")
                      .end()
                      .appendln("}")
                      .formatln("%s = %s;", target, tmpMap);
                break;
            }
        }
    }

    private void formatJsonFromValue(IndentedPrintWriter writer,
                                     PDescriptor descriptor,
                                     String target,
                                     String source,
                                     Boolean named) {
        if (descriptor.getType() == PType.MESSAGE) {
            writer.formatln("%s = %s.toJson(opt_named);",
                            target, source);
            return;
        } else if (descriptor.getType() == PType.ENUM) {
            PEnumDescriptor ed = (PEnumDescriptor) descriptor;
            if (named == Boolean.TRUE) {
                writer.formatln("%s = %s.%s.nameOf(%s) || %s;",
                                target, ed.getProgramName(), JSUtils.getClassName(ed), source, source);
                return;
            } else if (named == null) {
                writer.formatln("%s = !!opt_named && %s.%s.nameOf(%s) || %s;",
                                target, ed.getProgramName(), JSUtils.getClassName(ed), source, source);
                return;
            }
        } else if (descriptor.getType() == PType.LIST ||
                   descriptor.getType() == PType.SET) {
            PContainer pc = (PContainer) descriptor;

            if (pc.itemDescriptor().getType() == PType.MESSAGE) {
                writer.formatln("%s = %s.map(function(i) {return i.toJson(opt_named);});",
                                target, source);
                return;
            } else if (pc.itemDescriptor().getType() == PType.ENUM) {
                PEnumDescriptor ed = (PEnumDescriptor) pc.itemDescriptor();
                if (named == Boolean.TRUE) {
                    writer.formatln("%s = %s.map(function(i) {return %s.%s.nameOf(i) || i;});",
                                    target,
                                    source,
                                    ed.getProgramName(),
                                    JSUtils.getClassName(ed));
                    return;
                } else if (named == null) {
                    writer.formatln("%s = !!opt_named ? %s.map(function(i) {return %s.%s.nameOf(i) || i;}) : %s;",
                                    target,
                                    source,
                                    ed.getProgramName(),
                                    JSUtils.getClassName(ed),
                                    source);
                    return;
                }
            }
        } else if (descriptor.getType() == PType.MAP) {
            PMap map = (PMap) descriptor;

            String tmpKey = tmpVar("k");
            String tmpValue = tmpVar("v");
            String tmpMap = tmpVar("m");
            // ...
            writer.formatln("var %s = {};", tmpMap);
            if (options.es6) {
                writer.formatln("%s.forEach(function(%s,%s) {", source, tmpValue, tmpKey)
                      .begin();
            } else {
                writer.formatln("for (var %s in %s) {", tmpKey, source)
                      .begin()
                      .formatln("if (%s.hasOwnProperty(%s)) {", source, tmpKey)
                      .begin()
                      .formatln("var %s = %s[%s]", tmpValue, source, tmpKey);
            }

            if (map.keyDescriptor().getType() == PType.MESSAGE && named != Boolean.FALSE) {
                PMessageDescriptor md = (PMessageDescriptor) map.keyDescriptor();
                writer.formatln("%s = new %s.%s(%s).toJsonString(opt_named);",
                                tmpKey, md.getProgramName(), JSUtils.getClassName(md), tmpKey);
            } else {
                formatJsonFromValue(writer, map.keyDescriptor(), tmpKey, tmpKey, named);
            }
            formatJsonFromValue(writer, map.itemDescriptor(), tmpValue, tmpValue, named);

            writer.formatln("%s[String(%s)] = %s;", tmpMap, tmpKey, tmpValue);

            if (options.es6) {
                writer.end()
                      .appendln("});");
            } else {
                writer.end()
                      .appendln("}")
                      .end()
                      .appendln("}");
            }
            writer.formatln("%s = %s;", target, tmpMap);
            return;
        }
        if (!source.equals(target)) {
            writer.formatln("%s = %s;", target, source);
        }
    }

    protected void formatConstant(IndentedPrintWriter writer, CProgram program, CConst constant) {
        ClosureDocBuilder comment = new ClosureDocBuilder(writer);
        if (constant.getDocumentation() != null) {
            comment.comment(constant.getDocumentation())
                   .newline();
        }
        comment.const_(JSUtils.getDescriptorType(constant.getDescriptor()))
               .finish();
        writer.formatln("%s.%s = ", program.getProgramName(), constant.getName());
        new JSConstFormatter(writer, options).format(constant.getDefaultValue());
        writer.append(";")
              .newline();
    }

    protected void formatFooter(IndentedPrintWriter writer, CProgram program) {
        if (!options.node_js) {
            // node modules already handle the enclosure.
            writer.appendln("})();");
        }
    }
}
