package net.morimekta.providence.serializer.pretty;

import net.morimekta.util.Strings;

import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TokenTest {
    @Test
    public void testIdentifiers() throws IOException {
        assertThat(token("foo").asString(), is("foo"));
        assertThat(token("foo").isSymbol('f'), is(false));
        assertThat(token("foo").isStringLiteral(), is(false));
        assertThat(token("foo").isInteger(), is(false));
        assertThat(token("foo").isReal(), is(false));
        assertThat(token("foo").isIdentifier(), is(true));
        assertThat(token("foo").isQualifiedIdentifier(), is(false));
        assertThat(token("foo").isDoubleQualifiedIdentifier(), is(false));
        assertThat(token("foo").isReferenceIdentifier(), is(true));

        assertThat(token("foo.bar").asString(), is("foo.bar"));
        assertThat(token("foo.bar").isSymbol('f'), is(false));
        assertThat(token("foo.bar").isStringLiteral(), is(false));
        assertThat(token("foo.bar").isInteger(), is(false));
        assertThat(token("foo.bar").isReal(), is(false));
        assertThat(token("foo.bar").isIdentifier(), is(false));
        assertThat(token("foo.bar").isQualifiedIdentifier(), is(true));
        assertThat(token("foo.bar").isDoubleQualifiedIdentifier(), is(false));
        assertThat(token("foo.bar").isReferenceIdentifier(), is(true));

        assertThat(token("foo.bar.more").asString(), is("foo.bar.more"));
        assertThat(token("foo.bar.more").isSymbol('f'), is(false));
        assertThat(token("foo.bar.more").isStringLiteral(), is(false));
        assertThat(token("foo.bar.more").isInteger(), is(false));
        assertThat(token("foo.bar.more").isReal(), is(false));
        assertThat(token("foo.bar.more").isIdentifier(), is(false));
        assertThat(token("foo.bar.more").isQualifiedIdentifier(), is(false));
        assertThat(token("foo.bar.more").isDoubleQualifiedIdentifier(), is(true));
        assertThat(token("foo.bar.more").isReferenceIdentifier(), is(true));

        assertThat(token("foo").hashCode(), is(token("foo").hashCode()));
        assertThat(token("foo").hashCode(), is(not(token("bar").hashCode())));

        assertThat(token("foo"), is(token("foo")));
        assertThat(token("foo"), is(not(token("bar"))));
        assertThat(token("foo").toString(), is("Token('foo',1:1-3)"));
    }

    @Test
    public void testLiteral() {
        assertThat(token("\"foo\"").isStringLiteral(), is(true));
        assertThat(token("\"foo\"").decodeLiteral(), is("foo"));
        assertThat(token("\"foo\"").asString(), is("\"foo\""));
        assertThat(Strings.escape(token("\"\\b\\f\\n\\r\\t\\\"\\\'\\\\\\u2021\\\"\\177\\033\"").decodeLiteral()),
                   is("\\b\\f\\n\\r\\t\\\"\\\'\\\\\u2021\\\"\\177\\033"));
    }

    private Token token(String str) {
        return new Token(str.getBytes(UTF_8), 0, str.length(), 1, 1);
    }
}
