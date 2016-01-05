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
import net.morimekta.providence.model.*;
import net.morimekta.providence.reflect.parser.internal.Keyword;
import net.morimekta.providence.reflect.parser.internal.Symbol;
import net.morimekta.providence.reflect.parser.internal.Token;
import net.morimekta.providence.reflect.parser.internal.Tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ThriftParser implements Parser {
    private final static Pattern RE_BLOCK_LINE = Pattern.compile("^([\\s]*[*])?[\\s]?");

    @Override
    public ThriftDocument parse(InputStream in, String name) throws IOException, ParseException {
        ThriftDocument._Builder doc = ThriftDocument.builder();

        doc.setPackage(name.replaceAll(".*/", "").replace(".thrift", ""));
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

            Keyword keyword = Keyword.getByToken(token.getToken());
            if (keyword == null) {
                throw new ParseException("Unexpected token \"" + token.getToken() + "\"",
                                          tokenizer, token);
            }
            switch (keyword) {
                case NAMESPACE:
                    if (hasDeclaration) {
                        throw new ParseException("Unexpected token 'namespace', expected type declaration",
                                                  tokenizer, token);
                    }
                    if (comment != null && !hasHeader) {
                        doc.setComment(comment);
                    }
                    comment = null;
                    hasHeader = true;
                    parseNamespace(tokenizer, namespaces);
                    break;
                case INCLUDE:
                    if (hasDeclaration) {
                        throw new ParseException("Unexpected token 'include', expected type declaration",
                                                  tokenizer, token);
                    }
                    if (comment != null && !hasHeader) {
                        doc.setComment(comment);
                    }
                    comment = null;
                    hasHeader = true;
                    parseIncludes(tokenizer, includes);
                    break;
                case TYPEDEF:
                    hasHeader = true;
                    hasDeclaration = true;
                    parseTypedef(tokenizer, comment, declarations);
                    comment = null;
                    break;
                case ENUM:
                    hasHeader = true;
                    hasDeclaration = true;
                    EnumType et = parseEnum(tokenizer, comment);
                    declarations.add(Declaration.builder().setDeclEnum(et).build());
                    comment = null;
                    break;
                case STRUCT:
                case UNION:
                case EXCEPTION:
                    hasHeader = true;
                    hasDeclaration = true;
                    StructType st = parseStruct(tokenizer, token.getToken(), comment);
                    declarations.add(Declaration.builder().setDeclStruct(st).build());
                    comment = null;
                    break;
                case SERVICE:
                    hasHeader = true;
                    hasDeclaration = true;
                    ServiceType srv = parseService(tokenizer, comment);
                    declarations.add(Declaration.builder().setDeclService(srv).build());
                    comment = null;
                    break;
                case CONST:
                    hasHeader = true;
                    hasDeclaration = true;
                    ThriftField cnst = parseConst(tokenizer, comment);
                    declarations.add(Declaration.builder().setDeclConst(cnst).build());
                    comment = null;
                    break;
                default:
                    throw new ParseException("Unexpected token '" + token.getToken() + "', expected type declaration",
                                              tokenizer, token);
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

        tokenizer.expectSymbol(Symbol.MAP_ENTRY_VALUE_SEP, "parsing const identifier");

        String value = parseValue(tokenizer);

        Token sep = tokenizer.next();
        if (sep != null && sep.isSymbol()) {
            if (!sep.getSymbol().equals(Symbol.LIST_SEPARATOR) &&
                    !sep.getSymbol().equals(Symbol.ENTRY_SEPARATOR)) {
                tokenizer.unshift(sep);
            }
        } else {
            tokenizer.unshift(sep);
        }

        return ThriftField.builder()
                          .setComment(comment)
                          .setKey(-1)
                          .setName(id.getToken())
                          .setType(type)
                          .setDefaultValue(value)
                          .build();
    }

    private String parseValue(Tokenizer tokenizer) throws IOException, ParseException {
        Stack<Symbol> enclosures = new Stack<>();
        StringBuilder builder = new StringBuilder();
        while (true) {
            Token token = tokenizer.expect("Parsing const value.");

            if (token.startsBlockComment()) {
                parseBlockComment(tokenizer);  // ignore.
                continue;
            } else if (token.startsLineComment()) {
                parseLineComment(tokenizer, null);  // ignore
                continue;
            } else if (token.isSymbol()) {
                Symbol ct = token.getSymbol();
                if (ct.equals(Symbol.SHELL_COMMENT)) {
                    parseLineComment(tokenizer, null);  // ignore
                    continue;
                } else if (ct.equals(Symbol.MAP_START)) {
                    enclosures.push(Symbol.MAP_END);
                } else if (ct.equals(Symbol.LIST_START)) {
                    enclosures.push(Symbol.LIST_END);
                } else if (enclosures.size() > 0 && ct.equals(enclosures.peek())) {
                    enclosures.pop();
                }
            }

            builder.append(token.getToken());
            if (enclosures.isEmpty()) {
                return builder.toString();
            }
        }
    }

    private String parseLineComment(Tokenizer tokenizer, String comment) throws IOException {
        String line = tokenizer.readUntil("\n").trim();
        if (comment != null) {
             return comment + "\n" + line;
        }
        return line;
    }

    private String parseBlockComment(Tokenizer tokenizer) throws IOException {
        String block = tokenizer.readUntil(Keyword.BLOCK_COMMENT_END.keyword).trim();
        String[] lines = block.split("\n");
        StringBuilder builder = new StringBuilder();

        Pattern re = RE_BLOCK_LINE;
        for (String line : lines) {
            builder.append(re.matcher(line).replaceFirst(""));
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    private ServiceType parseService(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        ServiceType._Builder service = ServiceType.builder();

        if (comment != null) {
            service.setComment(comment);
            comment = null;
        }
        Token identifier = tokenizer.expectIdentifier("parsing service identifier");
        service.setName(identifier.getToken());

        tokenizer.expectSymbol(Symbol.MAP_START, "reading service start");

        Token token = tokenizer.expect("reading service method");
        while (true) {
            if (token.isSymbol() && token.getSymbol().equals(Symbol.MAP_END)) {
                break;
            }

            if (token.startsLineComment()) {
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

            if (token.getToken().equals(Keyword.ONEWAY.keyword)) {
                method.setIsOneway(true);
                token = tokenizer.expect("reading service method");
            }
            if (!token.isQualifiedIdentifier()) {
                throw new ParseException(token.getToken() + " is not a valid type identifier.", tokenizer, token);
            }
            if (!token.getToken().equals(Keyword.VOID.keyword)) {
                method.setReturnType(token.getToken());
            }

            token = tokenizer.expectIdentifier("reading method name");
            method.setName(token.getToken());

            tokenizer.expectSymbol(Symbol.PARAMS_BEGIN, "reading method params begin");

            token = tokenizer.expect("reading method params");
            while (true) {
                if (token.isSymbol() && token.getSymbol().equals(Symbol.PARAMS_END)) {
                    break;
                }

                if (token.startsLineComment()) {
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
                    field.setKey(token.intValue());
                    tokenizer.expectSymbol(Symbol.MAP_KEY_ENTRY_SEP, "reading method params (:)");
                    token = tokenizer.expect("reading method param type");
                }

                field.setType(parseType(tokenizer, token));

                token = tokenizer.expectIdentifier("reading method params");
                field.setName(token.getToken());

                method.addToParams(field.build());

                token = tokenizer.expect("reading method params");
                if (token.isSymbol()) {
                    if (token.getSymbol().equals(Symbol.LIST_SEPARATOR) ||
                            token.getSymbol().equals(Symbol.ENTRY_SEPARATOR)) {
                        token = tokenizer.expect("reading method params");
                    }
                }
            }

            token = tokenizer.expect("reading method params");
            if (token.isSymbol()) {
                if (token.getSymbol().equals(Symbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(Symbol.ENTRY_SEPARATOR)) {

                    service.addToMethods(method.build());

                    token = tokenizer.expect("reading method params");
                    continue;
                }
            }

            if (token.getToken().equals(Keyword.THROWS.keyword)) {
                tokenizer.expectSymbol(Symbol.PARAMS_BEGIN, "reading method exception begin");

                token = tokenizer.expect("reading method exception begin");
                while (true) {
                    if (token.isSymbol() && token.getSymbol().equals(Symbol.PARAMS_END)) {
                        break;
                    }

                    if (token.startsLineComment()) {
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
                        field.setKey(token.intValue());
                        tokenizer.expectSymbol(Symbol.MAP_KEY_ENTRY_SEP, "reading method exception (:)");
                        token = tokenizer.expect("reading method exception type");
                    }

                    if (!token.isIdentifier()) {
                        throw new ParseException("Expected exception type identifier.", tokenizer, token);
                    }
                    field.setType(token.getToken());

                    token = tokenizer.expectIdentifier("reading method exception");
                    field.setName(token.getToken());

                    method.addToExceptions(field.build());

                    token = tokenizer.expect("reading method exception");
                    if (token.isSymbol()) {
                        if (token.getSymbol().equals(Symbol.LIST_SEPARATOR) ||
                            token.getSymbol().equals(Symbol.ENTRY_SEPARATOR)) {
                            token = tokenizer.expect("reading method exception");
                        }
                    }
                }
            }

            service.addToMethods(method.build());

            token = tokenizer.expect("reading service method");
            if (token.isSymbol()) {
                if (token.getSymbol().equals(Symbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(Symbol.ENTRY_SEPARATOR)) {
                    token = tokenizer.expect("reading service method");
                }
            }
        }

        return service.build();
    }

    public void parseNamespace(Tokenizer tokenizer, Map<String,String> namespaces) throws IOException, ParseException {
        Token language = tokenizer.expectQualifiedIdentifier("parsing namespace language");
        Token namespace = tokenizer.expectQualifiedIdentifier("parsing namespace");

        if (!language.isIdentifier()) {
            throw new ParseException("Namespace language not valid identifier: '" + language.getToken() + "'");
        }
        if (!namespace.isQualifiedIdentifier()) {
            throw new ParseException("Namespace not valid: '" + namespace.getToken() + "'");
        }

        namespaces.put(language.getToken(), namespace.getToken());
    }

    public void parseIncludes(Tokenizer tokenizer, List<String> includes) throws IOException, ParseException {
        Token include = tokenizer.next();
        if (include == null) {
            throw new ParseException("Unecpected end of file.");
        }
        if (!include.isLiteral()) {
            throw new ParseException("Expected string literal for include",
                    tokenizer, include);
        }
        includes.add(include.literalValue());
    }

    private void parseTypedef(Tokenizer tokenizer, String comment, List<Declaration> declarations)
            throws IOException, ParseException {
        Token token = tokenizer.expect("parsing typedef type.");
        String type = parseType(tokenizer, token);
        Token id = tokenizer.expectIdentifier("parsing typedef identifier.");

        TypedefType typedef = TypedefType.builder()
                                         .setComment(comment)
                                         .setType(type)
                                         .setName(id.getToken())
                                         .build();
        declarations.add(Declaration.builder()
                                    .setDeclTypedef(typedef)
                                    .build());
    }

    public EnumType parseEnum(Tokenizer tokenizer, String comment) throws IOException, ParseException {
        Token id = tokenizer.expectIdentifier("parsing enum identifier");

        EnumType._Builder et = EnumType.builder();
        if (comment != null) {
            et.setComment(comment);
            comment = null;
        }
        et.setName(id.getToken());

        int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;
        String nextName = null;
        while (true) {
            Token token = tokenizer.expect("parsing enum " + id.getToken());
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            if (token.isSymbol()) {
                if (token.getSymbol().equals(Symbol.MAP_END)) {
                    if (nextName != null) {
                        et.addToValues(EnumValue.builder()
                                                .setComment(comment)
                                                .setName(nextName)
                                                .setValue(nextValue)
                                                .build());
                    }
                    break;
                } else if (token.getSymbol().equals(Symbol.ENTRY_SEPARATOR) ||
                           token.getSymbol().equals(Symbol.LIST_SEPARATOR)) {
                    // [;,]
                    if (nextName == null) throw new ParseException("Unexpected entry separator: '" + token.getToken() + "'",
                                                                    tokenizer, token);

                    et.addToValues(EnumValue.builder()
                                            .setComment(comment)
                                            .setName(nextName)
                                            .setValue(nextValue)
                                            .build());
                    comment = null;
                    nextName = null;
                    ++nextValue;
                } else if (token.getSymbol().equals(Symbol.MAP_ENTRY_VALUE_SEP)) {
                    // [=]
                    Token value = tokenizer.next();
                    if (!value.isInteger()) {
                        throw new ParseException("Expected numeric enum value, got " + value.getToken(),
                                                  tokenizer, value);
                    }
                    nextValue = value.intValue();
                }
            } else if (token.isIdentifier()) {
                if (nextName != null) {
                    et.addToValues(EnumValue.builder()
                                            .setComment(comment)
                                            .setName(nextName)
                                            .setValue(nextValue)
                                            .build());
                    comment = null;
                    ++nextValue;
                }
                nextName = token.getToken();
            } else {
                throw new ParseException("Unexpected token in enum: " + token.getToken(),
                                          tokenizer, token);
            }
        }

        return et.build();
    }

    private StructType parseStruct(Tokenizer tokenizer, String type, String comment) throws IOException, ParseException {
        StructType._Builder struct = StructType.builder();
        if (comment != null) {
            struct.setComment(comment);
            comment = null;
        }
        if (!type.equals("struct")) {
            struct.setVariant(StructVariant.valueOf(type.toUpperCase()));
        }

        Token id = tokenizer.expectIdentifier("parsing " + type + " identifier");
        if (!id.isIdentifier()) {
            throw new ParseException("Struct name " + id.getToken() + " is not valid identifier",
                                      tokenizer, id);
        }
        struct.setName(id.getToken());

        int nextDefaultKey = (1 << 16) - 1;

        tokenizer.expectSymbol(Symbol.MAP_START, "parsing struct " + id.getToken());

        ThriftField._Builder field = ThriftField.builder();
        while (true) {
            Token token = tokenizer.expect("parsing struct " + id.getToken());
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            } else if (token.isSymbol() && token.getSymbol().equals(Symbol.MAP_END)) {
                // good end of definition.
                break;
            }

            field.setComment(comment);
            comment = null;

            if (token.isInteger()) {
                field.setKey(token.intValue());
                tokenizer.expectSymbol(Symbol.MAP_KEY_ENTRY_SEP, "parsing struct " + id.getToken());
                token = tokenizer.expect("parsing struct " + id.getToken());
            } else {
                // TODO(steineldar): Maybe disallow for consistency?
                field.setKey(nextDefaultKey--);
            }

            if (token.getToken().equals(Keyword.REQUIRED.keyword)) {
                field.setRequirement(Requirement.REQUIRED);
                token = tokenizer.expect("parsing struct " + id.getToken());
            } else if (token.getToken().equals(Keyword.OPTIONAL.keyword)) {
                field.setRequirement(Requirement.OPTIONAL);
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            // Get type.... This is mandatory.
            field.setType(parseType(tokenizer, token));

            token = tokenizer.expect("parsing struct " + id.getToken());
            // get name... This is mandatory.
            if (!token.isIdentifier()) {
                throw new ParseException("Expected name identifier, but found " + token.getToken(),
                                          tokenizer, token);
            }
            field.setName(token.getToken());

            token = tokenizer.expect("parsing struct " + id.getToken());
            if (token.isSymbol() && token.getSymbol().equals(Symbol.MAP_ENTRY_VALUE_SEP)) {
                field.setDefaultValue(parseValue(tokenizer));
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            if (token.isSymbol() && (
                    token.getSymbol().equals(Symbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(Symbol.ENTRY_SEPARATOR))) {
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            struct.addToFields(field.build());
            field = ThriftField.builder();

            if (token.isSymbol() && token.getSymbol().equals(Symbol.MAP_END)) {
                // end of definition.
                break;
            }

            // new entry or comment.
            tokenizer.unshift(token);
        }

        return struct.build();
    }

    public String parseType(Tokenizer tokenizer, Token token) throws IOException, ParseException {
        if (!token.isQualifiedIdentifier()) {
            throw new ParseException("Expected type identifier but found " + token, tokenizer, token);
        }

        String type = token.getToken();
        Keyword kw = Keyword.getByToken(type);
        if (kw == null) return type;

        if (kw.equals(Keyword.LIST) || kw.equals(Keyword.SET)) {
            tokenizer.expectSymbol(Symbol.GENERIC_START, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String itemType = parseType(tokenizer, token);

            tokenizer.expectSymbol(Symbol.GENERIC_END, "parsing " + kw + " type");

            return String.format("%s<%s>", type, itemType);
        } else if (kw.equals(Keyword.MAP)) {
            tokenizer.expectSymbol(Symbol.GENERIC_START, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String keyType = parseType(tokenizer, token);

            tokenizer.expectSymbol(Symbol.LIST_SEPARATOR, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String itemType = parseType(tokenizer, token);

            tokenizer.expectSymbol(Symbol.GENERIC_END, "parsing " + kw + " type");

            return String.format("%s<%s,%s>", type, keyType, itemType);
        }

        return type;
    }
}
