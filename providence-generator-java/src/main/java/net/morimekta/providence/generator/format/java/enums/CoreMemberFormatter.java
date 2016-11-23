package net.morimekta.providence.generator.format.java.enums;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptorProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * TODO(steineldar): Make a proper class description.
 */
public class CoreMemberFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;

    public CoreMemberFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Collection<String> getExtraImplements(CEnumDescriptor type) throws GeneratorException {
        return ImmutableList.of(
                String.format("%s<%s>", PEnumValue.class.getName(), JUtils.getClassName(type))
        );
    }

    @Override
    public void appendMethods(CEnumDescriptor type) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public int getValue() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String getName() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public int asInteger() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String asString() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        appendBuilder(type);
        appendDescriptor(type);
    }

    private void appendDescriptor(PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("public static final %s<%s> kDescriptor;",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public %s<%s> descriptor() {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s<%s> provider() {",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .begin()
              .formatln("return new %s<%s>(kDescriptor);",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Factory")
              .begin()
              .formatln("    extends %s<%s> {",
                        PEnumBuilderFactory.class.getName(),
                        simpleClass)
              .appendln("@Override")
              .formatln("public %s._Builder builder() {", simpleClass)
              .begin()
              .formatln("return new %s._Builder();", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Descriptor")
              .formatln("        extends %s<%s> {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("public _Descriptor() {")
              .begin()
              .formatln("super(\"%s\", \"%s\", new _Factory());",
                        type.getPackageName(),
                        type.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s[] getValues() {", simpleClass)
              .begin()
              .formatln("return %s.values();", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueById(int id) {", simpleClass)
              .begin()
              .formatln("return %s.forValue(id);", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueByName(String name) {", simpleClass)
              .begin()
              .formatln("return %s.forName(name);", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("static {", simpleClass)
              .begin();
        writer.appendln("kDescriptor = new _Descriptor();")
              .end()
              .appendln('}');
    }

    private void appendBuilder(PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);
        writer.formatln("public static class _Builder extends %s<%s> {",
                        PEnumBuilder.class.getName(),
                        simpleClass)
              .begin()
              .formatln("%s mValue;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByValue(int value) {")
              .begin()
              .formatln("mValue = %s.forValue(value);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByName(String name) {")
              .begin()
              .formatln("mValue = %s.forName(name);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public boolean isValid() {")
              .begin()
              .appendln("return mValue != null;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s build() {", simpleClass)
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }
}
