package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JMessageAndroidFormat {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public JMessageAndroidFormat(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    public void appendParcelable(JMessage message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public int describeContents() {")
              .begin()
              .appendln("return 0;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public void writeToParcel(android.os.Parcel dest, int flags) {")
              .begin()
              .formatln("%s baos = new %s();",
                        ByteArrayOutputStream.class.getName(),
                        ByteArrayOutputStream.class.getName())
              .formatln("%s serializer = new %s();",
                        Serializer.class.getName(),
                        FastBinarySerializer.class.getName())
              .appendln("try {")
              .begin()
              .appendln("serializer.serialize(baos, this);")
              .appendln("dest.writeByteArray(baos.toByteArray());")
              .end()
              .formatln("} catch (%s e) {", IOException.class.getName())
              .formatln("    throw new %s(e);", UncheckedIOException.class.getName())
              .appendln("}")
              .end()
              .appendln('}');

        writer.formatln("public static final android.os.Parcelable.Creator<%s> CREATOR = new android.os.Parcelable.Creator<%s>() {",
                        message.instanceType(),
                        message.instanceType())
              .begin();

        writer.appendln("@Override")
              .formatln("public %s createFromParcel(android.os.Parcel source) {", message.instanceType())
              .begin()
              .formatln("%s bais = new %s(source.createByteArray());", ByteArrayInputStream.class.getName(), ByteArrayInputStream.class.getName())
              .formatln("%s serializer = new %s();",
                        Serializer.class.getName(),
                        FastBinarySerializer.class.getName())
              .appendln("try {")
              .begin()
              .formatln("return serializer.deserialize(bais, %s.kDescriptor);",
                        message.instanceType())
              .end()
              .formatln("} catch (%s e) {", IOException.class.getName())
              .formatln("    throw new %s(e);", UncheckedIOException.class.getName())
              .appendln("}")
              .end()
              .appendln('}');

        writer.appendln("@Override")
              .formatln("public %s[] newArray(int size) {", message.instanceType())
              .begin()
              .formatln("return new %s[size];", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln("};")
              .newline();
    }

}
