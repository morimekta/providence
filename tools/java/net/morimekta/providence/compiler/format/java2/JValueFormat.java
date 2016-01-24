package net.morimekta.providence.compiler.format.java2;

import net.morimekta.util.Binary;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.util.io.IndentedPrintWriter;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JValueFormat {
    private final JOptions options;
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public JValueFormat(IndentedPrintWriter writer, JOptions options, JHelper helper) {
        this.writer = writer;
        this.options = options;
        this.helper = helper;
    }

    public void appendDefaultConstants(List<JField> fields)
            throws GeneratorException {
        boolean hasDefault = false;
        for (JField field : fields) {
            if (field.hasDefault()) {
                Object defaultValue = helper.getDefaultValue(field.getPField());
                if (defaultValue != null) {
                    hasDefault = true;
                    writer.formatln("private final static %s %s = ",
                                    field.valueType(),
                                    field.kDefault())
                          .begin(IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT);
                    appendTypedValue(defaultValue, field.getPField().getDescriptor());
                    writer.append(';')
                          .end();
                }
            }
        }

        if (hasDefault) {
            writer.newline();
        }
    }

    public void addTypeImports(JHeader header, PDescriptor<?> descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case ENUM:
            case MESSAGE:
                // Avoid never-ending recursion (with circular contained
                // structs) by stopping on already included structs and enums.
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                break;
            case LIST:
                PContainer<?, ?> lType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PList.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                addTypeImports(header, lType.itemDescriptor());
                break;
            case SET:
                PContainer<?, ?> sType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PSet.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                if (options.android) {
                    header.include(ArrayList.class.getName());
                }
                addTypeImports(header, sType.itemDescriptor());
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) descriptor;
                header.include(java.util.Collections.class.getName());
                header.include(PMap.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                header.include(helper.getQualifiedInstanceClassName(mType.itemDescriptor()));
                header.include(helper.getQualifiedInstanceClassName(mType.keyDescriptor()));
                addTypeImports(header, mType.keyDescriptor());
                addTypeImports(header, mType.itemDescriptor());
                break;
            case BINARY:
                header.include(PPrimitive.class.getName());
                header.include(Binary.class.getName());
                break;
            default:
                header.include(PPrimitive.class.getName());
                break;
        }
    }

    public void appendTypedValue(Object value,
                                 PDescriptor<?> type)
            throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                writer.append(value.toString());
                break;
            case BYTE:
                writer.append("(byte)").append(value.toString());
                break;
            case I16:
                writer.append("(short)").append(value.toString());
                break;
            case I32:
                writer.append(value.toString());
                break;
            case I64:
                writer.append(value.toString()).append("L");
                break;
            case DOUBLE:
                writer.append(value.toString()).append("d");
                break;
            case BINARY:
                writer.append("Binary.wrap(new byte[]{");
                byte[] bytes = (byte[]) value;
                boolean first = true;
                for (byte b : bytes) {
                    if (first)
                        first = false;
                    else
                        writer.append(',');
                    writer.format("0x%02x", b);
                }
                writer.append("})");
                break;
            case STRING:
                try {
                    JsonWriter json = new JsonWriter(writer);
                    json.value(value.toString());
                    json.flush();
                } catch (JsonException je) {
                    throw new GeneratorException("Unable to format string value");
                }
                break;
            case ENUM:
                writer.format("%s.%s", helper.getInstanceClassName(type), value.toString());
                break;
            case MESSAGE:
                // writer.write("null");
                throw new GeneratorException("Message structs cannot have default values");
            case MAP:
            case LIST:
            case SET:
                // writer.write("null");
                throw new GeneratorException("Collections cannot have default value.");
        }
    }
}
