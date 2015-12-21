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

package org.apache.thrift.j2.reflect.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.model.Declaration;
import org.apache.thrift.j2.model.EnumType;
import org.apache.thrift.j2.model.EnumValue;
import org.apache.thrift.j2.model.ServiceMethod;
import org.apache.thrift.j2.model.ServiceType;
import org.apache.thrift.j2.model.StructType;
import org.apache.thrift.j2.model.StructVariant;
import org.apache.thrift.j2.model.ThriftDocument;
import org.apache.thrift.j2.model.ThriftField;
import org.apache.thrift.j2.model.TypedefType;
import org.apache.thrift.j2.reflect.parser.internal.TKeyword;
import org.apache.thrift.j2.reflect.parser.internal.TSymbol;
import org.apache.thrift.j2.reflect.parser.internal.TToken;
import org.apache.thrift.j2.reflect.parser.internal.TTokenizer;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TThriftParser implements TParser {
    private final static Pattern RE_BLOCK_LINE = Pattern.compile("^([\\s]*[*])?[\\s]?");

    @Override
    public ThriftDocument parse(InputStream in, String name) throws IOException, TParseException {
        ThriftDocument._Builder doc = ThriftDocument.builder();

        doc.setPackage(name.replaceAll(".*/", "").replace(".thrift", ""));
        List<String> includes = new LinkedList<>();
        Map<String, String> namespaces = new LinkedHashMap<>();

        List<Declaration> declarations = new LinkedList<>();

        TTokenizer tokenizer = new TTokenizer(in);

        boolean hasHeader = false;
        boolean hasDeclaration = false;

        String comment = null;
        TToken token;
        while ((token = tokenizer.next()) != null) {
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            TKeyword keyword = TKeyword.getByToken(token.getToken());
            if (keyword == null) {
                throw new TParseException("Unexpected token \"" + token.getToken() + "\"",
                                          tokenizer, token);
            }
            switch (keyword) {
                case NAMESPACE:
                    if (hasDeclaration) {
                        throw new TParseException("Unexpected token 'namespace', expected type declaration",
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
                        throw new TParseException("Unexpected token 'include', expected type declaration",
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
                    throw new TParseException("Unexpected token '" + token.getToken() + "', expected type declaration",
                                              tokenizer, token);
            }
        }

        doc.setNamespaces(namespaces);
        doc.setIncludes(includes);
        doc.setDecl(declarations);

        return doc.build();
    }

    private ThriftField parseConst(TTokenizer tokenizer, String comment) throws IOException, TParseException {
        TToken token = tokenizer.expectQualifiedIdentifier("parsing const type");
        String type = parseType(tokenizer, token);
        TToken id = tokenizer.expectIdentifier("parsing const identifier");

        tokenizer.expectSymbol(TSymbol.MAP_ENTRY_VALUE_SEP, "parsing const identifier");

        String value = parseValue(tokenizer);

        TToken sep = tokenizer.next();
        if (sep != null && sep.isSymbol()) {
            if (!sep.getSymbol().equals(TSymbol.LIST_SEPARATOR) &&
                    !sep.getSymbol().equals(TSymbol.ENTRY_SEPARATOR)) {
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

    private String parseValue(TTokenizer tokenizer) throws IOException, TParseException {
        Stack<TSymbol> enclosures = new Stack<>();
        StringBuilder builder = new StringBuilder();
        while (true) {
            TToken token = tokenizer.expect("Parsing const value.");

            if (token.startsBlockComment()) {
                parseBlockComment(tokenizer);  // ignore.
                continue;
            } else if (token.startsLineComment()) {
                parseLineComment(tokenizer, null);  // ignore
                continue;
            } else if (token.isSymbol()) {
                TSymbol ct = token.getSymbol();
                if (ct.equals(TSymbol.SHELL_COMMENT)) {
                    parseLineComment(tokenizer, null);  // ignore
                    continue;
                } else if (ct.equals(TSymbol.MAP_START)) {
                    enclosures.push(TSymbol.MAP_END);
                } else if (ct.equals(TSymbol.LIST_START)) {
                    enclosures.push(TSymbol.LIST_END);
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

    private String parseLineComment(TTokenizer tokenizer, String comment) throws IOException {
        String line = tokenizer.readUntil("\n").trim();
        if (comment != null) {
             return comment + "\n" + line;
        }
        return line;
    }

    private String parseBlockComment(TTokenizer tokenizer) throws IOException {
        String block = tokenizer.readUntil(TKeyword.BLOCK_COMMENT_END.keyword).trim();
        String[] lines = block.split("\n");
        StringBuilder builder = new StringBuilder();

        Pattern re = RE_BLOCK_LINE;
        for (String line : lines) {
            builder.append(re.matcher(line).replaceFirst(""));
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    private ServiceType parseService(TTokenizer tokenizer, String comment) throws IOException, TParseException {
        ServiceType._Builder service = ServiceType.builder();

        if (comment != null) {
            service.setComment(comment);
            comment = null;
        }
        TToken identifier = tokenizer.expectIdentifier("parsing service identifier");
        service.setName(identifier.getToken());

        tokenizer.expectSymbol(TSymbol.MAP_START, "reading service start");

        TToken token = tokenizer.expect("reading service method");
        while (true) {
            if (token.isSymbol() && token.getSymbol().equals(TSymbol.MAP_END)) {
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

            if (token.getToken().equals(TKeyword.ONEWAY.keyword)) {
                method.setIsOneway(true);
                token = tokenizer.expect("reading service method");
            }
            if (!token.isQualifiedIdentifier()) {
                throw new TParseException(token.getToken() + " is not a valid type identifier.", tokenizer, token);
            }
            if (!token.getToken().equals(TKeyword.VOID.keyword)) {
                method.setReturnType(token.getToken());
            }

            token = tokenizer.expectIdentifier("reading method name");
            method.setName(token.getToken());

            tokenizer.expectSymbol(TSymbol.PARAMS_BEGIN, "reading method params begin");

            token = tokenizer.expect("reading method params");
            while (true) {
                if (token.isSymbol() && token.getSymbol().equals(TSymbol.PARAMS_END)) {
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
                    tokenizer.expectSymbol(TSymbol.MAP_KEY_ENTRY_SEP, "reading method params (:)");
                    token = tokenizer.expect("reading method param type");
                }

                field.setType(parseType(tokenizer, token));

                token = tokenizer.expectIdentifier("reading method params");
                field.setName(token.getToken());

                method.addToParams(field.build());

                token = tokenizer.expect("reading method params");
                if (token.isSymbol()) {
                    if (token.getSymbol().equals(TSymbol.LIST_SEPARATOR) ||
                            token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR)) {
                        token = tokenizer.expect("reading method params");
                    }
                }
            }

            token = tokenizer.expect("reading method params");
            if (token.isSymbol()) {
                if (token.getSymbol().equals(TSymbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR)) {

                    service.addToMethods(method.build());

                    token = tokenizer.expect("reading method params");
                    continue;
                }
            }

            if (token.getToken().equals(TKeyword.THROWS.keyword)) {
                tokenizer.expectSymbol(TSymbol.PARAMS_BEGIN, "reading method exception begin");

                token = tokenizer.expect("reading method exception begin");
                while (true) {
                    if (token.isSymbol() && token.getSymbol().equals(TSymbol.PARAMS_END)) {
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
                        tokenizer.expectSymbol(TSymbol.MAP_KEY_ENTRY_SEP, "reading method exception (:)");
                        token = tokenizer.expect("reading method exception type");
                    }

                    if (!token.isIdentifier()) {
                        throw new TParseException("Expected exception type identifier.", tokenizer, token);
                    }
                    field.setType(token.getToken());

                    token = tokenizer.expectIdentifier("reading method exception");
                    field.setName(token.getToken());

                    method.addToExceptions(field.build());

                    token = tokenizer.expect("reading method exception");
                    if (token.isSymbol()) {
                        if (token.getSymbol().equals(TSymbol.LIST_SEPARATOR) ||
                            token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR)) {
                            token = tokenizer.expect("reading method exception");
                        }
                    }
                }
            }

            service.addToMethods(method.build());

            token = tokenizer.expect("reading service method");
            if (token.isSymbol()) {
                if (token.getSymbol().equals(TSymbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR)) {
                    token = tokenizer.expect("reading service method");
                }
            }
        }

        return service.build();
    }

    public void parseNamespace(TTokenizer tokenizer, Map<String,String> namespaces) throws IOException, TParseException {
        TToken language = tokenizer.expectQualifiedIdentifier("parsing namespace language");
        TToken namespace = tokenizer.expectQualifiedIdentifier("parsing namespace");

        if (!language.isIdentifier()) {
            throw new TParseException("Namespace language not valid identifier: '" + language.getToken() + "'");
        }
        if (!namespace.isQualifiedIdentifier()) {
            throw new TParseException("Namespace not valid: '" + namespace.getToken() + "'");
        }

        namespaces.put(language.getToken(), namespace.getToken());
    }

    public void parseIncludes(TTokenizer tokenizer, List<String> includes) throws IOException, TParseException {
        TToken include = tokenizer.next();
        if (include == null) {
            throw new TParseException("Unecpected end of file.");
        }
        if (!include.isLiteral()) {
            throw new TParseException("Expected string literal for include",
                    tokenizer, include);
        }
        includes.add(include.literalValue());
    }

    private void parseTypedef(TTokenizer tokenizer, String comment, List<Declaration> declarations)
            throws IOException, TParseException {
        TToken token = tokenizer.expect("parsing typedef type.");
        String type = parseType(tokenizer, token);
        TToken id = tokenizer.expectIdentifier("parsing typedef identifier.");

        TypedefType typedef = TypedefType.builder()
                                         .setComment(comment)
                                         .setType(type)
                                         .setName(id.getToken())
                                         .build();
        declarations.add(Declaration.builder()
                                    .setDeclTypedef(typedef)
                                    .build());
    }

    public EnumType parseEnum(TTokenizer tokenizer, String comment) throws IOException, TParseException {
        TToken id = tokenizer.expectIdentifier("parsing enum identifier");

        EnumType._Builder et = EnumType.builder();
        if (comment != null) {
            et.setComment(comment);
            comment = null;
        }
        et.setName(id.getToken());

        int nextValue = TEnumDescriptor.DEFAULT_FIRST_VALUE;
        String nextName = null;
        while (true) {
            TToken token = tokenizer.expect("parsing enum " + id.getToken());
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            }

            if (token.isSymbol()) {
                if (token.getSymbol().equals(TSymbol.MAP_END)) {
                    if (nextName != null) {
                        et.addToValues(EnumValue.builder()
                                                .setComment(comment)
                                                .setName(nextName)
                                                .setValue(nextValue)
                                                .build());
                    }
                    break;
                } else if (token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR) ||
                           token.getSymbol().equals(TSymbol.LIST_SEPARATOR)) {
                    // [;,]
                    if (nextName == null) throw new TParseException("Unexpected entry separator: '" + token.getToken() + "'",
                                                                    tokenizer, token);

                    et.addToValues(EnumValue.builder()
                                            .setComment(comment)
                                            .setName(nextName)
                                            .setValue(nextValue)
                                            .build());
                    comment = null;
                    nextName = null;
                    ++nextValue;
                } else if (token.getSymbol().equals(TSymbol.MAP_ENTRY_VALUE_SEP)) {
                    // [=]
                    TToken value = tokenizer.next();
                    if (!value.isInteger()) {
                        throw new TParseException("Expected numeric enum value, got " + value.getToken(),
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
                throw new TParseException("Unexpected token in enum: " + token.getToken(),
                                          tokenizer, token);
            }
        }

        return et.build();
    }

    private StructType parseStruct(TTokenizer tokenizer, String type, String comment) throws IOException, TParseException {
        StructType._Builder struct = StructType.builder();
        if (comment != null) {
            struct.setComment(comment);
            comment = null;
        }
        if (!type.equals("struct")) {
            struct.setVariant(StructVariant.valueOf(type.toUpperCase()));
        }

        TToken id = tokenizer.expectIdentifier("parsing " + type + " identifier");
        if (!id.isIdentifier()) {
            throw new TParseException("Struct name " + id.getToken() + " is not valid identifier",
                                      tokenizer, id);
        }
        struct.setName(id.getToken());

        int nextDefaultKey = (1 << 16) - 1;

        tokenizer.expectSymbol(TSymbol.MAP_START, "parsing struct " + id.getToken());

        ThriftField._Builder field = ThriftField.builder();
        while (true) {
            TToken token = tokenizer.expect("parsing struct " + id.getToken());
            if (token.startsLineComment()) {
                comment = parseLineComment(tokenizer, comment);
                continue;
            } else if (token.startsBlockComment()) {
                comment = parseBlockComment(tokenizer);
                continue;
            } else if (token.isSymbol() && token.getSymbol().equals(TSymbol.MAP_END)) {
                // good end of definition.
                break;
            }

            field.setComment(comment);
            comment = null;

            if (token.isInteger()) {
                field.setKey(token.intValue());
                tokenizer.expectSymbol(TSymbol.MAP_KEY_ENTRY_SEP, "parsing struct " + id.getToken());
                token = tokenizer.expect("parsing struct " + id.getToken());
            } else {
                // TODO(steineldar): Maybe disallow for consistency?
                field.setKey(nextDefaultKey--);
            }

            if (token.getToken().equals(TKeyword.REQUIRED.keyword)) {
                field.setIsRequired(true);
                token = tokenizer.expect("parsing struct " + id.getToken());
            } else if (token.getToken().equals(TKeyword.OPTIONAL.keyword)) {
                // skip. It's optional by default.
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            // Get type.... This is mandatory.
            field.setType(parseType(tokenizer, token));

            token = tokenizer.expect("parsing struct " + id.getToken());
            // get name... This is mandatory.
            if (!token.isIdentifier()) {
                throw new TParseException("Expected name identifier, but found " + token.getToken(),
                                          tokenizer, token);
            }
            field.setName(token.getToken());

            token = tokenizer.expect("parsing struct " + id.getToken());
            if (token.isSymbol() && token.getSymbol().equals(TSymbol.MAP_ENTRY_VALUE_SEP)) {
                field.setDefaultValue(parseValue(tokenizer));
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            if (token.isSymbol() && (
                    token.getSymbol().equals(TSymbol.LIST_SEPARATOR) ||
                    token.getSymbol().equals(TSymbol.ENTRY_SEPARATOR))) {
                token = tokenizer.expect("parsing struct " + id.getToken());
            }

            struct.addToFields(field.build());
            field = ThriftField.builder();

            if (token.isSymbol() && token.getSymbol().equals(TSymbol.MAP_END)) {
                // end of definition.
                break;
            }

            // new entry or comment.
            tokenizer.unshift(token);
        }

        return struct.build();
    }

    public String parseType(TTokenizer tokenizer, TToken token) throws IOException, TParseException {
        if (!token.isQualifiedIdentifier()) {
            throw new TParseException("Expected type identifier but found " + token, tokenizer, token);
        }

        String type = token.getToken();
        TKeyword kw = TKeyword.getByToken(type);
        if (kw == null) return type;

        if (kw.equals(TKeyword.LIST) || kw.equals(TKeyword.SET)) {
            tokenizer.expectSymbol(TSymbol.GENERIC_START, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String itemType = parseType(tokenizer, token);

            tokenizer.expectSymbol(TSymbol.GENERIC_END, "parsing " + kw + " type");

            return String.format("%s<%s>", type, itemType);
        } else if (kw.equals(TKeyword.MAP)) {
            tokenizer.expectSymbol(TSymbol.GENERIC_START, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String keyType = parseType(tokenizer, token);

            tokenizer.expectSymbol(TSymbol.LIST_SEPARATOR, "parsing " + kw + " type");

            token = tokenizer.expect("parsing " + kw + " type");
            String itemType = parseType(tokenizer, token);

            tokenizer.expectSymbol(TSymbol.GENERIC_END, "parsing " + kw + " type");

            return String.format("%s<%s,%s>", type, keyType, itemType);
        }

        return type;
    }
}
