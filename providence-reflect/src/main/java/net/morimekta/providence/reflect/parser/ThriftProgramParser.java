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
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.FieldRequirement;
import net.morimekta.providence.model.FieldType;
import net.morimekta.providence.model.FunctionType;
import net.morimekta.providence.model.MessageType;
import net.morimekta.providence.model.MessageVariant;
import net.morimekta.providence.model.Model_Constants;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.model.ServiceType;
import net.morimekta.providence.model.TypedefType;
import net.morimekta.providence.reflect.parser.internal.ThriftTokenizer;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
    private final static Pattern RE_BLOCK_LINE       = Pattern.compile("^([\\s]*[*])?[\\s]?");
    private static final Pattern VALID_PROGRAM_NAME  = Pattern.compile(
            "[-._a-zA-Z][-._a-zA-Z0-9]*");
    public static final Pattern  VALID_NAMESPACE     = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern  VALID_SDI_NAMESPACE = Pattern.compile(
            "([_a-zA-Z][-_a-zA-Z0-9]*[.])*[_a-zA-Z][-_a-zA-Z0-9]*");

    private final boolean requireFieldId;
    private final boolean requireEnumValue;

    public ThriftProgramParser() {
        this(false, false);
    }

    public ThriftProgramParser(boolean requireFieldId, boolean requireEnumValue) {
        this.requireFieldId = requireFieldId;
        this.requireEnumValue = requireEnumValue;
    }

    @Override
    public ProgramType parse(InputStream in, File file, Collection<File> includeDirs) throws IOException {
        ProgramType._Builder program = ProgramType.builder();

        String programName = ReflectionUtils.programNameFromPath(file.getName());
        if (!VALID_PROGRAM_NAME.matcher(programName).matches()) {
            throw new ParseException("Program name \"%s\" derived from filename \"%s\" is not valid.",
                                     Strings.escape(programName),
                                     Strings.escape(file.getName()));
        }
        program.setProgramName(programName);

        List<String> includeFiles = new LinkedList<>();
        Set<String> includedPrograms = new HashSet<>();
        Map<String, String> namespaces = new LinkedHashMap<>();

        List<Declaration> declarations = new LinkedList<>();

        ThriftTokenizer tokenizer = new ThriftTokenizer(in);

        boolean hasHeader = false;
        boolean hasDeclaration = false;

        String documentation = null;
        Token token;
        while ((token = tokenizer.next()) != null) {
            if (token.strEquals(kLineCommentStart)) {
                documentation = parseDocLine(tokenizer, documentation);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                documentation = parseDocBlock(tokenizer);
                continue;
            }

            String id = token.asString();
            if (!Model_Constants.kThriftKeywords.contains(id)) {
                throw tokenizer.failure(token,
                                        "Unexpected token \'%s\'", token.asString());
            }
            switch (id) {
                case kNamespace:
                    if (hasDeclaration) {
                        throw tokenizer.failure(token,
                                                "Unexpected token 'namespace', expected type declaration");
                    }
                    if (documentation != null && !hasHeader) {
                        program.setDocumentation(documentation);
                    }
                    documentation = null;
                    hasHeader = true;
                    parseNamespace(tokenizer, namespaces);
                    break;
                case kInclude:
                    if (hasDeclaration) {
                        throw tokenizer.failure(token,
                                                "Unexpected token 'include', expected type declaration");
                    }
                    if (documentation != null && !hasHeader) {
                        program.setDocumentation(documentation);
                    }
                    documentation = null;
                    hasHeader = true;
                    parseIncludes(tokenizer, includeFiles, file, includedPrograms, includeDirs);
                    break;
                case kTypedef:
                    hasHeader = true;
                    hasDeclaration = true;
                    parseTypedef(tokenizer, documentation, declarations, includedPrograms);
                    documentation = null;
                    break;
                case kEnum:
                    hasHeader = true;
                    hasDeclaration = true;
                    EnumType et = parseEnum(tokenizer, documentation);
                    declarations.add(Declaration.withDeclEnum(et));
                    documentation = null;
                    break;
                case kStruct:
                case kUnion:
                case kException:
                    hasHeader = true;
                    hasDeclaration = true;
                    MessageType st = parseMessage(tokenizer, token.asString(), documentation, includedPrograms);
                    declarations.add(Declaration.withDeclStruct(st));
                    documentation = null;
                    break;
                case kService:
                    hasHeader = true;
                    hasDeclaration = true;
                    ServiceType srv = parseService(tokenizer, documentation, includedPrograms);
                    declarations.add(Declaration.withDeclService(srv));
                    documentation = null;
                    break;
                case kConst:
                    hasHeader = true;
                    hasDeclaration = true;
                    ConstType cnst = parseConst(tokenizer, documentation, includedPrograms);
                    declarations.add(Declaration.withDeclConst(cnst));
                    documentation = null;
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
        if (includeFiles.size() > 0) {
            program.setIncludes(includeFiles);
        }
        if (declarations.size() > 0) {
            program.setDecl(declarations);
        }

        return program.build();
    }

    private ConstType parseConst(ThriftTokenizer tokenizer, String comment, Set<String> includedPrograms) throws IOException, ParseException {
        Token token = tokenizer.expect("const typename",
                                       t -> t.isIdentifier() ||
                                            t.isQualifiedIdentifier());
        String type = parseType(tokenizer, token, includedPrograms);
        Token id = tokenizer.expectIdentifier("const identifier");

        tokenizer.expectSymbol("const value separator", Token.kFieldValueSep);

        String value = parseValue(tokenizer);

        Token sep = tokenizer.peek();
        if (sep != null && (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2))) {
            tokenizer.next();
        }

        return ConstType.builder()
                          .setDocumentation(comment)
                          .setName(id.asString())
                          .setType(type)
                          .setValue(value)
                          .build();
    }

    private String parseValue(ThriftTokenizer tokenizer) throws IOException {
        Stack<Character> enclosures = new Stack<>();
        StringBuilder builder = new StringBuilder();
        while (true) {
            Token token = tokenizer.expect("const value");

            if (token.strEquals(kBlockCommentStart)) {
                parseDocBlock(tokenizer);  // ignore.
                continue;
            } else if (token.strEquals(kLineCommentStart)) {
                parseDocLine(tokenizer, null);  // ignore
                continue;
            } else if (token.isSymbol(Token.kMessageStart)) {
                enclosures.push(Token.kMessageEnd);
            } else if (token.isSymbol(Token.kListStart)) {
                enclosures.push(Token.kListEnd);
            } else if ((token.isSymbol(Token.kMessageEnd) || token.isSymbol(Token.kListEnd)) &&
                       enclosures.peek().equals(token.charAt(0))) {
                enclosures.pop();
            }

            builder.append(token.asString());
            if (enclosures.isEmpty()) {
                return builder.toString();
            }
        }
    }

    private String parseDocLine(ThriftTokenizer tokenizer, String comment) throws IOException {
        String line = IOUtils.readString(tokenizer, "\n").trim();
        if (comment != null) {
            return comment + "\n" + line;
        }
        return line;
    }

    private String parseDocBlock(ThriftTokenizer tokenizer) throws IOException {
        String block = IOUtils.readString(tokenizer, ThriftTokenizer.kBlockCommentEnd).trim();
        String[] lines = block.split("\\r?\\n");
        StringBuilder builder = new StringBuilder();

        Pattern re = RE_BLOCK_LINE;
        for (String line : lines) {
            builder.append(re.matcher(line)
                             .replaceFirst(""));
            builder.append('\n');
        }
        return builder.toString()
                      .trim();
    }

    private ServiceType parseService(ThriftTokenizer tokenizer, String comment, Set<String> includedPrograms) throws IOException, ParseException {
        ServiceType._Builder service = ServiceType.builder();

        if (comment != null) {
            service.setDocumentation(comment);
            comment = null;
        }
        Token identifier = tokenizer.expectIdentifier("service identifier");
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
                comment = parseDocLine(tokenizer, comment);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                comment = parseDocBlock(tokenizer);
                continue;
            }

            FunctionType._Builder method = FunctionType.builder();
            if (comment != null) {
                method.setDocumentation(comment);
                comment = null;
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

            String name = tokenizer.expectIdentifier("method name").asString();
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
                    comment = parseDocLine(tokenizer, comment);
                    continue;
                } else if (token.strEquals(kBlockCommentStart)) {
                    comment = parseDocBlock(tokenizer);
                    continue;
                }

                FieldType._Builder field = FieldType.builder();
                if (comment != null) {
                    field.setDocumentation(comment);
                    comment = null;
                }

                if (token.isInteger()) {
                    field.setKey((int) token.parseInteger());
                    tokenizer.expectSymbol("params kv sep", Token.kKeyValueSep);
                    token = tokenizer.expect("param type");
                } else {
                    if (requireFieldId) {
                        throw tokenizer.failure(token, "Missing param ID in strict declaration");
                    }
                    field.setKey(nextAutoParamKey--);
                }

                field.setType(parseType(tokenizer, token, includedPrograms));
                field.setName(tokenizer.expectIdentifier("param name")
                                       .asString());

                // Annotations.
                if (tokenizer.peek("method param annotation")
                             .isSymbol(Token.kParamsStart)) {
                    tokenizer.next();
                    char sep = Token.kParamsStart;
                    while (sep != Token.kParamsEnd) {
                        token = tokenizer.expect("annotation name",
                                                 t -> t.isIdentifier() || t.isQualifiedIdentifier());
                        name = token.asString();
                        tokenizer.expectSymbol("", Token.kFieldValueSep);
                        Token value = tokenizer.expectLiteral("annotation value");

                        field.putInAnnotations(name, value.decodeLiteral(true));

                        sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                    }
                }

                token = tokenizer.peek("method params");
                if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                    tokenizer.next();
                }

                method.addToParams(field.build());
            }  // for each param
            comment = null;

            if (tokenizer.peek("parsing method exceptions")
                         .strEquals(kThrows)) {
                tokenizer.next();
                tokenizer.expectSymbol("parsing method exceptions", Token.kParamsStart);

                int nextAutoExceptionKey = -1;

                while (true) {
                    token = tokenizer.expect("parsing method exception");

                    if (token.isSymbol(Token.kParamsEnd)) {
                        break;
                    } else if (token.strEquals(kLineCommentStart)) {
                        comment = parseDocLine(tokenizer, comment);
                        continue;
                    } else if (token.strEquals(kBlockCommentStart)) {
                        comment = parseDocBlock(tokenizer);
                        continue;
                    }

                    FieldType._Builder field = FieldType.builder();
                    if (comment != null) {
                        field.setDocumentation(comment);
                        comment = null;
                    }

                    if (token.isInteger()) {
                        field.setKey((int) token.parseInteger());
                        tokenizer.expectSymbol("reading method exception", Token.kKeyValueSep);
                        token = tokenizer.expect("reading method exception type");
                    } else {
                        if (requireFieldId) {
                            throw tokenizer.failure(token, "Missing exception ID in strict declaration");
                        }
                        field.setKey(nextAutoExceptionKey--);
                    }

                    field.setType(parseType(tokenizer, token, includedPrograms));
                    field.setName(tokenizer.expectIdentifier("reading method exception name")
                                           .asString());

                    // Annotations.
                    if (tokenizer.peek("reading method exception annotation")
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        char sep = Token.kParamsStart;
                        while (sep != Token.kParamsEnd) {
                            token = tokenizer.expect("exception annotation name",
                                                     t -> t.isIdentifier() || t.isQualifiedIdentifier());
                            name = token.asString();
                            tokenizer.expectSymbol("", Token.kFieldValueSep);
                            Token value = tokenizer.expectLiteral("exception annotation value");

                            field.putInAnnotations(name, value.decodeLiteral(true));

                            sep = tokenizer.expectSymbol("exception annotation sep",
                                                         Token.kParamsEnd,
                                                         Token.kLineSep1,
                                                         Token.kLineSep2);
                        }
                    }

                    method.addToExceptions(field.build());

                    token = tokenizer.peek("reading method exceptions");
                    if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                    }
                }
            }

            token = tokenizer.peek("");
            // Method Annotations.
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                char sep = Token.kParamsStart;
                while (sep != Token.kParamsEnd) {
                    token = tokenizer.expect("annotation name", Token::isReferenceIdentifier);
                    name = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token value = tokenizer.expectLiteral("annotation value");

                    method.putInAnnotations(name, value.decodeLiteral(true));

                    sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                }
                token = tokenizer.peek("reading method params");
            }

            service.addToMethods(method.build());

            if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
            }
        }  // for each method-line

        Token token = tokenizer.peek();
        if (token != null) {
            if (token.isSymbol(Token.kParamsStart)) {
                // Method Annotations.
                tokenizer.next();
                char sep = Token.kParamsStart;
                while (sep != Token.kParamsEnd) {
                    token = tokenizer.expect("annotation name", Token::isReferenceIdentifier);
                    String name = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token value = tokenizer.expectLiteral("annotation value");

                    service.putInAnnotations(name, value.decodeLiteral(true));

                    sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                }
            }
        }

        return service.build();
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
                "parsing namespace",
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
        String type = parseType(tokenizer, tokenizer.expect("parsing typedef type."), includedPrograms);
        Token id = tokenizer.expectIdentifier("parsing typedef identifier.");

        TypedefType typedef = TypedefType.builder()
                                         .setDocumentation(comment)
                                         .setType(type)
                                         .setName(id.asString())
                                         .build();
        declarations.add(Declaration.withDeclTypedef(typedef));
    }

    private EnumType parseEnum(ThriftTokenizer tokenizer, String comment) throws IOException {
        String id = tokenizer.expectIdentifier("parsing enum identifier").asString();

        EnumType._Builder etb = EnumType.builder();
        if (comment != null) {
            etb.setDocumentation(comment);
            comment = null;
        }
        etb.setName(id);

        int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;

        tokenizer.expectSymbol("parsing enum " + id, Token.kMessageStart);

        if (!tokenizer.peek("").isSymbol(Token.kMessageEnd)) {
            while (true) {
                Token token = tokenizer.expect("parsing enum " + id);
                if (token.isSymbol(Token.kMessageEnd)) {
                    break;
                } else if (token.strEquals(kLineCommentStart)) {
                    comment = parseDocLine(tokenizer, comment);
                } else if (token.strEquals(kBlockCommentStart)) {
                    comment = parseDocBlock(tokenizer);
                } else if (token.isIdentifier()) {
                    EnumValue._Builder evb = EnumValue.builder();
                    evb.setName(token.asString());
                    if (comment != null) {
                        evb.setDocumentation(comment);
                        comment = null;
                    }

                    int value = nextValue++;
                    if (tokenizer.peek("parsing enum " + id)
                                 .isSymbol(Token.kFieldValueSep)) {
                        tokenizer.next();
                        Token v = tokenizer.expectInteger("enum value");
                        value = (int) v.parseInteger();
                        nextValue = value + 1;
                    } else if (requireEnumValue) {
                        // So the token points at the token that *should* have been '='.
                        if (tokenizer.hasNext()) {
                            token = tokenizer.next();
                        }
                        throw tokenizer.failure(token, "Missing enum value in strict declaration");
                    }

                    evb.setValue(value);

                    // Enum value annotations.
                    if (tokenizer.peek("parsing enum " + id)
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        char sep2 = Token.kParamsStart;
                        while (sep2 != Token.kParamsEnd) {
                            token = tokenizer.expect("annotation name", Token::isReferenceIdentifier);
                            String name = token.asString();
                            tokenizer.expectSymbol("", Token.kFieldValueSep);
                            Token val = tokenizer.expectLiteral("annotation value");

                            evb.putInAnnotations(name, val.decodeLiteral(true));

                            sep2 = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                        }
                    }

                    etb.addToValues(evb.build());

                    // Optional separator...
                    token = tokenizer.peek("parsing enum " + id);
                    if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                    }
                } else {
                    throw tokenizer.failure(token,
                                            "Unexpected token while parsing enum %d: %s",
                                            id, token.asString());
                }
            }
        } // if has values.

        Token token = tokenizer.peek();
        if (token != null && token.isSymbol(Token.kParamsStart)) {
            tokenizer.next();
            char sep = token.charAt(0);
            while (sep != Token.kParamsEnd) {
                token = tokenizer.expect("annotation name", Token::isReferenceIdentifier);
                String name = token.asString();
                tokenizer.expectSymbol("", Token.kFieldValueSep);
                Token val = tokenizer.expectLiteral("annotation value");

                etb.putInAnnotations(name, val.decodeLiteral(true));

                sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
            }
        }

        return etb.build();
    }

    private MessageType parseMessage(ThriftTokenizer tokenizer, String type, String comment, Set<String> includedPrograms)
            throws IOException {
        MessageType._Builder struct = MessageType.builder();
        if (comment != null) {
            struct.setDocumentation(comment);
            comment = null;
        }
        boolean union = type.equals("union");
        if (!type.equals("struct")) {
            struct.setVariant(MessageVariant.forName(type.toUpperCase()));
        }

        Token id = tokenizer.expectIdentifier("parsing " + type + " identifier");
        if (!id.isIdentifier()) {
            throw tokenizer.failure(id, "Invalid type identifier " + id.asString());
        }
        struct.setName(id.asString());

        int nextAutoFieldKey = -1;

        tokenizer.expectSymbol("struct start", Token.kMessageStart);

        Set<String> fieldNames = new HashSet<>();
        Set<String> fieldNameVariants = new HashSet<>();
        Set<Integer> fieldIds = new HashSet<>();

        while (true) {
            Token token = tokenizer.expect("field def");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.strEquals(kLineCommentStart)) {
                comment = parseDocLine(tokenizer, comment);
                continue;
            } else if (token.strEquals(kBlockCommentStart)) {
                comment = parseDocBlock(tokenizer);
                continue;
            }

            FieldType._Builder field = FieldType.builder();
            field.setDocumentation(comment);
            comment = null;

            if (token.isInteger()) {
                int fId = (int) token.parseInteger();
                if (fId < 1) {
                    throw tokenizer.failure(token,
                                            "Negative field id " + fId + " not allowed.");
                }
                if (fieldIds.contains(fId)) {
                    throw tokenizer.failure(token,
                                            "Field id " + fId + " already exists in struct " + struct.build().getName());
                }
                fieldIds.add(fId);
                field.setKey(fId);

                tokenizer.expectSymbol("field id sep", Token.kKeyValueSep);
                token = tokenizer.expect("field requirement or type",
                                         t -> t.isIdentifier() || t.isQualifiedIdentifier());
            } else {
                if (requireFieldId) {
                    throw tokenizer.failure(token,
                                            "Missing field ID in strict declaration");
                }
                field.setKey(nextAutoFieldKey--);
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

            Token name = tokenizer.expectIdentifier("parsing struct " + id.asString());
            String fName = name.asString();
            if (fieldNames.contains(fName)) {
                throw tokenizer.failure(name,
                                        "Field %s already exists in struct %s",
                                        fName,
                                        struct.build().getName());
            }
            if (fieldNameVariants.contains(Strings.camelCase("get", fName))) {
                throw tokenizer.failure(name,
                                        "Field %s has field with conflicting name in %s",
                                        fName,
                                        struct.build().getName());
            }
            fieldNames.add(fName);
            fieldNameVariants.add(Strings.camelCase("get", fName));

            field.setName(fName);

            token = tokenizer.peek("");

            // Default value
            if (token.isSymbol(Token.kFieldValueSep)) {
                tokenizer.next();
                field.setDefaultValue(parseValue(tokenizer));
                token = tokenizer.peek("");
            }

            // annotation
            if (token.isSymbol(Token.kParamsStart)) {
                tokenizer.next();
                char sep = token.charAt(0);
                while (sep != Token.kParamsEnd) {
                    token = tokenizer.expect("annotation name", Token::isReferenceIdentifier);
                    String aName = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token val = tokenizer.expectLiteral("annotation value");

                    field.putInAnnotations(aName, val.decodeLiteral(true));

                    sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                }
                token = tokenizer.peek("");
            }

            struct.addToFields(field.build());

            if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
            }
        }

        Token token = tokenizer.peek();
        if (token != null && token.isSymbol(Token.kParamsStart)) {
            tokenizer.next();
            char sep = token.charAt(0);
            while (sep != Token.kParamsEnd) {
                token = tokenizer.expect("annotation name",
                                         Token::isReferenceIdentifier);
                String annotationKey = token.asString();
                sep = tokenizer.expectSymbol("annotation value sep",
                                             Token.kFieldValueSep,
                                             Token.kParamsEnd,
                                             Token.kLineSep1,
                                             Token.kLineSep2);
                if (sep != Token.kFieldValueSep) {
                    struct.putInAnnotations(annotationKey, "");
                    continue;
                }

                Token annotationValue = tokenizer.expectLiteral("annotation value");
                struct.putInAnnotations(annotationKey, annotationValue.decodeLiteral(true));

                sep = tokenizer.expectSymbol("annotation sep",
                                             Token.kParamsEnd,
                                             Token.kLineSep1,
                                             Token.kLineSep2);
            }
        }

        return struct.build();
    }

    private String parseType(ThriftTokenizer tokenizer, Token token, Set<String> includedPrograms) throws IOException {
        if (!token.isQualifiedIdentifier() &&
            !token.isIdentifier()) {
            throw tokenizer.failure(token, "Expected type identifier but found " + token);
        }

        String type = token.asString();
        switch (type) {
            case "list":
            case "set": {
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericStart);
                String item = parseType(tokenizer, tokenizer.expect("parsing " + type + " item type",
                                                                    t -> t.isIdentifier() || t.isQualifiedIdentifier()), includedPrograms);
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericEnd);

                return String.format("%s<%s>", type, item);
            }
            case "map": {
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericStart);
                String key = parseType(tokenizer, tokenizer.expect("parsing " + type + " key type",
                                                                   t -> t.isIdentifier() || t.isQualifiedIdentifier()
                                                                   ), includedPrograms);
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kLineSep1);
                String item = parseType(tokenizer, tokenizer.expect("parsing " + type + " item type",
                                                                    t -> t.isIdentifier() || t.isQualifiedIdentifier()), includedPrograms);
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericEnd);

                return String.format("%s<%s,%s>", type, key, item);
            }
            default:
                if (type.contains(".")) {
                    // Enforce scope by program name.
                    String program = type.replaceAll("[.].*", "");
                    if (!includedPrograms.contains(program)) {
                        throw tokenizer.failure(token, "Unknown program %s for type %s", program, type);
                    }
                }
                return type;
        }
    }
}
