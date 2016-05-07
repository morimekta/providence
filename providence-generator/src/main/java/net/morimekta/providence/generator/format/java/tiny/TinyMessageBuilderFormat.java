package net.morimekta.providence.generator.format.java.tiny;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JOptions;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.BitSet;
import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class TinyMessageBuilderFormat {
    private final JOptions options;
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public TinyMessageBuilderFormat(IndentedPrintWriter writer, JHelper helper, JOptions options) {
        this.writer = writer;
        this.helper = helper;
        this.options = options;
    }

    public void appendBuilder(JMessage<?> message) throws GeneratorException {
        appendMutators();

        if (options.jackson) {
            writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = \"set\")");
        }
        if (JAnnotation.isDeprecated(message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        writer.appendln("public static class _Builder {")
              .begin();

        appendFields(message);

        appendDefaultConstructor(message);
        appendMutateConstructor(message);

        for (JField field : message.fields()) {
            appendSetter(message, field);
            appendResetter(message, field);
        }

        writer.formatln("public %s build() {", message.instanceType())
              .begin()
              .formatln("return new %s(this);", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');
    }

    private void appendMutators() {
        writer.appendln("public _Builder mutate() {")
              .begin()
              .appendln("return new _Builder(this);")
              .end()
              .appendln('}')
              .newline();
        writer.formatln("public static _Builder builder() {")
              .begin()
              .appendln("return new _Builder();")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendFields(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            writer.appendln("private _Field tUnionField;");
        } else {
            writer.formatln("private %s optionals;",
                            BitSet.class.getName());
        }
        writer.newline();
        for (JField field : message.fields()) {
            writer.formatln("private %s %s;", field.fieldType(), field.member());
        }
        if (message.fields()
                   .size() > 0) {
            writer.newline();
        }
    }

    private void appendDefaultConstructor(JMessage<?> message) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a " + message.descriptor().getQualifiedName(null) + " builder.")
               .finish();
        writer.appendln("public _Builder() {")
              .begin();
        if (!message.isUnion()) {
            writer.formatln("optionals = new %s(%d);",
                            BitSet.class.getName(),
                            message.fields()
                                   .size());
        }
        message.fields()
               .stream()
               .filter(JField::hasDefault)
               .forEachOrdered(field -> writer.formatln("%s = %s;", field.member(), field.kDefault()));

        writer.end()
              .appendln('}')
              .newline();

    }

    private void appendMutateConstructor(JMessage<?> message) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a mutating builder off a base " + message.descriptor().getQualifiedName(null) + ".")
               .newline()
               .param_("base", "The base " + message.descriptor().getName())
               .finish();
        writer.formatln("public _Builder(%s base) {", message.instanceType())
              .begin()
              .appendln("this();")
              .newline();
        if (message.isUnion()) {
            writer.appendln("tUnionField = base.tUnionField;")
                  .newline();
        }
        for (JField field : message.fields()) {
            boolean checkPresence = !field.alwaysPresent();
            if (checkPresence) {
                writer.formatln("if (base.%s != null) {", field.member())
                      .begin();
            }
            if (!message.isUnion()) {
                writer.formatln("optionals.set(%d);", field.index());
            }
            writer.formatln("%s = base.%s;", field.member(), field.member());

            if (checkPresence) {
                writer.end()
                      .appendln('}');
            }
        }

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendSetter(JMessage message, JField field) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Sets the value of " + field.name() + ".")
               .newline();
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.param_("value", "The new value")
               .return_("The builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        if (options.jackson) {
            writer.formatln("@com.fasterxml.jackson.annotation.JsonProperty(\"%s\") ", field.name());
            if (field.binary()) {
                writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = net.morimekta.providence.jackson.BinaryJsonDeserializer.class) ");
            }
        }
        if (field.type() == PType.SET || field.type() == PType.LIST) {
            PContainer<?> cType = (PContainer<?>) field.getPField()
                                                             .getDescriptor();
            String iType = helper.getFieldType(cType.itemDescriptor());
            writer.formatln("public _Builder %s(%s<%s> value) {",
                            field.setter(), Collection.class.getName(), iType);
        } else {
            writer.formatln("public _Builder %s(%s value) {", field.setter(), field.valueType());
        }
        writer.begin();

        if (message.isUnion()) {
            writer.formatln("tUnionField = _Field.%s;", field.fieldEnum());
        } else {
            writer.formatln("optionals.set(%d);", field.index());
        }

        writer.formatln("%s = %s;", field.member(), field.copyOfUnsafe("value"));

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendResetter(JMessage message, JField field) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Clears the " + field.name() + " field.")
               .newline();
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.return_("The builder")
               .finish();
        writer.formatln("public _Builder %s() {", field.resetter())
              .begin();

        if (message.isUnion()) {
            writer.formatln("if (tUnionField == _Field.%s) tUnionField = null;", field.fieldEnum());
        } else {
            writer.formatln("optionals.clear(%d);", field.index());
        }

        if (field.hasDefault()) {
            writer.formatln("%s = %s;", field.member(), field.kDefault());
        } else {
            writer.formatln("%s = null;", field.member());
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }
}
