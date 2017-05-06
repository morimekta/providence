package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
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
        if (ProvidenceConfig.RESERVED_WORDS.contains(reference)) {
            throw new TokenizerException(token, "Trying to assign reference id '%s', which is reserved.", reference).setLine(tokenizer.getLine(token.getLineNo()));
        }

        TokenizerException ex = referenceExceptions.get(reference);
        if (ex != null) {
            if (references.containsKey(reference)) {
                throw new TokenizerException(token,
                                             "Trying to reassign reference '%s', original at line %d",
                                             reference,
                                             ex.getLineNo())
                        .setLine(tokenizer.getLine(token.getLineNo()))
                        .initCause(ex);
            }
            throw new TokenizerException(token,
                                         "Trying to reassign reference '%s' while calculating it's value, original at line %d",
                                         reference,
                                         ex.getLineNo())
                    .setLine(tokenizer.getLine(token.getLineNo()))
                    .initCause(ex);
        } else if (includeAliases.contains(reference)) {
            throw new TokenizerException(token,
                                         "Trying to reassign include alias '%s' to reference.",
                                         reference)
                    .setLine(tokenizer.getLine(token.getLineNo()));
        }

        referenceExceptions.put(reference, new TokenizerException(token, "Original reference")
                .setLine(tokenizer.getLine(token.getLineNo())));
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
                                         reference, ex.getLineNo()).setLine(tokenizer.getLine(token.getLineNo()));
        }
        throw new TokenizerException(token, "No such reference '%s'", reference)
                .setLine(tokenizer.getLine(token.getLineNo()));
    }
}
