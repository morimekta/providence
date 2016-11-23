package net.morimekta.providence.generator.format.java.messages.extra;

import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Binary;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JacksonMessageFormatter implements MessageMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public JacksonMessageFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    public void appendClassAnnotations(JMessage<?> message) {
        writer.formatln("@%s(", JsonSerialize.class.getName())
              .formatln("        using = %s._Serializer.class)", message.instanceType());
        writer.formatln("@%s(", JsonDeserialize.class.getName())
              .formatln("        using = %s._Deserializer.class)", message.instanceType());
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendJacksonDeserializer(message);
        appendJacksonSerializer(message);
    }

    private void appendReadValue(String builder, JField field) throws GeneratorException {
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
                writer.formatln("%s.%s(ctxt.readValue(jp, %s.TYPE));",
                                builder,
                                field.setter(),
                                field.instanceType());
                break;
        }

    }

    private void appendWriteValue(JField field) {
        switch (field.type()) {
            case VOID:
                writer.formatln("provider.defaultSerializeField(\"%s\", true, generator);",
                                field.name());
                break;
            case BINARY:
                writer.formatln("provider.defaultSerializeField(\"%s\", instance.%s.toBase64(), generator);",
                                field.name(), field.member());
                break;
            default:
                writer.formatln("provider.defaultSerializeField(\"%s\", instance.%s, generator);",
                                field.name(), field.member());
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

        for (JField field : message.declaredOrderFields()) {
            writer.formatln("case \"%d\":", field.id())
                  .formatln("case \"%s\": {", field.name())
                  .begin();

            appendReadValue("builder", field);

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

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %d: {", field.index())
                      .begin();

                appendReadValue("builder", field);

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

    private void appendJacksonSerializer(JMessage<?> message) throws GeneratorException {
        writer.formatln("public static class _Serializer extends %s<%s> {",
                        JsonSerializer.class.getName(),
                        message.instanceType())
              .begin()
              .appendln("@Override")
              .formatln("public void serialize(%s instance, %s generator, %s provider)",
                        message.instanceType(),
                        JsonGenerator.class.getName(),
                        SerializerProvider.class.getName())
              .formatln("        throws %s, %s {",
                        IOException.class.getName(), JsonProcessingException.class.getName())
              .begin();

        if (message.isUnion()) {
            writer.appendln("generator.writeStartObject();")
                  .appendln("switch (instance.tUnionField) {")
                  .begin();

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin();

                appendWriteValue(field);

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.end()
                  .appendln('}')
                  .appendln("generator.writeEndObject();");
        } else {
            // if (message.descriptor().isCompactible()) {
            //     writer.formatln("if (compact()) {")
            //           .begin();
            //
            //     // TODO: Make compact
            // }

            writer.formatln("generator.writeStartObject();");

            for (JField field : message.declaredOrderFields()) {
                if (!field.alwaysPresent()) {
                    writer.formatln("if (instance.%s != null) {", field.member())
                          .begin();
                }

                appendWriteValue(field);

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }

            writer.appendln("generator.writeEndObject();");

            // if (message.descriptor().isCompactible()) {
            //     writer.end()
            //           .appendln('}');
            // }
        }

        writer.end()
              .formatln("}")  // end serialize()
              .end()
              .formatln("}")  // end _Serializer
              .newline();
    }

}
