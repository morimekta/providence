/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.*;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.*;
import net.morimekta.providence.util.PTypeUtils;
import net.morimekta.providence.util.io.IndentedPrintWriter;
import net.morimekta.providence.util.json.JsonException;
import net.morimekta.providence.util.json.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static net.morimekta.providence.util.PStringUtils.c_case;
import static net.morimekta.providence.util.PStringUtils.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class Java2MessageFormatter {
    public static final String DBL_INDENT =
            IndentedPrintWriter.INDENT +
            IndentedPrintWriter.INDENT;

    private final Java2TypeHelper mTypeHelper;
    private final boolean         mAndroid;

    public Java2MessageFormatter(Java2TypeHelper helper,
                                 boolean android) {
        mTypeHelper = helper;
        mAndroid = android;
    }

    public void format(IndentedPrintWriter writer, PStructDescriptor<?,?> type) throws GeneratorException, IOException {
        appendFileHeader(writer, type);

        if (type.getComment() != null) {
            Java2Utils.appendBlockComment(writer, type.getComment());
            if (Java2Utils.hasDeprecatedAnnotation(type.getComment())) {
                writer.appendln(Java2Utils.DEPRECATED);
            }
        }

        appendClassDefinitionStart(writer, type);

        appendFieldDefaultConstants(writer, type);
        appendFieldDeclarations(writer, type);

        appendConstructor(writer, type);

        appendFieldGetters(writer, type);
        appendInheritedGetter_has(writer, type);
        appendInheritedGetter_num(writer, type);
        appendInheritedGetter_get(writer, type);

        appendObjectCompact(writer, type);
        appendObjectEquals(writer, type);
        appendObjectHashCode(writer, type);
        appendObjectToString(writer);

        appendIsValid(writer, type);

        appendFieldEnum(writer, type);
        appendDescriptor(writer, type);

        if (mAndroid) {
            appendParcelable(writer, type);
        }

        appendBuilder(writer, type);
        //  - mutate
        //  - _Builder class

        appendClassDefinitionEnd(writer);
    }

    private void appendObjectCompact(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        if (type.isCompactible()) {
            writer.appendln("@Override")
                  .appendln("public boolean isCompact() {")
                  .begin()
                  .appendln("boolean missing = false;");

            for (PField<?> field : type.getFields()) {
                writer.formatln("if (%s()) {", camelCase("has", field.getName()))
                      .begin()
                      .appendln("if (missing) return false;")
                      .end()
                      .appendln("} else {")
                      .begin()
                      .appendln("missing = true;")
                      .end()
                      .appendln('}');
            }

            writer.appendln("return true;")
                  .end()
                  .appendln('}')
                  .newline();
        } else {
            writer.appendln("@Override")
                  .appendln("public boolean isCompact() {")
                  .begin()
                  .appendln("return false;")
                  .end()
                  .appendln('}')
                  .newline();
        }
        writer.appendln("@Override")
                .appendln("public boolean isSimple() {")
                .begin()
                .appendln("return descriptor().isSimple();")
                .end()
                .appendln('}')
                .newline();
    }

    private void appendParcelableArrayConverter(IndentedPrintWriter writer, String srcType, String srcName, String destType, String itemConvert) {
        writer.formatln("%s[] arr = new %s[%s.size()];", destType, destType, srcName)
              .appendln("int pos = 0;")
              .formatln("for (%s item : %s) {", srcType, srcName)
              .begin()
              .formatln("arr[pos++] = %s;", itemConvert)
              .end()
              .appendln('}');
    }

    private void appendParcelableWriter(IndentedPrintWriter writer, String fName, PDescriptor<?> desc) throws GeneratorException {
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
                String iTypeName = mTypeHelper.getInstanceClassName(cType.itemDescriptor());
                switch (cType.itemDescriptor().getType()) {
                    case BOOL:
                        appendParcelableArrayConverter(writer, "Boolean", fName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter(writer, "Byte", fName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter(writer, "Short", fName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter(writer, "Integer", fName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter(writer, "Long", fName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter(writer, "Double", fName, "double", "item");
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
                        if (desc.getType().equals(PType.SET)) {
                            writer.formatln("dest.writeTypedList(new ArrayList<>(%s));",
                                            fName, iTypeName, fName);
                        } else {
                            writer.formatln("dest.writeTypedList(%s);",
                                            fName, iTypeName, fName);
                        }
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(writer, iTypeName, fName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is too complex to parse.", mTypeHelper.getFieldType(cType));
                        break;
                }
                break;
            case MAP:
                PMap<?,?> mType = (PMap<?, ?>) desc;
                switch (mType.itemDescriptor().getType()) {
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is to complext to parse.", mTypeHelper.getFieldType(mType));
                        return;
                }
                writer.appendln('{')
                      .begin();
                String kTypeName = mTypeHelper.getInstanceClassName(mType.keyDescriptor());
                iTypeName = mTypeHelper.getInstanceClassName(mType.itemDescriptor());
                String kName = fName + ".keySet()";
                switch (mType.keyDescriptor().getType()) {
                    case BOOL:
                        appendParcelableArrayConverter(writer, "Boolean", kName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter(writer, "Byte", kName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter(writer, "Short", kName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter(writer, "Integer", kName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter(writer, "Long", kName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter(writer, "Double", kName, "double", "item");
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
                                        kName, kTypeName, kName);
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(writer, kTypeName, kName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    default:
                        throw new GeneratorException("Map keys cannot contain containers.");
                }
                writer.end()
                      .appendln('}');
                String vName = fName + ".values()";
                switch (mType.itemDescriptor().getType()) {
                    case BOOL:
                        appendParcelableArrayConverter(writer, "Boolean", vName, "boolean", "item");
                        writer.appendln("dest.writeBooleanArray(arr);");
                        break;
                    case BYTE:
                        appendParcelableArrayConverter(writer, "Byte", vName, "byte", "item");
                        writer.appendln("dest.writeByteArray(arr);");
                        break;
                    case I16:
                        appendParcelableArrayConverter(writer, "Short", vName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I32:
                        appendParcelableArrayConverter(writer, "Integer", vName, "int", "item");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    case I64:
                        appendParcelableArrayConverter(writer, "Long", vName, "long", "item");
                        writer.appendln("dest.writeLongArray(arr);");
                        break;
                    case DOUBLE:
                        appendParcelableArrayConverter(writer, "Double", vName, "double", "item");
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
                                        vName, iTypeName, vName);
                        break;
                    case ENUM:
                        appendParcelableArrayConverter(writer, iTypeName, vName, "int", "item.getValue()");
                        writer.appendln("dest.writeIntArray(arr);");
                        break;
                    default:
                        throw new GeneratorException("Unknown map value type: " + mType.getName());
                }
                break;
        }
    }

    private void appendParcelableArrayReader(IndentedPrintWriter writer, String creator, String typeName, String addToF, String cast) {
        writer.formatln("%s[] tmp = source.%s();", typeName, creator)
              .appendln("for (int i = 0; i < tmp.length; ++i) {")
              .formatln("    builder.%s(%stmp[i]);", addToF, cast)
              .appendln('}');

    }

    private void appendParcelableReader(IndentedPrintWriter writer, String setF, String addToF, PDescriptor<?> desc) throws GeneratorException {
        String classF = mTypeHelper.getInstanceClassName(desc);

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
                writer.formatln("builder.%s(%s.forValue(source.readInt()));",
                                setF, classF);
                break;
            case MESSAGE:
                writer.formatln("builder.%s((%s) source.readTypedObject(%s.CREATOR));",
                                setF, classF, classF);
                break;
            case LIST:
            case SET:
                PContainer<?, ?> cType = (PContainer<?, ?>) desc;
                String cItemClass = mTypeHelper.getInstanceClassName(cType.itemDescriptor());
                switch (cType.itemDescriptor().getType()) {
                    case BOOL:
                        writer.formatln("builder.%s(source.createBooleanArray());", addToF);
                        break;
                    case BYTE:
                        writer.formatln("builder.%s(source.createByteArray());", addToF);
                        break;
                    case I16:
                        appendParcelableArrayReader(writer, "createIntArray", "int", addToF, "(short)");
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
                        writer.formatln(
                                "builder.%s(source.createTypedArrayList(%s.CREATOR));",
                                setF, cItemClass, cItemClass);
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
                        writer.formatln("// %s is too complex to parse.", mTypeHelper.getFieldType(cType));
                        break;
                }
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) desc;
                switch (mType.itemDescriptor().getType()) {
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("// %s is to complext to parse.", mTypeHelper.getFieldType(mType));
                        return;
                }
                String mkClass = mTypeHelper.getInstanceClassName(mType.keyDescriptor());
                String miClass = mTypeHelper.getInstanceClassName(mType.itemDescriptor());

                switch (mType.keyDescriptor().getType()) {
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
                switch (mType.itemDescriptor().getType()) {
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

    private void appendParcelable(IndentedPrintWriter writer, PStructDescriptor<?,?> type) throws GeneratorException {
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
        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());
            switch (field.getDescriptor().getType()) {
                case LIST:
                case SET:
                case MAP:
                    writer.formatln("if (%s() > 0) {", camelCase("num", field.getName()));
                    break;
                default:
                    writer.formatln("if (%s()) {", camelCase("has", field.getName()));
                    break;
            }
            writer.begin()
                  .formatln("dest.writeInt(%d);", field.getKey());
            appendParcelableWriter(writer, fName, field.getDescriptor());
            writer.end()
                  .appendln('}');
        }
        writer.appendln("dest.writeInt(0);")
              .end()
              .appendln('}')
              .newline();

        String simpleClass = mTypeHelper.getInstanceClassName(type);

        writer.formatln(
                "public static final Parcelable.Creator<%s> CREATOR = new Parcelable.Creator<%s>() {",
                simpleClass,
                simpleClass)
              .begin();

        writer.appendln("@Override")
              .formatln("public %s createFromParcel(Parcel source) {", simpleClass)
              .begin()
              .appendln("_Builder builder = new _Builder();")
              .appendln("loop: while (source.dataAvail() > 0) {")
              .begin()
              .appendln("int field = source.readInt();")
              .appendln("switch (field) {")
              .begin()
              .appendln("case 0: break loop;");

        for (PField<?> field : type.getFields()) {
            writer.formatln("case %d: {", field.getKey()).begin();

            String setF = camelCase("set", field.getName());
            String addToF = camelCase("addTo", field.getName());

            appendParcelableReader(writer, setF, addToF, field.getDescriptor());

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
              .formatln("public %s[] newArray(int size) {", simpleClass)
              .begin()
              .formatln("return new %s[size];", simpleClass)
              .end()
              .appendln('}');

        writer.end()
              .appendln("};")
              .newline();
    }

    private void appendBuilder(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        boolean union = type.getVariant().equals(PMessageVariant.UNION);

        String simpleClass = mTypeHelper.getInstanceClassName(type);
        writer.appendln("@Override")
              .appendln("public _Builder mutate() {")
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

        writer.appendln("public static class _Builder")
              .begin()
              .formatln("    extends PMessageBuilder<%s> {", simpleClass);
        for (PField<?> field : type.getFields()) {
            writer.formatln("private %s %s;",
                            mTypeHelper.getFieldType(field.getDescriptor()),
                            camelCase("m", field.getName()));
        }
        if (union) {
            writer.appendln("private _Field tUnionField;").newline();
        }

        writer.newline()
              .appendln("public _Builder() {")
              .begin();
        for (PField<?> field : type.getFields()) {
            switch (field.getDescriptor().getType()) {
                case MAP:
                case SET:
                case LIST:
                    writer.formatln("%s = new %s<>();",
                                    camelCase("m", field.getName()),
                                    mTypeHelper.getInstanceClassName(field.getDescriptor()));
                    break;
                default:
                    break;
            }
        }
        // _Builder - default constructor
        writer.end()
              .appendln('}')
              .newline();

        writer.formatln("public _Builder(%s base) {", simpleClass)
              .begin()
              .appendln("this();")
              .newline();
        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());
            switch (field.getDescriptor().getType()) {
                case LIST:
                case SET:
                    writer.formatln("%s.addAll(base.%s);", fName, fName);
                    break;
                case MAP:
                    writer.formatln("%s.putAll(base.%s);", fName, fName);
                    break;
                default:
                    writer.formatln("%s = base.%s;", fName, fName);
                    break;
            }
        }
        if (union) {
            writer.newline()
                  .appendln("tUnionField = base.tUnionField;");
        }

        // _Builder - mutate constructor
        writer.end()
              .appendln('}')
              .newline();

        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());
            String fEnumName = c_case("", field.getName()).toUpperCase();
            String vType = mTypeHelper.getValueType(field.getDescriptor());
            switch (field.getDescriptor().getType()) {
                case MAP:
                    PMap<?, ?> mType = (PMap<?, ?>) field.getDescriptor();
                    String mkType = mTypeHelper.getFieldType(mType.keyDescriptor());
                    String miType = mTypeHelper.getFieldType(mType.itemDescriptor());
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(Map<%s,%s> value) {",
                                    camelCase("set", field.getName()),
                                    mkType, miType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("%s.clear();", fName)
                          .formatln("%s.putAll(value);", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(%s key, %s value) {",
                                    camelCase("addTo", field.getName()),
                                    mkType, miType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("%s.put(key, value);", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();

                    writer.formatln("public _Builder %s() {",
                                    camelCase("clear", field.getName()))
                          .begin();
                    if (union) {
                        writer.formatln("if (%s.size() > 0) tUnionField = null;", fName);
                    }
                    writer.formatln("%s.clear();", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    break;
                case SET:
                case LIST:
                    PContainer<?, ?> lType = (PContainer<?, ?>) field.getDescriptor();
                    String liType = mTypeHelper.getFieldType(lType.itemDescriptor());
                    String liValueType = mTypeHelper.getValueType(lType.itemDescriptor());
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(Collection<%s> value) {",
                                    camelCase("set", field.getName()),
                                    liType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("%s.clear();", fName)
                          .formatln("%s.addAll(value);", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(%s... values) {",
                                    camelCase("addTo", field.getName()),
                                    liValueType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("for (%s item : values) {", liType)
                          .begin()
                          .formatln("%s.add(item);", fName)
                          .end()
                          .appendln('}')
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    writer.formatln("public _Builder %s() {",
                                    camelCase("clear", field.getName()))
                          .begin();
                    if (union) {
                        writer.formatln("if (%s.size() > 0) tUnionField = null;", fName);
                    }
                    writer.formatln("%s.clear();", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    break;
                case BINARY:
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(%s value) {",
                                    camelCase("set", field.getName()), vType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("%s = value;", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    writer.formatln("public _Builder %s() {",
                                    camelCase("clear", field.getName()))
                          .begin();
                    if (union) {
                        writer.formatln("if (%s != null) tUnionField = null;", fName);
                    }
                    writer.formatln("%s = null;", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    break;
                default:
                    if (field.getComment() != null) {
                        Java2Utils.appendBlockComment(writer, field.getComment());
                        if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                            writer.appendln(Java2Utils.DEPRECATED);
                        }
                    }
                    writer.formatln("public _Builder %s(%s value) {",
                                    camelCase("set", field.getName()), vType)
                          .begin();
                    if (union) {
                        writer.formatln("tUnionField = _Field.%s;", fEnumName);
                    }
                    writer.formatln("%s = value;", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    writer.formatln("public _Builder %s() {",
                                    camelCase("clear", field.getName()))
                          .begin();
                    if (union) {
                        writer.formatln("if (%s != null) tUnionField = null;", fName);
                    }
                    writer.formatln("%s = null;", fName)
                          .appendln("return this;")
                          .end()
                          .appendln('}')
                          .newline();
                    break;
            }
        }

        writer.appendln("@Override")
              .appendln("public _Builder set(int key, Object value) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (PField<?> field : type.getFields()) {
            writer.formatln("case %d: %s((%s) value); break;",
                            field.getKey(),
                            camelCase("set", field.getName()),
                            mTypeHelper.getValueType(field.getDescriptor()));
        }
        writer.end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        appendIsValid(writer, type);

        if (type.getVariant().equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("protected String createMessage() {")
                  .begin()
                  .appendln("StringBuilder builder = new StringBuilder();")
                  .appendln("builder.append('{');")
                  .appendln("boolean first = true;");
            for (PField<?> field : type.getFields()) {
                String fName = camelCase("m", field.getName());
                writer.formatln("if (%s != null) {", fName)
                      .begin()
                      .appendln("if (first) first = false;")
                      .appendln("else builder.append(',');")
                      .formatln("builder.append(\"%s:\");", field.getName());
                switch (field.getDescriptor().getType()) {
                    case LIST:
                    case SET:
                    case MAP:
                        writer.formatln("builder.append(PTypeUtils.toString(%s));", fName);
                        break;
                    case MESSAGE:
                        writer.formatln("builder.append(PTypeUtils.toString(%s));", fName);
                        break;
                    case STRING:
                        writer.formatln("builder.append(%s);", fName);
                        break;
                    case BINARY:
                        writer.formatln("builder.append(PTypeUtils.toString(%s));",
                                        fName);
                        break;
                    case ENUM:
                        writer.formatln("builder.append(%s.toString());", fName);
                        break;
                    default:
                        writer.formatln("builder.append(%s.toString());", fName);
                        break;
                }
                writer.end()
                      .appendln('}');
            }
            writer.appendln("builder.append('}');")
                  .appendln("return builder.toString();")
                  .end()
                  .appendln('}')
                  .newline();
        }

        writer.appendln("@Override")
              .formatln("public %s build() {", simpleClass)
              .begin()
              .formatln("return new %s(this);", simpleClass)
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');
    }

    private void appendFieldEnum(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("public enum _Field implements PField {")
              .begin();

        for (PField<?> field : type.getFields()) {
            String name = c_case("", field.getName()).toUpperCase(Locale.ENGLISH);
            String provider = mTypeHelper.getProviderName(field.getDescriptor());
            String defValue = "null";
            if (field.hasDefaultValue()) {
                defValue = String.format("new PDefaultValueProvider<>(%s)",
                                         camelCase("kDefault", field.getName()));
            }

            writer.formatln("%s(%d, %s, \"%s\", %s, %s),",
                            name,
                            field.getKey(),
                            field.getRequired() ? "true" : "false",
                            field.getName(),
                            provider,
                            defValue);
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mKey;")
              .appendln("private final boolean mRequired;")
              .appendln("private final String mName;")
              .appendln("private final PDescriptorProvider<?> mTypeProvider;")
              .appendln("private final PValueProvider<?> mDefaultValue;")
              .newline()
              .appendln(
                      "_Field(int key, boolean required, String name, PDescriptorProvider<?> typeProvider, PValueProvider<?> defaultValue) {")
              .begin()
              .appendln("mKey = key;")
              .appendln("mRequired = required;")
              .appendln("mName = name;")
              .appendln("mTypeProvider = typeProvider;")
              .appendln("mDefaultValue = defaultValue;")
              .end()
              .appendln('}')
              .newline();
        writer.appendln("@Override")
              .appendln("public String getComment() { return null; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public int getKey() { return mKey; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public boolean getRequired() { return mRequired; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public PType getType() { return getDescriptor().getType(); }")
              .newline();
        writer.appendln("@Override")
              .appendln("public PDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }")
              .newline();
        writer.appendln("@Override")
              .appendln("public String getName() { return mName; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public boolean hasDefaultValue() { return mDefaultValue != null; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public Object getDefaultValue() {")
              .appendln("    return hasDefaultValue() ? mDefaultValue.get() : null;")
              .appendln('}')
              .newline();

        String simpleClass = mTypeHelper.getInstanceClassName(type);

        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .appendln("StringBuilder builder = new StringBuilder();")
              .formatln("builder.append(%s.class.getSimpleName())", simpleClass)
              .appendln("       .append('{')")
              .appendln("       .append(mKey)")
              .appendln("       .append(\": \");")
              .appendln("if (mRequired) {")
              .appendln("    builder.append(\"required \");")
              .appendln("}")
              .appendln("builder.append(getDescriptor().getQualifiedName(null))")
              .appendln("       .append(' ')")
              .appendln("       .append(mName)")
              .appendln("       .append('}');")
              .appendln("return builder.toString();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forKey(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (PField<?> field : type.getFields()) {
            writer.formatln("case %d: return _Field.%s;",
                            field.getKey(), c_case("", field.getName()).toUpperCase());
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forName(String name) {")
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (PField<?> field : type.getFields()) {
            writer.formatln("case \"%s\": return _Field.%s;",
                            field.getName(), c_case("", field.getName()).toUpperCase());
        }
        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(IndentedPrintWriter writer, PStructDescriptor<?,?> type) throws GeneratorException {
        String simpleClass = mTypeHelper.getInstanceClassName(type);
        String typeClass;
        switch (type.getVariant()) {
            case STRUCT:
                typeClass = PStructDescriptor.class.getSimpleName();
                break;
            case UNION:
                typeClass = PUnionDescriptor.class.getSimpleName();
                break;
            case EXCEPTION:
                typeClass = PExceptionDescriptor.class.getSimpleName();
                break;
            default:
                throw new GeneratorException("Unable to determine type class for " + type.getVariant());
        }

        writer.formatln("public static %sProvider<%s,_Field> provider() {", typeClass, simpleClass)
              .begin()
              .formatln("return new _Provider();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final %s<%s,_Field> kDescriptor;", typeClass, simpleClass)
              .newline();

        writer.formatln("private static class _Descriptor")
              .formatln("        extends %s<%s,_Field> {", typeClass, simpleClass)
              .begin()
              .appendln("public _Descriptor() {")
              .begin();
        if (type.getVariant().equals(PMessageVariant.STRUCT)) {
            writer.formatln("super(null, \"%s\", \"%s\", new _Factory(), %b, %b);",
                            type.getPackageName(),
                            type.getName(),
                            type.isSimple(),
                            type.isCompactible());
        } else {
            writer.formatln("super(null, \"%s\", \"%s\", new _Factory(), %s);",
                            type.getPackageName(),
                            type.getName(),
                            type.isSimple());
        }
        writer.end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field[] getFields() {")
              .begin()
              .appendln("return _Field.values();")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field getField(String name) {")
              .begin()
              .appendln("return _Field.forName(name);")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field getField(int key) {")
              .begin()
              .appendln("return _Field.forKey(key);")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("static {", typeClass, simpleClass)
              .begin()
              .appendln("kDescriptor = new _Descriptor();")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("private final static class _Provider extends %sProvider<%s,_Field> {", typeClass, simpleClass)
              .begin()
              .appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .end()
              .appendln("}")
              .newline();

        writer.appendln("private final static class _Factory")
              .begin()
              .formatln("    extends PMessageBuilderFactory<%s> {", simpleClass)
              .appendln("@Override")
              .appendln("public _Builder builder() {")
              .begin()
              .appendln("return new _Builder();")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendIsValid(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@Override")
              .appendln("public boolean isValid() {")
              .begin()
              .appendln("return ")
              .begin("       ");
        if (type.getVariant().equals(PMessageVariant.UNION)) {
            boolean first = true;
            for (PField<?> field : type.getFields()) {
                if (first)
                    first = false;
                else
                    writer.append(" +").appendln("");
                writer.format("(%s != null ? 1 : 0)", camelCase("m", field.getName()));
            }
            writer.append(" == 1");
        } else {
            boolean first = true;
            for (PField<?> field : type.getFields()) {
                if (field.getRequired()) {
                    if (first)
                        first = false;
                    else
                        writer.append(" &&").appendln("");
                    writer.format("%s != null", camelCase("m", field.getName()));
                }
            }
            if (first) {
                writer.append("true");
            }
        }
        writer.end()  // alignment indent
                .append(';')
                .end()
                .appendln('}')
                .newline();
    }

    private void appendObjectToString(IndentedPrintWriter writer) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .appendln("return descriptor().getQualifiedName(null) + PTypeUtils.toString(this);")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendObjectEquals(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        String typeName = mTypeHelper.getInstanceClassName(type);
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .formatln("if (o == null || !(o instanceof %s)) return false;", typeName);
        if (type.getFields().length > 0) {
            boolean first = true;
            writer.formatln("%s other = (%s) o;", typeName, typeName)
                  .appendln("return ");
            for (PField<?> field : type.getFields()) {
                if (first)
                    first = false;
                else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                String fName = camelCase("m", field.getName());
                writer.format("PTypeUtils.equals(%s, other.%s)", fName, fName);
            }
            writer.append(';');
        } else {
            writer.appendln("return true;");
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendObjectHashCode(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@Override")
              .appendln("public int hashCode() {")
              .begin()
              .formatln("return %s.class.hashCode()", mTypeHelper.getInstanceClassName(type))
              .begin("       ");
        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());
            String fEnum = c_case("", field.getName()).toUpperCase();
            writer.append(" +")
                  .formatln("PTypeUtils.hashCode(_Field.%s,%s)", fEnum, fName);
        }
        writer.end()
              .append(";")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendInheritedGetter_has(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@Override")
              .appendln("public boolean has(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (PField<?> field : type.getFields()) {
            switch (field.getDescriptor().getType()) {
                case LIST:
                case MAP:
                case SET:
                    writer.formatln("case %d: return %s() > 0;",
                                    field.getKey(),
                                    camelCase("num", field.getName()));
                    break;
                default:
                    writer.formatln("case %d: return %s();",
                                    field.getKey(),
                                    camelCase("has", field.getName()));
                    break;
            }
        }

        writer.appendln("default: return false;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendInheritedGetter_num(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@Override")
              .appendln("public int num(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (PField<?> field : type.getFields()) {
            switch (field.getDescriptor().getType()) {
                case LIST:
                case MAP:
                case SET:
                    writer.formatln("case %d: return %s();",
                                    field.getKey(),
                                    camelCase("num", field.getName()));
                    break;
                default:
                    writer.formatln("case %d: return %s() ? 1 : 0;",
                                    field.getKey(),
                                    camelCase("has", field.getName()));
                    break;
            }
        }

        writer.appendln("default: return 0;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendInheritedGetter_get(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@Override")
              .appendln("public Object get(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (PField<?> field : type.getFields()) {
            writer.formatln("case %d: return %s();",
                            field.getKey(), camelCase("get", field.getName()));
        }

        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendFieldDefaultConstants(IndentedPrintWriter writer, PStructDescriptor<?,?> type)
            throws GeneratorException {
        boolean hasDefault = false;
        for (PField<?> field : type.getFields()) {
            Object defaultValue = mTypeHelper.getDefaultValue(field);
            if (defaultValue != null) {
                hasDefault = true;
                writer.formatln("private final static %s %s = ",
                                mTypeHelper.getValueType(field.getDescriptor()),
                                camelCase("kDefault", field.getName()))
                      .begin(DBL_INDENT);
                appendTypedValue(writer, defaultValue, field.getDescriptor());
                writer.append(";")
                      .end();
            }
        }
        if (hasDefault) {
            writer.newline();
        }
    }

    private void appendFieldGetters(IndentedPrintWriter writer, PStructDescriptor<?,?> type) throws GeneratorException {
        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());

            switch (field.getDescriptor().getType()) {
                case LIST:
                case SET:
                case MAP:
                    writer.formatln("public int %s() {", camelCase("num", field.getName()))
                          .begin()
                          .formatln("return %s.size();", fName)
                          .end()
                          .appendln('}')
                          .newline();
                    break;
                default:
                    writer.formatln("public boolean %s() {", camelCase("has", field.getName()))
                          .begin()
                          .formatln("return %s != null;", fName)
                          .end()
                          .appendln('}')
                          .newline();
                    break;
            }
            if (field.getComment() != null) {
                Java2Utils.appendBlockComment(writer, field.getComment());
                if (Java2Utils.hasDeprecatedAnnotation(field.getComment())) {
                    writer.appendln(Java2Utils.DEPRECATED);
                }
            }
            writer.formatln("public %s %s() {",
                            mTypeHelper.getValueType(field.getDescriptor()),
                            camelCase("get", field.getName()))
                  .begin();

            Object defaultValue = mTypeHelper.getDefaultValue(field);
            if (defaultValue != null) {
                writer.formatln("return %s() ? %s : %s;",
                        camelCase("has", field.getName()),
                        fName,
                        camelCase("kDefault", field.getName()));
            } else {
                writer.formatln("return %s;",
                        camelCase("m", field.getName()));
            }
            writer.end()
                  .appendln('}')
                  .newline();
        }

        if (type.getVariant().equals(PMessageVariant.UNION)) {
            writer.appendln("public _Field unionField() {")
                  .begin()
                  .appendln("return tUnionField;")
                  .end()
                  .appendln('}')
                  .newline();
        }
    }

    private void appendTypedValue(IndentedPrintWriter writer,
                                  Object defaultValue,
                                  PDescriptor type)
            throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                writer.append(defaultValue.toString());
                break;
            case BYTE:
                writer.append("(byte)").append(defaultValue.toString());
                break;
            case I16:
                writer.append("(short)").append(defaultValue.toString());
                break;
            case I32:
                writer.append(defaultValue.toString());
                break;
            case I64:
                writer.append(defaultValue.toString()).append("L");
                break;
            case DOUBLE:
                writer.append(defaultValue.toString()).append("d");
                break;
            case BINARY:
                writer.append("Binary.wrap(new byte[]{");
                byte[] bytes = (byte[]) defaultValue;
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
                    json.value(defaultValue.toString());
                    json.flush();
                } catch (JsonException je) {
                    throw new GeneratorException("Unable to format string value");
                }
                break;
            case ENUM:
                writer.format("%s.%s", mTypeHelper.getInstanceClassName(type), defaultValue.toString());
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

    private void appendFieldDeclarations(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        for (PField<?> field : type.getFields()) {
            writer.formatln("private final %s %s;",
                            mTypeHelper.getFieldType(field.getDescriptor()),
                            camelCase("m", field.getName()));
        }
        if (type.getVariant().equals(PMessageVariant.UNION)) {
            writer.appendln("private final _Field tUnionField;")
                  .newline();
        }
        writer.newline();
    }

    private void appendConstructor(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.formatln("private %s(_Builder builder) {", mTypeHelper.getInstanceClassName(type))
              .begin();
        if (type.getVariant().equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("super(builder.createMessage());")
                  .newline();
        }
        for (PField<?> field : type.getFields()) {
            String fName = camelCase("m", field.getName());
            switch (field.getDescriptor().getType()) {
                case LIST:
                    writer.formatln("%s = Collections.unmodifiableList(new %s<>(builder.%s));",
                                    fName, mTypeHelper.getInstanceClassName(field.getDescriptor()), fName);
                    break;
                case SET:
                    writer.formatln("%s = Collections.unmodifiableSet(new %s<>(builder.%s));",
                                    fName, mTypeHelper.getInstanceClassName(field.getDescriptor()), fName);
                    break;
                case MAP:
                    writer.formatln("%s = Collections.unmodifiableMap(new %s<>(builder.%s));",
                                    fName, mTypeHelper.getInstanceClassName(field.getDescriptor()), fName);
                    break;
                default:
                    writer.formatln("%s = builder.%s;", fName, fName);
                    break;
            }
        }
        if (type.getVariant().equals(PMessageVariant.UNION)) {
            writer.newline()
                  .appendln("tUnionField = builder.tUnionField;");
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendClassDefinitionStart(IndentedPrintWriter writer, PStructDescriptor<?,?> type) {
        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s", mTypeHelper.getInstanceClassName(type))
              .begin(DBL_INDENT);
        if (type.getVariant().equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("extends PException");
        }
        writer.formatln("implements PMessage<%s>, Serializable", mTypeHelper.getInstanceClassName(type));
        if (mAndroid) {
            writer.format(", Parcelable");
        }
        writer.append(" {")
                .end()  // double indent.
                .begin();

        if (type.getVariant().equals(PMessageVariant.EXCEPTION)) {
            writer.formatln("private final static long serialVersionUID = %dL;",
                            Java2Utils.generateSerialVersionUID(type))
                  .newline();
        }
    }

    private void appendClassDefinitionEnd(IndentedPrintWriter writer) {
        writer.end()
              .appendln('}')
              .newline();
    }

    private void addTypeImports(Java2HeaderFormatter header, PDescriptor<?> descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case ENUM:
            case MESSAGE:
                // Avoid never-ending recursion (with circular contained
                // structs) by stopping on already included structs and enums.
                header.include(mTypeHelper.getQualifiedInstanceClassName(descriptor));
                header.include(mTypeHelper.getQualifiedValueTypeName(descriptor));
                break;
            case LIST:
                PContainer<?, ?> lType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PList.class.getName());
                header.include(mTypeHelper.getQualifiedInstanceClassName(descriptor));
                header.include(mTypeHelper.getQualifiedValueTypeName(descriptor));
                addTypeImports(header, lType.itemDescriptor());
                break;
            case SET:
                PContainer<?, ?> sType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PSet.class.getName());
                header.include(mTypeHelper.getQualifiedInstanceClassName(descriptor));
                header.include(mTypeHelper.getQualifiedValueTypeName(descriptor));
                if (mAndroid) {
                    header.include(ArrayList.class.getName());
                }
                addTypeImports(header, sType.itemDescriptor());
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) descriptor;
                header.include(java.util.Collections.class.getName());
                header.include(PMap.class.getName());
                header.include(mTypeHelper.getQualifiedInstanceClassName(descriptor));
                header.include(mTypeHelper.getQualifiedValueTypeName(descriptor));
                header.include(mTypeHelper.getQualifiedInstanceClassName(mType.itemDescriptor()));
                header.include(mTypeHelper.getQualifiedInstanceClassName(mType.keyDescriptor()));
                addTypeImports(header, mType.keyDescriptor());
                addTypeImports(header, mType.itemDescriptor());
                break;
            case BINARY:
                header.include(Arrays.class.getName());
                header.include(PPrimitive.class.getName());
                header.include(Binary.class.getName());
                break;
            default:
                header.include(PPrimitive.class.getName());
                break;
        }
    }

    private void appendFileHeader(IndentedPrintWriter writer, PStructDescriptor<?,?> type)
            throws GeneratorException, IOException {
        Java2HeaderFormatter header = new Java2HeaderFormatter(mTypeHelper.getJavaPackage(type));
        header.include(java.io.Serializable.class.getName());
        header.include(PMessage.class.getName());
        header.include(PMessageBuilder.class.getName());
        header.include(PMessageBuilderFactory.class.getName());
        header.include(PField.class.getName());
        header.include(PTypeUtils.class.getName());
        header.include(PType.class.getName());
        header.include(PDescriptorProvider.class.getName());
        header.include(PValueProvider.class.getName());
        header.include(PDescriptor.class.getName());
        switch (type.getVariant()) {
            case STRUCT:
                header.include(PStructDescriptor.class.getName());
                header.include(PStructDescriptorProvider.class.getName());
                break;
            case UNION:
                header.include(PUnionDescriptor.class.getName());
                header.include(PUnionDescriptorProvider.class.getName());
                break;
            case EXCEPTION:
                header.include(PException.class.getName());
                header.include(PExceptionDescriptor.class.getName());
                header.include(PExceptionDescriptorProvider.class.getName());
                break;
        }
        for (PField<?> field : type.getFields()) {
            addTypeImports(header, field.getDescriptor());
            if (field.hasDefaultValue()) {
                header.include(PDefaultValueProvider.class.getName());
            }
        }
        if (mAndroid) {
            header.include("android.os.Parcel");
            header.include("android.os.Parcelable");
        }

        header.format(writer);
    }
}
