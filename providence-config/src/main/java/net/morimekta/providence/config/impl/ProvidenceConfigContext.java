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
package net.morimekta.providence.config.impl;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Context object related to a providence file being parsed. Takes care of
 * knowing about includes and registered references.
 */
class ProvidenceConfigContext {
    private final Set<String>                     includeAliases;
    private final Map<String, Object>             references;
    private final Map<String, TokenizerException> referenceExceptions;

    ProvidenceConfigContext() {
        this.references = new HashMap<>();
        this.referenceExceptions = new HashMap<>();
        this.includeAliases = new HashSet<>();
    }

    boolean containsReference(String name) {
        return referenceExceptions.containsKey(name) ||
               references.containsKey(name);
    }

    void setInclude(String alias, PMessage include) {
        references.put(alias, include);
        includeAliases.add(alias);
    }

    String initReference(Token token, Tokenizer tokenizer) throws TokenizerException {
        String reference = token.asString();
        if (ProvidenceConfigUtil.RESERVED_WORDS.contains(reference)) {
            throw new TokenizerException(token, "Trying to assign reference id '%s', which is reserved.", reference).setLine(tokenizer.getLine());
        }

        TokenizerException ex = referenceExceptions.get(reference);
        if (ex != null) {
            if (references.containsKey(reference)) {
                throw new TokenizerException(token,
                                             "Trying to reassign reference '%s', original at line %d",
                                             reference,
                                             ex.getLineNo())
                        .setLine(tokenizer.getLine())
                        .initCause(ex);
            }
            throw new TokenizerException(token,
                                         "Trying to reassign reference '%s' while calculating it's value, original at line %d",
                                         reference,
                                         ex.getLineNo())
                    .setLine(tokenizer.getLine())
                    .initCause(ex);
        } else if (includeAliases.contains(reference)) {
            throw new TokenizerException(token,
                                         "Trying to reassign include alias '%s' to reference.",
                                         reference)
                    .setLine(tokenizer.getLine());
        }

        referenceExceptions.put(reference, new TokenizerException(token, "Original reference")
                .setLine(tokenizer.getLine()));
        return reference;
    }

    Object setReference(String reference, Object value) {
        if (reference != null) {
            if (!referenceExceptions.containsKey(reference)) {
                throw new RuntimeException("Reference '" + reference + "' not initialised");
            }
            references.put(reference, value);
        }
        return value;
    }

    Object getReference(String reference, Token token, Tokenizer tokenizer) throws TokenizerException {
        if (references.containsKey(reference)) {
            return references.get(reference);
        }

        TokenizerException ex = referenceExceptions.get(reference);
        if (ex != null) {
            throw new TokenizerException(token, "Trying to reference '%s' while it's being defined, original at line %d",
                                         reference, ex.getLineNo()).setLine(tokenizer.getLine());
        }
        throw new TokenizerException(token, "No such reference '%s'", reference)
                .setLine(tokenizer.getLine());
    }
}
