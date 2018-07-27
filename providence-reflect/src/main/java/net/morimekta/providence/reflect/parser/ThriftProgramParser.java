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
package net.morimekta.providence.reflect.parser;

import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.FieldRequirement;
import net.morimekta.providence.model.FieldType;
import net.morimekta.providence.model.FunctionType;
import net.morimekta.providence.model.MessageType;
import net.morimekta.providence.model.MessageVariant;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.model.ProvidenceModel_Constants;
import net.morimekta.providence.model.ServiceType;
import net.morimekta.providence.model.TypedefType;
import net.morimekta.providence.reflect.parser.internal.ThriftTokenizer;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kBlockCommentStart;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kConst;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kEnum;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kException;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kExtends;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kInclude;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kLineCommentStart;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kNamespace;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kOneway;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kOptional;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kRequired;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kService;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kStruct;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kThrows;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kTypedef;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kUnion;
import static net.morimekta.providence.reflect.parser.internal.ThriftTokenizer.kVoid;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ThriftProgramParser implements ProgramParser {
    private static final Pattern VALID_PROGRAM_NAME  = Pattern.compile(
            "[-._a-zA-Z][-._a-zA-Z0-9]*");
    public static final Pattern  VALID_NAMESPACE     = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern  VALID_SDI_NAMESPACE = Pattern.compile(
            "([_a-zA-Z][-_a-zA-Z0-9]*[.])*[_a-zA-Z][-_a-zA-Z0-9]*");

    private final boolean requireFieldId;
    private final boolean requireEnumValue;
    private final boolean allowLanguageReservedNames;

    public ThriftProgramParser() {
        this(false, false);
    }

    public ThriftProgramParser(boolean requireFieldId,
                               boolean requireEnumValue) {
        this(requireFieldId, requireEnumValue, true);
    }

    public ThriftProgramParser(boolean requireFieldId,
                               boolean requireEnumValue,
                               boolean allowLanguageReservedNames) {
        this.requireFieldId = requireFieldId;
        this.requireEnumValue = requireEnumValue;
        this.allowLanguageReservedNames = allowLanguageReservedNames;
    }

    @Override
    public ProgramType parse(InputStream in, File file, Collection<File> includeDirs) throws IOException {
        try {
            return parseInternal(in, file, includeDirs);
        } catch (TokenizerException e) {
            if (e.getFile() == null) {
                e.setFile(file.getName());
            }
            throw e;
        }
    }

    private ProgramType parseInternal(InputStream in, File file, Collection<File> includeDirs) throws IOException {
        ProgramType._Builder program = ProgramType.builder();

        String programName = ReflectionUtils.programNameFromPath(file.getName());
        if (!VALID_PROGRAM_NAME.matcher(programName).matches()) {
            throw new ParseException("Program name \"%s\" derived from filename \"%s\" is not valid.",
                                     Strings.escape(programName),
                                     Strings.escape(file.getName()));
        }
        program.setProgramName(programName);

        List<String> include_files = new ArrayList<>();
        Set<String> includedPrograms = new HashSet<>();
        Map<String, String> namespaces = new LinkedHashMap<>();

        List<Declaration> declarations = new ArrayList<>();

        ThriftTokenizer tokenizer = new ThriftTokenizer(in);

        boolean has_header = false;
        boolean hasDeclaration = false;

        String doc_string = null;
        Token token;
        while ((token = tokenizer.next()) != null) {
            if (token.strEquals(kLineCommentStart)) {
                doc_string = parseDocLine(tokenizer, doc_string);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                doc_string = tokenizer.parseDocBlock();
                continue;
            }

            String keyword = token.asString();
            if (!ProvidenceModel_Constants.kThriftKeywords.contains(keyword)) {
                throw tokenizer.failure(token,
                                        "Unexpected token \'%s\'", token.asString());
            }
            switch (keyword) {
                case kNamespace:
                    if (hasDeclaration) {
                        throw tokenizer.failure(token,
                                                "Unexpected token 'namespace', expected type declaration");
                    }
                    if (doc_string != null && !has_header) {
                        program.setDocumentation(doc_string);
                    }
                    doc_string = null;
                    has_header = true;
                    parseNamespace(tokenizer, namespaces);
                    break;
                case kInclude:
                    if (hasDeclaration) {
                        throw tokenizer.failure(token,
                                                "Unexpected token 'include', expected type declaration");
                    }
                    if (doc_string != null && !has_header) {
                        program.setDocumentation(doc_string);
                    }
                    doc_string = null;
                    has_header = true;
                    parseIncludes(tokenizer, include_files, file, includedPrograms, includeDirs);
                    break;
                case kTypedef:
                    has_header = true;
                    hasDeclaration = true;
                    parseTypedef(tokenizer, doc_string, declarations, includedPrograms);
                    doc_string = null;
                    break;
                case kEnum:
                    has_header = true;
                    hasDeclaration = true;
                    EnumType et = parseEnum(tokenizer, doc_string);
                    declarations.add(Declaration.withDeclEnum(et));
                    doc_string = null;
                    break;
                case kStruct:
                case kUnion:
                case kException:
                    has_header = true;
                    hasDeclaration = true;
                    MessageType st = parseMessage(tokenizer, token.asString(), doc_string, includedPrograms);
                    declarations.add(Declaration.withDeclMessage(st));
                    doc_string = null;
                    break;
                case kService:
                    has_header = true;
                    hasDeclaration = true;
                    ServiceType srv = parseService(tokenizer, doc_string, includedPrograms);
                    declarations.add(Declaration.withDeclService(srv));
                    doc_string = null;
                    break;
                case kConst:
                    has_header = true;
                    hasDeclaration = true;
                    ConstType cnst = parseConst(tokenizer, doc_string, includedPrograms);
                    declarations.add(Declaration.withDeclConst(cnst));
                    doc_string = null;
                    break;
                default:
                    throw tokenizer.failure(token,
                                            "Unexpected token \'%s\'",
                                            Strings.escape(token.asString()));
            }
        }

        if (namespaces.size() > 0) {
            program.setNamespaces(namespaces);
        }
        if (include_files.size() > 0) {
            program.setIncludes(include_files);
        }
        if (declarations.size() > 0) {
            program.setDecl(declarations);
        }

        return program.build();
    }

    private boolean allowedNameIdentifier(String name) {
        if (ProvidenceModel_Constants.kThriftKeywords.contains(name)) {
            return false;
        } else return allowLanguageReservedNames || !ProvidenceModel_Constants.kReservedWords.contains(name);
    }

    private ConstType parseConst(ThriftTokenizer tokenizer, String comment, Set<String> includedPrograms) throws IOException {
        Token token = tokenizer.expect("const typename", t -> t.isIdentifier() || t.isQualifiedIdentifier());
        String type = parseType(tokenizer, token, includedPrograms);
        Token id = tokenizer.expectIdentifier("const identifier");

        tokenizer.expectSymbol("const value separator", Token.kFieldValueSep);

        Token value = tokenizer.parseValue();
        if (tokenizer.hasNext()) {
            Token sep = tokenizer.peek("");
            if (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
            }
        }

        return ConstType.builder()
                        .setDocumentation(comment)
                        .setName(id.asString())
                        .setType(type)
                        .setValue(value.asString())
                        .setStartLineNo(value.getLineNo())
                        .setStartLinePos(value.getLinePos())
                        .build();
    }

    private String parseDocLine(ThriftTokenizer tokenizer, String comment) throws IOException {
        String line = IOUtils.readString(tokenizer, "\n").trim();
        if (comment != null) {
            return comment + "\n" + line;
        }
        return line;
    }

    private ServiceType parseService(ThriftTokenizer tokenizer, String doc_string, Set<String> includedPrograms) throws IOException {
        ServiceType._Builder service = ServiceType.builder();

        if (doc_string != null) {
            service.setDocumentation(doc_string);
            doc_string = null;
        }
        Token identifier = tokenizer.expectIdentifier("service name");
        if (!allowedNameIdentifier(identifier.asString())) {
            throw tokenizer.failure(identifier, "Service with reserved name: " + identifier.asString());
        }
        service.setName(identifier.asString());

        if (tokenizer.peek("service start or extends").strEquals(kExtends)) {
            tokenizer.next();
            service.setExtend(tokenizer.expect("service extending identifier",
                                               t -> t.isIdentifier() || t.isQualifiedIdentifier()).asString());
        }

        tokenizer.expectSymbol("reading service start", Token.kMessageStart);

        Set<String> methodNames = new TreeSet<>();

        while (true) {
            Token token = tokenizer.expect("service method initializer");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.strEquals(kLineCommentStart)) {
                doc_string = parseDocLine(tokenizer, doc_string);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                doc_string = tokenizer.parseDocBlock();
                continue;
            }

            FunctionType._Builder method = FunctionType.builder();
            if (doc_string != null) {
                method.setDocumentation(doc_string);
                doc_string = null;
            }

            if (token.strEquals(kOneway)) {
                method.setOneWay(true);
                token = tokenizer.expect("service method type");
            }

            if (!token.strEquals(kVoid)) {
                if (method.isSetOneWay()) {
                    throw tokenizer.failure(token,
                                            "Oneway methods must have void return type, found '%s'",
                                            Strings.escape(token.asString()));
                }
                method.setReturnType(parseType(tokenizer, token, includedPrograms));
            }

            token = tokenizer.expectIdentifier("method name");
            String name = token.asString();
            if (!allowedNameIdentifier(name)) {
                throw tokenizer.failure(token, "Method with reserved name: " + name);
            }
            String normalized = Strings.camelCase("", name);
            if (methodNames.contains(normalized)) {
                throw tokenizer.failure(token,
                                         "Service method " + name +
                                         " has normalized name conflict");
            }
            methodNames.add(normalized);

            method.setName(name);

            tokenizer.expectSymbol("method params begin", Token.kParamsStart);

            int nextAutoParamKey = -1;
            while (true) {
                token = tokenizer.expect("method params");
                if (token.isSymbol(Token.kParamsEnd)) {
                    break;
                } else if (token.strEquals(kLineCommentStart)) {
                    doc_string = parseDocLine(tokenizer, doc_string);
                    continue;
                } else if (token.strEquals(kBlockCommentStart)) {
                    doc_string = tokenizer.parseDocBlock();
                    continue;
                }

                FieldType._Builder field = FieldType.builder();
                if (doc_string != null) {
                    field.setDocumentation(doc_string);
                    doc_string = null;
                }

                if (token.isInteger()) {
                    field.setId((int) token.parseInteger());
                    tokenizer.expectSymbol("params kv sep", Token.kKeyValueSep);
                    token = tokenizer.expect("param type");
                } else {
                    if (requireFieldId) {
                        throw tokenizer.failure(token, "Missing param ID in strict declaration");
                    }
                    field.setId(nextAutoParamKey--);
                }

                if (PRequirement.OPTIONAL.label.equals(token.asString())) {
                    field.setRequirement(FieldRequirement.OPTIONAL);
                    token = tokenizer.expect("param type");
                } else if (PRequirement.REQUIRED.label.equals(token.asString())) {
                    field.setRequirement(FieldRequirement.REQUIRED);
                    token = tokenizer.expect("param type");
                }

                field.setType(parseType(tokenizer, token, includedPrograms));

                token = tokenizer.expectIdentifier("param name");
                name = token.asString();
                if (!allowedNameIdentifier(name)) {
                    throw tokenizer.failure(token, "Param with reserved name: " + name);
                }

                field.setName(name);

                // Annotations.
                if (tokenizer.peek("method param annotation")
                             .isSymbol(Token.kParamsStart)) {
                    tokenizer.next();
                    field.setAnnotations(parseAnnotations(tokenizer, "params"));
                }

                token = tokenizer.peek("method params");
                if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                    tokenizer.next();
                }

                method.addToParams(field.build());
            }  // for each param
            doc_string = null;

            if (tokenizer.peek("possible throws statement")
                         .strEquals(kThrows)) {
                tokenizer.next();
                tokenizer.expectSymbol("throws group start", Token.kParamsStart);

                int nextAutoExceptionKey = -1;

                while (true) {
                    token = tokenizer.expect("exception key, type or end throws");

                    if (token.isSymbol(Token.kParamsEnd)) {
                        break;
                    } else if (token.strEquals(kLineCommentStart)) {
                        doc_string = parseDocLine(tokenizer, doc_string);
                        continue;
                    } else if (token.strEquals(kBlockCommentStart)) {
                        doc_string = tokenizer.parseDocBlock();
                        continue;
                    }

                    FieldType._Builder field = FieldType.builder();
                    if (doc_string != null) {
                        field.setDocumentation(doc_string);
                        doc_string = null;
                    }

                    if (token.isInteger()) {
                        field.setId((int) token.parseInteger());
                        tokenizer.expectSymbol("exception KV sep", Token.kKeyValueSep);
                        token = tokenizer.expect("exception type");
                    } else {
                        if (requireFieldId) {
                            throw tokenizer.failure(token, "Missing exception ID in strict declaration");
                        }
                        field.setId(nextAutoExceptionKey--);
                    }

                    field.setType(parseType(tokenizer, token, includedPrograms));

                    token = tokenizer.expectIdentifier("exception name");
                    name = token.asString();
                    if (!allowedNameIdentifier(name)) {
                        throw tokenizer.failure(token, "Thrown field with reserved name: " + name);
                    }
                    field.setName(name);

                    // Annotations.
                    if (tokenizer.peek("exception annotation start")
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        field.setAnnotations(parseAnnotations(tokenizer, "exception"));
                    }

                    method.addToExceptions(field.build());

                    token = tokenizer.peek("method exceptions");
                    if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                    }
                }
            }

            token = tokenizer.peek("");
            // Method Annotations.
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                method.setAnnotations(parseAnnotations(tokenizer, "method"));
                token = tokenizer.peek("method or service end");
            }

            service.addToMethods(method.build());

            if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
            }
        }  // for each method-line

        if (tokenizer.hasNext()) {
            Token token = tokenizer.peek("optional annotations");
            if (token.isSymbol(Token.kParamsStart)) {
                // Method Annotations.
                tokenizer.next();
                service.setAnnotations(parseAnnotations(tokenizer, "service"));
            }
        }

        return service.build();
    }

    private Map<String,String> parseAnnotations(ThriftTokenizer tokenizer, String annotationsOn) throws IOException {
        Map<String,String> annotations = new TreeMap<>();
        char sep = Token.kParamsStart;
        while (sep != Token.kParamsEnd) {
            Token token = tokenizer.expect(annotationsOn + " annotation name", Token::isReferenceIdentifier);
            String name = token.asString();
            sep = tokenizer.expectSymbol(annotationsOn + " annotation KV, sep or end", Token.kFieldValueSep, Token.kParamsEnd, Token.kParamsEnd, Token.kLineSep1);
            if (sep == Token.kFieldValueSep) {
                Token value = tokenizer.expectLiteral(annotationsOn + " annotation value");
                annotations.put(name, value.decodeLiteral(true));
                sep = tokenizer.expectSymbol(annotationsOn + " annotation sep or end", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
            } else {
                annotations.put(name, "");
            }
        }

        return annotations;
    }

    private void parseNamespace(ThriftTokenizer tokenizer, Map<String, String> namespaces) throws IOException {
        Token language = tokenizer.expect("namespace language",
                                          Token::isReferenceIdentifier);
        if (namespaces.containsKey(language.asString())) {
            throw tokenizer.failure(language,
                                    "Namespace for %s already defined.",
                                    language.asString());
        }

        Token namespace = tokenizer.expect(
                "namespace",
                t -> VALID_NAMESPACE.matcher(t.asString()).matches() ||
                     VALID_SDI_NAMESPACE.matcher(t.asString()).matches());

        namespaces.put(language.asString(), namespace.asString());
    }

    private void parseIncludes(ThriftTokenizer tokenizer, List<String> includeFiles, File currentFile, Set<String> includePrograms, Collection<File> includeDirs) throws IOException {
        Token include = tokenizer.expectLiteral("include file");
        String filePath = include.decodeLiteral(true);
        if (!ReflectionUtils.isThriftFile(filePath)) {
            throw tokenizer.failure(include,
                                    "Include not valid for thrift files " + filePath);
        }
        if (!includeExists(currentFile, filePath, includeDirs)) {
            throw tokenizer.failure(include, "Included file not found " + filePath);
        }
        includeFiles.add(filePath);
        includePrograms.add(ReflectionUtils.programNameFromPath(filePath));
    }

    private boolean includeExists(File currentFile, String filePath, Collection<File> includeDirs) {
        File currentDir = currentFile.getParentFile();
        if (new File(currentDir, filePath).isFile()) {
            return true;
        }
        for (File I : includeDirs) {
            if (new File(I, filePath).isFile()) {
                return true;
            }
        }

        return false;
    }

    private void parseTypedef(ThriftTokenizer tokenizer, String comment, List<Declaration> declarations, Set<String> includedPrograms)
            throws IOException {
        String type = parseType(tokenizer, tokenizer.expect("typename"), includedPrograms);
        Token id = tokenizer.expectIdentifier("typedef identifier");
        String name = id.asString();
        if (!allowedNameIdentifier(name)) {
            throw tokenizer.failure(id, "Typedef with reserved name: " + name);
        }

        TypedefType typedef = TypedefType.builder()
                                         .setDocumentation(comment)
                                         .setType(type)
                                         .setName(name)
                                         .build();
        declarations.add(Declaration.withDeclTypedef(typedef));
    }

    private EnumType parseEnum(ThriftTokenizer tokenizer, String doc_string) throws IOException {
        Token id = tokenizer.expectIdentifier("enum name");
        String enum_name = id.asString();
        if (!allowedNameIdentifier(enum_name)) {
            throw tokenizer.failure(id, "Enum with reserved name: " + enum_name);
        }

        EnumType._Builder enum_type = EnumType.builder();
        if (doc_string != null) {
            enum_type.setDocumentation(doc_string);
            doc_string = null;
        }
        enum_type.setName(enum_name);

        int nextValueID = PEnumDescriptor.DEFAULT_FIRST_VALUE;

        tokenizer.expectSymbol("enum start", Token.kMessageStart);

        if (!tokenizer.peek("").isSymbol(Token.kMessageEnd)) {
            while (true) {
                Token token = tokenizer.expect("enum value or end");
                if (token.isSymbol(Token.kMessageEnd)) {
                    break;
                } else if (token.strEquals(kLineCommentStart)) {
                    doc_string = parseDocLine(tokenizer, doc_string);
                } else if (token.strEquals(kBlockCommentStart)) {
                    doc_string = tokenizer.parseDocBlock();
                } else if (token.isIdentifier()) {
                    String value_name = token.asString();
                    if (!allowedNameIdentifier(value_name)) {
                        throw tokenizer.failure(token, "Enum value with reserved name: " + enum_name);
                    }
                    EnumValue._Builder enum_value = EnumValue.builder();
                    // TODO: Validate enum value name. This probably needs a different logic than
                    // type names, field names and methods.

                    enum_value.setName(value_name);
                    if (doc_string != null) {
                        enum_value.setDocumentation(doc_string);
                        doc_string = null;
                    }

                    int value_id = nextValueID++;
                    if (tokenizer.peek("enum value ID")
                                 .isSymbol(Token.kFieldValueSep)) {
                        tokenizer.next();
                        Token v = tokenizer.expectInteger("enum value");
                        value_id = (int) v.parseInteger();
                        nextValueID = value_id + 1;
                    } else if (requireEnumValue) {
                        // So the token points at the token that *should* have been '='.
                        if (tokenizer.hasNext()) {
                            token = tokenizer.next();
                        }
                        throw tokenizer.failure(token, "Missing enum value in strict declaration");
                    }

                    enum_value.setId(value_id);

                    // Enum value annotations.
                    if (tokenizer.peek("enum value annotation")
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        enum_value.setAnnotations(parseAnnotations(tokenizer, "enum value"));
                    }

                    enum_type.addToValues(enum_value.build());

                    // Optional separator...
                    token = tokenizer.peek("enum value or end");
                    if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                    }
                } else {
                    throw tokenizer.failure(token, "Unexpected token: %s", token.asString());
                }
            }
        } // if has values.

        if (tokenizer.hasNext()) {
            Token token = tokenizer.peek("optional annotations");
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                enum_type.setAnnotations(parseAnnotations(tokenizer, "enum type"));
            }
        }

        return enum_type.build();
    }

    private MessageType parseMessage(ThriftTokenizer tokenizer, String variant, String comment, Set<String> includedPrograms)
            throws IOException {
        MessageType._Builder struct = MessageType.builder();
        if (comment != null) {
            struct.setDocumentation(comment);
            comment = null;
        }
        boolean union = variant.equals("union");
        if (!variant.equals("struct")) {
            struct.setVariant(MessageVariant.valueForName(variant.toUpperCase(Locale.US)));
        }

        Token nameToken = tokenizer.expectIdentifier("message name identifier");
        String name = nameToken.asString();
        if (!allowedNameIdentifier(name)) {
            throw tokenizer.failure(nameToken, "Message with reserved name: " + name);
        }

        struct.setName(name);

        int nextAutoFieldKey = -1;

        tokenizer.expectSymbol("message start", Token.kMessageStart);

        Set<String> fieldNames = new HashSet<>();
        Set<String> fieldNameVariants = new HashSet<>();
        Set<Integer> fieldIds = new HashSet<>();

        while (true) {
            Token token = tokenizer.expect("field def or message end");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.strEquals(kLineCommentStart)) {
                comment = parseDocLine(tokenizer, comment);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                comment = tokenizer.parseDocBlock();
                continue;
            }

            FieldType._Builder field = FieldType.builder();
            field.setDocumentation(comment);
            comment = null;

            if (token.isInteger()) {
                int fId = (int) token.parseInteger();
                if (fId < 1) {
                    throw tokenizer.failure(token,
                                            "Negative or 0 field id " + fId + " not allowed.");
                }
                if (fieldIds.contains(fId)) {
                    throw tokenizer.failure(token,
                                            "Field id " + fId + " already exists in " + struct.build().getName());
                }
                fieldIds.add(fId);
                field.setId(fId);

                tokenizer.expectSymbol("field id sep", Token.kKeyValueSep);
                token = tokenizer.expect("field requirement or type",
                                         t -> t.isIdentifier() || t.isQualifiedIdentifier());
            } else {
                if (requireFieldId) {
                    throw tokenizer.failure(token,
                                            "Missing field ID in strict declaration");
                }
                field.setId(nextAutoFieldKey--);
            }

            if (token.strEquals(kRequired)) {
                if (union) {
                    throw tokenizer.failure(token,
                                            "Found required field in union");
                }
                field.setRequirement(FieldRequirement.REQUIRED);
                token = tokenizer.expect("field type",
                                         t -> t.isIdentifier() || t.isQualifiedIdentifier());
            } else if (token.strEquals(kOptional)) {
                if (!union) {
                    // All union fields are optional regardless.
                    field.setRequirement(FieldRequirement.OPTIONAL);
                }
                token = tokenizer.expect("field type",
                                         t -> t.isIdentifier() || t.isQualifiedIdentifier());
            }

            // Get type.... This is mandatory.
            field.setType(parseType(tokenizer, token, includedPrograms));

            nameToken = tokenizer.expectIdentifier("field name");
            String fName = nameToken.asString();
            if (!allowedNameIdentifier(fName)) {
                throw tokenizer.failure(nameToken, "Field with reserved name: " + fName);
            }

            if (fieldNames.contains(fName)) {
                throw tokenizer.failure(nameToken,
                                        "Field %s already exists in %s",
                                        fName,
                                        struct.build().getName());
            }
            if (fieldNameVariants.contains(Strings.camelCase("get", fName))) {
                throw tokenizer.failure(nameToken,
                                        "Field %s has field with conflicting name in %s",
                                        fName,
                                        struct.build().getName());
            }
            fieldNames.add(fName);
            fieldNameVariants.add(Strings.camelCase("get", fName));

            field.setName(fName);

            token = tokenizer.peek("default sep, annotation, field def or message end");

            // Default value
            if (token.isSymbol(Token.kFieldValueSep)) {
                tokenizer.next();
                Token defaultValue = tokenizer.parseValue();
                field.setDefaultValue(defaultValue.asString());
                field.setStartLineNo(defaultValue.getLineNo());
                field.setStartLinePos(defaultValue.getLinePos());
                token = tokenizer.peek("field annotation, def or message end");
            }

            // Annotation
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                field.setAnnotations(parseAnnotations(tokenizer, "field"));
                token = tokenizer.peek("field def or message end");
            }

            struct.addToFields(field.build());

            if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
            }
        }

        if (tokenizer.hasNext()) {
            Token token = tokenizer.peek("optional annotations");
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                struct.setAnnotations(parseAnnotations(tokenizer, "message"));
            }
        }

        return struct.build();
    }

    private String parseType(ThriftTokenizer tokenizer, Token token, Set<String> includedPrograms) throws IOException {
        if (!token.isQualifiedIdentifier() &&
            !token.isIdentifier()) {
            throw tokenizer.failure(token, "Expected type identifier but found " + token.asString());
        }

        String type = token.asString();
        switch (type) {
            case "list":
            case "set": {
                tokenizer.expectSymbol(type + " generic start", Token.kGenericStart);
                String item = parseType(tokenizer, tokenizer.expect(type + " item type",
                                                                    t -> t.isIdentifier() || t.isQualifiedIdentifier()), includedPrograms);
                tokenizer.expectSymbol(type + " generic end", Token.kGenericEnd);

                return String.format(Locale.US, "%s<%s>", type, item);
            }
            case "map": {
                tokenizer.expectSymbol(type + " generic start", Token.kGenericStart);
                String key = parseType(tokenizer, tokenizer.expect(type + " key type",
                                                                   t -> t.isIdentifier() || t.isQualifiedIdentifier()
                                                                   ), includedPrograms);
                tokenizer.expectSymbol(type + " generic sep", Token.kLineSep1);
                String item = parseType(tokenizer, tokenizer.expect(type + " item type",
                                                                    t -> t.isIdentifier() || t.isQualifiedIdentifier()), includedPrograms);
                tokenizer.expectSymbol(type + " generic end", Token.kGenericEnd);

                return String.format(Locale.US, "%s<%s,%s>", type, key, item);
            }
            default:
                if (type.contains(".")) {
                    // Enforce scope by program name.
                    String program = type.replaceAll("[.].*", "");
                    if (!includedPrograms.contains(program)) {
                        throw tokenizer.failure(token, "Unknown program '%s' for type %s", program, type);
                    }
                }
                return type;
        }
    }
}
