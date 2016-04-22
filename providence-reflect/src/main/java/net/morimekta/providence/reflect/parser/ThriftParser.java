/*
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
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.Model_Constants;
import net.morimekta.providence.model.Requirement;
import net.morimekta.providence.model.ServiceMethod;
import net.morimekta.providence.model.ServiceType;
import net.morimekta.providence.model.StructType;
import net.morimekta.providence.model.StructVariant;
import net.morimekta.providence.model.ThriftDocument;
import net.morimekta.providence.model.ThriftField;
import net.morimekta.providence.model.TypedefType;
import net.morimekta.providence.reflect.parser.internal.Token;
import net.morimekta.providence.reflect.parser.internal.Tokenizer;
import net.morimekta.util.Strings;

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

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ThriftParser implements Parser {
    private final static Pattern RE_BLOCK_LINE   = Pattern.compile("^([\\s]*[*])?[\\s]?");
    private static final Pattern VALID_PACKAGE   = Pattern.compile(
            "[-._a-zA-Z0-9]+");
    private static final Pattern VALID_NAMESPACE = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    private static final Pattern VALID_SDI_NAMESPACE = Pattern.compile(
            "([_a-zA-Z][-_a-zA-Z0-9]*[.])*[_a-zA-Z][-_a-zA-Z0-9]*");

    @Override
    public ThriftDocument parse(InputStream in, String name) throws IOException, ParseException {
        ThriftDocument._Builder doc = ThriftDocument.builder();

        String packageName = name.replaceAll(".*/", "")
                           .replace(".thrift", "");
        if (!VALID_PACKAGE.matcher(packageName).matches()) {
            throw new ParseException("Package name %s derived from filename %s is not valid.",
                                     packageName, name);
        }
        doc.setPackage(name.replaceAll(".*/", "")
                           .replace(".thrift", ""));
        List<String> includes = new LinkedList<>();
        Map<String, String> namespaces = new LinkedHashMap<>();

        List<Declaration> declarations = new LinkedList<>();

        Tokenizer tokenizer = new Tokenizer(in);

        boolean hasHeader = false;
        boolean hasDeclaration = false;

        String comment = null;
        Token token;
        while ((token = tokenizer.next()) != null) {
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
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
                    if (comment != null && !hasHeader) {
                        doc.setComment(comment);
                    }
                    comment = null;
                    hasHeader = true;
                    parseNamespace(tokenizer, namespaces);
                    break;
                case "include":
                    if (hasDeclaration) {
                        throw new ParseException(tokenizer, token,
                                                 "Unexpected token 'include', expected type declaration");
                    }
                    if (comment != null && !hasHeader) {
                        doc.setComment(comment);
                    }
                    comment = null;
                    hasHeader = true;
                    parseIncludes(tokenizer, includes);
                    break;
                case "typedef":
                    hasHeader = true;
                    hasDeclaration = true;
                    parseTypedef(tokenizer, comment, declarations);
                    comment = null;
                    break;
                case "enum":
                    hasHeader = true;
                    hasDeclaration = true;
                    EnumType et = parseEnum(tokenizer, comment);
                    declarations.add(Declaration.builder()
                                                .setDeclEnum(et)
                                                .build());
                    comment = null;
                    break;
                case "struct":
                case "union":
                case "exception":
                    hasHeader = true;
                    hasDeclaration = true;
                    StructType st = parseStruct(tokenizer, token.asString(), comment);
                    declarations.add(Declaration.builder()
                                                .setDeclStruct(st)
                                                .build());
                    comment = null;
                    break;
                case "service":
                    hasHeader = true;
                    hasDeclaration = true;
                    ServiceType srv = parseService(tokenizer, comment);
                    declarations.add(Declaration.builder()
                                                .setDeclService(srv)
                                                .build());
                    comment = null;
                    break;
                case "const":
                    hasHeader = true;
                    hasDeclaration = true;
                    ThriftField cnst = parseConst(tokenizer, comment);
                    declarations.add(Declaration.builder()
                                                .setDeclConst(cnst)
                                                .build());
                    comment = null;
                    break;
                default:
                    throw new ParseException(tokenizer, token,
                                             "Unexpected token '%s', expected type declaration",
                                             token.asString());
            }
        }

        doc.setNamespaces(namespaces);
        doc.setIncludes(includes);
        doc.setDecl(declarations);

        return doc.build();
    }

    private ThriftField parseConst(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        Token token = tokenizer.expectQualifiedIdentifier("parsing const type");
        String type = parseType(tokenizer, token);
        Token id = tokenizer.expectIdentifier("parsing const identifier");

        tokenizer.expectSymbol("parsing const identifier", Token.kFieldValueSep);

        String value = parseValue(tokenizer);

        Token sep = tokenizer.peek();
        if (sep != null && (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2))) {
            tokenizer.next();
        }

        return ThriftField.builder()
                          .setComment(comment)
                          .setKey(-1)
                          .setName(id.asString())
                          .setType(type)
                          .setDefaultValue(value)
                          .build();
    }

    private String parseValue(Tokenizer tokenizer) throws IOException, ParseException {
        Stack<Character> enclosures = new Stack<>();
        StringBuilder builder = new StringBuilder();
        while (true) {
            Token token = tokenizer.expect("Parsing const value.");

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
        String line = Strings.readString(tokenizer, "\n").trim();
        if (comment != null) {
            return comment + "\n" + line;
        }
        return line;
    }

    private String parseBlockComment(Tokenizer tokenizer) throws IOException {
        String block = Strings.readString(tokenizer, new String(Token.kBlockCommentEnd)).trim();
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
            service.setComment(comment);
            comment = null;
        }
        Token identifier = tokenizer.expectIdentifier("parsing service identifier");
        service.setName(identifier.asString());

        if (tokenizer.peek().strEquals(Token.kExtends)) {
            tokenizer.next();
            service.setExtend(tokenizer.expectQualifiedIdentifier("service extending identifier").asString());
        }

        tokenizer.expectSymbol("reading service start", Token.kMessageStart);

        Set<String> methodNames = new TreeSet<>();

        while (true) {
            Token token = tokenizer.expect("reading service method");
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            ServiceMethod._Builder method = ServiceMethod.builder();
            if (comment != null) {
                method.setComment(comment);
                comment = null;
            }

            if (token.strEquals(Token.kOneway)) {
                method.setOneWay(true);
                token = tokenizer.expect("reading service method");
            }

            if (!token.strEquals(Token.kVoid)) {
                method.setReturnType(parseType(tokenizer, token));
            }

            method.setName(tokenizer.expectIdentifier("reading method name").asString());

            tokenizer.expectSymbol("reading method params begin", Token.kParamsStart);
            while (true) {
                token = tokenizer.expect("reading method params");
                if (token.isSymbol(Token.kParamsEnd)) {
                    break;
                } else if (token.startsLineComment()) {
                    comment = parseLineComment(tokenizer, comment);
                    continue;
                } else if (token.startsBlockComment()) {
                    comment = parseBlockComment(tokenizer);
                    continue;
                }

                ThriftField._Builder field = ThriftField.builder();
                if (comment != null) {
                    field.setComment(comment);
                    comment = null;
                }

                if (token.isInteger()) {
                    field.setKey((int) token.parseInteger());
                    tokenizer.expectSymbol("reading method params (:)", Token.kFieldIdSep);
                    token = tokenizer.expect("reading method param type");
                }

                field.setType(parseType(tokenizer, token));
                field.setName(tokenizer.expectIdentifier("reading method param name")
                                       .asString());

                // Annotations.
                if (tokenizer.peek("reading method param annotation")
                             .isSymbol(Token.kParamsStart)) {
                    tokenizer.next();
                    char sep = Token.kParamsStart;
                    while (sep != Token.kParamsEnd) {
                        token = tokenizer.expectQualifiedIdentifier("annotation name");
                        String name = token.asString();
                        tokenizer.expectSymbol("", Token.kFieldValueSep);
                        Token value = tokenizer.expectStringLiteral("annotation value");

                        field.putInAnnotations(name, value.decodeLiteral());

                        sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
                    }
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

                    ThriftField._Builder field = ThriftField.builder();
                    if (comment != null) {
                        field.setComment(comment);
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
                            String name = token.asString();
                            tokenizer.expectSymbol("", Token.kFieldValueSep);
                            Token value = tokenizer.expectStringLiteral("exception annotation value");

                            field.putInAnnotations(name, value.decodeLiteral());

                            sep = tokenizer.expectSymbol("exception annotation sep",
                                                         Token.kParamsEnd,
                                                         Token.kLineSep1,
                                                         Token.kLineSep2);
                        }
                    }

                    method.addToExceptions(field.build());

                    token = tokenizer.peek("reading method params");
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
                    String name = token.asString();
                    tokenizer.expectSymbol("", Token.kFieldValueSep);
                    Token value = tokenizer.expectStringLiteral("annotation value");

                    method.putInAnnotations(name, value.decodeLiteral());

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

                    service.putInAnnotations(name, value.decodeLiteral());

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
        includes.add(include.substring(1, -1).asString());
    }

    private void parseTypedef(Tokenizer tokenizer, String comment, List<Declaration> declarations)
            throws IOException, ParseException {
        String type = parseType(tokenizer, tokenizer.expect("parsing typedef type."));
        Token id = tokenizer.expectIdentifier("parsing typedef identifier.");

        TypedefType typedef = TypedefType.builder()
                                         .setComment(comment)
                                         .setType(type)
                                         .setName(id.asString())
                                         .build();
        declarations.add(Declaration.withDeclTypedef(typedef));
    }

    public EnumType parseEnum(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        String id = tokenizer.expectIdentifier("parsing enum identifier").asString();

        EnumType._Builder etb = EnumType.builder();
        if (comment != null) {
            etb.setComment(comment);
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

                            evb.putInAnnotations(name, val.decodeLiteral());

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

                etb.putInAnnotations(name, val.decodeLiteral());

                sep = tokenizer.expectSymbol("annotation sep", Token.kParamsEnd, Token.kLineSep1, Token.kLineSep2);
            }
        }

        return etb.build();
    }

    private StructType parseStruct(Tokenizer tokenizer, String type, String comment)
            throws IOException, ParseException {
        StructType._Builder struct = StructType.builder();
        if (comment != null) {
            struct.setComment(comment);
            comment = null;
        }
        boolean union = type.equals("union");
        if (!type.equals("struct")) {
            struct.setVariant(StructVariant.forName(type.toUpperCase()));
        }

        Token id = tokenizer.expectIdentifier("parsing " + type + " identifier");
        if (!id.isIdentifier()) {
            throw new ParseException("Struct name " + id.asString() + " is not valid identifier", tokenizer, id);
        }
        struct.setName(id.asString());

        // Unsigned short max value.
        int nextDefaultKey = (1 << 16) - 1;

        tokenizer.expectSymbol("parsing struct " + id.asString(), Token.kMessageStart);

        Set<String> fieldNames = new HashSet<>();
        Set<String> fieldNameVariants = new HashSet<>();
        Set<Integer> fieldIds = new HashSet<>();

        while (true) {
            Token token = tokenizer.expect("parsing struct " + id.asString());
            if (token.isSymbol(Token.kMessageEnd)) {
                break;
            } else if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            ThriftField._Builder field = ThriftField.builder();
            field.setComment(comment);
            comment = null;

            if (token.isInteger()) {
                int fId = (int) token.parseInteger();
                if (fId < 1) {
                    throw new ParseException("Negative field id " + fId + " not allowed.",
                                             token, tokenizer);
                }
                if (fieldIds.contains(fId)) {
                    throw new ParseException("Field id " + fId + " already exists in struct " + struct.build().getName(),
                                             token, tokenizer);
                }
                fieldIds.add(fId);
                field.setKey(fId);

                tokenizer.expectSymbol("parsing struct " + id.asString(), Token.kFieldIdSep);
                token = tokenizer.expect("parsing struct " + id.asString());
            } else {
                // TODO(steineldar): Maybe disallow for consistency?
                field.setKey(nextDefaultKey--);
            }

            if (token.strEquals(Token.kRequired)) {
                if (union) {
                    throw new ParseException("Found required field in union. Not allowed. " + token.asString(),
                                             tokenizer,
                                             token);
                }
                field.setRequirement(Requirement.REQUIRED);
                token = tokenizer.expect("parsing struct " + id.asString());
            } else if (token.strEquals(Token.kOptional)) {
                if (!union) {
                    // All union fields are optional regardless.
                    field.setRequirement(Requirement.OPTIONAL);
                }
                token = tokenizer.expect("parsing struct " + id.asString());
            }

            // Get type.... This is mandatory.
            field.setType(parseType(tokenizer, token));

            Token name = tokenizer.expectIdentifier("parsing struct " + id.asString());
            String fName = name.asString();
            if (fieldNames.contains(name)) {
                throw new ParseException("Field name " + fName + " already exists in struct " + struct.build().getName(),
                                         token, tokenizer);
            }
            if (fieldNameVariants.contains(Strings.camelCase("get", fName))) {
                throw new ParseException("Field name " + fName + " conflicts with existing field in struct " + struct.build().getName(),
                                         token, tokenizer);
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

                    field.putInAnnotations(aName, val.decodeLiteral());

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

                struct.putInAnnotations(name, val.decodeLiteral());

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
