package net.morimekta.providence.generator.format.java.enums.extras;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class JacksonEnumFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;

    public JacksonEnumFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void appendClassAnnotations(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("@%s(", JsonDeserialize.class.getName())
              .formatln("        using = %s._Deserializer.class)", simpleClass);
    }

    @Override
    public void appendMethods(CEnumDescriptor type) throws GeneratorException {
        writer.formatln("@%s", JsonValue.class.getName())
              .appendln("public int jsonValue() {")
              .appendln("    return mValue;")
              .appendln('}')
              .newline();
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("public static class _Deserializer extends %s<%s> {",
                        JsonDeserializer.class.getName(), simpleClass)
              .appendln("    @Override")
              .formatln("    public %s deserialize(%s jp,",
                        simpleClass, JsonParser.class.getName())
              .formatln("           %s             %s ctxt)",
                        simpleClass.replaceAll(".", " "), DeserializationContext.class.getName())
              .formatln("            throws %s,", IOException.class.getName())
              .formatln("                   %s {", JsonProcessingException.class.getName())
              .formatln("        if (jp.getCurrentToken() == %s.%s) {",
                        JsonToken.class.getName(),
                        JsonToken.VALUE_NUMBER_INT.name())
              .formatln("            return %s.forValue(jp.getIntValue());", simpleClass)
              .formatln("        } else if (jp.getCurrentToken() == %s.%s) {",
                        JsonToken.class.getName(),
                        JsonToken.VALUE_STRING.name())
              .formatln("            return %s.forName(jp.getText());", simpleClass)
              .appendln("        } else {")
              .formatln("            throw new %s(jp, \"Invalid token for enum %s deserialization \" + jp.getText());",
                        JsonParseException.class.getName(),
                        type.getQualifiedName())
              .appendln("        }")
              .appendln("    }")
              .appendln('}')
              .newline();
    }
}
