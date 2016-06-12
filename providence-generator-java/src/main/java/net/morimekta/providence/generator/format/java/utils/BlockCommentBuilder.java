package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

/**
 * Created by morimekta on 5/3/16.
 */
public class BlockCommentBuilder {
    private final IndentedPrintWriter writer;
    private final Escaper             html;

    public BlockCommentBuilder(IndentedPrintWriter writer) {
        this.html = HtmlEscapers.htmlEscaper();
        this.writer = writer;

        writer.appendln("/**")
              .begin(" *");
    }

    public BlockCommentBuilder comment(String comment) {
        String escaped = html.escape(comment);
        for (String line : escaped.trim().split("\r?\n")) {
            if (line.length() == 0) {
                writer.appendln(" <p>");
            } else {
                writer.appendln(" " + line);
            }
        }

        return this;
    }

    public BlockCommentBuilder newline() {
        writer.appendln();
        return this;
    }

    public BlockCommentBuilder param_(String name, String comment) {
        writer.formatln(" @param %s %s", name, html.escape(comment));
        return this;
    }

    public BlockCommentBuilder return_(String comment) {
        writer.formatln(" @return %s", html.escape(comment));
        return this;
    }

    public BlockCommentBuilder throws_(Class<?> klass, String comment) {
        writer.formatln(" @throws %s %s",
                        klass.getName().replaceAll("[$]", "."),
                        html.escape(comment));
        return this;
    }

    public BlockCommentBuilder throws_(String klass, String comment) {
        writer.formatln(" @throws %s %s",
                        html.escape(comment));
        return this;
    }

    public BlockCommentBuilder see_(Class<?> klass, String comment) {
        writer.formatln(" @see %s %s",
                        klass.getName().replaceAll("[$]", "."),
                        html.escape(comment));
        return this;
    }

    public BlockCommentBuilder see_(String klass, String comment) {
        writer.formatln(" @see %s %s",
                        html.escape(comment));
        return this;
    }

    public void finish() {
        writer.end()
              .appendln(" */");
    }
}
