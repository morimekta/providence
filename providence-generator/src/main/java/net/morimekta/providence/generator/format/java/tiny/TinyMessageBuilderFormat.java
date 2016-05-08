package net.morimekta.providence.generator.format.java.tiny;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JOptions;
import net.morimekta.util.Binary;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

        // Compactible messages needs a special deserializer to also handle
        // the compact format. Note that it will still serializer as "normal"
        // json objects.
        if (options.jackson) {
            if (message.descriptor().isCompactible()) {
                 appendJacksonDeserializer(message);
            } else {
                writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = \"set\")");
            }
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

    private void appendReadValue(String builder, JField field, JMessage message) throws GeneratorException {
        switch (field.type()) {
            case MAP: {
                PMap mType = (PMap) field.getPField()
                                         .getDescriptor();
                PDescriptor kType = mType.keyDescriptor();
                writer.formatln("%s kType = ctxt.getTypeFactory().uncheckedSimpleType(%s.class);",
                                JavaType.class.getName(),
                                helper.getFieldType(kType));
                PDescriptor iType = mType.itemDescriptor();

                if (iType instanceof PMap) {
                    PMap imType = (PMap) iType;
                    PDescriptor ikType = imType.keyDescriptor();
                    PDescriptor iiType = imType.itemDescriptor();
                    if (iiType instanceof PContainer) {
                        throw new GeneratorException("Too many levels of containers: " + field.toString());
                    }

                    // double level of container...
                    writer.formatln("%s iType = ctxt.getTypeFactory().constructMapType(%s.class, %s.class, %s.class);",
                                    MapType.class.getName(),
                                    HashMap.class.getName(),
                                    helper.getFieldType(ikType),
                                    helper.getFieldType(iiType));
                } else if (iType instanceof PContainer) {
                    PContainer icType = (PContainer) iType;
                    PDescriptor iiType = icType.itemDescriptor();
                    if (iiType instanceof PContainer) {
                        throw new GeneratorException("Too many levels of containers: " + field.toString());
                    }

                    // double level of container...
                    writer.formatln("%s iType = ctxt.getTypeFactory().constructArrayType(%s.class);",
                                    MapType.class.getName(),
                                    helper.getFieldType(iiType));
                } else {
                    writer.formatln("%s iType = ctxt.getTypeFactory().uncheckedSimpleType(%s.class);",
                                    JavaType.class.getName(),
                                    helper.getFieldType(iType));
                }
                writer.formatln("%s type = ctxt.getTypeFactory().constructMapType(%s.class, kType, iType);",
                                MapType.class.getName(),
                                HashMap.class.getName());
                writer.formatln("%s.%s(ctxt.readValue(jp, type));", builder, field.setter());
                break;
            }
            case LIST: {
                PContainer cType = (PContainer) field.getPField()
                                                     .getDescriptor();
                PDescriptor iType = cType.itemDescriptor();
                if (iType instanceof PMap) {
                    PMap imType = (PMap) iType;
                    PDescriptor ikType = imType.keyDescriptor();
                    PDescriptor iiType = imType.itemDescriptor();
                    // double level of container...
                    writer.formatln(
                            "%s itype = ctxt.getTypeFactory().constructMapType(%s.class, %s.class, %s.class);",
                            MapType.class.getName(),
                            LinkedHashMap.class.getName(),
                            helper.getFieldType(ikType),
                            helper.getFieldType(iiType));
                    writer.formatln("%s type = ctxt.getTypeFactory().constructArrayType(itype);",
                                    ArrayType.class.getName(),
                                    helper.getFieldType(iType));
                } else if (iType instanceof PContainer) {
                    PContainer icType = (PContainer) iType;
                    PDescriptor iiType = icType.itemDescriptor();
                    // double level of container...
                    writer.formatln("%s itype = ctxt.getTypeFactory().constructArrayType(%s.class);",
                                    ArrayType.class.getName(),
                                    helper.getFieldType(iiType));
                    writer.formatln("%s type = ctxt.getTypeFactory().constructArrayType(itype);",
                                    ArrayType.class.getName(),
                                    helper.getFieldType(iType));
                } else {
                    writer.formatln("%s type = ctxt.getTypeFactory().constructArrayType(%s.class);",
                                    ArrayType.class.getName(),
                                    helper.getFieldType(iType));
                }
                writer.formatln("%s.%s(ctxt.readValue(jp, type));", builder, field.setter());
                break;
            }
            case SET: {
                PContainer cType = (PContainer) field.getPField()
                                                     .getDescriptor();
                PDescriptor iType = cType.itemDescriptor();
                writer.formatln("%s type = ctxt.getTypeFactory().constructArrayType(%s.class);",
                                ArrayType.class.getName(),
                                helper.getFieldType(iType));

                writer.formatln("%s.%s(ctxt.readValue(jp, type));", builder, field.setter());
                break;
            }
            case BINARY:
                writer.formatln("%s.%s(%s.fromBase64(ctxt.readValue(jp, String.class)));",
                                builder,
                                field.setter(),
                                Binary.class.getName());
                break;
            case STRING:
            case MESSAGE:
            case ENUM:
                writer.formatln("%s.%s(ctxt.readValue(jp, %s.class));",
                                builder,
                                field.setter(),
                                field.instanceType());
                break;
            default:
                writer.formatln("%s.%s(ctxt.readValue(jp, %s.class));",
                                builder,
                                field.setter(),
                                field.instanceType());
                break;
        }

    }

    private void appendJacksonDeserializer(JMessage<?> message) throws GeneratorException {
        writer.formatln("public static class _Deserializer extends %s<%s> {",
                        JsonDeserializer.class.getName(),
                        message.instanceType())
              .begin();

        writer.appendln("@Override")
              .formatln("public %s deserialize(%s jp,",
                        message.instanceType(),
                        JsonParser.class.getName())
              .formatln("       %s             %s ctxt)",
                        message.instanceType().replaceAll("[\\S]", " "),
                        DeserializationContext.class.getName())
              .formatln("         throws %s,",
                        IOException.class.getName())
              .formatln("                %s {",
                        JsonProcessingException.class.getName())
              .begin();

        writer.appendln("_Builder builder = builder();")
              .newline();

        writer.formatln("if (jp.isExpectedStartObjectToken()) {")
              .begin()
              .formatln("while (jp.nextToken() != %s.END_OBJECT) {", JsonToken.class.getName())
              .begin()
              .formatln("if (jp.getCurrentToken() != %s.FIELD_NAME) {", JsonToken.class.getName())
              .formatln("    throw new %s(jp, \"Invalid field name token \" + jp.getText());",
                        JsonParseException.class.getName())
              .appendln('}')
              .newline()
              .appendln("String field = jp.getCurrentName();")
              .appendln("jp.nextToken();")
              .appendln("switch (field) {")
              .begin();

        for (JField field : message.fields()) {
            writer.formatln("case \"%s\": {", field.name())
                  .begin();

            appendReadValue("builder", field, message);

            writer.appendln("break;")
                  .end()
                  .appendln('}');
        }

        writer.end()
              .appendln("}")
              .end()
              .appendln("}");

        if (message.descriptor().isCompactible()) {
            writer.end()
                  .appendln("} else if (jp.isExpectedStartArrayToken()) {")
                  .begin();

            writer.appendln("int idx = 0;")
                  .formatln("while (jp.nextToken() != %s.END_ARRAY) {", JsonToken.class.getName())
                  .begin()
                  .appendln("switch (idx++) {")
                  .begin();

            for (JField field : message.fields()) {
                writer.formatln("case %d: {", field.index())
                      .begin();

                appendReadValue("builder", field, message);

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.appendln("default:")
                  .formatln("    throw new %s(jp, \"Unexpected value: \" + jp.getText());",
                            JsonParseException.class.getName())
                  .end()
                  .appendln('}')
                  .end()
                  .appendln('}');
        }

        writer.end()
              .appendln("} else {")
              .formatln("    throw new %s(jp, \"Invalid token for object deserialization \" + jp.getText());",
                        JsonParseException.class.getName())
              .appendln('}')
              .newline()
              .appendln("return builder.build();");

        writer.end()
              .formatln("}")
              .end()
              .formatln("}")
              .newline();
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
