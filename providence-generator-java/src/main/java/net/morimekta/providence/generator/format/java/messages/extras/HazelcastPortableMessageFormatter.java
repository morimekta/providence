package net.morimekta.providence.generator.format.java.messages.extras;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.program.extras.HazelcastPortableProgramFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.Binary;
import net.morimekta.util.BinaryUtil;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastClassId;
import static net.morimekta.providence.generator.format.java.utils.JUtils.getHazelcastFactory;

/**
 * Formatter to handle hazelcast_portable formatting of Portable Implementation
 * See <a href="http://docs.hazelcast_portable.org/docs/3.5/manual/html/portableserialization.html">Hazelcast.org</a>
 *
 * @author andreas@zedge.net
 */
public class HazelcastPortableMessageFormatter implements MessageMemberFormatter {

    public static final String WRAPPER_CLASS_NAME = "_Builder";

    private static final String PORTABLE_WRITER = "portableWriter";
    private static final String PORTABLE_READER = "portableReader";

    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    private Integer uniqueVariable;

    public HazelcastPortableMessageFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
        this.uniqueVariable = 0;
    }

    private String tempVariable(String name) {
        return "tmp_" + name + "_" + (++uniqueVariable);
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        if (message.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
            return ImmutableList.of(Portable.class.getName());
        } else {
            return new LinkedList<>();
        }
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        if (message.hasAnnotation(ThriftAnnotation.JAVA_HAZELCAST_CLASS_ID)) {
            appendFactoryId(message);
            appendClassId(message);
            appendPortableWriter(message);
            appendPortableReader(message);
        }
    }

    /**
     * Method to append get factory id from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     * {@code
     * public int getFactoryId() {
     *   return ContentCmsPortableFactory.FACTORY_ID;
     * }
     * }
     * </pre>
     */
    private void appendFactoryId(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int getFactoryId() {")
              .begin()
              //TODO: The factory should be file unqiue. ID is struct unique so for id we want to define several constants.
              // so for content_cms we want the factory name ContentCmsFactory or ContentCmsPortableFactory.
              // as well as some way to count this up for each struct that has a hazelcast_portable tag in it.
              .formatln("return %s.%s;",
                        getHazelcastFactory(message.descriptor()),
                        HazelcastPortableProgramFormatter.FACTORY_ID)
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append get class id from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     * {@code
     * public int getClassId() {
     *   return ContentCmsPortableFactory.CREATE_CONTENT_ID;
     * }
     * }
     * </pre>
     */
    private void appendClassId(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int getClassId() {")
              .begin()
              //TODO: Need to add method to create a constant for the description or struct here.
              .formatln("return %s.%s;",
                        getHazelcastFactory(message.descriptor()),
                        getHazelcastClassId(message.descriptor()))
              .end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append writePortable from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     * {@code
     * public void writePortable(com.hazelcast.nio.serialization.PortableWriter portableWriter) throws java.io.IOException {
     *   ...
     *   portableWriter.writeByteArray("__hzOptionalsForClassOptionalFields", optionals.toByteArray());
     * }
     * }
     * </pre>
     */
    private void appendPortableWriter(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void writePortable(%s %s) throws %s {",
                        PortableWriter.class.getName(),
                        PORTABLE_WRITER,
                        IOException.class.getName())
              .begin();
        // TODO: This should be short[] instead, as field IDs are restricted to 16bit.
        writer.appendln("int[] setFields = presentFields().stream()")
              .appendln("                                 .mapToInt(_Field::getId)")
              .appendln("                                 .toArray();")
              .appendln("portableWriter.writeIntArray(\"__fields__\", setFields);");
        for (JField field : message.declaredOrderFields()) {
            if (!field.alwaysPresent()) {
                writer.formatln("if( %s() ) {", field.isSet())
                      .begin();
            }
            writePortableField(field);
            if (!field.alwaysPresent()) {
                writer.end()
                      .appendln("} else {")
                      .begin();
                writeDefaultPortableField(field);
                writer.end()
                      .appendln("}");
            }
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    /**
     * Method to append readPortable from hazelcast_portable.
     *
     * @param message JMessage with the information.
     * <pre>
     * {@code
     * public void readPortable(com.hazelcast.nio.serialization.PortableReader portableReader) throws java.io.IOException {
     *   java.util.BitSet __temp_optionals = java.util.BitSet.valueOf(portableReader.readByteArray("__hzOptionalsForClassOptionalFields"));
     *   ...
     * }
     * }
     * </pre>
     */
    private void appendPortableReader(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void readPortable(%s %s) throws %s {",
                        PortableReader.class.getName(),
                        PORTABLE_READER,
                        IOException.class.getName())
              .begin();
        // TODO: This should be short[] instead, as field IDs are restricted to 16bit.
        writer.formatln("int[] field_ids = %s.readIntArray(\"__fields__\");", PORTABLE_READER)
              .appendln()
              .appendln("for (int id : field_ids) {")
              .begin()
              .appendln("switch (id) {")
              .begin();
        for (JField field : message.declaredOrderFields()) {
            writer.formatln("case %d: {", field.id())
                  .begin();
            readPortableField(field);
            writer.appendln("break;")
                  .end()
                  .appendln("}");
        }
        writer.end()
              .appendln("}") // switch
              .end()
              .appendln("}")  // for loop
              .end()
              .appendln("}") // readPortable
              .newline();
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     *   // for required fields.
     *   portableWriter.writeInt("id", mId);
     *
     *   // for optional fields.
     *   if( isSetLabel() ) {
     *     portableWriter.writeUTF("label", mLabel);
     *   }
     * }
     * </pre>
     */
    private void writePortableField(JField field) throws GeneratorException {
        if (field.portableRequiresBinarySerialization()) {
            String baosTemp = tempVariable("baos");
            String bebwTemp = tempVariable("bebw");

            writer.formatln("try (%s %s = new %s();",
                            ByteArrayOutputStream.class.getName(),
                            baosTemp,
                            ByteArrayOutputStream.class.getName())
                  .formatln("     %s %s = new %s(%s) ) {",
                            BigEndianBinaryWriter.class.getName(),
                            bebwTemp,
                            BigEndianBinaryWriter.class.getName(),
                            baosTemp)
                  .begin();

            if (field.type() == PType.MESSAGE) {
                String tempVar = tempVariable("message");
                writer.formatln("%s %s = %s_builder != null ? %s_builder.build() : %s;",
                                field.fieldType(), tempVar, field.member(), field.member(), field.member());
                writePortableBinary(bebwTemp, tempVar, field.field().getDescriptor());
            } else {
                writePortableBinary(bebwTemp, field.member(), field.field().getDescriptor());
            }

            writer.formatln("%s.writeByteArray(\"%s\", %s.toByteArray());", PORTABLE_WRITER, field.name(), baosTemp)
                  .end()
                  .println("}");

            return;
        }

        switch (field.type()) {
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s.get());", PORTABLE_WRITER, field.name(), field.member());
                break;
            case BOOL:
                writer.formatln("%s.writeBoolean(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case BYTE:
                writer.formatln("%s.writeByte(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDouble(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case ENUM:
                writer.formatln("%s.writeInt(\"%s\", %s.asInteger());", PORTABLE_WRITER, field.name(), field.member());
                break;
            case I16:
                writer.formatln("%s.writeShort(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case I32:
                writer.formatln("%s.writeInt(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case I64:
                writer.formatln("%s.writeLong(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case STRING:
                writer.formatln("%s.writeUTF(\"%s\", %s);", PORTABLE_WRITER, field.name(), field.member());
                break;
            case LIST:
                writePortableFieldList(field,
                                       field.toPList()
                                            .itemDescriptor());
                break;
            case SET:
                writePortableFieldList(field,
                                       field.toPSet()
                                            .itemDescriptor());
                break;
            case MESSAGE: {
                writer.formatln("if (%s_builder != null) {", field.member())
                      .formatln("    %s.writePortable(\"%s\", %s_builder);",
                                PORTABLE_WRITER, field.name(), field.member())
                      .appendln("} else {")
                      .formatln("    %s.writePortable(\"%s\", %s.mutate());",
                                PORTABLE_WRITER, field.name(), field.member())
                      .appendln("}");
                break;
            }
            default:
                throw new GeneratorException("Not implemented writePortableField for type: " + field.type() + " in " +
                                             this.getClass()
                                                 .getSimpleName());
        }
    }

    /**
     * Method to convert fields to a binary array.
     *
     * @param bebw {@link BigEndianBinaryWriter} to append the information to.
     * @param getter Method to access the current data to serialize.
     * @param descriptor PDescriptor that is connected to the getter. Can be nested subtypes of the field.
     */
    private void writePortableBinary(String bebw, String getter, PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BINARY:
                writer.formatln("%s.%s(%s.length());", bebw, "writeInt", getter)
                      .formatln("%s.%s(%s.get());", bebw, "write", getter);
                break;
            case BOOL:
                writer.formatln("%s.%s(%s ? (byte)1 : 0);", bebw, "writeByte", getter);
                break;
            case BYTE:
                writer.formatln("%s.%s(%s);", bebw, "writeByte", getter);
                break;
            case DOUBLE:
                writer.formatln("%s.%s(%s);", bebw, "writeDouble", getter);
                break;
            case ENUM:
                writer.formatln("%s.%s(%s.getValue());", bebw, "writeInt", getter);
                break;
            case I16:
                writer.formatln("%s.%s(%s);", bebw, "writeShort", getter);
                break;
            case I32:
                writer.formatln("%s.%s(%s);", bebw, "writeInt", getter);
                break;
            case I64:
                writer.formatln("%s.%s(%s);", bebw, "writeLong", getter);
                break;
            case STRING: {
                final String tempBinary = tempVariable("bin");
                writer.formatln("%s[] %s = %s.getBytes(%s.UTF_8);",
                                byte.class.getName(),
                                tempBinary,
                                getter,
                                StandardCharsets.class.getName())
                      .formatln("%s.%s(%s.length);", bebw, "writeInt", tempBinary)
                      .formatln("%s.%s(%s);", bebw, "write", tempBinary);
                break;
            }
            case LIST: {
                PDescriptor innerList = ((PList) descriptor).itemDescriptor();
                final String iteratorList = tempVariable("it");
                writer.formatln("%s.%s(%s.size());", bebw, "writeInt", getter);
                writer.formatln("for( %s %s : %s ) {", helper.getFieldType(innerList), iteratorList, getter)
                      .begin();
                writePortableBinary(bebw, iteratorList, innerList);
                writer.end()
                      .println("}");
                break;
            }
            case SET: {
                PDescriptor innerSet = ((PSet) descriptor).itemDescriptor();
                final String iteratorSet = tempVariable("it");
                writer.formatln("%s.%s(%s.size());", bebw, "writeInt", getter);
                writer.formatln("for( %s %s : %s ) {", helper.getFieldType(innerSet), iteratorSet, getter)
                      .begin();
                writePortableBinary(bebw, iteratorSet, innerSet);
                writer.end()
                      .println("}");
                break;
            }
            case MAP: {
                PMap pMap = (PMap) descriptor;
                String iterator = "entry";
                writer.formatln("%s.writeInt(%s.size());", bebw, getter)
                      .formatln("for( %s.Entry<%s,%s> %s : %s.entrySet() ) {",
                                Map.class.getName(),
                                helper.getFieldType(pMap.keyDescriptor()),
                                helper.getFieldType(pMap.itemDescriptor()),
                                iterator,
                                getter)
                      .begin();
                writePortableBinary(bebw, iterator + ".getKey()", pMap.keyDescriptor());
                writePortableBinary(bebw, iterator + ".getValue()", pMap.itemDescriptor());
                writer.end()
                      .println("}");
                break;
            }
            case MESSAGE:
                writer.formatln("%s.writeBinary(%s);", getter, bebw);
                break;
            default:
                throw new GeneratorException(
                        "Not implemented writePortableBinary for type: " + helper.getFieldType(descriptor) + " in " +
                        this.getClass()
                            .getSimpleName());
        }
    }

    private void readPortableBinary(String bebr, String variable, PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BINARY:
                writer.formatln("%s = %s.%s(%s.%s());", variable, bebr, "expectBinary", bebr, "expectInt");
                break;
            case BOOL:
                writer.formatln("%s = (%s.%s() > 0 ? true : false);", variable, bebr, "expectByte");
                break;
            case BYTE:
                writer.formatln("%s = %s.%s();", variable, bebr, "expectByte");
                break;
            case DOUBLE:
                writer.formatln("%s = %s.%s();", variable, bebr, "expectDouble");
                break;
            case ENUM:
                writer.formatln("%s = %s.findById(%s.%s());",
                                variable,
                                helper.getFieldType(descriptor),
                                bebr,
                                "expectInt");
                break;
            case I16:
                writer.formatln("%s = %s.%s();", variable, bebr, "expectShort");
                break;
            case I32:
                writer.formatln("%s = %s.%s();", variable, bebr, "expectInt");
                break;
            case I64:
                writer.formatln("%s = %s.%s();", variable, bebr, "expectLong");
                break;
            case STRING:
                writer.formatln("%s = new %s(%s.%s(%s.%s()), %s.UTF_8);",
                                variable,
                                helper.getFieldType(descriptor),
                                bebr,
                                "expectBytes",
                                bebr,
                                "expectInt",
                                StandardCharsets.class.getName());
                break;
            case LIST: {
                PDescriptor itemType = ((PList) descriptor).itemDescriptor();
                final String size = tempVariable("size");
                final String iterator = tempVariable("it");
                final String var = tempVariable("var");
                writer.formatln("int %s = %s.%s();", size, bebr, "expectInt");
                writer.formatln("%s = new %s<>(%s);", variable, ArrayList.class.getName(), size);
                writer.formatln("%s %s;", helper.getFieldType(itemType), var);
                writer.formatln("for( int %s = 0; %s < %s; %s++ ) {",
                                iterator,
                                iterator,
                                size,
                                iterator)
                      .begin();
                readPortableBinary(bebr, var, itemType);
                writer.formatln("%s.add(%s);", variable, var);
                writer.end()
                      .println("}");
                break;
            }
            case SET: {
                PDescriptor itemType = ((PSet) descriptor).itemDescriptor();
                final String size = tempVariable("size");
                final String iterator = tempVariable("it");
                final String tmpVar = tempVariable("var");
                writer.formatln("%s = new %s<>();", variable, LinkedHashSet.class.getName());
                writer.formatln("int %s = %s.%s();", size, bebr, "expectInt");
                writer.formatln("%s %s;", helper.getFieldType(itemType), tmpVar);
                writer.formatln("for( int %s = 0; %s < %s; %s++ ) {",
                                iterator,
                                iterator,
                                size,
                                iterator)
                      .begin();
                readPortableBinary(bebr, tmpVar, itemType);
                writer.formatln("%s.add(%s);", variable, tmpVar);
                writer.end()
                      .println("}");
                break;
            }
            case MAP: {
                PMap pMap = (PMap) descriptor;

                String mapSize = tempVariable("size");
                String keyVariable = tempVariable("key");
                String valueVariable = tempVariable("value");
                String tempIterator = tempVariable("it");

                writer.formatln("%s = new %s<>();",
                                variable, LinkedHashMap.class.getName())
                      .formatln("%s %s = %s.%s();", int.class.getName(), mapSize, bebr, "expectInt")
                      .formatln("%s %s;", helper.getFieldType(pMap.keyDescriptor()), keyVariable)
                      .formatln("%s %s;", helper.getFieldType(pMap.itemDescriptor()), valueVariable)
                      .appendln()
                      .formatln("for( %s %s = 0; %s < %s; %s++) {",
                                int.class.getName(),
                                tempIterator,
                                tempIterator,
                                mapSize,
                                tempIterator)
                      .begin();
                readPortableBinary(bebr, keyVariable, pMap.keyDescriptor());
                readPortableBinary(bebr, valueVariable, pMap.itemDescriptor());

                writer.formatln("%s.put(%s, %s);", variable, keyVariable, valueVariable)
                      .end()
                      .println("}");
                break;
            }
            case MESSAGE:
                final String tempMessage = tempVariable("message");
                writer.formatln("%s._Builder %s = %s.builder();",
                                helper.getFieldType(descriptor),
                                tempMessage,
                                helper.getFieldType(descriptor))
                      .formatln("%s.readBinary(%s, false);", tempMessage, bebr)
                      .formatln("%s = %s.build();", variable, tempMessage);
                break;
            default:
                throw new GeneratorException(
                        "Not implemented readPortableBinary for type: " + helper.getFieldType(descriptor) + " in " +
                        this.getClass()
                            .getSimpleName());
        }
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     *   // for required fields.
     *   portableWriter.writeInt("id", mId);
     *
     *   // for optional fields.
     *   if( isSetLabel() ) {
     *     portableWriter.writeUTF("label", mLabel);
     *   }
     * }
     * </pre>
     */
    private void writeDefaultPortableField(JField field) throws GeneratorException {
        if (field.portableRequiresBinarySerialization()) {
            writer.formatln("%s.writeByteArray(\"%s\", new byte[0]);",
                            PORTABLE_WRITER,
                            field.name());
            return;
        }

        switch (field.type()) {
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", new byte[0]);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case BOOL:
                writer.formatln("%s.writeBoolean(\"%s\", false);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case BYTE:
                writer.formatln("%s.writeByte(\"%s\", (byte) 0);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDouble(\"%s\", 0.0);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case ENUM:
                writer.formatln("%s.writeInt(\"%s\", 0);", PORTABLE_WRITER, field.name());
                break;
            case I16:
                writer.formatln("%s.writeShort(\"%s\", (short) 0);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case I32:
                writer.formatln("%s.writeInt(\"%s\", 0);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case I64:
                writer.formatln("%s.writeLong(\"%s\", 0L);",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case STRING:
                writer.formatln("%s.writeUTF(\"%s\", \"\");",
                                PORTABLE_WRITER,
                                field.name());
                break;
            case LIST:
                writeDefaultPortableFieldList(field,
                                              field.toPList()
                                                   .itemDescriptor());
                break;
            case SET:
                writeDefaultPortableFieldList(field,
                                              field.toPSet()
                                                   .itemDescriptor());
                break;
            case MESSAGE:
                writer.formatln("%s.writeNullPortable(\"%s\", %s.%s, %s.%s);",
                                PORTABLE_WRITER,
                                field.name(),
                                getHazelcastFactory((PMessageDescriptor<?, ?>) field.field().getDescriptor()),
                                HazelcastPortableProgramFormatter.FACTORY_ID,
                                getHazelcastFactory((PMessageDescriptor<?, ?>) field.field().getDescriptor()),
                                getHazelcastClassId((PMessageDescriptor<?, ?>) field.field().getDescriptor()));
                break;
            default:
                throw new GeneratorException(
                        "Not implemented writeDefaultPortableField for type: " + field.type() + " in " + this.getClass()
                                                                                                             .getSimpleName());
        }
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     *   if( isSetBooleanValues() ) {
     *     portableWriter.writeBooleanArray("booleanValues", com.google.common.primitives.Booleans.toArray(mBooleanValues.build()));
     *   }
     * }
     * </pre>
     */
    private void writePortableFieldList(JField field, PDescriptor descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case BYTE:
                writer.formatln("%s.writeByteArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Bytes.class.getName(),
                                field.member());
                break;
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", %s.%s(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                BinaryUtil.class.getName(),
                                "fromBinaryCollection",
                                field.member());
                break;
            case BOOL:
                writer.formatln("%s.writeBooleanArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Booleans.class.getName(),
                                field.member());
                break;
            case DOUBLE:
                writer.formatln("%s.writeDoubleArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Doubles.class.getName(),
                                field.member());
                break;
            case ENUM:
                writer.formatln("%s.writeIntArray(\"%s\", %s.stream().mapToInt(t -> t.getValue()).toArray());",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case I16:
                writer.formatln("%s.writeShortArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Shorts.class.getName(),
                                field.member());
                break;
            case I32:
                writer.formatln("%s.writeIntArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Ints.class.getName(),
                                field.member());
                break;
            case I64:
                writer.formatln("%s.writeLongArray(\"%s\", %s.toArray(%s));",
                                PORTABLE_WRITER,
                                field.name(),
                                Longs.class.getName(),
                                field.member());
                break;
            case STRING:
                writer.formatln("%s.writeUTFArray(\"%s\", %s.toArray(new String[0]));",
                                PORTABLE_WRITER,
                                field.name(),
                                field.member());
                break;
            case MESSAGE:
                writer.formatln("%s<%s.%s> %sList = %s.stream().map(i -> i.mutate()).collect(%s.toList());",
                                List.class.getName(),
                                helper.getValueType(descriptor),
                                "_Builder",
                                camelCase("temp", field.name()),
                                field.member(),
                                Collectors.class.getName());
                writer.formatln("%s.writePortableArray(\"%s\", %sList.toArray(new %s.%s[%sList.size()]));",
                                PORTABLE_WRITER,
                                field.name(),
                                camelCase("temp", field.name()),
                                helper.getValueType(descriptor),
                                "_Builder",
                                camelCase("temp", field.name()));
                break;
            default:
                throw new GeneratorException(
                        "Not implemented writePortableFieldList for list with type: " + descriptor.getType() + " in " +
                        this.getClass()
                            .getSimpleName());
        }
    }

    /**
     * Method to append writing of a field to hazelcast_portable.
     *
     * @param field JField to write.
     * <pre>
     * {@code
     * if( isSetBooleanValues() ) {
     *   portableWriter.writeBooleanArray("booleanValues", com.google.common.primitives.Booleans.toArray(mBooleanValues.build()));
     * }
     * }
     * </pre>
     */
    private void writeDefaultPortableFieldList(JField field, PDescriptor descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case BYTE:

                writer.formatln("%s.writeByteArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case BINARY:
                writer.formatln("%s.writeByteArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                "byte");
                //TODO becomes binary otherwise, and doesn't fit with byte array.
                break;
            case BOOL:
                writer.formatln("%s.writeBooleanArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case DOUBLE:
                writer.formatln("%s.writeDoubleArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case ENUM:
                writer.formatln("%s.writeIntArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                int.class.getName());
                // TODO need fixed as value isn't doable.
                break;
            case I16:
                writer.formatln("%s.writeShortArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case I32:
                writer.formatln("%s.writeIntArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case I64:
                writer.formatln("%s.writeLongArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case STRING:
                writer.formatln("%s.writeUTFArray(\"%s\", new %s[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            case MESSAGE:
                writer.formatln("%s.writePortableArray(\"%s\", new %s._Builder[0]);",
                                PORTABLE_WRITER,
                                field.name(),
                                helper.getValueType(descriptor));
                break;
            default:
                throw new GeneratorException(
                        "Not implemented writeDefaultPortableFieldList for list with type: " + descriptor.getType() +
                        " in " + this.getClass()
                                     .getSimpleName());
        }
    }

    /**
     * Method to append reading of a field to hazelcast_portable.
     *
     * @param field JField to read.
     * <pre>
     * {@code
     *   // for required fields.
     *   setId(portableReader.readInt("id"));
     *
     *   // for optional fields.
     *   if( portableReader.hasField("label") && __temp_optionals.get(2) ) {
     *     setLabel(portableReader.readUTF("label"));
     *   }
     * }
     * </pre>
     */
    private void readPortableField(JField field) {
        if (field.portableRequiresBinarySerialization()) {
            String baisTemp = tempVariable("bais");
            String bebrTemp = tempVariable("bebr");
            String valueVariable = tempVariable("val");

            writer.formatln("try ( %s %s = new %s(%s.readByteArray(\"%s\"));",
                            ByteArrayInputStream.class.getName(),
                            baisTemp,
                            ByteArrayInputStream.class.getName(),
                            PORTABLE_READER,
                            field.name())
                  .formatln("      %s %s = new %s(%s) ) {",
                            BigEndianBinaryReader.class.getName(),
                            bebrTemp,
                            BigEndianBinaryReader.class.getName(),
                            baisTemp)
                  .begin()
                  .formatln("%s %s;", helper.getFieldType(field.field().getDescriptor()), valueVariable);

            readPortableBinary(bebrTemp, valueVariable, field.field().getDescriptor());

            writer.formatln("%s(%s);", field.setter(), valueVariable)
                  .end()
                  .println("}");

            return;
        }

        switch (field.type()) {
            case BINARY:
                writer.formatln("%s(new %s(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                Binary.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BOOL:
                writer.formatln("%s(%s.readBoolean(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case BYTE:
                writer.formatln("%s(%s.readByte(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case DOUBLE:
                writer.formatln("%s(%s.readDouble(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case ENUM:
                writer.formatln("%s(%s.findById(%s.readInt(\"%s\")));",
                                field.setter(),
                                field.instanceType(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I16:
                writer.formatln("%s(%s.readShort(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case I32:
                writer.formatln("%s(%s.readInt(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case I64:
                writer.formatln("%s(%s.readLong(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case STRING:
                writer.formatln("%s(%s.readUTF(\"%s\"));", field.setter(), PORTABLE_READER, field.name());
                break;
            case LIST:
                readPortableFieldList(field, field.toPList().itemDescriptor());
                break;
            case SET:
                readPortableFieldList(field, field.toPSet().itemDescriptor());
                break;
            case MESSAGE: // ((CompactFields._Builder)portableReader.readPortable("compactValue")).build()
                writer.formatln("%s(((%s.%s)%s.readPortable(\"%s\")).%s());",
                                field.setter(),
                                field.instanceType(),
                                "_Builder",
                                PORTABLE_READER,
                                field.name(),
                                "build");
                break;
            default:
                throw new GeneratorException("Not implemented readPortableField for '" + field.name() +
                                             "' type: " + field.type() + " in " +
                                             this.getClass().getSimpleName());
        }
    }

    /**
     * Method to append reading of a field to hazelcast_portable.
     *
     * @param field JField to read.
     * <pre>
     * {@code
     * if( portableReader.hasField("integerValue") && __temp_optionals.get(3) ) {
     *   setIntegerValue(com.google.common.primitives.Ints.asList(portableReader.readIntArray("integerValue")));
     * }
     * }
     * </pre>
     */
    private void readPortableFieldList(JField field, PDescriptor descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case BYTE:
                writer.formatln("%s(%s.asList(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                Bytes.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case BINARY:
                writer.formatln("%s(%s.%s(%s.readByteArray(\"%s\")));",
                                field.setter(),
                                BinaryUtil.class.getName(),
                                "toBinaryCollection",
                                PORTABLE_READER,
                                field.name());
                break;
            case BOOL:
                writer.formatln("%s(%s.asList(%s.readBooleanArray(\"%s\")));",
                                field.setter(),
                                Booleans.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case DOUBLE:
                writer.formatln("%s(%s.asList(%s.readDoubleArray(\"%s\")));",
                                field.setter(),
                                Doubles.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case ENUM:
                writer.formatln(
                        "%s(%s.asList(%s.readIntArray(\"%s\")).stream().map(t -> %s.%s(t.intValue())).collect(%s.toList()));",
                        field.setter(),
                        Ints.class.getName(),
                        PORTABLE_READER,
                        field.name(),
                        descriptor.getName(),
                        "findById",
                        //TODO need to change this to another value.
                        Collectors.class.getName());
                break;
            case I16:
                writer.formatln("%s(%s.asList(%s.readShortArray(\"%s\")));",
                                field.setter(),
                                Shorts.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I32:
                writer.formatln("%s(%s.asList(%s.readIntArray(\"%s\")));",
                                field.setter(),
                                Ints.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case I64:
                writer.formatln("%s(%s.asList(%s.readLongArray(\"%s\")));",
                                field.setter(),
                                Longs.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case STRING:
                writer.formatln("%s(%s.asList(%s.readUTFArray(\"%s\")));",
                                field.setter(),
                                Arrays.class.getName(),
                                PORTABLE_READER,
                                field.name());
                break;
            case MESSAGE:
                writer.formatln("%s(%s.asList(%s.readPortableArray(\"%s\")).stream()" +
                                ".map(i -> ((%s.%s)i).build()).collect(%s.toList()));",
                                field.setter(),
                                Arrays.class.getName(),
                                PORTABLE_READER,
                                field.name(),
                                descriptor.getName(),
                                WRAPPER_CLASS_NAME,
                                Collectors.class.getName());

                break;
            default:
                throw new GeneratorException(
                        "Not implemented readPortableField for list with type: " + descriptor.getType() + " in " +
                        this.getClass()
                            .getSimpleName());
        }
    }

}
