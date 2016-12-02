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
import net.morimekta.providence.reflect.parser.internal.Token;
import net.morimekta.providence.reflect.parser.internal.Tokenizer;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ThriftProgramParser implements ProgramParser {
    private final static Pattern RE_BLOCK_LINE        = Pattern.compile("^([\\s]*[*])?[\\s]?");
    private static final Pattern VALID_PROGRAM_NAME   = Pattern.compile(
            "[-._a-zA-Z0-9]+");
    public static final Pattern  VALID_IDENTIFIER     = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern  VALID_SDI_IDENTIFIER = Pattern.compile(
            "([_a-zA-Z][-_a-zA-Z0-9]*[.])*[_a-zA-Z][-_a-zA-Z0-9]*");

    @Override
    public ProgramType parse(InputStream in, String filePath) throws IOException, ParseException {
        ProgramType._Builder program = ProgramType.builder();

        String programName = ReflectionUtils.programNameFromPath(filePath);
        if (!VALID_PROGRAM_NAME.matcher(programName).matches()) {
            throw new ParseException("Program name \"%s\" derived from filename \"%s\" is not valid.",
                                     Strings.escape(programName),
                                     Strings.escape(filePath));
        }
        program.setProgramName(programName);

        List<String> includes = new LinkedList<>();
        Map<String, String> namespaces = new LinkedHashMap<>();

        List<Declaration> declarations = new LinkedList<>();

        Tokenizer tokenizer = new Tokenizer(in);

        boolean hasHeader = false;
        boolean hasDeclaration = false;

        String documentation = null;
        Token token;
        while ((token = tokenizer.next()) != null) {
            if (token.startsLineComment()) {
                documentation = parseLineComment(tokenizer, documentation);
                continue;
            } else if (token.startsBlockComment()) {
                documentation = parseBlockComment(tokenizer);
                continue;
            }

            String id = token.asString();
            if (!Model_Constants.kThriftKeywords.contains(id)) {
                throw new ParseException(tokenizer, token, "Unexpected token \'%s\'", token.asString());
            }
            switch (id) {
                case "namespace":
                    if (hasDeclaration) {
                        throw new ParseException(tokenizer, token,
                                                 "Unexpected token 'namespace', expected type declaration");
                    }
                    if (documentation != null && !hasHeader) {
                        program.setDocumentation(documentation);
                    }
                    documentation = null;
                    hasHeader = true;
                    parseNamespace(tokenizer, namespaces);
                    break;
                case "include":
                    if (hasDeclaration) {
                        throw new ParseException(tokenizer, token,
                                                 "Unexpected token 'include', expected type declaration");
                    }
                    if (documentation != null && !hasHeader) {
                        program.setDocumentation(documentation);
                    }
                    documentation = null;
                    hasHeader = true;
                    parseIncludes(tokenizer, includes);
                    break;
                case "typedef":
                    hasHeader = true;
                    hasDeclaration = true;
                    parseTypedef(tokenizer, documentation, declarations);
                    documentation = null;
                    break;
                case "enum":
                    hasHeader = true;
                    hasDeclaration = true;
                    EnumType et = parseEnum(tokenizer, documentation);
                    declarations.add(Declaration.withDeclEnum(et));
                    documentation = null;
                    break;
                case "struct":
                case "union":
                case "exception":
                    hasHeader = true;
                    hasDeclaration = true;
                    MessageType st = parseMessage(tokenizer, token.asString(), documentation);
                    declarations.add(Declaration.withDeclStruct(st));
                    documentation = null;
                    break;
                case "service":
                    hasHeader = true;
                    hasDeclaration = true;
                    ServiceType srv = parseService(tokenizer, documentation);
                    declarations.add(Declaration.withDeclService(srv));
                    documentation = null;
                    break;
                case "const":
                    hasHeader = true;
                    hasDeclaration = true;
                    ConstType cnst = parseConst(tokenizer, documentation);
                    declarations.add(Declaration.withDeclConst(cnst));
                    documentation = null;
                    break;
                default:
                    throw new ParseException(tokenizer, token,
                                             "Expected type declaration, but got '%s'",
                                             Strings.escape(token.asString()));
            }
        }

        program.setNamespaces(namespaces);
        program.setIncludes(includes);
        program.setDecl(declarations);

        return program.build();
    }

    private ConstType parseConst(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        Token token = tokenizer.expectQualifiedIdentifier("const typename");
        String type = parseType(tokenizer, token);
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

    private String parseValue(Tokenizer tokenizer) throws IOException, ParseException {
        Stack<Character> enclosures = new Stack<>();
        StringBuilder builder = new StringBuilder();
        while (true) {
            Token token = tokenizer.expect("const value");

            if (token.startsBlockComment()) {
                parseBlockComment(tokenizer);  // ignore.
                continue;
            } else if (token.startsLineComment()) {
                parseLineComment(tokenizer, null);  // ignore
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

    private String parseLineComment(Tokenizer tokenizer, String comment) throws IOException {
        String line = IOUtils.readString(tokenizer, "\n").trim();
        if (comment != null) {
            return comment + "\n" + line;
        }
        return line;
    }

    private String parseBlockComment(Tokenizer tokenizer) throws IOException {
        String block = IOUtils.readString(tokenizer, new String(Token.kBlockCommentEnd, US_ASCII)).trim();
        String[] lines = block.split("\n");
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

    private ServiceType parseService(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        ServiceType._Builder service = ServiceType.builder();

        if (comment != null) {
            service.setDocumentation(comment);
            comment = null;
        }
        Token identifier = tokenizer.expectIdentifier("service identifier");
        service.setName(identifier.asString());

        if (tokenizer.peek("service start or extends").strEquals(Token.kExtends)) {
            tokenizer.next();
            service.setExtend(tokenizer.expectQualifiedIdentifier("service extending identifier").asString());
        }

        tokenizer.expectSymbol("reading service start", Token.kMessageStart);

        Set<String> methodNames = new TreeSet<>();

        while (true) {
            Token token = tokenizer.expect("service method initializer");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            FunctionType._Builder method = FunctionType.builder();
            if (comment != null) {
                method.setDocumentation(comment);
                comment = null;
            }

            if (token.strEquals(Token.kOneway)) {
                method.setOneWay(true);
                token = tokenizer.expect("service method type");
            }

            if (!token.strEquals(Token.kVoid)) {
                if (method.isSetOneWay()) {
                    throw new ParseException(tokenizer,
                                             token,
                                             "Oneway methods must have void return type, found '%s'",
                                             Strings.escape(token.asString()));
                }
                method.setReturnType(parseType(tokenizer, token));
            }

            String name = tokenizer.expectIdentifier("method name").asString();
            String normalized = Strings.camelCase("", name);
            if (methodNames.contains(normalized)) {
                throw new ParseException(tokenizer,
                                         token,
                                         "Service method " + name +
                                         " has normalized name conflict");
            }
            methodNames.add(normalized);

            method.setName(name);

            tokenizer.expectSymbol("method params begin", Token.kParamsStart);
            while (true) {
                token = tokenizer.expect("method params");
                if (token.isSymbol(Token.kParamsEnd)) {
                    break;
                } else if (token.startsLineComment()) {
                    comment = parseLineComment(tokenizer, comment);
                    continue;
                } else if (token.startsBlockComment()) {
                    comment = parseBlockComment(tokenizer);
                    continue;
                }

                FieldType._Builder field = FieldType.builder();
                if (comment != null) {
                    field.setDocumentation(comment);
                    comment = null;
                }

                if (token.isInteger()) {
                    field.setKey((int) token.parseInteger());
                    tokenizer.expectSymbol("method params (:)", Token.kFieldIdSep);
                    token = tokenizer.expect("method param type");
                }

                field.setType(parseType(tokenizer, token));
                field.setName(tokenizer.expectIdentifier("method param name")
                                       .asString());

                // Annotations.
                if (tokenizer.peek("method param annotation")
                             .isSymbol(Token.kParamsStart)) {
                    tokenizer.next();
                    char sep = Token.kParamsStart;
                    while (sep != Token.kParamsEnd) {
                        token = tokenizer.expectQualifiedIdentifier("annotation name");
                        name = token.asString();
                        tokenizer.expectSymbol("", Token.kFieldValueSep);
                        Token value = tokenizer.expectStringLiteral("annotation value");

                        field.putInAnnotations(name, value.decodeStringLiteral());

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
                         .strEquals(Token.kThrows)) {
                tokenizer.next();
                tokenizer.expectSymbol("parsing method exceptions", Token.kParamsStart);

                while (true) {
                    token = tokenizer.expect("parsing method exception");

                    if (token.isSymbol(Token.kParamsEnd)) {
                        break;
                    } else if (token.startsLineComment()) {
                        comment = parseLineComment(tokenizer, comment);
                        continue;
                    } else if (token.startsBlockComment()) {
                        comment = parseBlockComment(tokenizer);
                        continue;
                    }

                    FieldType._Builder field = FieldType.builder();
                    if (comment != null) {
                        field.setDocumentation(comment);
                        comment = null;
                    }

                    if (token.isInteger()) {
                        field.setKey((int) token.parseInteger());
                        tokenizer.expectSymbol("reading method exception (:)", Token.kFieldIdSep);
                        token = tokenizer.expect("reading method exception type");
                    }

                    field.setType(parseType(tokenizer, token));
                    field.setName(tokenizer.expectIdentifier("reading method exception name")
                                           .asString());

                    // Annotations.
                    if (tokenizer.peek("reading method exception annotation")
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        char sep = Token.kParamsStart;
                        while (sep != Token.kParamsEnd) {
                            token = tokenizer.expectQualifiedIdentifier("exception annotation name");
                            name = token.asString();
                            tokenizer.expectSymbol("", Token.kFieldValueSep);
                            Token value = tokenizer.expectStringLiteral("exception annotation value");

                            field.putInAnnotations(name, value.decodeStringLiteral());

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
                    token = tokenizer.expectQualifiedIdentifier("annotation name");
                    name = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token value = tokenizer.expectStringLiteral("annotation value");

                    method.putInAnnotations(name, value.decodeStringLiteral());

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
                    token = tokenizer.expectQualifiedIdentifier("annotation name");
                    String name = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token value = tokenizer.expectStringLiteral("annotation value");

                    service.putInAnnotations(name, value.decodeStringLiteral());

                    sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                }
            }
        }

        return service.build();
    }

    public void parseNamespace(Tokenizer tokenizer, Map<String, String> namespaces) throws IOException, ParseException {
        Token language = tokenizer.expectQualifiedIdentifier("parsing namespace language");
        if (!language.isQualifiedIdentifier()) {
            throw new ParseException(tokenizer, language,
                                     "Namespace language not valid identifier: '%s'",
                                     language.asString());
        }
        if (namespaces.containsKey(language.asString())) {
            throw new ParseException(tokenizer, language,
                                     "Namespace for %s already defined.",
                                     language.asString());
        }

        Token namespace = tokenizer.expectQualifiedIdentifier("parsing namespace");

        if (!namespace.isQualifiedIdentifier()) {
            throw new ParseException(tokenizer, namespace,
                                     "Namespace not valid: '%s'",
                                     namespace.asString());
        }

        namespaces.put(language.asString(), namespace.asString());
    }

    public void parseIncludes(Tokenizer tokenizer, List<String> includes) throws IOException, ParseException {
        Token include = tokenizer.expectStringLiteral("include file");
        String name = include.substring(1, -1).asString();
        if (!ReflectionUtils.isThriftFile(name)) {
            throw new ParseException(tokenizer, include,
                                     "Include not valid for thrift files " + name);
        }
        includes.add(include.substring(1, -1).asString());
    }

    private void parseTypedef(Tokenizer tokenizer, String comment, List<Declaration> declarations)
            throws IOException, ParseException {
        String type = parseType(tokenizer, tokenizer.expect("parsing typedef type."));
        Token id = tokenizer.expectIdentifier("parsing typedef identifier.");

        TypedefType typedef = TypedefType.builder()
                                         .setDocumentation(comment)
                                         .setType(type)
                                         .setName(id.asString())
                                         .build();
        declarations.add(Declaration.withDeclTypedef(typedef));
    }

    public EnumType parseEnum(Tokenizer tokenizer, String comment) throws IOException, ParseException {
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
                } else if (token.startsLineComment()) {
                    comment = parseLineComment(tokenizer, comment);
                } else if (token.startsBlockComment()) {
                    comment = parseBlockComment(tokenizer);
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
                        Token v = tokenizer.expectInteger("");
                        value = (int) v.parseInteger();
                        nextValue = value + 1;
                    }

                    evb.setValue(value);

                    // Enum value annotations.
                    if (tokenizer.peek("parsing enum " + id)
                                 .isSymbol(Token.kParamsStart)) {
                        tokenizer.next();
                        char sep2 = Token.kParamsStart;
                        while (sep2 != Token.kParamsEnd) {
                            token = tokenizer.expectQualifiedIdentifier("annotation name");
                            String name = token.asString();
                            tokenizer.expectSymbol("", Token.kFieldValueSep);
                            Token val = tokenizer.expectStringLiteral("annotation value");

                            evb.putInAnnotations(name, val.decodeStringLiteral());

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
                    throw new ParseException(tokenizer, token,
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
                token = tokenizer.expectQualifiedIdentifier("annotation name");
                String name = token.asString();
                tokenizer.expectSymbol("", Token.kFieldValueSep);
                Token val = tokenizer.expectStringLiteral("annotation value");

                etb.putInAnnotations(name, val.decodeStringLiteral());

                sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
            }
        }

        return etb.build();
    }

    private MessageType parseMessage(Tokenizer tokenizer, String type, String comment)
            throws IOException, ParseException {
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
            throw new ParseException(tokenizer, id,
                                     "Invalid type identifier " + id.asString());
        }
        struct.setName(id.asString());

        // Unsigned short max value.
        int nextDefaultKey = (1 << 16) - 1;

        tokenizer.expectSymbol("struct start", Token.kMessageStart);

        Set<String> fieldNames = new HashSet<>();
        Set<String> fieldNameVariants = new HashSet<>();
        Set<Integer> fieldIds = new HashSet<>();

        while (true) {
            Token token = tokenizer.expect("field def");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            FieldType._Builder field = FieldType.builder();
            field.setDocumentation(comment);
            comment = null;

            if (token.isInteger()) {
                int fId = (int) token.parseInteger();
                if (fId < 1) {
                    throw new ParseException(tokenizer, token,
                                             "Negative field id " + fId + " not allowed.");
                }
                if (fieldIds.contains(fId)) {
                    throw new ParseException(tokenizer, token,
                                             "Field id " + fId + " already exists in struct " + struct.build().getName());
                }
                fieldIds.add(fId);
                field.setKey(fId);

                tokenizer.expectSymbol("field id sep", Token.kFieldIdSep);
                token = tokenizer.expect("field requirement or type");
            } else {
                // TODO(steineldar): Maybe disallow for consistency?
                field.setKey(nextDefaultKey--);
            }

            if (token.strEquals(Token.kRequired)) {
                if (union) {
                    throw new ParseException(tokenizer, token,
                                             "Found required field in union");
                }
                field.setRequirement(FieldRequirement.REQUIRED);
                token = tokenizer.expect("field type");
            } else if (token.strEquals(Token.kOptional)) {
                if (!union) {
                    // All union fields are optional regardless.
                    field.setRequirement(FieldRequirement.OPTIONAL);
                }
                token = tokenizer.expect("field type");
            }

            // Get type.... This is mandatory.
            field.setType(parseType(tokenizer, token));

            Token name = tokenizer.expectIdentifier("parsing struct " + id.asString());
            String fName = name.asString();
            if (fieldNames.contains(fName)) {
                throw new ParseException(tokenizer, name,
                                         "Field %s already exists in struct %s",
                                         fName,
                                         struct.build().getName());
            }
            if (fieldNameVariants.contains(Strings.camelCase("get", fName))) {
                throw new ParseException(tokenizer, name,
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
                    token = tokenizer.expectQualifiedIdentifier("annotation name");
                    String aName = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token val = tokenizer.expectStringLiteral("annotation value");

                    field.putInAnnotations(aName, val.decodeStringLiteral());

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
                token = tokenizer.expectQualifiedIdentifier("annotation name");
                String name = token.asString();
                tokenizer.expectSymbol("", Token.kFieldValueSep);
                Token val = tokenizer.expectStringLiteral("annotation value");

                struct.putInAnnotations(name, val.decodeStringLiteral());

                sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
            }
        }

        return struct.build();
    }

    private String parseType(Tokenizer tokenizer, Token token) throws IOException, ParseException {
        if (!token.isQualifiedIdentifier()) {
            throw new ParseException(tokenizer, token, "Expected type identifier but found " + token);
        }

        String type = token.asString();
        switch (type) {
            case "list":
            case "set": {
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericStart);
                String item = parseType(tokenizer, tokenizer.expectQualifiedIdentifier("parsing " + type + " item type"));
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericEnd);

                return String.format("%s<%s>", type, item);
            }
            case "map": {
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericStart);
                String key = parseType(tokenizer, tokenizer.expectQualifiedIdentifier("parsing " + type + " key type"));
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kLineSep1);
                String item = parseType(tokenizer, tokenizer.expectQualifiedIdentifier("parsing " + type + " item type"));
                tokenizer.expectSymbol("parsing " + type + " item type", Token.kGenericEnd);

                return String.format("%s<%s,%s>", type, key, item);
            }
            default:
                return type;
        }
    }
}
