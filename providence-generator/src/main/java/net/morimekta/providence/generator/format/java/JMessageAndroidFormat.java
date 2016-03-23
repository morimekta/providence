package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PType;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.util.io.IndentedPrintWriter;

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

    private void appendParcelableArrayConverter(String srcType, String srcName, String destType, String itemConvert) {
        writer.formatln("%s[] arr = new %s[%s.size()];", destType, destType, srcName)
              .appendln("int pos = 0;")
              .formatln("for (%s item : %s) {", srcType, srcName)
              .begin()
              .formatln("arr[pos++] = %s;", itemConvert)
              .end()
              .appendln('}');
    }

    private void appendParcelableWriter(String fName, PDescriptor<?> desc) throws GeneratorException {
        switch (desc.getType()) {
            case BOOL:
                writer.formatln("dest.writeByte(%s ? (byte) 1 : (byte) 0);", fName);
                break;
            case BYTE:
                writer.formatln("dest.writeByte(%s);", fName);
                break;
            case I16:
            case I32:
                writer.formatln("dest.writeInt(%s);", fName);
                break;
            case I64:
                writer.formatln("dest.writeLong(%s);", fName);
                break;
            case DOUBLE:
                writer.formatln("dest.writeDouble(%s);", fName);
                break;
            case STRING:
                writer.formatln("dest.writeString(%s);", fName);
                break;
            case BINARY:
                writer.formatln("dest.writeByteArray(%s.get());", fName);
                break;
            case ENUM:
                writer.formatln("dest.writeInt(%s.getValue());", fName);
                break;
            case MESSAGE:
                writer.formatln("dest.writeTypedObject(%s, 0);", fName);
                break;
            case LIST:
            case SET:
                PContainer<?, ?> cType = (PContainer<?, ?>) desc;
                String iTypeName = helper.getInstanceClassName(cType.itemDescriptor());
                switch (cType.itemDescriptor()
                             .getType()) {
                    case BOOL:
                        appendParcelableArrayConverter("Boolean", fName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter("Byte", fName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter("Short", fName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter("Integer", fName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter("Long", fName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter("Double", fName, "double", "item");
                        writer.appendln("dest.writeDoubleArray(arr);");
                        break;
                    case STRING:
                        writer.formatln("dest.writeStringArray(%s.toArray(new String[%s.size()]));", fName, fName);
                        break;
                    case BINARY:
                        writer.formatln("dest.writeInt(%s.size());", fName)
                              .formatln("for (Binary item : %s) {", fName)
                              .begin()
                              .appendln("dest.writeByteArray(item.get());")
                              .end()
                              .appendln('}');
                        break;
                    case MESSAGE:
                        if (desc.getType()
                                .equals(PType.SET)) {
                            writer.formatln("dest.writeTypedList(new ArrayList<>(%s));", fName, iTypeName, fName);
                        } else {
                            writer.formatln("dest.writeTypedList(%s);", fName, iTypeName, fName);
                        }
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(iTypeName, fName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is too complex to parse.", helper.getFieldType(cType));
                        break;
                }
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) desc;
                switch (mType.itemDescriptor()
                             .getType()) {
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is to complext to parse.", helper.getFieldType(mType));
                        return;
                }
                writer.appendln('{')
                      .begin();
                String kTypeName = helper.getInstanceClassName(mType.keyDescriptor());
                iTypeName = helper.getInstanceClassName(mType.itemDescriptor());
                String kName = fName + ".keySet()";
                switch (mType.keyDescriptor()
                             .getType()) {
                    case BOOL:
                        appendParcelableArrayConverter("Boolean", kName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter("Byte", kName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter("Short", kName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter("Integer", kName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter("Long", kName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter("Double", kName, "double", "item");
                        writer.appendln("dest.writeDoubleArray(arr);");
                        break;
                    case STRING:
                        writer.formatln("dest.writeStringArray(%s.toArray(new String[%s.size()]));", kName, kName);
                        break;
                    case BINARY:
                        writer.formatln("dest.writeInt(%s.size());", kName)
                              .formatln("for (Binary item : %s) {", kName)
                              .begin()
                              .appendln("dest.writeByteArray(item.get());")
                              .end()
                              .appendln('}');
                        break;
                    case MESSAGE:
                        writer.formatln("dest.writeParcelableArray(%s.toArray(new %s[%s.size()]), 0);",
                                        kName,
                                        kTypeName,
                                        kName);
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(kTypeName, kName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    default:
                        throw new GeneratorException("Map keys cannot contain containers.");
                }
                writer.end()
                      .appendln('}');
                String vName = fName + ".values()";
                switch (mType.itemDescriptor()
                             .getType()) {
                    case BOOL:
                        appendParcelableArrayConverter("Boolean", vName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter("Byte", vName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter("Short", vName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter("Integer", vName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter("Long", vName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter("Double", vName, "double", "item");
                        writer.appendln("dest.writeDoubleArray(arr);");
                        break;
                    case STRING:
                        writer.formatln("dest.writeStringArray(%s.toArray(new String[%s.size()]));", vName, vName);
                        break;
                    case BINARY:
                        writer.formatln("dest.writeInt(%s.size());", vName)
                              .formatln("for (Binary item : %s) {", vName)
                              .begin()
                              .appendln("dest.writeByteArray(item.get());")
                              .end()
                              .appendln('}');
                        break;
                    case MESSAGE:
                        writer.formatln("dest.writeParcelableArray(%s.toArray(new %s[%s.size()]), 0);",
                                        vName,
                                        iTypeName,
                                        vName);
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(iTypeName, vName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    default:
                        throw new GeneratorException("Unknown map value type: " + mType.getName());
                }
                break;
        }
    }

    private void appendParcelableArrayReader(String creator, String typeName, String addToF, String cast) {
        writer.formatln("%s[] tmp = source.%s();", typeName, creator)
              .appendln("for (int i = 0; i < tmp.length; ++i) {")
              .formatln("    builder.%s(%stmp[i]);", addToF, cast)
              .appendln('}');

    }

    private void appendParcelableReader(String setF, String addToF, PDescriptor<?> desc) throws GeneratorException {
        String classF = helper.getInstanceClassName(desc);

        switch (desc.getType()) {
            case BOOL:
                writer.formatln("builder.%s(source.readByte() > 0);", setF);
                break;
            case BYTE:
                writer.formatln("builder.%s(source.readByte());", setF);
                break;
            case I16:
                writer.formatln("builder.%s((short)source.readInt());", setF);
                break;
            case I32:
                writer.formatln("builder.%s(source.readInt());", setF);
                break;
            case I64:
                writer.formatln("builder.%s(source.readLong());", setF);
                break;
            case DOUBLE:
                writer.formatln("builder.%s(source.readDouble());", setF);
                break;
            case STRING:
                writer.formatln("builder.%s(source.readString());", setF);
                break;
            case BINARY:
                writer.formatln("builder.%s(Binary.wrap(source.createByteArray()));", setF);
                break;
            case ENUM:
                writer.formatln("builder.%s(%s.forValue(source.readInt()));", setF, classF);
                break;
            case MESSAGE:
                writer.formatln("builder.%s((%s) source.readTypedObject(%s.CREATOR));", setF, classF, classF);
                break;
            case LIST:
            case SET:
                PContainer<?, ?> cType = (PContainer<?, ?>) desc;
                String cItemClass = helper.getInstanceClassName(cType.itemDescriptor());
                switch (cType.itemDescriptor()
                             .getType()) {
                    case BOOL:
                        writer.formatln("builder.%s(source.createBooleanArray());", addToF);
                        break;
                    case BYTE:
                        writer.formatln("builder.%s(source.createByteArray());", addToF);
                        break;
                    case I16:
                        appendParcelableArrayReader("createIntArray", "int", addToF, "(short)");
                        break;
                    case I32:
                        writer.formatln("builder.%s(source.createIntArray());", addToF);
                        break;
                    case I64:
                        writer.formatln("builder.%s(source.createLongArray());", addToF);
                        break;
                    case DOUBLE:
                        writer.formatln("builder.%s(source.createDoubleArray());", addToF);
                        break;
                    case STRING:
                        writer.formatln("builder.%s(source.createStringArray());", addToF);
                        break;
                    case BINARY:
                        writer.formatln("final int len = source.readInt();")
                              .appendln("for (int i = 0; i < len; ++i) {")
                              .begin()
                              .formatln("builder.%s(Binary.wrap(source.createByteArray()));", addToF)
                              .end()
                              .appendln('}');
                        break;
                    case MESSAGE:
                        writer.formatln("builder.%s(source.createTypedArrayList(%s.CREATOR));",
                                        setF,
                                        cItemClass,
                                        cItemClass);
                        break;
                    case ENUM:
                        writer.appendln("int[] tmp = source.createIntArray();")
                              .formatln("%s[] values = new %s[tmp.length];", cItemClass, cItemClass)
                              .appendln("for (int i = 0; i < tmp.length; ++i) {")
                              .formatln("    values[i] = %s.forValue(tmp[i]);", cItemClass)
                              .appendln('}')
                              .formatln("builder.%s(values);", addToF);
                        break;
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is too complex to parse.", helper.getFieldType(cType));
                        break;
                }
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) desc;
                switch (mType.itemDescriptor()
                             .getType()) {
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is to complext to parse.", helper.getFieldType(mType));
                        return;
                }
                String mkClass = helper.getInstanceClassName(mType.keyDescriptor());
                String miClass = helper.getInstanceClassName(mType.itemDescriptor());

                switch (mType.keyDescriptor()
                             .getType()) {
                    case BOOL:
                        writer.appendln("boolean[] keys = source.createBooleanArray();");
                        break;
                    case BYTE:
                        writer.appendln("byte[] keys = source.createByteArray();");
                        break;
                    case I16:
                        writer.appendln("int[] key_tmp = source.createIntArray();")
                              .appendln("short[] keys = new short[key_tmp.length];")
                              .appendln("for (int i = 0; i < key_tmp.length; ++i) {")
                              .appendln("    keys[i] = (short) key_tmp[i];")
                              .appendln("}");
                        break;
                    case I32:
                        writer.appendln("int[] keys = source.createIntArray();");
                        break;
                    case I64:
                        writer.appendln("long[] keys = source.createLongArray();");
                        break;
                    case DOUBLE:
                        writer.appendln("double[] keys = source.createDoubleArray();");
                        break;
                    case STRING:
                        writer.appendln("String[] keys = source.createStringArray();");
                        break;
                    case BINARY:
                        writer.appendln("final int len = source.readInt();")
                              .appendln("Binary[] keys = new Binary[len];")
                              .appendln("for (int i = 0; i < len; ++i) {")
                              .formatln("    keys[i] = Binary.wrap(source.createByteArray());")
                              .appendln('}');
                        break;
                    case MESSAGE:
                        writer.formatln("%s[] keys = source.createTypedArray(%s.CREATOR);", mkClass, mkClass);
                        break;
                    case ENUM:
                        writer.appendln("int[] key_tmp = source.createIntArray();")
                              .formatln("%s[] keys = new %s[key_tmp.length];", mkClass, mkClass)
                              .appendln("for (int i = 0; i < key_tmp.length; ++i) {")
                              .formatln("    keys[i] = %s.forValue(key_tmp[i]);", mkClass)
                              .appendln('}');
                        break;
                    default:
                        throw new GeneratorException("Containers not allowed in map key.");
                }
                switch (mType.itemDescriptor()
                             .getType()) {
                    case BOOL:
                        writer.appendln("boolean[] values = source.createBooleanArray();");
                        break;
                    case BYTE:
                        writer.appendln("byte[] values = source.createByteArray();");
                        break;
                    case I16:
                        writer.appendln("int[] val_tmp = source.createIntArray();")
                              .appendln("short[] values = new short[val_tmp.length];")
                              .appendln("for (int i = 0; i < val_tmp.length; ++i) {")
                              .appendln("    values[i] = (short) val_tmp[i];")
                              .appendln("}");
                        break;
                    case I32:
                        writer.appendln("int[] values = source.createIntArray();");
                        break;
                    case I64:
                        writer.appendln("long[] values = source.createLongArray();");
                        break;
                    case DOUBLE:
                        writer.appendln("double[] values = source.createDoubleArray();");
                        break;
                    case STRING:
                        writer.appendln("String[] values = source.createStringArray();");
                        break;
                    case BINARY:
                        writer.appendln("Binary[] values = new Binary[keys.length];")
                              .appendln("for (int i = 0; i < keys.length; ++i) {")
                              .formatln("    values[i] = Binary.wrap(source.createByteArray());")
                              .appendln('}');
                        break;
                    case MESSAGE:
                        writer.formatln("%s[] values = source.createTypedArray(%s.CREATOR);", miClass, miClass);
                        break;
                    case ENUM:
                        writer.appendln("int[] val_tmp = source.createIntArray();")
                              .formatln("%s[] values = new %s[val_tmp.length];", miClass, miClass)
                              .appendln("for (int i = 0; i < val_tmp.length; ++i) {")
                              .formatln("    values[i] = %s.forValue(val_tmp[i]);", miClass)
                              .appendln('}');
                        break;
                    case LIST:
                    case SET:
                    case MAP:
                        // ... Handled.
                        break;
                }
                writer.formatln("for (int i = 0; i < keys.length; ++i) {")
                      .begin()
                      .formatln("builder.%s(keys[i], values[i]);", addToF)
                      .end()
                      .appendln('}');
                break;
        }
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
              .appendln("public void writeToParcel(Parcel dest, int flags) {")
              .begin();
        if (message.isUnion()) {
            writer.appendln("if (tUnionField != null) {")
                  .begin()
                  .appendln("switch (tUnionField) {")
                  .begin();
            for (JField field : message.fields()) {
                writer.formatln("case %s:", field.fieldEnum())
                      .begin()
                      .formatln("dest.writeInt(%d);", field.id());
                appendParcelableWriter(field.member(),
                                       field.getPField()
                                            .getDescriptor());
                writer.appendln("break;")
                      .end();
            }
            writer.end()
                  .appendln("}")
                  .end()
                  .appendln("}");
        } else {
            for (JField field : message.fields()) {
                if (!field.alwaysPresent()) {
                    switch (field.type()) {
                        case LIST:
                        case SET:
                        case MAP:
                            writer.formatln("if (%s() > 0) {", field.counter());
                            break;
                        default:
                            writer.formatln("if (%s()) {", field.presence());
                            break;
                    }
                    writer.begin();
                }
                writer.formatln("dest.writeInt(%d);", field.id());
                appendParcelableWriter(field.member(),
                                       field.getPField()
                                            .getDescriptor());
                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }
        }
        writer.appendln("dest.writeInt(0);")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final Parcelable.Creator<%s> CREATOR = new Parcelable.Creator<%s>() {",
                        message.instanceType(),
                        message.instanceType())
              .begin();

        writer.appendln("@Override")
              .formatln("public %s createFromParcel(Parcel source) {", message.instanceType())
              .begin()
              .appendln("_Builder builder = new _Builder();")
              .appendln("loop: while (source.dataAvail() > 0) {")
              .begin()
              .appendln("int field = source.readInt();")
              .appendln("switch (field) {")
              .begin()
              .appendln("case 0: break loop;");

        for (JField field : message.fields()) {
            writer.formatln("case %d: {", field.id())
                  .begin();

            appendParcelableReader(field.setter(),
                                   field.adder(),
                                   field.getPField()
                                        .getDescriptor());

            writer.appendln("break;")
                  .end()
                  .appendln('}');
        }

        writer.appendln("default: throw new IllegalArgumentException(\"Unknown field ID: \" + field);")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline()
              .appendln("return builder.build();")
              .end()
              .appendln('}')
              .newline();

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
